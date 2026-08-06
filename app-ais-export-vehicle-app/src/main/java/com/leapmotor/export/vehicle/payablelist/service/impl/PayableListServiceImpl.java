package com.leapmotor.export.vehicle.payablelist.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.leapmotor.export.vehicle.payablelist.convertor.PayableListDTOStructMapper;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListImportPriceDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListImportPriceExe;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import com.leapmotor.export.vehicle.payablelist.req.*;
import com.leapmotor.export.vehicle.payablelist.resp.ExcelBaseResult;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListAvailableVehicleResp;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.export.vehicle.payablelist.service.PayableListService;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 应付清单服务实现
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@AllArgsConstructor
@Service
public class PayableListServiceImpl implements PayableListService {

    private PayableListGateway payableListGateway;
    private PayableListImportPriceExe payableListImportPriceExe;

    @Override
    public PageResult<PayableListResp> pagePayableList(PayableListPageParam param) {
        PayableListQo qo = new PayableListQo();
        qo.setPageNum(param.getPageNum());
        qo.setPageSize(param.getPageSize());
        qo.setPayableListNo(param.getPayableListNo());
        qo.setBillStatusList(param.getBillStatusList());
        qo.setCustomerCode(param.getCustomerCode());
        qo.setCustomerNameLike(param.getCustomerName());
        qo.setGenerateMonth(param.getGenerateMonth());
        qo.setVin(param.getVin());
        qo.setCreateTimeStart(param.getCreateTimeStart());
        qo.setCreateTimeEnd(param.getCreateTimeEnd());
        qo.setSubmitTimeStart(param.getSubmitTimeStart());
        qo.setSubmitTimeEnd(param.getSubmitTimeEnd());
        qo.setConfirmTimeStart(param.getConfirmTimeStart());
        qo.setConfirmTimeEnd(param.getConfirmTimeEnd());

        PageResult<PayableListDo> result = payableListGateway.pagePayableList(qo);
        return PayableListDTOStructMapper.INSTANCE.domainsToRespPage(result);
    }

    @Override
    public PayableListResp getPayableListById(String id) {
        PayableListDo payableListDo = payableListGateway.getPayableListById(id);
        if (payableListDo == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }
        return PayableListDTOStructMapper.INSTANCE.domainToResp(payableListDo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitPayableList(PayableListSubmitReq req) {
        // 查询应付清单
        PayableListDo payableList = payableListGateway.getPayableListById(req.getId());

        // 校验状态
        PayableListStatusChecker.checkCanSubmit(payableList);

        // 校验版本号
        PayableListStatusChecker.checkVersion(payableList, req.getVersion());

        // 更新状态和提交时间
        payableList.setBillStatus(PayableListStatusEnum.SUBMITTED.getCode());
        payableList.setSubmitTime(LocalDateTime.now());
        payableList.setVersion(payableList.getVersion() + 1);

        // 保存
        payableListGateway.updatePayableList(payableList);

        // TODO: 同步红旗系统

        return payableList.getId();
    }

    @Override
    public List<PayableListAvailableVehicleResp> queryAvailableVehicles(String payableListId) {
        // TODO: 调用 SO Gateway 查询可添加车辆
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addVehicles(PayableListAddVehiclesReq req) {
        // TODO: 实现添加车辆逻辑
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String removeVehicles(PayableListRemoveVehiclesReq req) {
        // TODO: 实现删除车辆逻辑
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelBaseResult importPrice(PayableListImportPriceReq req) {
        return payableListImportPriceExe.execute(req);
    }

    @Override
    public boolean deletePayableListByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 批量软删除
        for (String id : ids) {
            payableListGateway.deleteById(id);
        }
        return true;
    }
}
