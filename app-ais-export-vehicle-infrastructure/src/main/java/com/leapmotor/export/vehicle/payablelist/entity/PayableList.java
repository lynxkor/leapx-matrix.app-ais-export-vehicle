package com.leapmotor.export.vehicle.payablelist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应付清单抬头表
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("export_vehicle_payable_list")
public class PayableList extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单编号，规则：YF+年月+5位流水号
     */
    private String payableListNo;

    /**
     * 单据状态（数字编码）：10=草稿/20=已提交待确认/30=已确认/40=驳回/50=部分收款/60=已收款
     */
    private String billStatus;

    /**
     * 客户编码（来源SO）
     */
    private String customerCode;

    /**
     * 客户名称（来源SO）
     */
    private String customerName;

    /**
     * 车辆总数量
     */
    private Integer totalVehicles;

    /**
     * 金额合计
     */
    private BigDecimal totalAmount;

    /**
     * 生成月份 yyyyMM，仅作批次标记字段，记录定时任务执行时间的上一个月份，不参与查询过滤
     */
    private String generateMonth;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认说明
     */
    private String confirmRemark;

    /**
     * 同步红旗时间
     */
    private LocalDateTime syncHqTime;

    /**
     * 同步红旗结果：SUCCESS/FAIL
     */
    private String syncHqResult;

    /**
     * 同步红旗接口返回报文
     */
    private String syncHqMessage;

    /**
     * 同步SAP时间
     */
    private LocalDateTime syncSapTime;

    /**
     * 同步SAP结果：SUCCESS/FAIL
     */
    private String syncSapResult;

    /**
     * 同步SAP接口返回报文
     */
    private String syncSapMessage;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    private Long ownerUserId;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    private Long ownerOrgId;
}