-- ============================================================
-- PO订单管理 建表脚本
-- 表：export_cbu_vehicle_po / export_cbu_vehicle_po_detail / export_cbu_vehicle_po_guarantee
-- 来源：技术评审 00-总览 §3.1.1 / §3.2.1 / §3.3.1
-- 主键：id VARCHAR(64)，应用层 UUID（LpBaseEntity @TableId(type=ASSIGN_UUID) 自动生成，非自增、不用手填）
-- 基础字段：平台模板 §7.0（11 个，三表统一）
-- ============================================================

-- 1. PO订单主表
CREATE TABLE `export_cbu_vehicle_po` (
    `id`                                  VARCHAR(64)      NOT NULL                         COMMENT '主键ID(UUID 或雪花ID 字符串形式)',
    `po_no`                           VARCHAR(30)      NOT NULL                         COMMENT 'PO单号(取自红旗CBU接口),业务唯一键',
    `order_status`                        VARCHAR(10)      NOT NULL                         COMMENT '订单状态,枚举见评审§2.1(草稿/销售已提交/销售已驳回/供需审核通过/供需审核驳回/产销平衡通过/产销平衡驳回)',
    `sales_company`                       VARCHAR(30)      DEFAULT NULL                     COMMENT '销售公司,下拉选,提交时必填',
    `customer_code`                       VARCHAR(30)      DEFAULT NULL                     COMMENT '客户编码,按 external_customer_code 匹配客户主数据,匹配不到则空;提交时必填',
    `customer_name`                       VARCHAR(30)      DEFAULT NULL                     COMMENT '客户名称,按客户编码带出',
    `external_customer_code`              VARCHAR(30)      NOT NULL                         COMMENT '外部客户编码,取自红旗CBU接口',
    `total_amount`                        DECIMAL(20,2)    DEFAULT NULL                     COMMENT '总金额,= SUM(明细 total_amount)',
    `submit_time`                         DATETIME         DEFAULT NULL                     COMMENT '提交时间,销售提交时记录',
    `reject_time`                         DATETIME         DEFAULT NULL                     COMMENT '驳回时间,各类驳回时记录',
    `reject_user`                         VARCHAR(30)      DEFAULT NULL                     COMMENT '驳回人,界面不显示',
    `supply_demand_approve_time`          DATETIME         DEFAULT NULL                     COMMENT '供需审核通过时间',
    `supply_demand_approver`              VARCHAR(30)      DEFAULT NULL                     COMMENT '供需审核人',
    `production_sales_balance_approve_time` DATETIME       DEFAULT NULL                     COMMENT '产销平衡通过时间',
    `production_sales_balance_approver`     VARCHAR(30)    DEFAULT NULL                     COMMENT '产销平衡审核人',
    `reject_reason`                       VARCHAR(100)     DEFAULT NULL                     COMMENT '驳回说明,各类驳回时必填',
    `data_source`                         VARCHAR(10)      NOT NULL DEFAULT '红旗'          COMMENT '数据来源,枚举:销服/红旗',
    `order_type`                          VARCHAR(10)      NOT NULL DEFAULT 'CBU'           COMMENT '订单类型,红旗接口同步',
    `plan_version`                        VARCHAR(30)      NOT NULL                         COMMENT '计划版本号,取自红旗CBU接口',
    `audit_result_sync_time`              DATETIME         DEFAULT NULL                     COMMENT '审结结果同步时间,反馈红旗接口的发送时间',
    `audit_result_sync_result`            VARCHAR(30)      DEFAULT NULL                     COMMENT '审结结果同步结果,= 成功/失败',
    `customer_system_return_info`         VARCHAR(200)     DEFAULT NULL                     COMMENT '客户系统返回信息,红旗接口对方返回',
    -- 基础字段(平台模板 §7.0)
    `creator`                             BIGINT           NOT NULL                         COMMENT '创建者ID',
    `creator_name`                        VARCHAR(100)     NOT NULL                         COMMENT '创建人名称',
    `create_time`                         DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`                            BIGINT           DEFAULT NULL                     COMMENT '修改者ID',
    `modifier_name`                       VARCHAR(100)     DEFAULT NULL                     COMMENT '修改人名称',
    `update_time`                         DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status`                       BIGINT           NOT NULL DEFAULT '0'             COMMENT '删除状态(0=正常,1=已删除),逻辑删除',
    `status`                              SMALLINT UNSIGNED NOT NULL DEFAULT '1'            COMMENT '状态(1:启用,0:停用),平台规范,业务流转用 order_status',
    `status_desc`                         VARCHAR(256)     DEFAULT NULL                     COMMENT '状态变更描述',
    `owner_user_id`                       BIGINT(20) UNSIGNED NOT NULL DEFAULT '0'          COMMENT '归属用户ID',
    `owner_org_id`                        BIGINT(20) UNSIGNED NOT NULL DEFAULT '0'          COMMENT '归属门店ID(组织ID)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_po_no` (`po_no`)
) DEFAULT CHARSET=utf8mb4 COMMENT='PO订单主表';

-- 2. PO订单明细表
CREATE TABLE `export_cbu_vehicle_po_detail` (
    `id`                    VARCHAR(64)     NOT NULL                        COMMENT '主键ID(UUID 或雪花ID 字符串形式)',
    `po_no`             VARCHAR(30)     NOT NULL                        COMMENT 'PO单号,关联主表 export_cbu_vehicle_po.po_no',
    `po_detail_number`      VARCHAR(30)     NOT NULL                        COMMENT 'PO明细编号,自动生成 POD+年月日+时分+5位流水',
    `order_detail_number`   VARCHAR(30)     NOT NULL                        COMMENT '订单明细编号,取自红旗CBU接口',
    `material_code`         VARCHAR(30)     DEFAULT NULL                    COMMENT '物料编码,按 sync_material_code 匹配整编主数据;提交时必填',
    `sync_material_code`    VARCHAR(30)     NOT NULL                        COMMENT '同步物料编码,红旗18位物料编码',
    `material_description`  VARCHAR(255)    DEFAULT NULL                    COMMENT '物料描述,关联整编主数据',
    `sales_market`          VARCHAR(50)     DEFAULT NULL                    COMMENT '销售市场,关联整编主数据',
    `year_model`            VARCHAR(20)     DEFAULT NULL                    COMMENT '年款,关联整编主数据',
    `optional_package`      VARCHAR(100)    DEFAULT NULL                    COMMENT '选装包,关联整编主数据',
    `demand_quantity`       DECIMAL(20,2)   NOT NULL                        COMMENT '需求数量,取自红旗CBU接口',
    `expected_offline_date` DATE            NOT NULL                        COMMENT '期望下线日期,取自红旗CBU接口',
    `vin_year`              VARCHAR(10)     NOT NULL                        COMMENT 'VIN码年份,取自红旗CBU接口',
    `production_remark`     VARCHAR(1000)   DEFAULT NULL                    COMMENT '生产备注,可编辑,非必填,最多1000字',
    `unit_price`            DECIMAL(20,2)   DEFAULT NULL                    COMMENT '单价,按 物料编码+创建时间+订单类型 匹配价格主数据(取最后审核通过价);提交时必填',
    `total_amount`          DECIMAL(20,2)   DEFAULT NULL                    COMMENT '金额合计,= 单价 × 需求数量',
    -- 基础字段(平台模板 §7.0)
    `creator`               BIGINT          NOT NULL                        COMMENT '创建者ID',
    `creator_name`          VARCHAR(100)    NOT NULL                        COMMENT '创建人名称',
    `create_time`           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`              BIGINT          DEFAULT NULL                    COMMENT '修改者ID',
    `modifier_name`         VARCHAR(100)    DEFAULT NULL                    COMMENT '修改人名称',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status`         BIGINT          NOT NULL DEFAULT '0'            COMMENT '删除状态(0=正常,1=已删除),逻辑删除',
    `status`                SMALLINT UNSIGNED NOT NULL DEFAULT '1'          COMMENT '状态(1:启用,0:停用),平台规范',
    `status_desc`           VARCHAR(256)    DEFAULT NULL                    COMMENT '状态变更描述',
    `owner_user_id`         BIGINT(20) UNSIGNED NOT NULL DEFAULT '0'       COMMENT '归属用户ID',
    `owner_org_id`          BIGINT(20) UNSIGNED NOT NULL DEFAULT '0'       COMMENT '归属门店ID(组织ID)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_po_detail` (`po_no`, `po_detail_number`)
) DEFAULT CHARSET=utf8mb4 COMMENT='PO订单明细表';

-- 3. PO-保函关联表（一对多，一个 PO 可冻结多个保函）
CREATE TABLE `export_cbu_vehicle_po_guarantee` (
    `id`                            VARCHAR(64)   NOT NULL                 COMMENT '主键ID(UUID 或雪花ID 字符串形式)',
    `po_no`                     VARCHAR(30)   NOT NULL                 COMMENT 'PO单号,关联主表 export_cbu_vehicle_po.po_no',
    `guarantee_id`                  VARCHAR(32)   NOT NULL                 COMMENT '保函ID,关联保函表',
    `guarantee_number`              VARCHAR(30)   NOT NULL                 COMMENT '保函编号',
    `po_current_occupied_amount`    DECIMAL(20,2) NOT NULL DEFAULT '0.00'  COMMENT 'PO当前占用金额,= 该保函的冻结金额',
    `po_current_receivable_amount`  DECIMAL(20,2) NOT NULL DEFAULT '0.00'  COMMENT 'PO当前应收金额,= 0',
    -- 基础字段(平台模板 §7.0)
    `creator`                       BIGINT        NOT NULL                 COMMENT '创建者ID',
    `creator_name`                  VARCHAR(100)  NOT NULL                 COMMENT '创建人名称',
    `create_time`                   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`                      BIGINT        DEFAULT NULL             COMMENT '修改者ID',
    `modifier_name`                 VARCHAR(100)  DEFAULT NULL             COMMENT '修改人名称',
    `update_time`                   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status`                 BIGINT        NOT NULL DEFAULT '0'     COMMENT '删除状态(0=正常,1=已删除),逻辑删除',
    `status`                        SMALLINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态(1:启用,0:停用),平台规范',
    `status_desc`                   VARCHAR(256)  DEFAULT NULL             COMMENT '状态变更描述',
    `owner_user_id`                 BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '归属用户ID',
    `owner_org_id`                  BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '归属门店ID(组织ID)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_po_guarantee` (`po_no`, `guarantee_id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='PO-保函关联表(一个PO多保函)';
