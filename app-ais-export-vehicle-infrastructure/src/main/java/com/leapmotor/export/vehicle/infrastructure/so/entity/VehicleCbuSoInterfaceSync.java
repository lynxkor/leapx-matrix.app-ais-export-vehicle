package com.leapmotor.export.vehicle.infrastructure.so.entity;

import java.io.Serializable;

import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;


/**
 * <p>
 * SO接口同步记录表领域对象
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_vehicle_cbu_so_interface_sync", autoResultMap = true)
public class VehicleCbuSoInterfaceSync extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态(1:启用,0=停用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 关联SO主表ID
     */
    @TableField("so_id")
    private String soId;

    /**
     * 接口类型：1下发排产同步APS、2作废同步APS、3生产状态同步客户、4订单同步SAP
     */
    @TableField("interface_type")
    private Integer interfaceType;

    /**
     * 接口描述
     */
    @TableField("interface_desc")
    private String interfaceDesc;

    /**
     * 调用时间
     */
    @TableField("call_time")
    private LocalDateTime callTime;

    /**
     * 请求报文
     */
    @TableField("request_info")
    private String requestInfo;

    /**
     * 返回结果：0失败、1成功
     */
    @TableField("result_status")
    private Integer resultStatus;

    /**
     * 返回报文
     */
    @TableField("response_info")
    private String responseInfo;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
