package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.convertor.PayableListDTOStructMapper;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 应付清单分页查询执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Component
@AllArgsConstructor
public class PayableListPageQryExe {

    private PayableListGateway payableListGateway;

    public PageResult<PayableListResp> execute(PayableListPageParam param) {
        PayableListQo qo = PayableListDTOStructMapper.INSTANCE.pageParamToQo(param);
        PageResult<PayableListDo> page = payableListGateway.pagePayableList(qo);
        return PayableListDTOStructMapper.INSTANCE.pageDoToResp(page);
    }
}
