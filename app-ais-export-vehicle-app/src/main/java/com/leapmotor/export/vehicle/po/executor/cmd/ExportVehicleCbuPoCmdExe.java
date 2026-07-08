package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.nexus.customer.api.CustomerApi;
import com.leapmotor.export.nexus.customer.request.CustomerRequest;
import com.leapmotor.export.nexus.customer.response.CustomerResponse;
import com.leapmotor.export.nexus.vehicle.api.VehicleMaterialApi;
import com.leapmotor.export.nexus.vehicle.api.VehiclePriceHqApi;
import com.leapmotor.export.nexus.vehicle.dto.VehicleMaterialDto;
import com.leapmotor.export.nexus.vehicle.dto.VehiclePriceHqDto;
import com.leapmotor.export.nexus.vehicle.request.VehicleMaterialBatchMatchReq;
import com.leapmotor.export.nexus.vehicle.request.VehiclePriceHqMatchReq;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.po.enums.DataSourceEnum;
import com.leapmotor.export.vehicle.po.enums.ExportVehicleCbuPoStatusEnum;
import com.leapmotor.export.vehicle.po.enums.OrderTypeEnum;
import com.leapmotor.export.vehicle.po.req.HongqiCbuOrderDetailReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.platform.ais.common.redis.annotation.DistributeLock;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * ExportVehicleCbuPo 写操作执行器
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Slf4j
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoCmdExe {

    private ExportVehicleCbuPoGateway purchaseOrderGateway;
    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;
    /** 客户主数据，对接 app-ais-export-nexus 客户服务 */
    private CustomerApi customerApi;
    /** 整编物料主数据，对接 app-ais-export-nexus 整编物料服务 */
    private VehicleMaterialApi vehicleMaterialApi;
    /** 价格主数据，对接 app-ais-export-nexus 价格服务 */
    private VehiclePriceHqApi vehiclePriceHqApi;


    public String addOrUpdateExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo) {
        return purchaseOrderDo.getId() == null
                ? purchaseOrderGateway.addExportVehicleCbuPo(purchaseOrderDo)
                : purchaseOrderGateway.updateExportVehicleCbuPo(purchaseOrderDo);
    }

    public boolean deleteExportVehicleCbuPoByIds(List<String> ids) {
        return purchaseOrderGateway.deleteExportVehicleCbuPoByIds(ids);
    }

    public boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param) {
        return purchaseOrderGateway.enableOrDisableExportVehicleCbuPoByIds(param);
    }

    // ==================== 4.1 红旗CBU推送创建草稿订单 ====================

    /**
     * 红旗 CBU 订单采购申请 → 创建草稿 PO 订单（主表 + 明细联保存）。
     * 匹配类字段（客户/物料/单价）匹配不到则留空，留待 4.3 提交时强校验。
     */
    @DistributeLock(nameSpace = "export-vehicle", prefix = "po:createFromHongqi", value = "#req.poNo", tryLockTimeout = 3000, expireAfter = 60000, failedCode = 600, failedMsg = "订单正在创建，请稍后重试")
    @Transactional(rollbackFor = Exception.class)
    public String createFromHongqi(HongqiCbuPurchaseOrderReq req) {
        // 1. PO单号唯一性（已存在则报错，重复推送同样报错）
        ExportVehicleCbuPoQo existQo = new ExportVehicleCbuPoQo();
        existQo.setPoNo(req.getPoNo());
        if (purchaseOrderGateway.getOneExportVehicleCbuPo(existQo) != null) {
            throw new BizException("PO单号已存在: " + req.getPoNo());
        }

        // 2. 同一 PO 内订单明细编号唯一
        checkDetailNumberUnique(req);

        // 3. 客户匹配（接口客户编码 = 外部客户编码 → 客户主数据）
        CustomerResponse customer = queryCustomerByExternalCode(req.getCustomerCode());

        // 4. 抬头组装
        LocalDateTime now = LocalDateTime.now();
        ExportVehicleCbuPoDo order = new ExportVehicleCbuPoDo();
        order.setPoNo(req.getPoNo());
        order.setOrderStatus(ExportVehicleCbuPoStatusEnum.DRAFT.getCode());
        order.setExternalCustomerCode(req.getCustomerCode());
        if (customer != null) {
            order.setCustomerCode(customer.getCustomerCode());
            order.setCustomerName(customer.getCustomerName());
        }
        order.setDataSource(DataSourceEnum.HONGQI.getCode());
        order.setOrderType(OrderTypeEnum.CBU.getCode());
        order.setPlanVersion(req.getPlanVersion());

        // 5. 明细组装（物料匹配 + 单价匹配 + 金额合计）
        List<ExportVehicleCbuPoDetailDo> details = buildDetails(req, now);

        // 6. 总金额 = SUM(明细金额合计)
        BigDecimal total = details.stream()
                .map(ExportVehicleCbuPoDetailDo::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        // 7. 联保存：主表 + 明细批量（同事务）
        String poId = purchaseOrderGateway.addExportVehicleCbuPo(order);
        purchaseOrderDetailGateway.addBatchExportVehicleCbuPoDetail(details);
        return poId;
    }

    /** 同一 PO 内订单明细编号唯一校验 */
    private void checkDetailNumberUnique(HongqiCbuPurchaseOrderReq req) {
        Set<String> seen = new HashSet<>();
        for (HongqiCbuOrderDetailReq d : req.getOrderDetailList()) {
            if (!seen.add(d.getDetailNumber())) {
                throw new BizException("订单明细编号重复: " + d.getDetailNumber());
            }
        }
    }

    /** 明细组装：物料匹配（带出描述/市场/年款/选装）+ 单价匹配 + 金额合计 */
    private List<ExportVehicleCbuPoDetailDo> buildDetails(HongqiCbuPurchaseOrderReq req, LocalDateTime orderCreateTime) {
        List<ExportVehicleCbuPoDetailDo> list = new ArrayList<>();
        for (HongqiCbuOrderDetailReq d : req.getOrderDetailList()) {
            ExportVehicleCbuPoDetailDo detail = new ExportVehicleCbuPoDetailDo();
            detail.setPoNo(req.getPoNo());
            detail.setPoDetailNo(NoGenerator.genNoOnly("POD", "yyyyMMddHHmm", 5));
            detail.setOrderDetailNumber(d.getDetailNumber());
            detail.setSyncMaterialCode(d.getMaterialCode());
            detail.setDemandQuantity(d.getDemandQuantity());
            detail.setExpectedOfflineDate(parseDate(d.getExpectedOfflineDate()));
            detail.setVinYear(d.getVinYear());
            // 生产备注：接口不传，草稿态业务填，留空

            // 物料匹配（18位同步物料编码 → 整编主数据）
            VehicleMaterialDto material = queryMaterialByCode(d.getMaterialCode());
            if (material != null) {
                detail.setMaterialCode(material.getCode());
                detail.setMaterialDescription(material.getMaterialDesc());
                detail.setSalesMarket(material.getSalesMarket());
                detail.setModelYear(material.getYearType());
                detail.setOptionalPackage(material.getOptionPackage());
            }

            // 单价匹配（物料编码 + 订单类型 + 订单创建时间 → 价格主数据，多条取审批最新）
            if (detail.getMaterialCode() != null) {
                BigDecimal unitPrice = queryLatestApprovedPrice(
                        detail.getMaterialCode(), 1, orderCreateTime);
                detail.setUnitPrice(unitPrice);
                if (unitPrice != null && d.getDemandQuantity() != null) {
                    detail.setTotalAmount(unitPrice.multiply(d.getDemandQuantity()));
                }
            }
            list.add(detail);
        }
        return list;
    }

    /** 期望下线日期 YYYYMMDD → LocalDate */
    private LocalDate parseDate(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(yyyymmdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            throw new BizException("期望下线日期格式错误(应为YYYYMMDD): " + yyyymmdd);
        }
    }

    // ==================== 4.2 订单编辑保存（仅草稿态；仅销售公司 + 明细生产备注） ====================

    /**
     * 草稿态编辑保存：仅改销售公司（抬头）+ 明细生产备注（逐行）。
     * 防篡改：update Do 只 set 可编辑字段，updateById 走 not_null 策略只更新这几项，其余字段不动。
     * 防并发：@DistributeLock 按 poId 加锁。
     */
    @DistributeLock(nameSpace = "export-vehicle", prefix = "po:save", value = "#req.poId",
            tryLockTimeout = 3000, expireAfter = 60000, failedCode = 600, failedMsg = "订单正在被他人编辑，请稍后重试")
    @Transactional(rollbackFor = Exception.class)
    public void saveExportVehicleCbuPo(ExportVehicleCbuPoSaveReq req) {
        // 1. 查订单 + 草稿态校验
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅草稿态订单可编辑");
        }

        // 2. 改抬头销售公司（只 set id + salesCompanyCode，其余字段不更新）
        ExportVehicleCbuPoDo headerUpdate = new ExportVehicleCbuPoDo();
        headerUpdate.setId(req.getPoId());
        headerUpdate.setSalesCompanyCode(req.getSalesCompanyCode());
        purchaseOrderGateway.updateExportVehicleCbuPo(headerUpdate);

        // 3. 明细生产备注逐行更新（按 detailId，只 set id + productionRemark）
        for (ExportVehicleCbuPoDetailSaveReq d : req.getDetailList()) {
            ExportVehicleCbuPoDetailDo detailUpdate = new ExportVehicleCbuPoDetailDo();
            detailUpdate.setId(d.getDetailId());
            detailUpdate.setProductionRemark(d.getProductionRemark());
            purchaseOrderDetailGateway.updateExportVehicleCbuPoDetail(detailUpdate);
        }
    }

    // ==================== 主数据查询辅助方法（直调 nexus Feign API） ====================

    private CustomerResponse queryCustomerByExternalCode(String externalCode) {
        if (externalCode == null || externalCode.isEmpty()) {
            return null;
        }
        try {
            CustomerRequest req = new CustomerRequest();
            req.setExternalCode(externalCode);
            AjaxResult<List<CustomerResponse>> ret = customerApi.list(req);
            List<CustomerResponse> list = (ret != null) ? ret.getData() : null;
            return (list != null && !list.isEmpty()) ? list.get(0) : null;
        } catch (Exception e) {
            log.error("客户主数据查询失败 externalCode={}", externalCode, e);
            return null;
        }
    }

    private VehicleMaterialDto queryMaterialByCode(String materialCode) {
        if (materialCode == null || materialCode.isEmpty()) {
            return null;
        }
        try {
            VehicleMaterialBatchMatchReq req = new VehicleMaterialBatchMatchReq();
            req.setMaterialCodes(Collections.singletonList(materialCode));
            AjaxResult<List<VehicleMaterialDto>> ret = vehicleMaterialApi.batchMatch(req);
            List<VehicleMaterialDto> list = (ret != null) ? ret.getData() : null;
            return (list != null && !list.isEmpty()) ? list.get(0) : null;
        } catch (Exception e) {
            log.error("整编主数据查询失败 materialCode={}", materialCode, e);
            return null;
        }
    }

    private BigDecimal queryLatestApprovedPrice(String materialCode, Integer orderType, LocalDateTime orderCreateTime) {
        if (materialCode == null || materialCode.isEmpty()) {
            return null;
        }
        try {
            VehiclePriceHqMatchReq req = new VehiclePriceHqMatchReq();
            req.setMaterialCode(materialCode);
            req.setOrderType(orderType != null ? String.valueOf(orderType) : null);
            req.setOrderCreateTime(orderCreateTime);
            AjaxResult<VehiclePriceHqDto> ret = vehiclePriceHqApi.match(req);
            VehiclePriceHqDto dto = (ret != null) ? ret.getData() : null;
            return dto != null ? dto.getPrice() : null;
        } catch (Exception e) {
            log.error("价格主数据查询失败 materialCode={}, orderType={}", materialCode, orderType, e);
            return null;
        }
    }
}
