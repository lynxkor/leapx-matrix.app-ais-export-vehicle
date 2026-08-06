package com.leapmotor.export.vehicle.payablelist.gateway;

import com.leapmotor.export.vehicle.payablelist.dto.LgBindInfoDto;
import com.leapmotor.export.vehicle.payablelist.dto.LgReleaseReceivableReq;

import java.util.List;

/**
 * 保函领域网关接口（应付清单模块调用）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
public interface LetterGuaranteeGateway {

    /**
     * 按soNo查询SO关联的、转应收类型的保函信息
     *
     * @param soNo SO订单号
     * @return 保函绑定信息列表
     */
    List<LgBindInfoDto> queryBySoNo(String soNo);

    /**
     * 释放应收：SAP清账场景调用
     *
     * @param req 释放应收请求
     * @return 是否成功
     */
    boolean releaseReceivable(LgReleaseReceivableReq req);
}
