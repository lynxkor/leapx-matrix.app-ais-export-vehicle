-- ============================================================
-- 数据来源(data_source)字段改造迁移脚本
-- 适用：将 PO 主表 data_source 由 VARCHAR(10) 中文改为 tinyint 数字，
--       并在 SO 主表新增 data_source 字段（置于 receive_time 之后）。
-- 执行顺序：先执行本脚本，再发布应用代码。
-- ============================================================

-- 1) PO 历史中文数据迁移（必须在 MODIFY 之前执行）
UPDATE `export_vehicle_cbu_po`
SET `data_source` = '2'
WHERE `data_source` = '红旗';

UPDATE `export_vehicle_cbu_po`
SET `data_source` = '1'
WHERE `data_source` = '销服';

-- 2) PO 主表修改 data_source 列为 tinyint
ALTER TABLE `export_vehicle_cbu_po`
    MODIFY COLUMN `data_source` tinyint NOT NULL DEFAULT 2 COMMENT '数据来源,枚举:1销服/2红旗';

-- 3) SO 主表新增 data_source 列，置于 receive_time 之后
ALTER TABLE `export_vehicle_cbu_so`
    ADD COLUMN `data_source` tinyint DEFAULT NULL COMMENT '数据来源,枚举:1销服/2红旗,取自PO单' AFTER `receive_time`;
