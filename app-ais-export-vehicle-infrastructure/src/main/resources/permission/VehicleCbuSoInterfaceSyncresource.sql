-- 生成 SO接口同步记录表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/export/vehicle/cbuso/so/vehicleCbuSoInterfaceSync','SO接口同步记录表', 'AISEV-vehicleCbuSoInterfaceSync', 'AISEV-vehicleCbuSoInterfaceSync', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:so:vehicleCbuSoInterfaceSync:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSoInterfaceSync'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSoInterfaceSync' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSoInterfaceSync' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('SO接口同步记录表列表', 'AISEV-vehicleCbuSoInterfaceSync-list', 'AISEV-vehicleCbuSoInterfaceSync-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:list'),
('SO接口同步记录表分页', 'AISEV-vehicleCbuSoInterfaceSync-page', 'AISEV-vehicleCbuSoInterfaceSync-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:page'),
('SO接口同步记录表详情', 'AISEV-vehicleCbuSoInterfaceSync-detail', 'AISEV-vehicleCbuSoInterfaceSync-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:get'),
('SO接口同步记录表新增/编辑', 'AISEV-vehicleCbuSoInterfaceSync-addOrUpdate', 'AISEV-vehicleCbuSoInterfaceSync-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:addOrUpdate'),
('SO接口同步记录表删除', 'AISEV-vehicleCbuSoInterfaceSync-delete', 'AISEV-vehicleCbuSoInterfaceSync-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:delete'),
('SO接口同步记录表启用/禁用', 'AISEV-vehicleCbuSoInterfaceSync-enableOrDisable', 'AISEV-vehicleCbuSoInterfaceSync-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:enableOrDisable'),
('SO接口同步记录表导入', 'AISEV-vehicleCbuSoInterfaceSync-import', 'AISEV-vehicleCbuSoInterfaceSync-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:import'),
('SO接口同步记录表导出', 'AISEV-vehicleCbuSoInterfaceSync-export', 'AISEV-vehicleCbuSoInterfaceSync-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:export'),
('SO接口同步记录表审计日志', 'AISEV-vehicleCbuSoInterfaceSync-auditLog', 'AISEV-vehicleCbuSoInterfaceSync-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoInterfaceSync:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISEV-vehicleCbuSoInterfaceSync%';
