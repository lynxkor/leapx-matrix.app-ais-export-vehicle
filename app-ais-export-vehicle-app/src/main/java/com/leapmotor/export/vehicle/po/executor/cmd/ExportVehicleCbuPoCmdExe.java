package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.nexus.company.request.CompanyRequest;
import com.leapmotor.export.nexus.company.response.CompanyResponse;
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
import com.leapmotor.export.vehicle.po.enums.PoOrderTypeEnum;
import com.leapmotor.export.vehicle.po.req.HongqiCbuOrderDetailReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.export.vehicle.po.validator.HongqiCbuPurchaseOrderValidator;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    /** 销售公司主数据，对接 app-ais-export-nexus 销售公司服务 */
    private CompanyApi companyApi;
    /** 红旗CBU采购申请入参校验器（手写全量收集，替代注解校验） */
    private HongqiCbuPurchaseOrderValidator purchaseOrderValidator;


    public boolean deleteExportVehicleCbuPoByIds(List<String> ids) {
        return purchaseOrderGateway.deleteExportVehicleCbuPoByIds(ids);
    }

    public boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param) {
        return purchaseOrderGateway.enableOrDisableExportVehicleCbuPoByIds(param);
    }

    // ==================== 红旗CBU推送创建草稿订单 ====================

    /**
     * 红旗 CBU 订单采购申请 → 创建草稿 PO 订单（主表 + 明细联保存）。
     * 匹配类字段（客户/物料/单价）匹配不到则留空，留待 提交时强校验。
     */
    @Transactional(rollbackFor = Exception.class)
    public String createFromHongqi(HongqiCbuPurchaseOrderReq req) {
        // 0. 参数全量校验：一次性收集全部参数错误（[字段]/第N行[字段]+错误值），不依赖注解，message 可控
        List<String> paramErrors = purchaseOrderValidator.validate(req);
        if (!paramErrors.isEmpty()) {
            throw new BizException(String.join("；", paramErrors));
        }

        // 1. PO单号唯一性（已存在则报错，重复推送同样报错）
        ExportVehicleCbuPoQo existQo = new ExportVehicleCbuPoQo();
        existQo.setPoNo(req.getPoNo());
        if (purchaseOrderGateway.getOneExportVehicleCbuPo(existQo) != null) {
            throw new BizException("PO单号已存在: " + req.getPoNo());
        }

        // 2. 客户匹配（接口客户编码 = 外部客户编码 → 客户主数据）
        CustomerResponse customer = queryCustomerByExternalCode(req.getCustomerCode());

        // 3. 抬头组装
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
        order.setSalesCompanyCode("1000");
        order.setOrderType(PoOrderTypeEnum.CBU.getCode());
        order.setPlanVersion(req.getPlanVersion());

        // 4. 明细组装（物料匹配 + 单价匹配 + 金额合计）
        List<ExportVehicleCbuPoDetailDo> details = buildDetails(req, now);

        // 5. 总金额 = SUM(明细金额合计)
        BigDecimal total = details.stream()
                .map(ExportVehicleCbuPoDetailDo::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        // 6. 联保存：主表 + 明细批量（同事务）
        String poId = purchaseOrderGateway.addExportVehicleCbuPo(order);
        purchaseOrderDetailGateway.addBatchExportVehicleCbuPoDetail(details);
        return poId;
    }

    /** 校验销售公司编码在主数据中存在（数据来源为销售公司主数据下拉，禁止随意填写） */
    /** 校验销售公司编码在主数据中存在（数据来源为销售公司主数据下拉，禁止随意填写） */
    private void checkSalesCompany(String companyCode) {
        try {
            CompanyRequest cReq = new CompanyRequest();
            cReq.setCompanyCode(companyCode);
            AjaxResult<List<CompanyResponse>> ret = companyApi.list(cReq);
            if (ret == null || !Objects.equals(ResultEnum.SUCCESS.getCode(), ret.getCode())) {
                throw new BizException("销售公司信息查询失败");
            }
            List<CompanyResponse> data = ret.getData();
            if (data == null || data.stream().noneMatch(c -> companyCode.equals(c.getCompanyCode()))) {
                throw new BizException("销售公司不存在: " + companyCode);
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("销售公司主数据查询失败 companyCode={}", companyCode, e);
            throw new BizException("销售公司信息查询失败");
        }
    }

    /** 明细组装：物料匹配（带出描述/市场/年款/选装）+ 单价匹配 + 金额合计 */
    private List<ExportVehicleCbuPoDetailDo> buildDetails(HongqiCbuPurchaseOrderReq req, LocalDateTime orderCreateTime) {
        // 物料批量匹配：一次 Feign，循环内查 Map
        Map<String, VehicleMaterialDto> materialMap = queryMaterialByCodes(req.getOrderDetailList().stream()
                .map(HongqiCbuOrderDetailReq::getMaterialCode)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .collect(Collectors.toList()));
        // 价格批量匹配：用物料匹配到的 code 一次 batchMatch
        Map<String, BigDecimal> priceMap = queryLatestApprovedPrices(
                new ArrayList<>(materialMap.values().stream().map(VehicleMaterialDto::getCode).collect(Collectors.toSet())),
                1, orderCreateTime);
        List<ExportVehicleCbuPoDetailDo> list = new ArrayList<>();
        for (HongqiCbuOrderDetailReq d : req.getOrderDetailList()) {
            ExportVehicleCbuPoDetailDo detail = new ExportVehicleCbuPoDetailDo();
            detail.setPoNo(req.getPoNo());
            detail.setPoDetailNo("POD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + NoGenerator.genNoOnly("POD", "yyyyMMddHHmm", 5));
            detail.setOrderDetailNumber(d.getDetailNumber());
            detail.setSyncMaterialCode(d.getMaterialCode());
            detail.setDemandQuantity(d.getDemandQuantity());
            detail.setExpectedOfflineDate(parseDate(d.getExpectedOfflineDate()));
            detail.setVinYear(d.getVinYear());
            // 生产备注：接口不传，草稿态业务填，留空

            // 物料匹配（18位同步物料编码 → 整编主数据）
            VehicleMaterialDto material = materialMap.get(d.getMaterialCode());
            if (material != null) {
                detail.setMaterialCode(material.getCode());
                detail.setMaterialDescription(material.getMaterialDesc());
                detail.setSalesMarket(material.getSalesMarket());
                detail.setModelYear(material.getYearType());
                detail.setOptionalPackage(material.getOptionPackage());
            }

            // 单价匹配（物料编码 + 订单类型 + 订单创建时间 → 价格主数据，多条取审批最新）
            if (detail.getMaterialCode() != null) {
                BigDecimal unitPrice = priceMap.get(detail.getMaterialCode());
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

    // ==================== 订单编辑保存（仅草稿态；仅销售公司 + 明细生产备注） ====================

    /**
     * 草稿态编辑保存：仅改销售公司（抬头）+ 明细生产备注（逐行）。
     * 防篡改：update Do 只 set 可编辑字段，updateById 走 not_null 策略只更新这几项，其余字段不动。
     */
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

        // 校验销售公司存在于主数据（下拉数据源，禁止随意填写）
        checkSalesCompany(req.getSalesCompanyCode());

        // 2. 改抬头销售公司（只 set id + salesCompanyCode，其余字段不更新）
        ExportVehicleCbuPoDo headerUpdate = new ExportVehicleCbuPoDo();
        headerUpdate.setId(req.getPoId());
        headerUpdate.setSalesCompanyCode(req.getSalesCompanyCode());
        purchaseOrderGateway.updateExportVehicleCbuPo(headerUpdate);

        // 3. 明细生产备注逐行更新（按 detailId，只 set id + productionRemark）
        for (ExportVehicleCbuPoDetailSaveReq d : req.getDetailList()) {
            // 校验明细归属当前 PO，防跨 PO 篡改他人明细
            ExportVehicleCbuPoDetailDo existDetail = purchaseOrderDetailGateway.getExportVehicleCbuPoDetailById(d.getDetailId());
            if (existDetail == null || !order.getPoNo().equals(existDetail.getPoNo())) {
                throw new BizException("明细不属于当前订单，不允许编辑");
            }
            // 生产备注：前端传什么更新什么，传 null/空即清空（显式 set，绕开 updateById 的 not_null 策略）
            purchaseOrderDetailGateway.updateProductionRemark(d.getDetailId(), d.getProductionRemark());
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

    /** 批量查整编物料主数据（一次 Feign），返回 code → dto，循环内查 Map 替代逐条 */
    private Map<String, VehicleMaterialDto> queryMaterialByCodes(List<String> materialCodes) {
        if (materialCodes == null || materialCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            VehicleMaterialBatchMatchReq req = new VehicleMaterialBatchMatchReq();
            req.setMaterialCodes(materialCodes);
            AjaxResult<List<VehicleMaterialDto>> ret = vehicleMaterialApi.batchMatch(req);
            List<VehicleMaterialDto> list = (ret != null) ? ret.getData() : null;
            if (list == null || list.isEmpty()) {
                return Collections.emptyMap();
            }
            return list.stream().collect(Collectors.toMap(VehicleMaterialDto::getCode, dto -> dto, (a, b) -> a));
        } catch (Exception e) {
            log.error("整编主数据批量查询失败 codes={}", materialCodes, e);
            return Collections.emptyMap();
        }
    }

    /** 批量匹配价格（一次 Feign batchMatch），返回 materialCode → price，无价的不入 Map */
    private Map<String, BigDecimal> queryLatestApprovedPrices(List<String> materialCodes, Integer orderType, LocalDateTime orderCreateTime) {
        if (materialCodes == null || materialCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<VehiclePriceHqMatchReq> reqs = materialCodes.stream().map(code -> {
                VehiclePriceHqMatchReq r = new VehiclePriceHqMatchReq();
                r.setMaterialCode(code);
                r.setOrderType(orderType != null ? String.valueOf(orderType) : null);
                r.setOrderCreateTime(orderCreateTime);
                return r;
            }).collect(Collectors.toList());
            AjaxResult<List<VehiclePriceHqDto>> ret = vehiclePriceHqApi.batchMatch(reqs);
            List<VehiclePriceHqDto> list = (ret != null) ? ret.getData() : null;
            if (list == null || list.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, BigDecimal> map = new HashMap<>();
            for (int i = 0; i < materialCodes.size() && i < list.size(); i++) {
                VehiclePriceHqDto dto = list.get(i);
                if (dto != null && dto.getPrice() != null) {
                    map.put(materialCodes.get(i), dto.getPrice());
                }
            }
            return map;
        } catch (Exception e) {
            log.error("价格主数据批量查询失败 codes={}", materialCodes, e);
            return Collections.emptyMap();
        }
    }
}
