package com.leapmotor.export.vehicle.acl.rpc.aps.req;

import lombok.Data;

import java.io.Serializable;

/**
 * APS 下发排产同步请求，对齐《海外销售订单接口文档》POST /api/abroad/batchOrder。
 *
 * <p>仅 vehicleMaterialName 必填，其余非必填；按 vehCbuSoName 唯一，重复下发走更新。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class ApsIssueReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO 订单 ID
     */
    private String vehPoId;

    /**
     * PO 订单号
     */
    private String vehPoName;

    /**
     * 批售订单 ID（SO 主表 ID）
     */
    private String vehCbuSoId;

    /**
     * 批售订单号（SO 单号，唯一，重复下发走更新）
     */
    private String vehCbuSoName;

    /**
     * 接收订单时间，取 SO.receiveOrderTime（PO 创建时间），yyyy-MM-dd HH:mm:ss
     */
    private String issueOn;

    /**
     * 销售公司编码
     */
    private String salesCompanyCode;

    /**
     * 销售公司描述（从主数据查）
     */
    private String salesCompanyName;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户描述
     */
    private String customerName;

    /**
     * 订单类型，固定 10（CBU）
     */
    private Integer orderType;

    /**
     * 物料编码（必填，须存在于整车特征值）
     */
    private String vehicleMaterialName;

    /**
     * 物料描述（必传）
     */
    private String vehicleMaterialDesc;

    /**
     * 数量，固定 1（SO 一车一单）
     */
    private Integer quantity;

    /**
     * 行号，同一 PO 拆 SO 生成 1、2、3
     */
    private Integer lineNo;

    /**
     * 需求下线日期，取 SO.expectedOfflineDate，yyyy-MM-dd HH:mm:ss
     */
    private String expectProducedOn;

    /**
     * VIN 码年份
     */
    private String vinYear;

    /**
     * 订单分类，固定 100
     */
    private Integer orderCategory;

    /**
     * 订单来源，固定 200
     */
    private Integer vehOrderSource;
}
