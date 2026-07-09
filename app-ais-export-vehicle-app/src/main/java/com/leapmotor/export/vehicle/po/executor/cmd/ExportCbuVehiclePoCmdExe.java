package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.PlaceholderCustomerDataGateway;
import com.leapmotor.export.vehicle.po.gateway.PlaceholderMaterialDataGateway;
import com.leapmotor.export.vehicle.po.gateway.PlaceholderPriceDataGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGateway;
import com.leapmotor.export.vehicle.po.model.PlaceholderCustomerDataDo;
import com.leapmotor.export.vehicle.po.model.PlaceholderMaterialDataDo;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import com.leapmotor.export.vehicle.po.enums.ExportCbuVehiclePoStatusEnum;
import com.leapmotor.export.vehicle.po.req.HongqiCbuOrderDetailReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSaveReq;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.platform.ais.common.redis.annotation.DistributeLock;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * ExportCbuVehiclePo 写操作执行器
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportCbuVehiclePoCmdExe {

    private ExportCbuVehiclePoGateway purchaseOrderGateway;
    private ExportCbuVehiclePoDetailGateway purchaseOrderDetailGateway;
    /** 【占位】客户主数据查询，团队真代码到位后换成真 Gateway */
    private PlaceholderCustomerDataGateway customerDataGateway;
    /** 【占位】整编主数据查询 */
    private PlaceholderMaterialDataGateway materialDataGateway;
    /** 【占位】价格主数据查询 */
    private PlaceholderPriceDataGateway priceDataGateway;


    public String addOrUpdateExportCbuVehiclePo(ExportCbuVehiclePoDo purchaseOrderDo) {
        return purchaseOrderDo.getId() == null
                ? purchaseOrderGateway.addExportCbuVehiclePo(purchaseOrderDo)
                : purchaseOrderGateway.updateExportCbuVehiclePo(purchaseOrderDo);
    }

    public boolean deleteExportCbuVehiclePoByIds(List<String> ids) {
        return purchaseOrderGateway.deleteExportCbuVehiclePoByIds(ids);
    }

    public boolean enableOrDisableExportCbuVehiclePoByIds(LpBaseParam param) {
        return purchaseOrderGateway.enableOrDisableExportCbuVehiclePoByIds(param);
    }

    // ==================== 4.1 红旗CBU推送创建草稿订单 ====================

    /**
     * 红旗 CBU 订单采购申请 → 创建草稿 PO 订单（主表 + 明细联保存）。
     * 匹配类字段（客户/物料/单价）匹配不到则留空，留待 4.3 提交时强校验。
     */
    @Transactional(rollbackFor = Exception.class)
    public String createFromHongqi(HongqiCbuPurchaseOrderReq req) {
        // 1. PO单号唯一性（已存在则报错，重复推送同样报错）
        ExportCbuVehiclePoQo existQo = new ExportCbuVehiclePoQo();
        existQo.setPoNo(req.getPoNo());
        if (purchaseOrderGateway.getOneExportCbuVehiclePo(existQo) != null) {
            throw new BizException("PO单号已存在: " + req.getPoNo());
        }

        // 2. 同一 PO 内订单明细编号唯一
        checkDetailNumberUnique(req);

        // 3. 客户匹配（接口客户编码 = 外部客户编码 → 客户主数据）
        PlaceholderCustomerDataDo customer = customerDataGateway.getByExternalCode(req.getCustomerCode());

        // 4. 抬头组装
        LocalDateTime now = LocalDateTime.now();
        ExportCbuVehiclePoDo order = new ExportCbuVehiclePoDo();
        order.setPoNo(req.getPoNo());
        order.setOrderStatus(ExportCbuVehiclePoStatusEnum.DRAFT.getCode());
        order.setExternalCustomerCode(req.getCustomerCode());
        if (customer != null) {
            order.setCustomerCode(customer.getCustomerCode());
            order.setCustomerName(customer.getCustomerName());
        }
        order.setDataSource("红旗");
        order.setOrderType("CBU");
        order.setPlanVersion(req.getPlanVersion());

        // 5. 明细组装（物料匹配 + 单价匹配 + 金额合计）
        List<ExportCbuVehiclePoDetailDo> details = buildDetails(req, now);

        // 6. 总金额 = SUM(明细金额合计)
        BigDecimal total = details.stream()
                .map(ExportCbuVehiclePoDetailDo::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        // 7. 联保存：主表 + 明细批量（同事务）
        String poId = purchaseOrderGateway.addExportCbuVehiclePo(order);
        purchaseOrderDetailGateway.addBatchExportCbuVehiclePoDetail(details);
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
    private List<ExportCbuVehiclePoDetailDo> buildDetails(HongqiCbuPurchaseOrderReq req, LocalDateTime orderCreateTime) {
        List<ExportCbuVehiclePoDetailDo> list = new ArrayList<>();
        for (HongqiCbuOrderDetailReq d : req.getOrderDetailList()) {
            ExportCbuVehiclePoDetailDo detail = new ExportCbuVehiclePoDetailDo();
            detail.setPoNo(req.getPoNo());
            detail.setPoDetailNumber(generatePoDetailNumber());
            detail.setOrderDetailNumber(d.getDetailNumber());
            detail.setSyncMaterialCode(d.getMaterialCode());
            detail.setDemandQuantity(d.getDemandQuantity());
            detail.setExpectedOfflineDate(parseDate(d.getExpectedOfflineDate()));
            detail.setVinYear(d.getVinYear());
            // 生产备注：接口不传，草稿态业务填，留空

            // 物料匹配（18位同步物料编码 → 整编主数据）
            PlaceholderMaterialDataDo material = materialDataGateway.getBySyncMaterialCode(d.getMaterialCode());
            if (material != null) {
                detail.setMaterialCode(material.getMaterialCode());
                detail.setMaterialDescription(material.getMaterialDescription());
                detail.setSalesMarket(material.getSalesMarket());
                detail.setYearModel(material.getYearModel());
                detail.setOptionalPackage(material.getOptionalPackage());
            }

            // 单价匹配（物料编码 + 订单类型 + 订单创建时间 → 价格主数据，多条取审批最新）
            if (detail.getMaterialCode() != null) {
                BigDecimal unitPrice = priceDataGateway.getLatestApprovedPrice(
                        detail.getMaterialCode(), "CBU", orderCreateTime);
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

    /**
     * PO 明细编号：POD + 年月日 + 时分 + 5位流水。
     * TODO 正式用平台 @AutoCode 或 NoGenerator（防并发冲突），当前占位。
     */
    private String generatePoDetailNumber() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String seq = String.format("%05d", new Random().nextInt(100000));
        return "POD" + ts + seq;
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
    public void saveExportCbuVehiclePo(ExportCbuVehiclePoSaveReq req) {
        // 1. 查订单 + 草稿态校验
        ExportCbuVehiclePoDo order = purchaseOrderGateway.getExportCbuVehiclePoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportCbuVehiclePoStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅草稿态订单可编辑");
        }

        // 2. 改抬头销售公司（只 set id + salesCompany，其余字段不更新）
        ExportCbuVehiclePoDo headerUpdate = new ExportCbuVehiclePoDo();
        headerUpdate.setId(req.getPoId());
        headerUpdate.setSalesCompany(req.getSalesCompany());
        purchaseOrderGateway.updateExportCbuVehiclePo(headerUpdate);

        // 3. 明细生产备注逐行更新（按 detailId，只 set id + productionRemark）
        for (ExportCbuVehiclePoDetailSaveReq d : req.getDetailList()) {
            ExportCbuVehiclePoDetailDo detailUpdate = new ExportCbuVehiclePoDetailDo();
            detailUpdate.setId(d.getDetailId());
            detailUpdate.setProductionRemark(d.getProductionRemark());
            purchaseOrderDetailGateway.updateExportCbuVehiclePoDetail(detailUpdate);
        }
    }
}
