-- 生成 保函主表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/service/maintainexportVehicleLetterGuarantee','保函主表', 'AISMC-exportVehicleLetterGuarantee', 'AISMC-exportVehicleLetterGuarantee', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:basic:exportVehicleLetterGuarantee:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISMC-exportVehicleLetterGuarantee'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISMC-exportVehicleLetterGuarantee' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISMC-exportVehicleLetterGuarantee' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('保函主表列表', 'AISMC-exportVehicleLetterGuarantee-list', 'AISMC-exportVehicleLetterGuarantee-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:list'),
('保函主表分页', 'AISMC-exportVehicleLetterGuarantee-page', 'AISMC-exportVehicleLetterGuarantee-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:page'),
('保函主表详情', 'AISMC-exportVehicleLetterGuarantee-detail', 'AISMC-exportVehicleLetterGuarantee-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:get'),
('保函主表新增/编辑', 'AISMC-exportVehicleLetterGuarantee-addOrUpdate', 'AISMC-exportVehicleLetterGuarantee-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:addOrUpdate'),
('保函主表删除', 'AISMC-exportVehicleLetterGuarantee-delete', 'AISMC-exportVehicleLetterGuarantee-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:delete'),
('保函主表启用/禁用', 'AISMC-exportVehicleLetterGuarantee-enableOrDisable', 'AISMC-exportVehicleLetterGuarantee-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:enableOrDisable'),
('保函主表导入', 'AISMC-exportVehicleLetterGuarantee-import', 'AISMC-exportVehicleLetterGuarantee-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:import'),
('保函主表导出', 'AISMC-exportVehicleLetterGuarantee-export', 'AISMC-exportVehicleLetterGuarantee-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:export'),
('保函主表审计日志', 'AISMC-exportVehicleLetterGuarantee-auditLog', 'AISMC-exportVehicleLetterGuarantee-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:basic:exportVehicleLetterGuarantee:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISMC-exportVehicleLetterGuarantee%';
