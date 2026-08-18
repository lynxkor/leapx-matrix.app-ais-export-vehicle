-- 生成 红旗PO订单 页面资源权限

SET @newParentId = (SELECT IFNULL(resourceId, 0) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);
SET @parentPath = (SELECT concat(path,',', resourceId) FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc LIMIT 1);
set @newParentGrade = (SELECT grade+1 FROM app_portal_sit.resource_info WHERE resourceTag = 'AISEX00001003' order by resourceId desc  LIMIT 1);


-- 1. 先插入父级目录
INSERT INTO app_portal_sit.resource_info
(resourceUri, resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('/export/vehicle/exportVehicleCbuPo','PO订单', 'AISEV-exportVehicleCbuPo', 'AISEV-exportVehicleCbuPo', '0', '1', '0', '0', @parentPath, @newParentId, @newParentGrade, 'web:po:exportVehicleCbuPo:page');

-- 2. 保存父级 ID
SET @pid = (SELECT IFNULL(resourceId, 0) FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPo'  LIMIT 1);
SET @newPath = (SELECT concat(path,',', resourceId) FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPo' LIMIT 1);
set @grade = (SELECT grade+1 FROM resource_info WHERE resourceTag = 'AISEV-exportVehicleCbuPo' LIMIT 1);


-- 3. 插入子级按钮
INSERT INTO app_portal_sit.resource_info
(resourceName, resourceTag, resourceDesc, enabled, resourceType, resourceCate, showStatus, `path`, parentId, grade, permission)
VALUES
('PO订单分页', 'AISEV-exportVehicleCbuPo-page', 'AISEV-exportVehicleCbuPo-page', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:page'),
('PO订单详情', 'AISEV-exportVehicleCbuPo-detail', 'AISEV-exportVehicleCbuPo-detail', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:get'),
('PO订单联查详情', 'AISEV-exportVehicleCbuPo-detailFull', 'AISEV-exportVehicleCbuPo-detailFull', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:detail'),
('PO订单编辑保存', 'AISEV-exportVehicleCbuPo-save', 'AISEV-exportVehicleCbuPo-save', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:save'),
('PO订单删除', 'AISEV-exportVehicleCbuPo-delete', 'AISEV-exportVehicleCbuPo-delete', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:delete'),
('PO订单启用/禁用', 'AISEV-exportVehicleCbuPo-enableOrDisable', 'AISEV-exportVehicleCbuPo-enableOrDisable', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:enableOrDisable'),
('PO订单导出', 'AISEV-exportVehicleCbuPo-export', 'AISEV-exportVehicleCbuPo-export', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:export'),
('PO订单审计日志', 'AISEV-exportVehicleCbuPo-auditLog', 'AISEV-exportVehicleCbuPo-auditLog', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:auditLog'),
('PO订单销售提交', 'AISEV-exportVehicleCbuPo-submit', 'AISEV-exportVehicleCbuPo-submit', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:submit'),
('PO订单销售驳回', 'AISEV-exportVehicleCbuPo-reject', 'AISEV-exportVehicleCbuPo-reject', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:reject'),
('PO订单供需审核通过', 'AISEV-exportVehicleCbuPo-supplyApprove', 'AISEV-exportVehicleCbuPo-supplyApprove', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:supplyApprove'),
('PO订单供需审核驳回', 'AISEV-exportVehicleCbuPo-supplyReject', 'AISEV-exportVehicleCbuPo-supplyReject', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:supplyReject'),
('PO订单产销平衡通过', 'AISEV-exportVehicleCbuPo-balanceApprove', 'AISEV-exportVehicleCbuPo-balanceApprove', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:balanceApprove'),
('PO订单产销平衡驳回', 'AISEV-exportVehicleCbuPo-balanceReject', 'AISEV-exportVehicleCbuPo-balanceReject', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:balanceReject'),
('PO订单刷新价格', 'AISEV-exportVehicleCbuPo-refreshPrice', 'AISEV-exportVehicleCbuPo-refreshPrice', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:refreshPrice'),
('PO订单批量提交', 'AISEV-exportVehicleCbuPo-batchSubmit', 'AISEV-exportVehicleCbuPo-batchSubmit', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchSubmit'),
('PO订单批量销售驳回', 'AISEV-exportVehicleCbuPo-batchReject', 'AISEV-exportVehicleCbuPo-batchReject', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchReject'),
('PO订单批量供需通过', 'AISEV-exportVehicleCbuPo-batchSupplyApprove', 'AISEV-exportVehicleCbuPo-batchSupplyApprove', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchSupplyApprove'),
('PO订单批量供需驳回', 'AISEV-exportVehicleCbuPo-batchSupplyReject', 'AISEV-exportVehicleCbuPo-batchSupplyReject', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchSupplyReject'),
('PO订单批量产销通过', 'AISEV-exportVehicleCbuPo-batchBalanceApprove', 'AISEV-exportVehicleCbuPo-batchBalanceApprove', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchBalanceApprove'),
('PO订单批量产销驳回', 'AISEV-exportVehicleCbuPo-batchBalanceReject', 'AISEV-exportVehicleCbuPo-batchBalanceReject', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchBalanceReject'),
('PO订单单条补推', 'AISEV-exportVehicleCbuPo-resend', 'AISEV-exportVehicleCbuPo-resend', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:resend'),
('PO订单批量补推', 'AISEV-exportVehicleCbuPo-batchResend', 'AISEV-exportVehicleCbuPo-batchResend', '0', '1', '2', '0', @newPath, @pid, 5, 'web:po:exportVehicleCbuPo:batchResend');


select * from app_portal_sit.resource_info where resourceTag like 'AISEV-exportVehicleCbuPo%';
