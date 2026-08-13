package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.convertor.PayableListDetailDTOStructMapper;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 应付清单明细查询操作执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@AllArgsConstructor
@Component
public class PayableListDetailQryExe {

    private PayableListDetailGateway payableListDetailGateway;

    /**
     * 分页查询应付清单明细
     *
     * @param payableListDetailPageParam 分页查询参数
     * @return 分页结果
     */
    public PageResult<PayableListDetailDo> pagePayableListDetail(PayableListDetailPageParam payableListDetailPageParam) {
        PayableListDetailQo payableListDetailQo = PayableListDetailDTOStructMapper.INSTANCE.paramToQuery(payableListDetailPageParam);
        return payableListDetailGateway.pagePayableListDetail(payableListDetailQo);
    }
}
