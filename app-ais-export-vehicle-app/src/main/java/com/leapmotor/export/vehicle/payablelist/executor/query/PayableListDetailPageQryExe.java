package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.convertor.PayableListDetailDTOStructMapper;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListDetailResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 应付清单明细独立分页查询执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Component
@AllArgsConstructor
public class PayableListDetailPageQryExe {

    private PayableListDetailGateway payableListDetailGateway;

    public PageResult<PayableListDetailResp> execute(PayableListDetailPageParam param) {
        PayableListDetailQo qo = PayableListDetailDTOStructMapper.INSTANCE.detailPageParamToQo(param);
        PageResult<PayableListDetailDo> page = payableListDetailGateway.pagePayableListDetail(qo);
        return PayableListDetailDTOStructMapper.INSTANCE.pageDoToResp(page);
    }
}
