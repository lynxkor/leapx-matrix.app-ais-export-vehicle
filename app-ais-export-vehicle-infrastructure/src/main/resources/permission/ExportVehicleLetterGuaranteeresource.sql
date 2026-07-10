-- 生成 保函主表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/service/maintainexportVehicleLetterGuarantee','保函主表', 'AISEP-exportVehicleLetterGuarantee', 'AISEP-exportVehicleLetterGuarantee', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:basic:exportVehicleLetterGuarantee:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEP-exportVehicleLetterGuarantee'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEP-exportVehicleLetterGuarantee' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEP-exportVehicleLetterGuarantee' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('保函主表列表', 'AISEP-exportVehicleLetterGuarantee-list', 'AISEP-exportVehicleLetterGuarantee-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:list'),
('保函主表分页', 'AISEP-exportVehicleLetterGuarantee-page', 'AISEP-exportVehicleLetterGuarantee-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:page'),
('保函主表详情', 'AISEP-exportVehicleLetterGuarantee-detail', 'AISEP-exportVehicleLetterGuarantee-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:get'),
('保函主表新增/编辑', 'AISEP-exportVehicleLetterGuarantee-addOrUpdate', 'AISEP-exportVehicleLetterGuarantee-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:addOrUpdate'),
('保函主表删除', 'AISEP-exportVehicleLetterGuarantee-delete', 'AISEP-exportVehicleLetterGuarantee-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:delete'),
('保函主表启用/禁用', 'AISEP-exportVehicleLetterGuarantee-enableOrDisable', 'AISEP-exportVehicleLetterGuarantee-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:enableOrDisable'),
('保函主表导入', 'AISEP-exportVehicleLetterGuarantee-import', 'AISEP-exportVehicleLetterGuarantee-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:import'),
('保函主表导出', 'AISEP-exportVehicleLetterGuarantee-export', 'AISEP-exportVehicleLetterGuarantee-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:export'),
('保函主表批量提交', 'AISEP-exportVehicleLetterGuarantee-batchSubmit', 'AISEP-exportVehicleLetterGuarantee-batchSubmit', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:submit'),
('保函主表批量审核', 'AISEP-exportVehicleLetterGuarantee-batchApprove', 'AISEP-exportVehicleLetterGuarantee-batchApprove', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:approve'),
('保函主表批量作废', 'AISEP-exportVehicleLetterGuarantee-batchCancel', 'AISEP-exportVehicleLetterGuarantee-batchCancel', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:cancel');
('保函主表审计日志', 'AISEP-exportVehicleLetterGuarantee-auditLog', 'AISEP-exportVehicleLetterGuarantee-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISEP-exportVehicleLetterGuarantee%';
