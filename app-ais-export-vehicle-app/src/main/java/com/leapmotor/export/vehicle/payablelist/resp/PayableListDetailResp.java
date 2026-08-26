package com.leapmotor.export.vehicle.payablelist.resp;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.platform.ais.common.web.serializer.DictEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应付清单明细响应对象
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListDetailResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联应付清单ID")
    private String payableListId;

    @Schema(description = "应付清单编号")
    private String payableListNo;

    @Schema(description = "明细编号")
    private String detailNo;

    @Schema(description = "生成月份")
    private String generateMonth;

    @Schema(description = "单据状态")
    @DictEnumType(prefix = "PayableListStatusEnum")
    private String billStatus;

    @Schema(description = "VIN号")
    private String vin;

    @Schema(description = "SO订单号")
    private String soNo;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "整车物料编码")
    private String materialCode;

    @Schema(description = "物料描述")
    private String materialDesc;

    @Schema(description = "车辆价格")
    private BigDecimal price;

    @Schema(description = "质检通过时间")
    private LocalDateTime qcPassTime;

    @Schema(description = "是否收款")
    @DictEnumType(prefix = "IsReceivedEnum")
    private Integer isReceived;

    @Schema(description = "收款时间")
    private LocalDateTime receiveTime;

    @Schema(description = "PO创建时间")
    private LocalDateTime poCreateTime;

    @Schema(description = "销售市场")
    private String saleMarket;

    @Schema(description = "年款")
    private String modelYear;

    @Schema(description = "选装包")
    private String optionPackage;

    @Schema(description = "提交日期")
    private LocalDateTime submitDate;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;
}