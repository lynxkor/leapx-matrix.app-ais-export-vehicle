package com.leapmotor.export.vehicle.so.executor.cmd;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.pricechangerecord.gateway.PriceChangeRecordGateway;
import com.leapmotor.export.vehicle.pricechangerecord.model.PriceChangeRecordDo;
import com.leapmotor.export.vehicle.so.dto.SoImportPriceDto;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.paltform.ais.common.model.excel.in.ExcelBaseResult;
import com.leapmotor.paltform.ais.common.model.excel.in.ImportExcelDTO;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.redis.BlockLock;
import com.leapmotor.platform.ais.common.redis.DistributedLockTemplate;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class SoImportPriceExe {

    /** 与平台导入模板 commonDataProcessExcelModelMap.maxRowNum 对齐，防御平台配置失效 */
    private static final int MAX_IMPORT_ROWS = 5000;

    /** DDL before_price/after_price 为 decimal(20,4)，整数部分最多16位 */
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999999999.99");

    /** 启用状态，与既有代码 setStatus(1) 惯例一致 */
    private static final int STATUS_ENABLED = 1;

    /** 流水号时间戳格式：JG+yyyyMMddHHmmss+5位流水 */
    private static final DateTimeFormatter SERIAL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VehicleCbuSoGateway vehicleCbuSoGateway;
    private final VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;
    private final PayableListDetailGateway payableListDetailGateway;
    private final PayableListGateway payableListGateway;
    private final PriceChangeRecordGateway priceChangeRecordGateway;
    private final DistributedLockTemplate distributedLockTemplate;
    private final TransactionTemplate transactionTemplate;

    public ExcelBaseResult execute(ImportExcelDTO dto) {
        ExcelBaseResult result = new ExcelBaseResult();

        if (dto == null || StrUtil.isBlank(dto.getExcel())) {
            result.setSuccessNum(0);
            result.setFailNum(0);
            return result;
        }

        List<SoImportPriceDto> rawList = JSONObject.parseArray(dto.getExcel(), SoImportPriceDto.class);
        if (CollectionUtils.isEmpty(rawList)) {
            result.setSuccessNum(0);
            result.setFailNum(0);
            return result;
        }

        if (rawList.size() > MAX_IMPORT_ROWS) {
            throw new BizException("单次导入不能超过" + MAX_IMPORT_ROWS + "行，请分批导入");
        }

        // SO单号去重，按最后一笔为准
        LinkedHashMap<String, SoImportPriceDto> deduped = new LinkedHashMap<>();
        for (SoImportPriceDto item : rawList) {
            if (item != null && StrUtil.isNotBlank(item.getSoNo())) {
                deduped.put(item.getSoNo().trim(), item);
            }
        }

        // 批量查询SO，构建Map
        List<String> soNos = new ArrayList<>(deduped.keySet());
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setSoNos(soNos);
        List<VehicleCbuSoDo> soList = vehicleCbuSoGateway.listVehicleCbuSo(qo);
        Map<String, VehicleCbuSoDo> soNoMap = soList.stream()
                .collect(Collectors.toMap(VehicleCbuSoDo::getSoNo, s -> s, (a, b) -> a));

        // 链式校验
        List<SoImportPriceContext> validList = new ArrayList<>();
        List<SoImportPriceContext> failList = new ArrayList<>();

        for (Map.Entry<String, SoImportPriceDto> entry : deduped.entrySet()) {
            SoImportPriceDto item = entry.getValue();
            SoImportPriceContext ctx = new SoImportPriceContext();
            ctx.setSoNo(entry.getKey());
            ctx.setPrice(item.getPrice());

            ctx.checkSoExists(soNoMap)
               .checkOrderStatus()
               .checkPrice();

            if (ctx.isValid()) {
                validList.add(ctx);
            } else {
                failList.add(ctx);
            }
        }

        if (CollectionUtils.isEmpty(validList)) {
            result.setSuccessNum(0);
            result.setFailNum(failList.size());
            result.setData(buildFailData(failList));
            return result;
        }

        // 按应付清单ID分组，用于加锁；有效行可能在锁内缩减，锁内联动范围以重查后为准
        List<String> validSoNos = validList.stream().map(c -> c.getSo().getSoNo()).collect(Collectors.toList());
        List<PayableListDetailDo> relatedDetails = payableListDetailGateway.listBySoNos(validSoNos);
        Set<String> payableListIds = new HashSet<>();
        for (PayableListDetailDo detail : relatedDetails) {
            payableListIds.add(detail.getPayableListId());
        }

        // 先获取分布式锁，再在锁内开启并提交事务，确保锁释放晚于事务提交（并发读不到未提交数据）
        Long userId = LoginUserUtils.getCurrentUserOrSysDefault().getUserId();
        List<BlockLock> locks = new ArrayList<>();
        List<SoImportPriceContext> staleFailList = new ArrayList<>();
        try {
            locks.add(distributedLockTemplate.blockLock("so:importPrice:user:" + userId, 30000, 60000));
            for (String plId : payableListIds) {
                locks.add(distributedLockTemplate.blockLock("so:importPrice:payableList:" + plId, 30000, 60000));
            }

            transactionTemplate.execute(status -> {
                doImportInLockTransaction(validList, relatedDetails, staleFailList);
                return null;
            });
        } finally {
            for (BlockLock lock : locks) {
                try { lock.close(); } catch (Exception e) { log.warn("释放锁失败", e); }
            }
        }

        failList.addAll(staleFailList);
        result.setSuccessNum(validList.size());
        result.setFailNum(failList.size());
        result.setData(buildFailData(failList));
        return result;
    }

    /**
     * 锁内事务体：刷新SO快照后重查状态守卫，再对仍有效的行执行导入更新。
     *
     * 锁外校验与事务提交间存在并发窗口（应付清单清账置SO为已收款、SO作废流程均不持有导入维度锁），
     * 若不重查，SO被并发置为已收款/已作废后仍会被改价并重算其应付清单总额，绕过"已收款、已作废不可改价"闸门；
     * 重查失败的行移入 staleFailList，与校验失败行同样进入失败文件。
     */
    private void doImportInLockTransaction(List<SoImportPriceContext> validList,
                                           List<PayableListDetailDo> relatedDetails,
                                           List<SoImportPriceContext> staleFailList) {
        refreshSoSnapshot(validList);
        for (SoImportPriceContext ctx : validList) {
            ctx.checkOrderStatus();
        }
        List<SoImportPriceContext> newlyFailed = validList.stream()
                .filter(ctx -> !ctx.isValid())
                .collect(Collectors.toList());
        validList.removeIf(ctx -> !ctx.isValid());
        staleFailList.addAll(newlyFailed);
        if (validList.isEmpty()) {
            return;
        }
        Set<String> effectiveSoNos = validList.stream()
                .map(SoImportPriceContext::getSoNo)
                .collect(Collectors.toSet());
        List<PayableListDetailDo> effectiveDetails = relatedDetails.stream()
                .filter(d -> effectiveSoNos.contains(d.getSoNo()))
                .collect(Collectors.toList());
        Map<String, PayableListDetailDo> effectiveSoNoToDetail = effectiveDetails.stream()
                .collect(Collectors.toMap(PayableListDetailDo::getSoNo, d -> d, (a, b) -> a));
        Set<String> effectivePayableListIds = effectiveDetails.stream()
                .map(PayableListDetailDo::getPayableListId)
                .collect(Collectors.toSet());
        doImport(validList, effectiveSoNoToDetail, effectivePayableListIds);
    }

    /**
     * 锁内刷新SO快照：SO在锁外读取，等待锁期间可能被并发导入更新，
     * 事务内重读保证 before_price 与实际落库前价格一致。
     */
    private void refreshSoSnapshot(List<SoImportPriceContext> validList) {
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setSoNos(validList.stream().map(SoImportPriceContext::getSoNo).collect(Collectors.toList()));
        Map<String, VehicleCbuSoDo> freshMap = vehicleCbuSoGateway.listVehicleCbuSo(qo).stream()
                .collect(Collectors.toMap(VehicleCbuSoDo::getSoNo, s -> s, (a, b) -> a));
        for (SoImportPriceContext ctx : validList) {
            VehicleCbuSoDo fresh = freshMap.get(ctx.getSoNo());
            if (fresh != null) {
                ctx.setSo(fresh);
            }
        }
    }

    /**
     * 锁内事务执行：批量稀疏更新SO价格与应付清单明细价格，生成变更记录，重算应付清单总额。
     * 稀疏更新只携带本次变更的列，避免全量回写覆盖并发流程对同一行的其它字段修改。
     */
    private void doImport(List<SoImportPriceContext> validList,
                          Map<String, PayableListDetailDo> soNoToDetail,
                          Set<String> payableListIds) {
        // 批量查应付清单抬头（取编号，用于变更记录关联）
        Map<String, PayableListDo> plMap = payableListIds.isEmpty() ? Collections.emptyMap()
                : payableListGateway.listByIds(payableListIds).stream()
                        .collect(Collectors.toMap(PayableListDo::getId, p -> p, (a, b) -> a));

        // SO主表无VIN，从明细表批量回查（SO与明细1:1，未生产车辆VIN为空）
        VehicleCbuSoDetailQo detailQo = new VehicleCbuSoDetailQo();
        detailQo.setSoNos(validList.stream().map(c -> c.getSo().getSoNo()).collect(Collectors.toList()));
        Map<String, String> vinBySoNo = vehicleCbuSoDetailGateway.listVehicleCbuSoDetail(detailQo).stream()
                .filter(d -> d.getSoNo() != null)
                .collect(Collectors.toMap(VehicleCbuSoDetailDo::getSoNo, d -> d.getVin() != null ? d.getVin() : "", (a, b) -> a));

        List<VehicleCbuSoDo> soUpdates = new ArrayList<>();
        List<PriceChangeRecordDo> changeRecords = new ArrayList<>();

        for (SoImportPriceContext ctx : validList) {
            VehicleCbuSoDo so = ctx.getSo();
            BigDecimal beforePrice = so.getVehiclePrice() != null ? so.getVehiclePrice() : BigDecimal.ZERO;
            BigDecimal afterPrice = ctx.getPrice();

            VehicleCbuSoDo soUpdate = new VehicleCbuSoDo();
            soUpdate.setId(so.getId());
            soUpdate.setVehiclePrice(afterPrice);
            soUpdates.add(soUpdate);

            String payableListNo = null;
            PayableListDetailDo detail = soNoToDetail.get(so.getSoNo());
            if (detail != null) {
                PayableListDo pl = plMap.get(detail.getPayableListId());
                if (pl != null) {
                    payableListNo = pl.getPayableListNo();
                }
            }

            // 生成价格变更记录（仅新旧价格不一致时）
            if (beforePrice.compareTo(afterPrice) != 0) {
                PriceChangeRecordDo record = new PriceChangeRecordDo();
                record.setSerialNo("JG" + LocalDateTime.now().format(SERIAL_DATETIME_FORMAT)
                        + NoGenerator.genNoOnly("JG", "yyyyMMddHHmmss", 5));
                record.setVin(vinBySoNo.get(so.getSoNo()));
                record.setSoNo(so.getSoNo());
                record.setBeforePrice(beforePrice);
                record.setAfterPrice(afterPrice);
                record.setPayableListNo(payableListNo);
                record.setStatus(STATUS_ENABLED);
                record.setOwnerUserId(so.getOwnerUserId() != null ? so.getOwnerUserId() : 0L);
                record.setOwnerOrgId(so.getOwnerOrgId() != null ? so.getOwnerOrgId() : 0L);
                changeRecords.add(record);
            }
        }
        vehicleCbuSoGateway.updateBatchById(soUpdates);

        // 批量稀疏更新应付清单明细价格
        Map<String, BigDecimal> priceBySoNo = validList.stream()
                .collect(Collectors.toMap(SoImportPriceContext::getSoNo, SoImportPriceContext::getPrice, (a, b) -> a));
        List<PayableListDetailDo> detailUpdates = new ArrayList<>();
        for (Map.Entry<String, PayableListDetailDo> entry : soNoToDetail.entrySet()) {
            BigDecimal price = priceBySoNo.get(entry.getKey());
            if (price == null) {
                continue;
            }
            PayableListDetailDo detailUpdate = new PayableListDetailDo();
            detailUpdate.setId(entry.getValue().getId());
            detailUpdate.setPrice(price);
            detailUpdates.add(detailUpdate);
        }
        if (!detailUpdates.isEmpty()) {
            payableListDetailGateway.updateBatchById(detailUpdates);
        }

        // 批量插入变更记录
        if (!changeRecords.isEmpty()) {
            priceChangeRecordGateway.addBatchPriceChangeRecord(changeRecords);
        }

        // 语句内聚合重算应付清单总额并递增版本号：单条UPDATE原子完成，
        // 消除"读取-计算-回写"间隙被加车/移车等并发写方覆盖导致的总额丢失更新与版本号回退
        if (!payableListIds.isEmpty()) {
            payableListGateway.recalcTotalAmountAndBumpVersion(payableListIds);
        }
    }

    private List<SoImportPriceDto> buildFailData(List<SoImportPriceContext> failList) {
        return failList.stream().map(ctx -> {
            SoImportPriceDto dto = new SoImportPriceDto();
            dto.setSoNo(ctx.getSoNo());
            dto.setPrice(ctx.getPrice());
            dto.setFailReason(ctx.getFailReason());
            return dto;
        }).collect(Collectors.toList());
    }

    @Data
    public static class SoImportPriceContext {
        private String soNo;
        private BigDecimal price;
        private VehicleCbuSoDo so;
        private boolean valid = true;
        private String failReason;

        public SoImportPriceContext checkSoExists(Map<String, VehicleCbuSoDo> soNoMap) {
            if (!valid) return this;
            VehicleCbuSoDo matched = soNoMap.get(this.soNo);
            if (matched == null) {
                this.valid = false;
                this.failReason = "SO订单不存在或状态为已收款、已作废，请确认";
            } else {
                this.so = matched;
            }
            return this;
        }

        public SoImportPriceContext checkOrderStatus() {
            if (!valid) return this;
            Integer status = so.getOrderStatus();
            if (VehicleCbuSoStatusEnum.RECEIVED.getCode().equals(status)
                    || VehicleCbuSoStatusEnum.CANCELLED.getCode().equals(status)) {
                this.valid = false;
                this.failReason = "SO订单不存在或状态为已收款、已作废，请确认";
            }
            return this;
        }

        public SoImportPriceContext checkPrice() {
            if (!valid) return this;
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                this.valid = false;
                this.failReason = "车辆价格必须大于0";
                return this;
            }
            if (price.scale() > 2) {
                this.valid = false;
                this.failReason = "车辆价格最多两位小数";
                return this;
            }
            if (price.compareTo(MAX_PRICE) > 0) {
                this.valid = false;
                this.failReason = "车辆价格超出允许的最大值";
            }
            return this;
        }
    }
}
