package com.leapmotor.export.vehicle.payablelist.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应付清单明细查询对象
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListDetailQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联应付清单ID
     */
    private String payableListId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 应付清单编号
     */
    private List<String> payableListNoList;

    /**
     * 单据状态
     */
    private String billStatus;

    /**
     * 生成月份 yyyyMM
     */
    private String generateMonth;

    /**
     * VIN号（多选）
     */
    private List<String> vinList;

    /**
     * 应付清单ID列表（由应付清单编号转换得到，内部使用）
     */
    private List<String> payableListIds;

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

    /**
     * SO订单号
     */
    private String soNo;

    /**
     * PO单号
     */
    private String poNo;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 是否收款：0=否/1=是
     */
    private Integer isReceived;

    /**
     * 质检通过时间开始
     */
    private LocalDateTime qcPassTimeStart;

    /**
     * 质检通过时间结束
     */
    private LocalDateTime qcPassTimeEnd;

    /**
     * VIN号（模糊查询）
     */
    private String vinLike;

    /**
     * SO订单号（模糊查询）
     */
    private String soNoLike;
}