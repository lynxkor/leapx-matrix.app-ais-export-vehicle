-- ============================================================
-- 保函管理 字典数据
-- 表结构已存在(平台提供),本脚本仅插入数据,不重建表
--   common_dict         字典表      (主键 bigint 自增, dict_type 唯一)
--   common_dict_value   字典值表    (主键 bigint 自增, dict_type+dict_value 联合唯一含 delete_status)
-- 关联键: dict_type 冗余字段,不建外键
-- 国际化文案见 i18n/messages*.properties 与 properties/common_*.properties
-- ============================================================


-- ============================================================
-- 1) 字典类型注册 (5 类保函相关字典,写入 common_dict)
-- ============================================================
INSERT INTO `common_dict` (`dict_type`, `dict_name`, `creator`, `creator_name`, `create_time`)
VALUES ('letter_guarantee_lg_type',         '保函类型',     NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_order_type',       '订单类型',     NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_approval_status',  '保函审核状态', NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_lg_status',        '保函状态',     NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_record_type',      '资金变动类型', NULL, 'system', CURRENT_TIMESTAMP);


-- ============================================================
-- 2) 字典项数据 (写入 common_dict_value)
-- 注意: letter_guarantee_lg_status 用 10/20/30 三态(10=未生效/20=有效/30=失效),
--       数据库 lg_status 字段 NOT NULL DEFAULT '10',业务不再依赖 NULL 兜底。
-- ============================================================

-- ---------- 保函类型 letter_guarantee_lg_type ----------
-- G117 红旗项目目前仅启用 预付款保函;其它类型(履约/质量/投标)预留扩展位,业务侧不展示。
-- 表字段 lg_type NOT NULL,默认 PREPAYMENT。
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('letter_guarantee_lg_type', 'PREPAYMENT', '预付款保函', 1, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 订单类型 letter_guarantee_order_type ----------
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('letter_guarantee_order_type', 'CBU', 'CBU', 1, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 审核状态 letter_guarantee_approval_status ----------
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('letter_guarantee_approval_status', '10', '草稿',     1, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_approval_status', '20', '已提交',   2, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_approval_status', '30', '审核通过', 3, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_approval_status', '40', '驳回',     4, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_approval_status', '50', '作废',     5, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 保函状态 letter_guarantee_lg_status ----------
-- 10=未生效(主表默认值,草稿/提交/驳回/作废);
-- 20=有效(审核通过时由 Gateway 置位);
-- 30=失效(到期自动失效 / 手动 expireEarly 置位)。
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('letter_guarantee_lg_status', '10', '未生效', 1, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_lg_status', '20', '有效',   2, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_lg_status', '30', '失效',   3, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 资金变动类型 letter_guarantee_record_type ----------
-- 与主表审核/保函状态对齐 10/20/30/40/50 编码规范(预留扩展,当前仅 5 项)
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('letter_guarantee_record_type', '10', '冻结',     1, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_record_type', '20', '解冻',     2, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_record_type', '30', '转应收',   3, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_record_type', '40', '应收释放', 4, NULL, 'system', CURRENT_TIMESTAMP),
       ('letter_guarantee_record_type', '50', '保函失效', 5, NULL, 'system', CURRENT_TIMESTAMP);