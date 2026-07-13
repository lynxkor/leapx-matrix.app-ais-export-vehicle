package com.leapmotor.export.vehicle.so.resp;

import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * SO接口同步记录表值对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoInterfaceSyncResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态(1:启用,0=停用)")
    private Integer status;

    @Schema(description = "关联SO主表ID")
    private String soId;

    @Schema(description = "接口类型：1下发排产同步APS、2作废同步APS、3生产状态同步客户、4订单同步SAP")
    private Integer interfaceType;

    @Schema(description = "接口描述")
    private String interfaceDesc;

    @Schema(description = "调用时间")
    private LocalDateTime callTime;

    @Schema(description = "请求报文")
    private String requestInfo;

    @Schema(description = "返回结果：0失败、1成功")
    private Integer resultStatus;

    @Schema(description = "返回报文")
    private String responseInfo;

    @Schema(description = "备注")
    private String remark;


}
