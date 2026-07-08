-- 生成 PO-保函关联表(一个PO多保函) 页面资源权限
             
SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = '' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/service/maintainpurchaseOrderGuarantee','PO-保函关联表(一个PO多保函)', 'AISMC-purchaseOrderGuarantee', 'AISMC-purchaseOrderGuarantee', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:po:purchaseOrderGuarantee:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrderGuarantee'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrderGuarantee' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISMC-purchaseOrderGuarantee' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('PO-保函关联表(一个PO多保函)列表', 'AISMC-purchaseOrderGuarantee-list', 'AISMC-purchaseOrderGuarantee-list', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:list'),
('PO-保函关联表(一个PO多保函)分页', 'AISMC-purchaseOrderGuarantee-page', 'AISMC-purchaseOrderGuarantee-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:page'),
('PO-保函关联表(一个PO多保函)详情', 'AISMC-purchaseOrderGuarantee-detail', 'AISMC-purchaseOrderGuarantee-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:get'),
('PO-保函关联表(一个PO多保函)新增/编辑', 'AISMC-purchaseOrderGuarantee-addOrUpdate', 'AISMC-purchaseOrderGuarantee-addOrUpdate', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:addOrUpdate'),
('PO-保函关联表(一个PO多保函)删除', 'AISMC-purchaseOrderGuarantee-delete', 'AISMC-purchaseOrderGuarantee-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:delete'),
('PO-保函关联表(一个PO多保函)启用/禁用', 'AISMC-purchaseOrderGuarantee-enableOrDisable', 'AISMC-purchaseOrderGuarantee-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:enableOrDisable'),
('PO-保函关联表(一个PO多保函)导入', 'AISMC-purchaseOrderGuarantee-import', 'AISMC-purchaseOrderGuarantee-import', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:import'),
('PO-保函关联表(一个PO多保函)导出', 'AISMC-purchaseOrderGuarantee-export', 'AISMC-purchaseOrderGuarantee-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:export'),
('PO-保函关联表(一个PO多保函)审计日志', 'AISMC-purchaseOrderGuarantee-auditLog', 'AISMC-purchaseOrderGuarantee-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:purchaseOrderGuarantee:auditLog');


select * from app_portal_sit.resource_info where resourceTag like 'AISMC-purchaseOrderGuarantee%';
