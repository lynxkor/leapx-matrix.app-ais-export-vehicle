package com.leapmotor.export.vehicle.demo.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 整车订单示例 查询参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "整车订单示例 查询参数")
public class DemoOrderParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单类型（CBU/SKD/CKD）")
    private String orderType;

    @Schema(description = "备注")
    private String remark;
}
