package com.leapmotor.export.vehicle.payablelist.gateway;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.dto.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.payablelist.dto.SoUpdateStatusReq;

import java.util.List;

/**
 * SO订单领域网关接口（应付清单模块调用）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
public interface SoOrderGateway {

    /**
     * 查询所有质检通过且无活动明细（delete_status=0）的SO车辆
     *
     * @return 未绑定的SO车辆列表
     */
    List<SoOrderForPayableDto> queryQcPassedAndUnbound();

    /**
     * 更新SO车辆售价（导入最新价格场景）
     *
     * @param req 更新价格请求
     * @return 是否成功
     */
    boolean updatePrice(SoUpdatePriceReq req);

    /**
     * 更新SO状态为已收款（SAP清账场景）
     *
     * @param req 更新状态请求
     * @return 是否成功
     */
    boolean updateStatusReceived(SoUpdateStatusReq req);
}
