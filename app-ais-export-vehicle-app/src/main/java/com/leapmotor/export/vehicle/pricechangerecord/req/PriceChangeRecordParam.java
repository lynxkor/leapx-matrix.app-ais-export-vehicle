package com.leapmotor.export.vehicle.pricechangerecord.req;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.platform.ais.common.web.serializer.DictEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PriceChangeRecordParam extends com.leapmotor.paltform.ais.common.model.base.LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "VIN号列表（批量精确查询）")
    @Size(max = 2000, message = "VIN号批量查询不超过2000条")
    private List<String> vins;

    @Schema(description = "SO订单号列表（批量精确查询）")
    @Size(max = 2000, message = "SO订单号批量查询不超过2000条")
    private List<String> soNos;

    @Schema(description = "操作时间范围起")
    private LocalDateTime createTimeStart;

    @Schema(description = "操作时间范围止")
    private LocalDateTime createTimeEnd;
}
