package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListUpdateReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应付清单基础信息更新执行器
 *
 * @author 赵吴涯
 * @since 2026-08-12
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListUpdateExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;

    @Transactional(rollbackFor = Exception.class)
    public String execute(PayableListUpdateReq req) {
        PayableListDo db = payableListGateway.getPayableListById(req.getId());
        if (db == null) {
            throw new BizException("应付清单不存在");
        }
        // 状态前置校验：仅草稿/驳回状态可编辑
        PayableListStatusChecker.checkEditable(db);
        // 乐观锁校验
        PayableListStatusChecker.checkVersion(db, req.getVersion());

        // 更新可编辑字段（版本号由后端统一 +1，不回写请求值）
        db.setCustomerCode(req.getCustomerCode());
        db.setCustomerName(req.getCustomerName());
        db.setExternalCustomerCode(req.getExternalCustomerCode());
        db.setGenerateMonth(req.getGenerateMonth());
        db.setTotalVehicles(req.getTotalVehicles());
        db.setTotalAmount(req.getTotalAmount());
        db.setSubmitTime(req.getSubmitTime());
        db.setConfirmTime(req.getConfirmTime());
        db.setConfirmRemark(req.getConfirmRemark());
        db.setVersion(db.getVersion() + 1);
        payableListGateway.updatePayableList(db);

        // 同步明细表冗余字段（客户信息、生成月份、提交日期、确认时间）
        PayableListDetailDo redundantFields = new PayableListDetailDo();
        redundantFields.setCustomerCode(req.getCustomerCode());
        redundantFields.setCustomerName(req.getCustomerName());
        redundantFields.setGenerateMonth(req.getGenerateMonth());
        redundantFields.setSubmitDate(req.getSubmitTime());
        redundantFields.setConfirmTime(req.getConfirmTime());
        payableListDetailGateway.updateRedundantFieldsByPayableListId(db.getId(), redundantFields);

        log.info("应付清单基础信息更新成功，id={}, no={}, newVersion={}", db.getId(), db.getPayableListNo(), db.getVersion());
        return db.getId();
    }
}
