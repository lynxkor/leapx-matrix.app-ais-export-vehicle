package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListDetailResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;

/**
 * 应付清单明细服务接口
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public interface PayableListDetailService {

    /**
     * 分页查询应付清单明细
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<PayableListDetailResp> pagePayableListDetail(PayableListDetailPageParam param);
}
