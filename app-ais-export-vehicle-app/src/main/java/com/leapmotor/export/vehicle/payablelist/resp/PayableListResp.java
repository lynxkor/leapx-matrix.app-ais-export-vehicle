package com.leapmotor.export.vehicle.payablelist.resp;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.platform.ais.common.web.serializer.DictEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 应付清单响应对象
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单编号")
    private String payableListNo;

    @Schema(description = "单据状态")
    @DictEnumType(prefix = "PayableListStatusEnum")
    private String billStatus;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "车辆总数量")
    private Integer totalVehicles;

    @Schema(description = "金额合计")
    private BigDecimal totalAmount;

    @Schema(description = "生成月份")
    private String generateMonth;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "确认说明")
    private String confirmRemark;

    @Schema(description = "同步红旗时间")
    private LocalDateTime syncHqTime;

    @Schema(description = "同步红旗结果")
    private String syncHqResult;

    @Schema(description = "同步红旗接口返回报文")
    private String syncHqMessage;

    @Schema(description = "同步SAP时间")
    private LocalDateTime syncSapTime;

    @Schema(description = "同步SAP结果")
    private String syncSapResult;

    @Schema(description = "同步SAP接口返回报文")
    private String syncSapMessage;

    @Schema(description = "明细列表")
    private List<PayableListDetailResp> detailList = new ArrayList<>();
}