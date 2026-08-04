-- 生成 PO订单明细表 页面资源权限

SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/export/vehicle/exportVehicleCbuPoDetail','PO订单明细', 'AISEV-exportVehicleCbuPoDetail', 'AISEV-exportVehicleCbuPoDetail', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:po:exportVehicleCbuPoDetail:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPoDetail'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPoDetail' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPoDetail' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('PO订单明细分页', 'AISEV-exportVehicleCbuPoDetail-page', 'AISEV-exportVehicleCbuPoDetail-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:page'),
('PO订单明细列表', 'AISEV-exportVehicleCbuPoDetail-list', 'AISEV-exportVehicleCbuPoDetail-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:list'),
('PO订单明细详情', 'AISEV-exportVehicleCbuPoDetail-detail', 'AISEV-exportVehicleCbuPoDetail-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:get'),
('PO订单明细新增/编辑', 'AISEV-exportVehicleCbuPoDetail-addOrUpdate', 'AISEV-exportVehicleCbuPoDetail-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:addOrUpdate'),
('PO订单明细删除', 'AISEV-exportVehicleCbuPoDetail-delete', 'AISEV-exportVehicleCbuPoDetail-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:delete'),
('PO订单明细启用/禁用', 'AISEV-exportVehicleCbuPoDetail-enableOrDisable', 'AISEV-exportVehicleCbuPoDetail-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:enableOrDisable'),
('PO订单明细导出', 'AISEV-exportVehicleCbuPoDetail-export', 'AISEV-exportVehicleCbuPoDetail-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:export'),
('PO订单明细审计日志', 'AISEV-exportVehicleCbuPoDetail-auditLog', 'AISEV-exportVehicleCbuPoDetail-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPoDetail:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISEV-exportVehicleCbuPoDetail%';
