-- ============================================================
-- 应付清单管理 字典数据
-- 表结构已存在(平台提供),本脚本仅插入数据,不重建表
--   common_dict         字典表      (主键 bigint 自增, dict_type 唯一)
--   common_dict_value   字典值表    (主键 bigint 自增, dict_type+dict_value 联合唯一含 delete_status)
-- 关联键: dict_type 冗余字段,不建外键
-- 对应枚举: PayableListStatusEnum(app-ais-export-vehicle-client/payablelist/enums)
--   DRAFT("10","草稿") / SUBMITTED("20","已提交待确认") / CONFIRMED("30","已确认")
--   REJECTED("40","驳回") / PARTIAL_RECEIVED("50","部分收款") / RECEIVED("60","已收款")
-- 可重复执行: 先清理后插入,避免唯一键冲突
-- ============================================================

-- ============================================================
-- 0) 清理旧数据(保证脚本可重复执行)
-- ============================================================
DELETE FROM `common_dict_value` WHERE `dict_type` = 'payable_list_status';
DELETE FROM `common_dict` WHERE `dict_type` = 'payable_list_status';

-- ============================================================
-- 1) 字典类型注册 (写入 common_dict)
-- ============================================================
INSERT INTO `common_dict` (`dict_type`, `dict_name`, `creator`, `creator_name`, `create_time`)
VALUES ('payable_list_status', '应付清单单据状态', NULL, 'system', CURRENT_TIMESTAMP);

-- ============================================================
-- 2) 字典项数据 (写入 common_dict_value)
-- 编码与 PayableListStatusEnum 枚举一致,供前端 fetch-select 下拉使用
-- ============================================================
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('payable_list_status', '10', '草稿',          1, NULL, 'system', CURRENT_TIMESTAMP),
       ('payable_list_status', '20', '已提交待确认',  2, NULL, 'system', CURRENT_TIMESTAMP),
       ('payable_list_status', '30', '已确认',        3, NULL, 'system', CURRENT_TIMESTAMP),
       ('payable_list_status', '40', '驳回',          4, NULL, 'system', CURRENT_TIMESTAMP),
       ('payable_list_status', '50', '部分收款',      5, NULL, 'system', CURRENT_TIMESTAMP),
       ('payable_list_status', '60', '已收款',        6, NULL, 'system', CURRENT_TIMESTAMP);
