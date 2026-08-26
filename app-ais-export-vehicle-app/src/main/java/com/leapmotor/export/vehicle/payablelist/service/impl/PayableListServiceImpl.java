package com.leapmotor.export.vehicle.payablelist.service.impl;

import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.export.vehicle.payablelist.convertor.PayableListDTOStructMapper;
import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListAddVehiclesExe;
import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListBatchSubmitExe;
import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListDeleteVehicleExe;
import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListImportPriceExe;
import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListUpdateExe;
import com.leapmotor.export.vehicle.payablelist.executor.query.PayableListAvailableVehicleQryExe;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListAddVehiclesReq;
import com.leapmotor.export.vehicle.payablelist.req.PayableListBatchSubmitReq;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDeleteVehicleReq;
import com.leapmotor.export.vehicle.payablelist.req.PayableListPageParam;
import com.leapmotor.export.vehicle.payablelist.req.PayableListQueryAvailableVehiclesReq;
import com.leapmotor.export.vehicle.payablelist.req.PayableListSubmitReq;
import com.leapmotor.export.vehicle.payablelist.req.PayableListUpdateReq;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.export.vehicle.payablelist.service.PayableListService;
import com.leapmotor.paltform.ais.common.model.excel.in.ExcelBaseResult;
import com.leapmotor.paltform.ais.common.model.excel.in.ImportExcelDTO;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private EventHandlerHelper eventHandlerHelper;
    private PayableListImportPriceExe payableListImportPriceExe;
    private PayableListAddVehiclesExe payableListAddVehiclesExe;
    private PayableListUpdateExe payableListUpdateExe;
    private PayableListDeleteVehicleExe payableListDeleteVehicleExe;
    private PayableListBatchSubmitExe payableListBatchSubmitExe;
    private PayableListAvailableVehicleQryExe payableListAvailableVehicleQryExe;

    @Override
    public PageResult<PayableListResp> pagePayableList(PayableListPageParam param) {
        PayableListQo qo = new PayableListQo();
        qo.setPageNum(param.getPageNum());
        qo.setPageSize(param.getPageSize());
        qo.setPayableListNo(param.getPayableListNo());
        qo.setPayableListNoLike(param.getPayableListNoLike());
        qo.setPayableListNos(param.getPayableListNos());
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

        // 车辆明细由明细分页接口单独查询，详情接口不再组装返回
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

        // 发布红旗同步事件（异步，事务提交后执行）
        // 第4参必须强转 Object 以命中带 afterCommit 保护的 data 重载（null 字面量会命中 String relatedNo 重载，事务回滚后事件仍会消费）
        eventHandlerHelper.publishAsync(
                ThirdTargetPlatformEnum.HONGQI,
                HongqiEventTypeEnum.PAYABLE_LIST_SYNC_HQ_SUBMIT,
                payableList.getPayableListNo(),
                (Object) null
        );

        return payableList.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> batchSubmitPayableList(PayableListBatchSubmitReq req) {
        return payableListBatchSubmitExe.execute(req);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelBaseResult importPrice(ImportExcelDTO dto) {
        return payableListImportPriceExe.execute(dto);
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

    @Override
    public PageResult<SoOrderForPayableDto> queryAvailableVehicles(PayableListQueryAvailableVehiclesReq req) {
        return payableListAvailableVehicleQryExe.execute(req);
    }

    @Override
    public String addVehicles(PayableListAddVehiclesReq req) {
        return payableListAddVehiclesExe.execute(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteVehicles(PayableListDeleteVehicleReq req) {
        return payableListDeleteVehicleExe.execute(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updatePayableList(PayableListUpdateReq req) {
        return payableListUpdateExe.execute(req);
    }
}
