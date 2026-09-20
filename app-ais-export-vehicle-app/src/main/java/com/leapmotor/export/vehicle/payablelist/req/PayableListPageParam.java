package com.leapmotor.export.vehicle.payablelist.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应付清单分页查询参数
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListPageParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单编号")
    private String payableListNo;

    @Schema(description = "应付清单编号（单条模糊查询）")
    private String payableListNoLike;

    @Schema(description = "应付清单编号列表（批量精确查询）")
    private List<String> payableListNos;

    @Schema(description = "单据状态列表")
    private List<String> billStatusList;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称（模糊查询）")
    private String customerName;

    @Schema(description = "生成月份")
    private String generateMonth;

    @Schema(description = "VIN号")
    private String vin;

    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "提交时间开始")
    private LocalDateTime submitTimeStart;

    @Schema(description = "提交时间结束")
    private LocalDateTime submitTimeEnd;

    @Schema(description = "确认时间开始")
    private LocalDateTime confirmTimeStart;

    @Schema(description = "确认时间结束")
    private LocalDateTime confirmTimeEnd;
}