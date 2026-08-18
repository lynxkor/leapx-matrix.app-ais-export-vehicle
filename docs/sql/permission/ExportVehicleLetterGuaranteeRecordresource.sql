-- 生成 保函交易明细 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/vehicle/exportVehicleLetterGuaranteeRecord','保函交易明细', 'AISEP-exportVehicleLetterGuaranteeRecord', 'AISEP-exportVehicleLetterGuaranteeRecord', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:basic:exportVehicleLetterGuaranteeRecord:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEP-exportVehicleLetterGuaranteeRecord'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEP-exportVehicleLetterGuaranteeRecord' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEP-exportVehicleLetterGuaranteeRecord' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('保函交易明细列表', 'AISEP-exportVehicleLetterGuaranteeRecord-list', 'AISEP-exportVehicleLetterGuaranteeRecord-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:list'),
('保函交易明细分页', 'AISEP-exportVehicleLetterGuaranteeRecord-page', 'AISEP-exportVehicleLetterGuaranteeRecord-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:page'),
('保函交易明细详情', 'AISEP-exportVehicleLetterGuaranteeRecord-detail', 'AISEP-exportVehicleLetterGuaranteeRecord-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:get'),
('保函交易明细新增/编辑', 'AISEP-exportVehicleLetterGuaranteeRecord-addOrUpdate', 'AISEP-exportVehicleLetterGuaranteeRecord-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:addOrUpdate'),
('保函交易明细删除', 'AISEP-exportVehicleLetterGuaranteeRecord-delete', 'AISEP-exportVehicleLetterGuaranteeRecord-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:delete'),
('保函交易明细启用/禁用', 'AISEP-exportVehicleLetterGuaranteeRecord-enableOrDisable', 'AISEP-exportVehicleLetterGuaranteeRecord-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:enableOrDisable'),
('保函交易明细导入', 'AISEP-exportVehicleLetterGuaranteeRecord-import', 'AISEP-exportVehicleLetterGuaranteeRecord-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:import'),
('保函交易明细导出', 'AISEP-exportVehicleLetterGuaranteeRecord-export', 'AISEP-exportVehicleLetterGuaranteeRecord-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:export'),
('保函交易明细审计日志', 'AISEP-exportVehicleLetterGuaranteeRecord-auditLog', 'AISEP-exportVehicleLetterGuaranteeRecord-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuaranteeRecord:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISEP-exportVehicleLetterGuaranteeRecord%';
