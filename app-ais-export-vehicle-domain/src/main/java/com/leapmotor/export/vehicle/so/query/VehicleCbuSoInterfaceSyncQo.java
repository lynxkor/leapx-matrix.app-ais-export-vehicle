package com.leapmotor.export.vehicle.so.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * SO接口同步记录表查询对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoInterfaceSyncQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联SO主表ID
     */
    private String soId;

    /**
     * 接口类型：1下发排产同步APS、2作废同步APS、3生产状态同步客户、4订单同步SAP
     */
    private Integer interfaceType;

    /**
     * 接口描述
     */
    private String interfaceDesc;

    /**
     * 调用时间
     */
    private LocalDateTime callTime;

    /**
     * 请求报文
     */
    private String requestInfo;

    /**
     * 返回结果：0失败、1成功
     */
    private Integer resultStatus;

    /**
     * 返回报文
     */
    private String responseInfo;

    /**
     * 备注
     */
    private String remark;


}
