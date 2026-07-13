-- 生成 SO车辆明细表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/export/vehicle/cbuso/so/vehicleCbuSoDetail','SO车辆明细表', 'AISEV-vehicleCbuSoDetail', 'AISEV-vehicleCbuSoDetail', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:so:vehicleCbuSoDetail:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSoDetail'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSoDetail' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSoDetail' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('SO车辆明细表列表', 'AISEV-vehicleCbuSoDetail-list', 'AISEV-vehicleCbuSoDetail-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:list'),
('SO车辆明细表分页', 'AISEV-vehicleCbuSoDetail-page', 'AISEV-vehicleCbuSoDetail-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:page'),
('SO车辆明细表详情', 'AISEV-vehicleCbuSoDetail-detail', 'AISEV-vehicleCbuSoDetail-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:get'),
('SO车辆明细表新增/编辑', 'AISEV-vehicleCbuSoDetail-addOrUpdate', 'AISEV-vehicleCbuSoDetail-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:addOrUpdate'),
('SO车辆明细表删除', 'AISEV-vehicleCbuSoDetail-delete', 'AISEV-vehicleCbuSoDetail-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:delete'),
('SO车辆明细表启用/禁用', 'AISEV-vehicleCbuSoDetail-enableOrDisable', 'AISEV-vehicleCbuSoDetail-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:enableOrDisable'),
('SO车辆明细表导入', 'AISEV-vehicleCbuSoDetail-import', 'AISEV-vehicleCbuSoDetail-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:import'),
('SO车辆明细表导出', 'AISEV-vehicleCbuSoDetail-export', 'AISEV-vehicleCbuSoDetail-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:export'),
('SO车辆明细表审计日志', 'AISEV-vehicleCbuSoDetail-auditLog', 'AISEV-vehicleCbuSoDetail-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSoDetail:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISEV-vehicleCbuSoDetail%';
