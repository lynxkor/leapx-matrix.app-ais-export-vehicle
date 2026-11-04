package com.leapmotor.export.vehicle.pricechangerecord.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PriceChangeRecordResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流水号")
    private String serialNo;

    @Schema(description = "VIN号")
    private String vin;

    @Schema(description = "SO订单号")
    private String soNo;

    @Schema(description = "变更前价格")
    private BigDecimal beforePrice;

    @Schema(description = "变更后价格")
    private BigDecimal afterPrice;

    @Schema(description = "关联应付清单编号")
    private String payableListNo;

    @Schema(description = "操作人名称")
    private String creatorName;

    @Schema(description = "操作时间")
    private LocalDateTime createTime;
}
