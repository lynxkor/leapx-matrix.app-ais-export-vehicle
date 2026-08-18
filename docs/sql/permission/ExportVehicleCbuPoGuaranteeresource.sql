-- 生成 PO-保函关联表 页面资源权限

SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/export/vehicle/exportVehicleCbuPoGuarantee','PO-保函关联', 'AISEV-exportVehicleCbuPoGuarantee', 'AISEV-exportVehicleCbuPoGuarantee', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:po:exportVehicleCbuPoGuarantee:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPoGuarantee'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPoGuarantee' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPoGuarantee' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('PO-保函关联分页', 'AISEV-exportVehicleCbuPoGuarantee-page', 'AISEV-exportVehicleCbuPoGuarantee-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:page'),
('PO-保函关联列表', 'AISEV-exportVehicleCbuPoGuarantee-list', 'AISEV-exportVehicleCbuPoGuarantee-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:list'),
('PO-保函关联详情', 'AISEV-exportVehicleCbuPoGuarantee-detail', 'AISEV-exportVehicleCbuPoGuarantee-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:get'),
('PO-保函关联新增/编辑', 'AISEV-exportVehicleCbuPoGuarantee-addOrUpdate', 'AISEV-exportVehicleCbuPoGuarantee-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:addOrUpdate'),
('PO-保函关联删除', 'AISEV-exportVehicleCbuPoGuarantee-delete', 'AISEV-exportVehicleCbuPoGuarantee-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:delete'),
('PO-保函关联启用/禁用', 'AISEV-exportVehicleCbuPoGuarantee-enableOrDisable', 'AISEV-exportVehicleCbuPoGuarantee-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:enableOrDisable'),
('PO-保函关联导出', 'AISEV-exportVehicleCbuPoGuarantee-export', 'AISEV-exportVehicleCbuPoGuarantee-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:export'),
('PO-保函关联审计日志', 'AISEV-exportVehicleCbuPoGuarantee-auditLog', 'AISEV-exportVehicleCbuPoGuarantee-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoGuarantee:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISEV-exportVehicleCbuPoGuarantee%';
