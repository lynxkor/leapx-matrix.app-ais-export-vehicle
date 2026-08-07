-- ============================================================
-- so-aps-contract-alignment 变更迁移脚本
-- SO 主表新增 receive_order_time 列（接收订单时间，取 PO 创建时间，用于 APS 同步）
-- 建表脚本 so_order_tables.sql 用 CREATE TABLE IF NOT EXISTS，对已存在的库需执行本 ALTER
-- ============================================================

ALTER TABLE `export_vehicle_cbu_so`
    ADD COLUMN `receive_order_time` DATETIME DEFAULT NULL
        COMMENT '接收订单时间，取 PO 创建时间，用于 APS 同步'
        AFTER `expected_offline_date`;
