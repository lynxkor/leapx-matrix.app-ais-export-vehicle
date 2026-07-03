-- ============================================================
-- 保函管理 建表脚本
-- 适用: app-ais-export-vehicle 服务 - G117 红旗项目
-- 关联文档: docs/保函管理-设计文档.md §7.1 / §7.2
-- 依赖: 执行 letter-of-guarantee-dict.sql 导入字典数据
-- 前置: 与平台组对齐 LpBaseEntity/LpBaseModel 升级方案
--       (基础字段参照平台模板,见设计文档 §7.0)
-- ============================================================

-- ---------- 主表 export_vehicle_letter_guarantee ----------
DROP TABLE IF EXISTS `export_vehicle_letter_guarantee`;
CREATE TABLE `export_vehicle_letter_guarantee` (
    -- 主键
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID(UUID 或雪花ID 字符串形式)',

    -- 业务字段
    `lg_code`             VARCHAR(64)    NOT NULL                COMMENT '保函编号',
    `customer_id`         VARCHAR(64)    NOT NULL                COMMENT '客户ID',
    `lg_type`             VARCHAR(10)    NOT NULL                COMMENT '保函类型(字典 letter_guarantee_lg_type: PREPAYMENT 预付款保函;G117 红旗项目目前仅启用预付款;新增时必填,无默认值)',
    `order_type`          VARCHAR(10)    NOT NULL                COMMENT '订单类型(字典 letter_guarantee_order_type: CBU/SKD/CKD;新增时必填,无默认值;仅草稿/驳回可编辑)',
    `bank_name`           VARCHAR(256)   NOT NULL                COMMENT '开户银行(开立银行+支行合并,如"中国工商银行杭州武林支行")',
    `total_amount`        DECIMAL(20,4)  NOT NULL                COMMENT '保函金额(人民币CNY)',
    `conversion_ratio`    DECIMAL(10,6)  NOT NULL DEFAULT 1.000000 COMMENT '换算订单额度比例 0~1,保留 6 位小数',
    `available_amount`    DECIMAL(20,4)  NOT NULL DEFAULT 0      COMMENT '可用订单金额',
    `occupied_amount`     DECIMAL(20,4)  NOT NULL DEFAULT 0      COMMENT '占用订单金额',
    `receivable_amount`   DECIMAL(20,4)  NOT NULL DEFAULT 0      COMMENT '应收订单金额',
    `effective_date`      DATE           NOT NULL                COMMENT '生效日期',
    `expiry_date`         DATE           NOT NULL                COMMENT '失效日期',
    `approval_status`     VARCHAR(8)     NOT NULL DEFAULT '10'   COMMENT '审核状态(字典 letter_guarantee_approval_status: 10草稿 20已提交 30审核通过 40驳回 50作废)',
    `lg_status`           VARCHAR(8)     NOT NULL DEFAULT '10'   COMMENT '保函状态(字典 letter_guarantee_lg_status):10=未生效(草稿/提交/驳回/作废,默认值),20=有效(审核通过时置),30=失效(到期/手动失效时置)',
    `submitter_id`        BIGINT         DEFAULT NULL            COMMENT '提交人ID',
    `submitter_name`      VARCHAR(64)    DEFAULT NULL            COMMENT '提交人名称',
    `submit_time`         DATETIME       DEFAULT NULL            COMMENT '提交时间',
    `approver_id`         BIGINT         DEFAULT NULL            COMMENT '审核人ID',
    `approver_name`       VARCHAR(64)    DEFAULT NULL            COMMENT '审核人名称',
    `approve_time`        DATETIME       DEFAULT NULL            COMMENT '审核时间',
    `approve_remark`      VARCHAR(512)   DEFAULT NULL            COMMENT '审核意见',
    `remark`              VARCHAR(512)   DEFAULT NULL            COMMENT '业务备注',

    -- 基础字段(参照平台模板,见设计文档 §7.0)
    `creator`            BIGINT         NOT NULL                COMMENT '创建者ID',
    `creator_name`       VARCHAR(100)   NOT NULL                COMMENT '创建人名称',
    `create_time`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifier`           BIGINT         DEFAULT NULL            COMMENT '修改者ID',
    `modifier_name`      VARCHAR(100)   DEFAULT NULL            COMMENT '修改人名称',
    `update_time`        DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status`      BIGINT         NOT NULL DEFAULT '0'    COMMENT '删除状态(0=正常,1=已删除),逻辑删除',
    `status`             SMALLINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态(1:启用,0:停用)',
    `status_desc`        VARCHAR(256)   DEFAULT NULL            COMMENT '状态变更描述',
    `owner_user_id`      BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '归属用户ID,数据权限归属到用户时使用',
    `owner_org_id`       BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '归属门店ID(组织ID),数据权限归属到本组织以及本下级',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lg_code`   (`lg_code`),
    KEY        `idx_customer` (`customer_id`),
    KEY        `idx_expiry`   (`expiry_date`, `lg_status`),
    KEY        `idx_approval` (`approval_status`, `lg_status`),
    KEY        `idx_owner`    (`owner_user_id`, `owner_org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保函主表';


-- ---------- 明细表 export_vehicle_letter_guarantee_record ----------
-- 追加写:仅追加,不允许修改/删除(数据库层 UPDATE/DELETE 权限收紧),确保审计追溯完整。
-- delete_status 仍按模板保留(默认 0),但业务上永不置 1;status 默认 1。
-- 操作人/操作时间由基础字段 creator/creator_name/create_time 承载(平台模板),
-- 不再单独保留 operator_id/operator_name/operate_time 三个字段。
DROP TABLE IF EXISTS `export_vehicle_letter_guarantee_record`;
CREATE TABLE `export_vehicle_letter_guarantee_record` (
    -- 主键
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID(UUID 或雪花ID 字符串形式)',

    -- 业务字段
    `lg_record_code`       VARCHAR(64)    NOT NULL                COMMENT '保函交易明细编号,生成规则 BK+yyyyMMdd+HHmmss+5位流水号(例 BK20260415143000001),全库唯一',
    `lg_id`                VARCHAR(64)    NOT NULL                COMMENT '保函ID',
    `lg_code`              VARCHAR(64)    NOT NULL                COMMENT '保函编号(冗余)',
    `lg_type`              VARCHAR(10)    NOT NULL                COMMENT '保函类型(冗余,字典 letter_guarantee_lg_type)',
    `customer_id`          VARCHAR(64)    NOT NULL                COMMENT '客户ID',
    `transaction_type`     VARCHAR(8)     NOT NULL                COMMENT '变动类型(字典 letter_guarantee_record_type: 10冻结 20解冻 30转应收 40释放应收 50失效)',
    `transaction_amount`   DECIMAL(20,4)  NOT NULL                COMMENT '变动金额(始终为正)',
    `transaction_remark`   VARCHAR(512)   DEFAULT NULL            COMMENT '变动说明(如 "PO单 101提交,资金冻结")',
    `before_available`     DECIMAL(20,4)  DEFAULT NULL            COMMENT '变动前可用订单金额(冻结/解冻/失效 时填值,转应收/释放应收 时为 NULL)',
    `after_available`      DECIMAL(20,4)  DEFAULT NULL            COMMENT '变动后可用订单金额(同上)',
    `before_occupied`      DECIMAL(20,4)  DEFAULT NULL            COMMENT '变动前占用订单金额(冻结/解冻/转应收/失效 时填值,释放应收 时为 NULL)',
    `after_occupied`       DECIMAL(20,4)  DEFAULT NULL            COMMENT '变动后占用订单金额(同上)',
    `before_receivable`    DECIMAL(20,4)  DEFAULT NULL            COMMENT '变动前应收订单金额(转应收/释放应收 时填值,冻结/解冻/失效 时为 NULL)',
    `after_receivable`     DECIMAL(20,4)  DEFAULT NULL            COMMENT '变动后应收订单金额(同上)',
    `po_no`                VARCHAR(30)    DEFAULT NULL            COMMENT 'PO 单号(不允许编辑,根据冻结/解冻情况自动赋值)',
    `so_no`                VARCHAR(30)    DEFAULT NULL            COMMENT 'SO 单号(不允许编辑,根据转应收/释放应收情况自动赋值,冻结/解冻 时为 NULL)',
    `vin`                  VARCHAR(30)    DEFAULT NULL            COMMENT 'VIN 车辆识别码(不允许编辑,根据转应收情况自动赋值,冻结/解冻 时为 NULL)',

    -- 基础字段(参照平台模板,见设计文档 §7.0)
    `creator`            BIGINT         NOT NULL                COMMENT '创建者ID(即操作人)',
    `creator_name`       VARCHAR(100)   NOT NULL                COMMENT '创建人名称(即操作人名称)',
    `create_time`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(即操作时间)',
    `modifier`           BIGINT         DEFAULT NULL            COMMENT '修改者ID(业务上永不更新)',
    `modifier_name`      VARCHAR(100)   DEFAULT NULL            COMMENT '修改人名称(业务上永不更新)',
    `update_time`        DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间(业务上永不更新)',
    `delete_status`      SMALLINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除状态(0=正常,1=已删除),业务上永不置 1',
    `status`             SMALLINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态(1:启用,0:停用)',
    `status_desc`        VARCHAR(256)   DEFAULT NULL            COMMENT '状态变更描述',
    `owner_user_id`      BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '归属用户ID(沿用主保函的归属)',
    `owner_org_id`       BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '归属门店ID(沿用主保函的归属)',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lg_record_code` (`lg_record_code`),
    KEY        `idx_lg_id`       (`lg_id`, `create_time`),
    KEY        `idx_type_time`   (`transaction_type`, `create_time`),
    KEY        `idx_customer`    (`customer_id`),
    KEY        `idx_vin`         (`vin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保函交易明细(追加写)';
