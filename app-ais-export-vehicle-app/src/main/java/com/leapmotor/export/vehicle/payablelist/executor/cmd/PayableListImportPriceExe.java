package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListImportPriceDto;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListImportPriceReq;
import com.leapmotor.export.vehicle.payablelist.resp.ExcelBaseResult;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 导入最新价格执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListImportPriceExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    /**
     * 导入最新价格
     *
     * @param req 导入请求
     * @return 导入结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ExcelBaseResult execute(PayableListImportPriceReq req) {
        ExcelBaseResult result = new ExcelBaseResult();

        // 1. 基础校验
        if (req == null || StrUtil.isBlank(req.getExcel())) {
            result.setSuccessNum(0);
            result.setFailNum(0);
            return result;
        }

        // 2. 解析Excel数据
        List<PayableListImportPriceDto> importList = JSONObject.parseArray(req.getExcel(), PayableListImportPriceDto.class);
        if (CollectionUtils.isEmpty(importList)) {
            result.setSuccessNum(0);
            result.setFailNum(0);
            return result;
        }

        // 3. 查询应付清单并校验
        PayableListDo db = payableListGateway.getPayableListById(req.getPayableListId());
        if (db == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }
        // 乐观锁校验
        if (db.getVersion() == null || !db.getVersion().equals(req.getVersion())) {
            throw new BizException(ResultEnum.BAD_REQUEST, "数据已被他人修改，请刷新后重试");
        }
        // 状态前置校验
        PayableListStatusChecker.checkEditable(db);

        // 4. 查询当前清单明细，构建VIN映射
        List<PayableListDetailDo> details = payableListDetailGateway.listByPayableListId(req.getPayableListId());
        Map<String, PayableListDetailDo> vinMap = details.stream()
                .collect(Collectors.toMap(PayableListDetailDo::getVin, Function.identity(), (a, b) -> a));

        // 5. 校验并分组
        List<PayableListImportPriceDto> failList = new ArrayList<>();
        List<PayableListImportPriceDto> validList = new ArrayList<>();

        for (PayableListImportPriceDto item : importList) {
            StringBuilder failMsg = new StringBuilder();

            if (Objects.isNull(item)) {
                failMsg.append("导入行数据为空，跳过本条；");
                PayableListImportPriceDto emptyItem = new PayableListImportPriceDto();
                emptyItem.setFailReason(failMsg.toString());
                failList.add(emptyItem);
                continue;
            }

            // VIN校验
            if (StrUtil.isBlank(item.getVin())) {
                failMsg.append("VIN不能为空；");
            }

            // 价格校验
            if (item.getPrice() == null) {
                failMsg.append("价格不能为空；");
            } else if (item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                failMsg.append("价格必须大于0；");
            }

            // VIN是否在清单明细中
            if (StrUtil.isNotBlank(item.getVin()) && !vinMap.containsKey(item.getVin())) {
                failMsg.append("VIN不在当前清单明细中；");
            }

            item.setFailReason(failMsg.toString());
            if (StrUtil.isNotBlank(item.getFailReason())) {
                failList.add(item);
            } else {
                validList.add(item);
            }
        }

        // 6. 任一失败则全量不入库
        if (!CollectionUtils.isEmpty(failList)) {
            result.setSuccessNum(0);
            result.setFailNum(failList.size());
            result.setData(failList);
            return result;
        }

        // 7. 更新价格
        for (PayableListImportPriceDto item : validList) {
            PayableListDetailDo detail = vinMap.get(item.getVin());
            // 更新明细价格
            detail.setPrice(item.getPrice());
            payableListDetailGateway.updatePayableListDetail(detail);
            // 同步更新 SO 车辆售价
            VehicleCbuSoDo soDo = vehicleCbuSoGateway.getVehicleCbuSoById(detail.getSoId());
            if (soDo != null) {
                soDo.setVehiclePrice(item.getPrice());
                vehicleCbuSoGateway.updateVehicleCbuSo(soDo);
            } else {
                log.warn("导入价格：SO主表不存在，soId={}", detail.getSoId());
            }
        }

        // 8. 重算汇总
        List<PayableListDetailDo> updatedDetails = payableListDetailGateway.listByPayableListId(req.getPayableListId());
        BigDecimal totalAmount = updatedDetails.stream()
                .map(PayableListDetailDo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        db.setTotalVehicles(updatedDetails.size());
        db.setTotalAmount(totalAmount);
        db.setVersion(db.getVersion() + 1);
        payableListGateway.updatePayableList(db);

        log.info("导入最新价格完成，payableListId={}, total={}, fail={}", req.getPayableListId(), validList.size(), failList.size());

        // 9. 成功返回
        result.setSuccessNum(validList.size());
        result.setFailNum(0);
        return result;
    }
}
