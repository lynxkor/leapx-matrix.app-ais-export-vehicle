package com.leapmotor.export.vehicle.payablelist.model;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应付清单明细领域对象
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListDetailDo extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联应付清单ID
     */
    private String payableListId;

    /**
     * 明细编号，规则：YX+年月日+6位流水号
     */
    private String detailNo;

    /**
     * 生成月份 yyyyMM
     */
    private String generateMonth;

    /**
     * 单据状态：10=草稿/20=已提交待确认/30=已确认/40=驳回/50=部分收款/60=已收款
     */
    private String billStatus;

    /**
     * 关联SO订单ID
     */
    private String soId;

    /**
     * SO订单号
     */
    private String soNo;

    /**
     * PO单号
     */
    private String poNo;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 整车物料编码
     */
    private String materialCode;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 车辆价格
     */
    private BigDecimal price;

    /**
     * 质检通过时间
     */
    private LocalDateTime qcPassTime;

    /**
     * 是否收款：0=否/1=是
     */
    private Integer isReceived;

    /**
     * 收款时间
     */
    private LocalDateTime receiveTime;

    /**
     * PO创建时间
     */
    private LocalDateTime poCreateTime;

    /**
     * 销售市场
     */
    private String saleMarket;

    /**
     * 年款
     */
    private String modelYear;

    /**
     * 选装包
     */
    private String optionPackage;

    /**
     * 提交日期
     */
    private LocalDateTime submitDate;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    private Long ownerUserId;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    private Long ownerOrgId;
}