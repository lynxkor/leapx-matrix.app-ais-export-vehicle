-- 资源菜单迁移脚本（特殊车辆销售）
-- 说明：378 销售管理已存在，不迁移；其余菜单按层级插入，resourceId 由目标库自增生成
-- path 规则：当前节点 path = 父节点 path + ',' + 父节点 ID；父节点为销售管理时为 @sales_id
SET @sales_id = (SELECT resourceId FROM resource_info WHERE resourceName = '销售管理' AND resourceTag = 'M10183' LIMIT 1);

START TRANSACTION;

-- 特殊车辆销售 (AISEX00001)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('特殊车辆销售', NULL, NULL, 'AISEX00001', '特殊车辆销售', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '1', '0', @sales_id, @sales_id, 2, 9, 0, '', 1, '', 1, '', NULL, NULL, NULL, NULL, '', 1);
SET @id_AISEX00001 = LAST_INSERT_ID();

-- 主数据管理 (AISEX00001001)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('主数据管理', NULL, NULL, 'AISEX00001001', '主数据管理', NULL, '2026-07-10 08:32:51', NULL, '0', '1', '1', '0', CONCAT(@sales_id, ',', @id_AISEX00001), @id_AISEX00001, 3, 1, 0, '', 1, '', 1, '', NULL, NULL, NULL, NULL, '', 1);
SET @id_AISEX00001001 = LAST_INSERT_ID();

-- 资金管理 (AISEX00001002)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('资金管理', NULL, NULL, 'AISEX00001002', '资金管理', NULL, '2026-07-10 08:33:30', NULL, '0', '1', '1', '0', CONCAT(@sales_id, ',', @id_AISEX00001), @id_AISEX00001, 3, 2, 0, '', 1, '', 1, '', NULL, NULL, NULL, NULL, NULL, 1);
SET @id_AISEX00001002 = LAST_INSERT_ID();

-- 订单管理 (AISEX00001003)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('订单管理', NULL, NULL, 'AISEX00001003', '订单管理', NULL, '2026-07-10 08:34:16', NULL, '0', '1', '1', '0', CONCAT(@sales_id, ',', @id_AISEX00001), @id_AISEX00001, 3, 3, 0, '', 1, '', 1, '', NULL, NULL, NULL, NULL, NULL, 1);
SET @id_AISEX00001003 = LAST_INSERT_ID();

-- 保函管理 (AISEP-exportVehicleLetterGuarantee)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函管理', '/export/vehicle/exportVehicleLetterGuarantee', NULL, 'AISEP-exportVehicleLetterGuarantee', 'AISEP-exportVehicleLetterGuarantee', NULL, '2026-07-10 10:05:11', '2026-07-13 03:13:05', '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), @id_AISEX00001002, 3, 1, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee = LAST_INSERT_ID();

-- 保函交易明细 (AISEP-exportVehicleLetterGuaranteeRecord)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细', '/export/vehicle/exportVehicleLetterGuaranteeRecord', NULL, 'AISEP-exportVehicleLetterGuaranteeRecord', 'AISEP-exportVehicleLetterGuaranteeRecord', NULL, '2026-07-10 10:08:41', '2026-07-13 03:13:19', '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), @id_AISEX00001002, 3, 2, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord = LAST_INSERT_ID();

-- 客户信息 (AIS-export-nexus-customer)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('客户信息', '/export/nexus/customer', NULL, 'AIS-export-nexus-customer', 'AIS-export-nexus-customer', NULL, '2026-07-10 09:27:19', '2026-07-13 01:46:33', '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), @id_AISEX00001001, 4, 1, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer', '', 1);
SET @id_AISEX_customer = LAST_INSERT_ID();

-- 保函主表列表 (AISEP-exportVehicleLetterGuarantee-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表列表', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-list', 'AISEP-exportVehicleLetterGuarantee-list', NULL, '2026-07-10 10:05:11', '2026-07-13 06:12:33', '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:list', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_list = LAST_INSERT_ID();

-- 保函主表分页 (AISEP-exportVehicleLetterGuarantee-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表分页', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-page', 'AISEP-exportVehicleLetterGuarantee-page', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:page', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_page = LAST_INSERT_ID();

-- 保函主表详情 (AISEP-exportVehicleLetterGuarantee-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表详情', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-detail', 'AISEP-exportVehicleLetterGuarantee-detail', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:get', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_detail = LAST_INSERT_ID();

-- 保函交易明细列表 (AISEP-exportVehicleLetterGuaranteeRecord-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细列表', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-list', 'AISEP-exportVehicleLetterGuaranteeRecord-list', NULL, '2026-07-10 10:08:41', '2026-07-13 06:13:23', '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:list', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_list = LAST_INSERT_ID();

-- 保函交易明细分页 (AISEP-exportVehicleLetterGuaranteeRecord-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细分页', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-page', 'AISEP-exportVehicleLetterGuaranteeRecord-page', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:page', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_page = LAST_INSERT_ID();

-- 保函交易明细详情 (AISEP-exportVehicleLetterGuaranteeRecord-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细详情', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-detail', 'AISEP-exportVehicleLetterGuaranteeRecord-detail', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:get', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_detail = LAST_INSERT_ID();

-- 销售公司 (AIS-export-nexus-company)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('销售公司', '/export/nexus/company', NULL, 'AIS-export-nexus-company', 'AIS-export-nexus-company', NULL, '2026-07-13 03:18:55', '2026-07-16 02:17:40', '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), @id_AISEX00001001, 4, 2, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company', NULL, NULL);
SET @id_AISEX_company = LAST_INSERT_ID();

-- 物料主数据 (AISVS-vehicleMaterial)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('物料主数据', '/export/nexus/vehicleMaterial', NULL, 'AISVS-vehicleMaterial', 'AISVS-vehicleMaterial', NULL, '2026-07-13 05:13:35', '2026-07-13 05:16:22', '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), @id_AISEX00001001, 4, 3, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial', '', 1);
SET @id_AISEX_vehicleMaterial = LAST_INSERT_ID();

-- 应付清单管理 (AISEX-payableListManagement)
-- INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
-- VALUES ('应付清单管理', NULL, NULL, 'AISEX-payableListManagement', '应付清单管理', NULL, '2026-07-13 05:32:39', NULL, '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), @id_AISEX00001003, 4, 0, 0, '', 1, '', 1, '', NULL, NULL, NULL, NULL, '', NULL);
-- SET @id_AISEX_payableListManagement = LAST_INSERT_ID();

-- 应付清单明细 (AISEX-payableListDetail)
-- INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
-- VALUES ('应付清单明细', NULL, NULL, 'AISEX-payableListDetail', '应付清单明细', NULL, '2026-07-13 05:36:02', NULL, '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), @id_AISEX00001003, 4, 0, 0, '', 1, '', 1, '', NULL, NULL, NULL, NULL, NULL, NULL);
-- SET @id_AISEX_payableListDetail = LAST_INSERT_ID();

-- 价格主数据 (AISVS-vehiclePriceHq)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格主数据', '/export/nexus/vehiclePriceHq', NULL, 'AISVS-vehiclePriceHq', 'AISVS-vehiclePriceHq', NULL, '2026-07-13 06:57:44', NULL, '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), @id_AISEX00001001, 4, 4, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq', '', 1);
SET @id_AISEX_vehiclePriceHq = LAST_INSERT_ID();

-- SO订单 (AISEV-vehicleCbuSo)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单', '/export/vehicle/vehicleCbuSo', NULL, 'AISEV-vehicleCbuSo', 'AISEV-vehicleCbuSo', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), @id_AISEX00001003, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:page', NULL, NULL);
SET @id_AISEV_vehicleCbuSo = LAST_INSERT_ID();

-- PO订单管理 (AISEV-exportVehicleCbuPo)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单管理', '/export/vehicle/exportVehicleCbuPo', NULL, 'AISEV-exportVehicleCbuPo', 'AISEV-exportVehicleCbuPo', NULL, '2026-07-20 02:41:37', '2026-07-20 02:54:29', '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), @id_AISEX00001003, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:page', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo = LAST_INSERT_ID();

-- PO订单明细 (AISEV-exportVehicleCbuPoDetail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细', '/export/vehicle/exportVehicleCbuPoDetail', NULL, 'AISEV-exportVehicleCbuPoDetail', 'AISEV-exportVehicleCbuPoDetail', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '0', '0', CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), @id_AISEX00001003, 4, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:page', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail = LAST_INSERT_ID();

-- 详情 (AIS-export-nexus-customer-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('详情', NULL, NULL, 'AIS-export-nexus-customer-detail', 'AIS-export-nexus-customer-detail', NULL, '2026-07-10 09:30:13', '2026-07-13 03:31:41', '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 1, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:get', NULL, NULL);
SET @id_AISEX_customer_detail = LAST_INSERT_ID();

-- 客户列表 (AIS-export-nexus-customer-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('客户列表', NULL, NULL, 'AIS-export-nexus-customer-list', 'AIS-export-nexus-customer-list', NULL, '2026-07-10 09:32:32', '2026-07-13 02:06:01', '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 0, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:list', NULL, NULL);
SET @id_AISEX_customer_list = LAST_INSERT_ID();

-- 保函主表新增/编辑 (AISEP-exportVehicleLetterGuarantee-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表新增/编辑', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-addOrUpdate', 'AISEP-exportVehicleLetterGuarantee-addOrUpdate', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:addOrUpdate', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_addOrUpdate = LAST_INSERT_ID();

-- 保函主表删除 (AISEP-exportVehicleLetterGuarantee-delete)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表删除', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-delete', 'AISEP-exportVehicleLetterGuarantee-delete', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:delete', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_delete = LAST_INSERT_ID();

-- 保函主表启用/禁用 (AISEP-exportVehicleLetterGuarantee-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表启用/禁用', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-enableOrDisable', 'AISEP-exportVehicleLetterGuarantee-enableOrDisable', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:enableOrDisable', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_enableOrDisable = LAST_INSERT_ID();

-- 保函主表导入 (AISEP-exportVehicleLetterGuarantee-import)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表导入', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-import', 'AISEP-exportVehicleLetterGuarantee-import', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:import', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_import = LAST_INSERT_ID();

-- 保函主表导出 (AISEP-exportVehicleLetterGuarantee-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表导出', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-export', 'AISEP-exportVehicleLetterGuarantee-export', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:export', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_export = LAST_INSERT_ID();

-- 保函主表批量提交 (AISEP-exportVehicleLetterGuarantee-batchSubmit)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表批量提交', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-batchSubmit', 'AISEP-exportVehicleLetterGuarantee-batchSubmit', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:submit', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_batchSubmit = LAST_INSERT_ID();

-- 保函主表批量审核 (AISEP-exportVehicleLetterGuarantee-batchApprove)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表批量审核', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-batchApprove', 'AISEP-exportVehicleLetterGuarantee-batchApprove', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:approve', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_batchApprove = LAST_INSERT_ID();

-- 保函主表批量作废 (AISEP-exportVehicleLetterGuarantee-batchCancel)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表批量作废', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-batchCancel', 'AISEP-exportVehicleLetterGuarantee-batchCancel', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:cancel', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_batchCancel = LAST_INSERT_ID();


-- 保函主表提前失效 (AISEP-exportVehicleLetterGuarantee-expireEarly)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表提前失效', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-expireEarly', 'AISEP-exportVehicleLetterGuarantee-expireEarly', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:expireEarly', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_expireEarly = LAST_INSERT_ID();


-- 保函主表审计日志 (AISEP-exportVehicleLetterGuarantee-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函主表审计日志', NULL, NULL, 'AISEP-exportVehicleLetterGuarantee-auditLog', 'AISEP-exportVehicleLetterGuarantee-auditLog', NULL, '2026-07-10 10:05:11', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuarantee), @id_AISEP_exportVehicleLetterGuarantee, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuarantee:auditLog', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuarantee_auditLog = LAST_INSERT_ID();

-- 保函交易明细新增/编辑 (AISEP-exportVehicleLetterGuaranteeRecord-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细新增/编辑', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-addOrUpdate', 'AISEP-exportVehicleLetterGuaranteeRecord-addOrUpdate', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:addOrUpdate', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_addOrUpdate = LAST_INSERT_ID();

-- 保函交易明细删除 (AISEP-exportVehicleLetterGuaranteeRecord-delete)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细删除', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-delete', 'AISEP-exportVehicleLetterGuaranteeRecord-delete', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:delete', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_delete = LAST_INSERT_ID();

-- 保函交易明细启用/禁用 (AISEP-exportVehicleLetterGuaranteeRecord-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细启用/禁用', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-enableOrDisable', 'AISEP-exportVehicleLetterGuaranteeRecord-enableOrDisable', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:enableOrDisable', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_enableOrDisable = LAST_INSERT_ID();

-- 保函交易明细导入 (AISEP-exportVehicleLetterGuaranteeRecord-import)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细导入', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-import', 'AISEP-exportVehicleLetterGuaranteeRecord-import', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:import', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_import = LAST_INSERT_ID();

-- 保函交易明细导出 (AISEP-exportVehicleLetterGuaranteeRecord-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细导出', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-export', 'AISEP-exportVehicleLetterGuaranteeRecord-export', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:export', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_export = LAST_INSERT_ID();

-- 保函交易明细审计日志 (AISEP-exportVehicleLetterGuaranteeRecord-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('保函交易明细审计日志', NULL, NULL, 'AISEP-exportVehicleLetterGuaranteeRecord-auditLog', 'AISEP-exportVehicleLetterGuaranteeRecord-auditLog', NULL, '2026-07-10 10:08:41', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001002), ',', @id_AISEP_exportVehicleLetterGuaranteeRecord), @id_AISEP_exportVehicleLetterGuaranteeRecord, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:basic:exportVehicleLetterGuaranteeRecord:auditLog', NULL, 1);
SET @id_AISEP_exportVehicleLetterGuaranteeRecord_auditLog = LAST_INSERT_ID();

-- 分页 (AIS-export-nexus-customer-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('分页', NULL, NULL, 'AIS-export-nexus-customer-page', 'AIS-export-nexus-customer-page', NULL, '2026-07-13 03:13:21', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 2, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:page', '', NULL);
SET @id_AISEX_customer_page = LAST_INSERT_ID();

-- 新增/编辑 (AIS-export-nexus-customer-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('新增/编辑', NULL, NULL, 'AIS-export-nexus-customer-addOrUpdate', 'AIS-export-nexus-customer-addOrUpdate', NULL, '2026-07-13 03:15:06', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 3, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:addOrUpdate', NULL, NULL);
SET @id_AISEX_customer_addOrUpdate = LAST_INSERT_ID();

-- 启用/禁用 (AIS-export-nexus-customer-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('启用/禁用', NULL, NULL, 'AIS-export-nexus-customer-enableOrDisable', 'AIS-export-nexus-customer-enableOrDisable', NULL, '2026-07-13 03:16:07', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 4, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:enableOrDisable', NULL, NULL);
SET @id_AISEX_customer_enableOrDisable = LAST_INSERT_ID();

-- 新增/编辑 (AIS-export-nexus-company-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('新增/编辑', NULL, NULL, 'AIS-export-nexus-company-addOrUpdate', 'web:nexus:company:addOrUpdate', NULL, '2026-07-13 03:31:17', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 3, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:addOrUpdate', NULL, NULL);
SET @id_AISEX_company_addOrUpdate = LAST_INSERT_ID();

-- 详情 (AIS-export-nexus-company-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('详情', NULL, NULL, 'AIS-export-nexus-company-detail', 'AIS-export-nexus-company-detail', NULL, '2026-07-13 03:32:40', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 1, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:get', NULL, NULL);
SET @id_AISEX_company_detail = LAST_INSERT_ID();

-- 分页 (AIS-export-nexus-company-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('分页', NULL, NULL, 'AIS-export-nexus-company-page', 'AIS-export-nexus-company-page', NULL, '2026-07-13 03:33:38', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 2, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:page', NULL, NULL);
SET @id_AISEX_company_page = LAST_INSERT_ID();

-- 公司列表 (AIS-export-nexus-company-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('公司列表', NULL, NULL, 'AIS-export-nexus-company-list', 'AIS-export-nexus-company-list', NULL, '2026-07-13 03:34:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 0, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:list', NULL, NULL);
SET @id_AISEX_company_list = LAST_INSERT_ID();

-- 启用/禁用 (AIS-export-nexus-company-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('启用/禁用', NULL, NULL, 'AIS-export-nexus-company-enableOrDisable', 'AIS-export-nexus-company-enableOrDisable', NULL, '2026-07-13 03:35:27', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 4, 0, '', 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:enableOrDisable', NULL, NULL);
SET @id_AISEX_company_enableOrDisable = LAST_INSERT_ID();

-- SO订单分页 (AISEV-vehicleCbuSo-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单分页', NULL, NULL, 'AISEV-vehicleCbuSo-page', 'AISEV-vehicleCbuSo-page', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:page', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_page = LAST_INSERT_ID();

-- SO订单详情 (AISEV-vehicleCbuSo-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单详情', NULL, NULL, 'AISEV-vehicleCbuSo-detail', 'AISEV-vehicleCbuSo-detail', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:get', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_detail = LAST_INSERT_ID();

-- SO订单新增/编辑 (AISEV-vehicleCbuSo-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单新增/编辑', NULL, NULL, 'AISEV-vehicleCbuSo-addOrUpdate', 'AISEV-vehicleCbuSo-addOrUpdate', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:addOrUpdate', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_addOrUpdate = LAST_INSERT_ID();

-- SO订单删除 (AISEV-vehicleCbuSo-delete)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单删除', NULL, NULL, 'AISEV-vehicleCbuSo-delete', 'AISEV-vehicleCbuSo-delete', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:delete', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_delete = LAST_INSERT_ID();

-- SO订单导出 (AISEV-vehicleCbuSo-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单导出', NULL, NULL, 'AISEV-vehicleCbuSo-export', 'AISEV-vehicleCbuSo-export', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:export', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_export = LAST_INSERT_ID();

-- SO订单审计日志 (AISEV-vehicleCbuSo-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单审计日志', NULL, NULL, 'AISEV-vehicleCbuSo-auditLog', 'AISEV-vehicleCbuSo-auditLog', NULL, '2026-07-18 06:23:30', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:auditLog', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_auditLog = LAST_INSERT_ID();

-- SO订单批量下发生产 (AISEV-vehicleCbuSo-issueToProduction)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单批量下发生产', NULL, NULL, 'AISEV-vehicleCbuSo-issueToProduction', 'AISEV-vehicleCbuSo-issueToProduction', NULL, '2026-07-18 06:32:59', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:issueToProduction', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_issueToProduction = LAST_INSERT_ID();

-- SO订单同步SAP (AISEV-vehicleCbuSo-reissueSap)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('SO订单同步SAP', NULL, NULL, 'AISEV-vehicleCbuSo-reissueSap', 'AISEV-vehicleCbuSo-reissueSap', NULL, '2026-07-18 06:32:59', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_vehicleCbuSo), @id_AISEV_vehicleCbuSo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:so:vehicleCbuSo:reissueSap', NULL, NULL);
SET @id_AISEV_vehicleCbuSo_reissueSap = LAST_INSERT_ID();

-- PO订单分页 (AISEV-exportVehicleCbuPo-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单分页', NULL, NULL, 'AISEV-exportVehicleCbuPo-page', 'AISEV-exportVehicleCbuPo-page', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:page', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_page = LAST_INSERT_ID();

-- PO订单详情 (AISEV-exportVehicleCbuPo-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单详情', NULL, NULL, 'AISEV-exportVehicleCbuPo-detail', 'AISEV-exportVehicleCbuPo-detail', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:get', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_detail = LAST_INSERT_ID();

-- PO订单联查详情 (AISEV-exportVehicleCbuPo-detailFull)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单联查详情', NULL, NULL, 'AISEV-exportVehicleCbuPo-detailFull', 'AISEV-exportVehicleCbuPo-detailFull', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:detail', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_detailFull = LAST_INSERT_ID();

-- PO订单编辑保存 (AISEV-exportVehicleCbuPo-save)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单编辑保存', NULL, NULL, 'AISEV-exportVehicleCbuPo-save', 'AISEV-exportVehicleCbuPo-save', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:save', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_save = LAST_INSERT_ID();

-- PO订单删除 (AISEV-exportVehicleCbuPo-delete)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单删除', NULL, NULL, 'AISEV-exportVehicleCbuPo-delete', 'AISEV-exportVehicleCbuPo-delete', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:delete', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_delete = LAST_INSERT_ID();

-- PO订单启用/禁用 (AISEV-exportVehicleCbuPo-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单启用/禁用', NULL, NULL, 'AISEV-exportVehicleCbuPo-enableOrDisable', 'AISEV-exportVehicleCbuPo-enableOrDisable', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:enableOrDisable', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_enableOrDisable = LAST_INSERT_ID();

-- PO订单导出 (AISEV-exportVehicleCbuPo-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单导出', NULL, NULL, 'AISEV-exportVehicleCbuPo-export', 'AISEV-exportVehicleCbuPo-export', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:export', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_export = LAST_INSERT_ID();

-- PO订单审计日志 (AISEV-exportVehicleCbuPo-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单审计日志', NULL, NULL, 'AISEV-exportVehicleCbuPo-auditLog', 'AISEV-exportVehicleCbuPo-auditLog', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:auditLog', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_auditLog = LAST_INSERT_ID();

-- PO订单销售提交 (AISEV-exportVehicleCbuPo-submit)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单销售提交', NULL, NULL, 'AISEV-exportVehicleCbuPo-submit', 'AISEV-exportVehicleCbuPo-submit', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:submit', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_submit = LAST_INSERT_ID();

-- PO订单销售驳回 (AISEV-exportVehicleCbuPo-reject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单销售驳回', NULL, NULL, 'AISEV-exportVehicleCbuPo-reject', 'AISEV-exportVehicleCbuPo-reject', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:reject', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_reject = LAST_INSERT_ID();

-- PO订单供需审核通过 (AISEV-exportVehicleCbuPo-supplyApprove)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单供需审核通过', NULL, NULL, 'AISEV-exportVehicleCbuPo-supplyApprove', 'AISEV-exportVehicleCbuPo-supplyApprove', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:supplyApprove', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_supplyApprove = LAST_INSERT_ID();

-- PO订单供需审核驳回 (AISEV-exportVehicleCbuPo-supplyReject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单供需审核驳回', NULL, NULL, 'AISEV-exportVehicleCbuPo-supplyReject', 'AISEV-exportVehicleCbuPo-supplyReject', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:supplyReject', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_supplyReject = LAST_INSERT_ID();

-- PO订单产销平衡通过 (AISEV-exportVehicleCbuPo-balanceApprove)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单产销平衡通过', NULL, NULL, 'AISEV-exportVehicleCbuPo-balanceApprove', 'AISEV-exportVehicleCbuPo-balanceApprove', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:balanceApprove', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_balanceApprove = LAST_INSERT_ID();

-- PO订单产销平衡驳回 (AISEV-exportVehicleCbuPo-balanceReject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单产销平衡驳回', NULL, NULL, 'AISEV-exportVehicleCbuPo-balanceReject', 'AISEV-exportVehicleCbuPo-balanceReject', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:balanceReject', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_balanceReject = LAST_INSERT_ID();

-- PO订单刷新价格 (AISEV-exportVehicleCbuPo-refreshPrice)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单刷新价格', NULL, NULL, 'AISEV-exportVehicleCbuPo-refreshPrice', 'AISEV-exportVehicleCbuPo-refreshPrice', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:refreshPrice', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_refreshPrice = LAST_INSERT_ID();

-- PO订单批量提交 (AISEV-exportVehicleCbuPo-batchSubmit)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量提交', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchSubmit', 'AISEV-exportVehicleCbuPo-batchSubmit', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchSubmit', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchSubmit = LAST_INSERT_ID();

-- PO订单批量销售驳回 (AISEV-exportVehicleCbuPo-batchReject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量销售驳回', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchReject', 'AISEV-exportVehicleCbuPo-batchReject', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchReject', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchReject = LAST_INSERT_ID();

-- PO订单批量供需通过 (AISEV-exportVehicleCbuPo-batchSupplyApprove)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量供需通过', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchSupplyApprove', 'AISEV-exportVehicleCbuPo-batchSupplyApprove', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchSupplyApprove', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchSupplyApprove = LAST_INSERT_ID();

-- PO订单批量供需驳回 (AISEV-exportVehicleCbuPo-batchSupplyReject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量供需驳回', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchSupplyReject', 'AISEV-exportVehicleCbuPo-batchSupplyReject', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchSupplyReject', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchSupplyReject = LAST_INSERT_ID();

-- PO订单批量产销通过 (AISEV-exportVehicleCbuPo-batchBalanceApprove)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量产销通过', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchBalanceApprove', 'AISEV-exportVehicleCbuPo-batchBalanceApprove', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchBalanceApprove', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchBalanceApprove = LAST_INSERT_ID();

-- PO订单批量产销驳回 (AISEV-exportVehicleCbuPo-batchBalanceReject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量产销驳回', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchBalanceReject', 'AISEV-exportVehicleCbuPo-batchBalanceReject', NULL, '2026-07-20 02:41:37', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchBalanceReject', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchBalanceReject = LAST_INSERT_ID();

-- PO订单明细分页 (AISEV-exportVehicleCbuPoDetail-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细分页', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-page', 'AISEV-exportVehicleCbuPoDetail-page', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:page', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_page = LAST_INSERT_ID();

-- PO订单明细列表 (AISEV-exportVehicleCbuPoDetail-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细列表', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-list', 'AISEV-exportVehicleCbuPoDetail-list', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:list', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_list = LAST_INSERT_ID();

-- PO订单明细详情 (AISEV-exportVehicleCbuPoDetail-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细详情', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-detail', 'AISEV-exportVehicleCbuPoDetail-detail', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:get', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_detail = LAST_INSERT_ID();

-- PO订单明细新增/编辑 (AISEV-exportVehicleCbuPoDetail-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细新增/编辑', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-addOrUpdate', 'AISEV-exportVehicleCbuPoDetail-addOrUpdate', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:addOrUpdate', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_addOrUpdate = LAST_INSERT_ID();

-- PO订单明细删除 (AISEV-exportVehicleCbuPoDetail-delete)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细删除', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-delete', 'AISEV-exportVehicleCbuPoDetail-delete', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:delete', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_delete = LAST_INSERT_ID();

-- PO订单明细启用/禁用 (AISEV-exportVehicleCbuPoDetail-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细启用/禁用', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-enableOrDisable', 'AISEV-exportVehicleCbuPoDetail-enableOrDisable', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:enableOrDisable', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_enableOrDisable = LAST_INSERT_ID();

-- PO订单明细导出 (AISEV-exportVehicleCbuPoDetail-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细导出', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-export', 'AISEV-exportVehicleCbuPoDetail-export', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:export', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_export = LAST_INSERT_ID();

-- PO订单明细审计日志 (AISEV-exportVehicleCbuPoDetail-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单明细审计日志', NULL, NULL, 'AISEV-exportVehicleCbuPoDetail-auditLog', 'AISEV-exportVehicleCbuPoDetail-auditLog', NULL, '2026-07-20 02:42:14', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPoDetail), @id_AISEV_exportVehicleCbuPoDetail, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPoDetail:auditLog', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPoDetail_auditLog = LAST_INSERT_ID();

-- PO订单单条补推 (AISEV-exportVehicleCbuPo-resend)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单单条补推', NULL, NULL, 'AISEV-exportVehicleCbuPo-resend', 'AISEV-exportVehicleCbuPo-resend', NULL, '2026-07-31 05:20:08', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:resend', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_resend = LAST_INSERT_ID();

-- PO订单批量补推 (AISEV-exportVehicleCbuPo-batchResend)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('PO订单批量补推', NULL, NULL, 'AISEV-exportVehicleCbuPo-batchResend', 'AISEV-exportVehicleCbuPo-batchResend', NULL, '2026-07-31 05:20:08', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001003), ',', @id_AISEV_exportVehicleCbuPo), @id_AISEV_exportVehicleCbuPo, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:po:exportVehicleCbuPo:batchResend', NULL, NULL);
SET @id_AISEV_exportVehicleCbuPo_batchResend = LAST_INSERT_ID();

-- ============================================================
-- 补全资源按钮
-- 1. 客户信息导出 / 销售公司导出
-- 2. 物料主数据子按钮（列表/详情/分页/新增/编辑/启用禁用/导出）
-- 3. 价格主数据子按钮（列表/详情/分页/新增/编辑/启用禁用/导出/导入）
-- ============================================================

-- 客户信息导出 (AIS-export-nexus-customer-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('客户信息导出', NULL, NULL, 'AIS-export-nexus-customer-export', 'AIS-export-nexus-customer-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:export', NULL, NULL);
SET @id_AIS_export_nexus_customer_export = LAST_INSERT_ID();

-- 审计日志 (AIS-export-nexus-customer-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('审计日志', NULL, NULL, 'AIS-export-nexus-customer-auditLog', 'AIS-export-nexus-customer-auditLog', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_customer), @id_AISEX_customer, 5, 6, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:customer:auditLog', NULL, NULL);
SET @id_AIS_export_nexus_customer_auditLog = LAST_INSERT_ID();

-- 销售公司导出 (AIS-export-nexus-company-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('销售公司导出', NULL, NULL, 'AIS-export-nexus-company-export', 'AIS-export-nexus-company-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:export', NULL, NULL);
SET @id_AIS_export_nexus_company_export = LAST_INSERT_ID();

-- 审计日志 (AIS-export-nexus-company-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('审计日志', NULL, NULL, 'AIS-export-nexus-company-auditLog', 'AIS-export-nexus-company-auditLog', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 6, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:auditLog', NULL, NULL);
SET @id_AIS_export_nexus_company_auditLog = LAST_INSERT_ID();

-- 物料列表 (AISVS-vehicleMaterial-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('物料列表', NULL, NULL, 'AISVS-vehicleMaterial-list', 'AISVS-vehicleMaterial-list', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:list', NULL, NULL);
SET @id_AISEX_vehicleMaterial_list = LAST_INSERT_ID();

-- 详情 (AISVS-vehicleMaterial-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('详情', NULL, NULL, 'AISVS-vehicleMaterial-detail', 'AISVS-vehicleMaterial-detail', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 1, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:get', NULL, NULL);
SET @id_AISEX_vehicleMaterial_detail = LAST_INSERT_ID();

-- 分页 (AISVS-vehicleMaterial-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('分页', NULL, NULL, 'AISVS-vehicleMaterial-page', 'AISVS-vehicleMaterial-page', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 2, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:page', NULL, NULL);
SET @id_AISEX_vehicleMaterial_page = LAST_INSERT_ID();

-- 新增/编辑 (AISVS-vehicleMaterial-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('新增/编辑', NULL, NULL, 'AISVS-vehicleMaterial-addOrUpdate', 'AISVS-vehicleMaterial-addOrUpdate', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 3, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:addOrUpdate', NULL, NULL);
SET @id_AISEX_vehicleMaterial_addOrUpdate = LAST_INSERT_ID();

-- 启用/禁用 (AISVS-vehicleMaterial-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('启用/禁用', NULL, NULL, 'AISVS-vehicleMaterial-enableOrDisable', 'AISVS-vehicleMaterial-enableOrDisable', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 4, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:enableOrDisable', NULL, NULL);
SET @id_AISEX_vehicleMaterial_enableOrDisable = LAST_INSERT_ID();

-- 物料主数据导出 (AISVS-vehicleMaterial-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('物料主数据导出', NULL, NULL, 'AISVS-vehicleMaterial-export', 'AISVS-vehicleMaterial-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:export', NULL, NULL);
SET @id_AIS_export_nexus_vehicleMaterial_export = LAST_INSERT_ID();

-- 审计日志 (AISVS-vehicleMaterial--auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('审计日志', NULL, NULL, 'AISVS-vehicleMaterial--auditLog', 'AISVS-vehicleMaterial--auditLog', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 6, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:auditLog', NULL, NULL);
SET @id_AISVS_vehicleMaterial__auditLog = LAST_INSERT_ID();

-- 价格列表 (AISVS-vehiclePriceHq-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格列表', NULL, NULL, 'AISVS-vehiclePriceHq-list', 'AISVS-vehiclePriceHq-list', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:list', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_list = LAST_INSERT_ID();

-- 详情 (AISVS-vehiclePriceHq-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('详情', NULL, NULL, 'AISVS-vehiclePriceHq-detail', 'AISVS-vehiclePriceHq-detail', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 1, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:get', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_detail = LAST_INSERT_ID();

-- 分页 (AISVS-vehiclePriceHq-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('分页', NULL, NULL, 'AISVS-vehiclePriceHq-page', 'AISVS-vehiclePriceHq-page', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 2, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:page', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_page = LAST_INSERT_ID();

-- 新增/编辑 (AISVS-vehiclePriceHq-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('新增/编辑', NULL, NULL, 'AISVS-vehiclePriceHq-addOrUpdate', 'AISVS-vehiclePriceHq-addOrUpdate', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 3, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:addOrUpdate', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_addOrUpdate = LAST_INSERT_ID();

-- 启用/禁用 (AISVS-vehiclePriceHq-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('启用/禁用', NULL, NULL, 'AISVS-vehiclePriceHq-enableOrDisable', 'AISVS-vehiclePriceHq-enableOrDisable', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 4, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:enableOrDisable', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_enableOrDisable = LAST_INSERT_ID();

-- 价格主数据导出 (AISVS-vehiclePriceHq-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格主数据导出', NULL, NULL, 'AISVS-vehiclePriceHq-export', 'AISVS-vehiclePriceHq-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:export', NULL, NULL);
SET @id_AIS_export_nexus_vehiclePriceHq_export = LAST_INSERT_ID();

-- 价格主数据导入 (AISVS-vehiclePriceHq-import)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格主数据导入', NULL, NULL, 'AISVS-vehiclePriceHq-import', 'AISVS-vehiclePriceHq-import', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 6, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:import', NULL, NULL);
SET @id_AISEP_vehiclePriceHq_import = LAST_INSERT_ID();

-- 审计日志 (AISVS-vehiclePriceHq-auditLog)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('审计日志', NULL, NULL, 'AISVS-vehiclePriceHq-auditLog', 'AISVS-vehiclePriceHq-auditLog', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 7, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:auditLog', NULL, NULL);
SET @id_AISVS_vehiclePriceHq_auditLog = LAST_INSERT_ID();

-- 批量提交 (AISVS-vehiclePriceHq-submit)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('批量提交', NULL, NULL, 'AISVS-vehiclePriceHq-submit', 'AISVS-vehiclePriceHq-submit', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 8, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:submit', NULL, NULL);
SET @id_AISEP_vehiclePriceHq_submit = LAST_INSERT_ID();

-- 批量审批通过 (AISVS-vehiclePriceHq-auditPass)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('批量审批通过', NULL, NULL, 'AISVS-vehiclePriceHq-auditPass', 'AISVS-vehiclePriceHq-auditPass', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 9, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:auditPass', NULL, NULL);
SET @id_AISEP_vehiclePriceHq_auditPass = LAST_INSERT_ID();

-- 批量审批驳回 (AISVS-vehiclePriceHq-auditReject)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('批量审批驳回', NULL, NULL, 'AISVS-vehiclePriceHq-auditReject', 'AISVS-vehiclePriceHq-auditReject', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 10, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:auditReject', NULL, NULL);
SET @id_AISEP_vehiclePriceHq_auditReject = LAST_INSERT_ID();

-- 批量作废 (AISVS-vehiclePriceHq-batchCance)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('批量作废', NULL, NULL, 'AISVS-vehiclePriceHq-batchCance', 'AISVS-vehiclePriceHq-batchCance', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 11, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:batchCancel', NULL, NULL);
SET @id_AISEP_vehiclePriceHq_batchCance = LAST_INSERT_ID();

COMMIT;

-- 验证：检查 path 是否为空或 parentId 为 0 的异常记录
-- SELECT * FROM resource_info WHERE resourceTag LIKE 'AISEX%' OR resourceTag LIKE 'AISEP%' OR resourceTag LIKE 'AISEV%' AND (path = '' OR parentId = 0);

select * from resource_info where resourceTag like 'AISEX%' or resourceTag like 'AISEP%' or resourceTag like 'AISEV%';