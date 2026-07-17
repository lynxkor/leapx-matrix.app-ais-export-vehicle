-- ============================================================
-- CBU 整车销售订单 字典数据
-- 表结构已存在(平台提供),本脚本仅插入数据,不重建表
--   common_dict         字典表      (主键 bigint 自增, dict_type 唯一)
--   common_dict_value   字典值表    (主键 bigint 自增, dict_type+dict_value 联合唯一含 delete_status)
-- 关联键: dict_type 冗余字段,不建外键
-- 国际化文案见 i18n/messages*.properties 与 properties/common_*.properties
-- ============================================================


-- ============================================================
-- 1) 字典类型注册 (3 类 CBU SO 相关字典,写入 common_dict)
-- ============================================================
INSERT INTO `common_dict` (`dict_type`, `dict_name`, `creator`, `creator_name`, `create_time`)
VALUES ('vehicle_cbu_so_order_status', 'CBU整车销售订单状态', NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_qc_result',  'CBU整车销售订单质检结果', NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_sync_result', 'CBU整车销售订单同步结果', NULL, 'system', CURRENT_TIMESTAMP);


-- ============================================================
-- 2) 字典项数据 (写入 common_dict_value)
-- ============================================================

-- ---------- 订单状态 vehicle_cbu_so_order_status ----------
-- 对应字段 order_status,枚举 VehicleCbuSoStatusEnum
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('vehicle_cbu_so_order_status', '1001', '未下发生产', 1, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1002', '已下发生产', 2, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1003', '排产中',     3, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1004', '焊装上线',   4, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1005', '涂装上线',   5, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1006', '总装上线',   6, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1007', '总装下线',   7, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1008', '质检通过',   8, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1009', '质检未通过', 9, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1010', '质检未收到车', 10, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1011', '已收款',    11, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_order_status', '1012', '已作废',    12, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 质检结果 vehicle_cbu_so_qc_result ----------
-- 对应字段 qc_result / last_qc_result,枚举 VehicleCbuSoQcResultEnum
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('vehicle_cbu_so_qc_result', '10', '通过',     1, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_qc_result', '20', '不通过',   2, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_qc_result', '30', '未收到车辆', 3, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 同步结果 vehicle_cbu_so_sync_result ----------
-- 对应字段 sync_aps_result / sync_sap_result / sync_customer_result,枚举 VehicleCbuSoSyncResultEnum
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('vehicle_cbu_so_sync_result', '0', '失败', 1, NULL, 'system', CURRENT_TIMESTAMP),
       ('vehicle_cbu_so_sync_result', '1', '成功', 2, NULL, 'system', CURRENT_TIMESTAMP);
