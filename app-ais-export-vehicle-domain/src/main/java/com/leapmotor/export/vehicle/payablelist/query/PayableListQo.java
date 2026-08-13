package com.leapmotor.export.vehicle.payablelist.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应付清单抬头查询对象
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单编号
     */
    private String payableListNo;

    /**
     * 单据状态（支持多个状态查询）
     */
    private List<String> billStatusList;

    /**
     * 单据状态
     */
    private String billStatus;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称（模糊查询）
     */
    private String customerNameLike;

    /**
     * 生成月份
     */
    private String generateMonth;

    /**
     * VIN号（明细中查询）
     */
    private String vin;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 提交时间开始
     */
    private LocalDateTime submitTimeStart;

    /**
     * 提交时间结束
     */
    private LocalDateTime submitTimeEnd;

    /**
     * 确认时间开始
     */
    private LocalDateTime confirmTimeStart;

    /**
     * 确认时间结束
     */
    private LocalDateTime confirmTimeEnd;
}