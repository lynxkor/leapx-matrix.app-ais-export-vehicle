package com.leapmotor.export.vehicle.payablelist.service.impl;

import com.leapmotor.export.vehicle.payablelist.convertor.PayableListDetailDTOStructMapper;
import com.leapmotor.export.vehicle.payablelist.executor.query.PayableListDetailQryExe;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListDetailResp;
import com.leapmotor.export.vehicle.payablelist.service.PayableListDetailService;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 应付清单明细服务实现
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@AllArgsConstructor
@Service
public class PayableListDetailServiceImpl implements PayableListDetailService {

    private PayableListDetailQryExe payableListDetailQryExe;

    @Override
    public PageResult<PayableListDetailResp> pagePayableListDetail(PayableListDetailPageParam param) {
        PageResult<PayableListDetailDo> page = payableListDetailQryExe.pagePayableListDetail(param);
        return PayableListDetailDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }
}
