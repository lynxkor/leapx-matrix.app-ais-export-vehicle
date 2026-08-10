package com.leapmotor.export.vehicle.infrastructure.payablelist.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
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
@TableName(value = "export_vehicle_payable_list", autoResultMap = true)
public class PayableList extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单编号，规则：YF+年月+5位流水号
     */
    @TableField("payable_list_no")
    private String payableListNo;

    /**
     * 单据状态（数字编码）：10=草稿/20=已提交待确认/30=已确认/40=驳回/50=部分收款/60=已收款
     */
    @TableField("bill_status")
    private String billStatus;

    /**
     * 客户编码（来源SO）
     */
    @TableField("customer_code")
    private String customerCode;

    /**
     * 客户名称（来源SO）
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 车辆总数量
     */
    @TableField("total_vehicles")
    private Integer totalVehicles;

    /**
     * 金额合计
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 生成月份 yyyyMM，仅作批次标记字段，记录定时任务执行时间的上一个月份，不参与查询过滤
     */
    @TableField("generate_month")
    private String generateMonth;

    /**
     * 乐观锁版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 提交时间
     */
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
     * 确认时间
     */
    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    /**
     * 确认说明
     */
    @TableField("confirm_remark")
    private String confirmRemark;

    /**
     * 同步红旗时间
     */
    @TableField("sync_hq_time")
    private LocalDateTime syncHqTime;

    /**
     * 同步红旗结果：SUCCESS/FAIL
     */
    @TableField("sync_hq_result")
    private String syncHqResult;

    /**
     * 同步红旗接口返回报文
     */
    @TableField("sync_hq_message")
    private String syncHqMessage;

    /**
     * 同步SAP时间
     */
    @TableField("sync_sap_time")
    private LocalDateTime syncSapTime;

    /**
     * 同步SAP结果：SUCCESS/FAIL
     */
    @TableField("sync_sap_result")
    private String syncSapResult;

    /**
     * 同步SAP接口返回报文
     */
    @TableField("sync_sap_message")
    private String syncSapMessage;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    @TableField("owner_org_id")
    private Long ownerOrgId;
}
