-- 生成 PO订单主表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/service/maintainpurchaseOrder','PO订单主表', 'AISMC-purchaseOrder', 'AISMC-purchaseOrder', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:po:purchaseOrder:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrder'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrder' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrder' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('PO订单主表列表', 'AISMC-purchaseOrder-list', 'AISMC-purchaseOrder-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:list'),
('PO订单主表分页', 'AISMC-purchaseOrder-page', 'AISMC-purchaseOrder-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:page'),
('PO订单主表详情', 'AISMC-purchaseOrder-detail', 'AISMC-purchaseOrder-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:get'),
('PO订单主表新增/编辑', 'AISMC-purchaseOrder-addOrUpdate', 'AISMC-purchaseOrder-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:addOrUpdate'),
('PO订单主表删除', 'AISMC-purchaseOrder-delete', 'AISMC-purchaseOrder-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:delete'),
('PO订单主表启用/禁用', 'AISMC-purchaseOrder-enableOrDisable', 'AISMC-purchaseOrder-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:enableOrDisable'),
('PO订单主表导入', 'AISMC-purchaseOrder-import', 'AISMC-purchaseOrder-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:import'),
('PO订单主表导出', 'AISMC-purchaseOrder-export', 'AISMC-purchaseOrder-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:export'),
('PO订单主表审计日志', 'AISMC-purchaseOrder-auditLog', 'AISMC-purchaseOrder-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrder:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISMC-purchaseOrder%';
