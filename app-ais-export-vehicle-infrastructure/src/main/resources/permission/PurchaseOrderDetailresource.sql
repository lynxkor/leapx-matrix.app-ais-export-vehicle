-- 生成 PO订单明细表 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/service/maintainpurchaseOrderDetail','PO订单明细表', 'AISMC-purchaseOrderDetail', 'AISMC-purchaseOrderDetail', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:po:purchaseOrderDetail:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrderDetail'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrderDetail' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrderDetail' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('PO订单明细表列表', 'AISMC-purchaseOrderDetail-list', 'AISMC-purchaseOrderDetail-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:list'),
('PO订单明细表分页', 'AISMC-purchaseOrderDetail-page', 'AISMC-purchaseOrderDetail-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:page'),
('PO订单明细表详情', 'AISMC-purchaseOrderDetail-detail', 'AISMC-purchaseOrderDetail-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:get'),
('PO订单明细表新增/编辑', 'AISMC-purchaseOrderDetail-addOrUpdate', 'AISMC-purchaseOrderDetail-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:addOrUpdate'),
('PO订单明细表删除', 'AISMC-purchaseOrderDetail-delete', 'AISMC-purchaseOrderDetail-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:delete'),
('PO订单明细表启用/禁用', 'AISMC-purchaseOrderDetail-enableOrDisable', 'AISMC-purchaseOrderDetail-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:enableOrDisable'),
('PO订单明细表导入', 'AISMC-purchaseOrderDetail-import', 'AISMC-purchaseOrderDetail-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:import'),
('PO订单明细表导出', 'AISMC-purchaseOrderDetail-export', 'AISMC-purchaseOrderDetail-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:export'),
('PO订单明细表审计日志', 'AISMC-purchaseOrderDetail-auditLog', 'AISMC-purchaseOrderDetail-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderDetail:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISMC-purchaseOrderDetail%';
