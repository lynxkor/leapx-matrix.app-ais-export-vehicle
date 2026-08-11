-- 应付清单明细表新增字段
-- 执行前请备份数据

-- 1. 添加生成月份字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN generate_month VARCHAR(6) COMMENT '生成月份 yyyyMM' AFTER detail_no;

-- 2. 添加单据状态字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN bill_status VARCHAR(10) COMMENT '单据状态：10=草稿/20=已提交待确认/30=已确认/40=驳回/50=部分收款/60=已收款' AFTER generate_month;

-- 3. 添加客户编码字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN customer_code VARCHAR(50) COMMENT '客户编码' AFTER po_no;

-- 4. 添加客户名称字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN customer_name VARCHAR(200) COMMENT '客户名称' AFTER customer_code;

-- 5. 添加PO创建时间字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN po_create_time DATETIME COMMENT 'PO创建时间' AFTER receive_time;

-- 6. 添加销售市场字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN sale_market VARCHAR(100) COMMENT '销售市场' AFTER po_create_time;

-- 7. 添加年款字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN model_year VARCHAR(20) COMMENT '年款' AFTER sale_market;

-- 8. 添加选装包字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN option_package VARCHAR(500) COMMENT '选装包' AFTER model_year;

-- 9. 添加提交日期字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN submit_date DATETIME COMMENT '提交日期' AFTER option_package;

-- 10. 添加确认时间字段
ALTER TABLE export_vehicle_payable_list_detail ADD COLUMN confirm_time DATETIME COMMENT '确认时间' AFTER submit_date;

-- 添加索引（可选，根据查询需求添加）
-- ALTER TABLE export_vehicle_payable_list_detail ADD INDEX idx_customer_code (customer_code);
-- ALTER TABLE export_vehicle_payable_list_detail ADD INDEX idx_generate_month (generate_month);
