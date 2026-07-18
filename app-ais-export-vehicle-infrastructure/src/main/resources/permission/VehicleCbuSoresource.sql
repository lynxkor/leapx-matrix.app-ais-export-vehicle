-- 生成 CBU整车销售订单主表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/export/vehicle/vehicleCbuSo','SO订单', 'AISEV-vehicleCbuSo', 'AISEV-vehicleCbuSo', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:so:vehicleCbuSo:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSo'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSo' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEV-vehicleCbuSo' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('SO订单分页', 'AISEV-vehicleCbuSo-page', 'AISEV-vehicleCbuSo-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:page'),
('SO订单详情', 'AISEV-vehicleCbuSo-detail', 'AISEV-vehicleCbuSo-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:get'),
('SO订单新增/编辑', 'AISEV-vehicleCbuSo-addOrUpdate', 'AISEV-vehicleCbuSo-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:addOrUpdate'),
('SO订单删除', 'AISEV-vehicleCbuSo-delete', 'AISEV-vehicleCbuSo-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:delete'),
('SO订单导出', 'AISEV-vehicleCbuSo-export', 'AISEV-vehicleCbuSo-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:export'),
('SO订单审计日志', 'AISEV-vehicleCbuSo-auditLog', 'AISEV-vehicleCbuSo-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:auditLog'),
('SO订单批量下发生产', 'AISEV-vehicleCbuSo-issueToProduction', 'AISEV-vehicleCbuSo-issueToProduction', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:issueToProduction'),
('SO订单同步SAP', 'AISEV-vehicleCbuSo-reissueSap', 'AISEV-vehicleCbuSo-reissueSap', '0', '1', '2', '0', @newPath, @pid, 5, 'web:so:vehicleCbuSo:reissueSap');


select * from app_portal_sit.resource_info where resourceTag like 'AISEV-vehicleCbuSo%';
