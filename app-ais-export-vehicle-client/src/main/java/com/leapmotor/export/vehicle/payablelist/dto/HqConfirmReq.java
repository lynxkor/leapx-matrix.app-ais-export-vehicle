package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 红旗确认结果请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class HqConfirmReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单编号
     */
    private String payableListNo;

    /**
     * 确认结果：30=确认/40=驳回
     */
    private String confirmResult;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认说明
     */
    private String confirmRemark;
}
