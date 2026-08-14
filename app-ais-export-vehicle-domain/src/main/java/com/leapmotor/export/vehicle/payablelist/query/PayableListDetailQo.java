package com.leapmotor.export.vehicle.payablelist.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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