-- ============================================================
-- PO 订单 字典数据
-- 表结构已存在(平台提供),本脚本仅插入数据,不重建表
--   common_dict         字典表      (主键 bigint 自增, dict_type 唯一)
--   common_dict_value   字典值表    (主键 bigint 自增, dict_type+dict_value 联合唯一含 delete_status)
-- ============================================================


-- ============================================================
-- 1) 字典类型注册 (2 类 PO 订单相关字典,写入 common_dict)
-- ============================================================
INSERT INTO `common_dict` (`dict_type`, `dict_name`, `creator`, `creator_name`, `create_time`)
VALUES ('export_vehicle_cbu_po_order_status', 'PO订单状态', NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_type',   'PO订单类型', NULL, 'system', CURRENT_TIMESTAMP);


-- ============================================================
-- 2) 字典项数据 (写入 common_dict_value)
-- ============================================================

-- ---------- 订单状态 export_vehicle_cbu_po_order_status ----------
-- 对应字段 order_status,枚举 ExportVehicleCbuPoStatusEnum
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('export_vehicle_cbu_po_order_status', '10', '草稿',       1, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_status', '20', '销售已提交',  2, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_status', '30', '销售已驳回',  3, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_status', '40', '供需审核通过', 4, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_status', '50', '供需审核驳回', 5, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_status', '60', '产销平衡通过', 6, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_status', '70', '产销平衡驳回', 7, NULL, 'system', CURRENT_TIMESTAMP);

-- ---------- 订单类型 export_vehicle_cbu_po_order_type ----------
-- 对应字段 order_type,枚举 OrderTypeEnum
INSERT INTO `common_dict_value` (`dict_type`, `dict_value`, `dict_label`, `sort`, `creator`, `creator_name`, `create_time`)
VALUES ('export_vehicle_cbu_po_order_type', '1', 'CBU', 1, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_type', '2', 'SKD', 2, NULL, 'system', CURRENT_TIMESTAMP),
       ('export_vehicle_cbu_po_order_type', '3', 'CKD', 3, NULL, 'system', CURRENT_TIMESTAMP);
