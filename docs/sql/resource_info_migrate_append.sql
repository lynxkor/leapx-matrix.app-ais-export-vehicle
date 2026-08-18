
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

-- 销售公司导出 (AIS-export-nexus-company-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('销售公司导出', NULL, NULL, 'AIS-export-nexus-company-export', 'AIS-export-nexus-company-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_company), @id_AISEX_company, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:company:export', NULL, NULL);
SET @id_AIS_export_nexus_company_export = LAST_INSERT_ID();

-- 物料列表 (AISEX-vehicleMaterial-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('物料列表', NULL, NULL, 'AISEX-vehicleMaterial-list', 'AISEX-vehicleMaterial-list', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:list', NULL, NULL);
SET @id_AISEX_vehicleMaterial_list = LAST_INSERT_ID();

-- 详情 (AISEX-vehicleMaterial-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('详情', NULL, NULL, 'AISEX-vehicleMaterial-detail', 'AISEX-vehicleMaterial-detail', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 1, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:get', NULL, NULL);
SET @id_AISEX_vehicleMaterial_detail = LAST_INSERT_ID();

-- 分页 (AISEX-vehicleMaterial-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('分页', NULL, NULL, 'AISEX-vehicleMaterial-page', 'AISEX-vehicleMaterial-page', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 2, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:page', NULL, NULL);
SET @id_AISEX_vehicleMaterial_page = LAST_INSERT_ID();

-- 新增/编辑 (AISEX-vehicleMaterial-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('新增/编辑', NULL, NULL, 'AISEX-vehicleMaterial-addOrUpdate', 'AISEX-vehicleMaterial-addOrUpdate', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 3, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:addOrUpdate', NULL, NULL);
SET @id_AISEX_vehicleMaterial_addOrUpdate = LAST_INSERT_ID();

-- 启用/禁用 (AISEX-vehicleMaterial-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('启用/禁用', NULL, NULL, 'AISEX-vehicleMaterial-enableOrDisable', 'AISEX-vehicleMaterial-enableOrDisable', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 4, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:enableOrDisable', NULL, NULL);
SET @id_AISEX_vehicleMaterial_enableOrDisable = LAST_INSERT_ID();

-- 物料主数据导出 (AIS-export-nexus-vehicleMaterial-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('物料主数据导出', NULL, NULL, 'AIS-export-nexus-vehicleMaterial-export', 'AIS-export-nexus-vehicleMaterial-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehicleMaterial), @id_AISEX_vehicleMaterial, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehicleMaterial:export', NULL, NULL);
SET @id_AIS_export_nexus_vehicleMaterial_export = LAST_INSERT_ID();

-- 价格列表 (AISEX-vehiclePriceHq-list)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格列表', NULL, NULL, 'AISEX-vehiclePriceHq-list', 'AISEX-vehiclePriceHq-list', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 0, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:list', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_list = LAST_INSERT_ID();

-- 详情 (AISEX-vehiclePriceHq-detail)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('详情', NULL, NULL, 'AISEX-vehiclePriceHq-detail', 'AISEX-vehiclePriceHq-detail', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 1, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:get', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_detail = LAST_INSERT_ID();

-- 分页 (AISEX-vehiclePriceHq-page)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('分页', NULL, NULL, 'AISEX-vehiclePriceHq-page', 'AISEX-vehiclePriceHq-page', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 2, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:page', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_page = LAST_INSERT_ID();

-- 新增/编辑 (AISEX-vehiclePriceHq-addOrUpdate)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('新增/编辑', NULL, NULL, 'AISEX-vehiclePriceHq-addOrUpdate', 'AISEX-vehiclePriceHq-addOrUpdate', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 3, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:addOrUpdate', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_addOrUpdate = LAST_INSERT_ID();

-- 启用/禁用 (AISEX-vehiclePriceHq-enableOrDisable)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('启用/禁用', NULL, NULL, 'AISEX-vehiclePriceHq-enableOrDisable', 'AISEX-vehiclePriceHq-enableOrDisable', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 4, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:enableOrDisable', NULL, NULL);
SET @id_AISEX_vehiclePriceHq_enableOrDisable = LAST_INSERT_ID();

-- 价格主数据导出 (AIS-export-nexus-vehiclePriceHq-export)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格主数据导出', NULL, NULL, 'AIS-export-nexus-vehiclePriceHq-export', 'AIS-export-nexus-vehiclePriceHq-export', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 5, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:export', NULL, NULL);
SET @id_AIS_export_nexus_vehiclePriceHq_export = LAST_INSERT_ID();

-- 价格主数据导入 (AISEP-vehiclePriceHq-import)
INSERT INTO resource_info (resourceName, resourceUri, componentUri, resourceTag, resourceDesc, creator, createTime, updateTime, enabled, resourceType, resourceCate, showStatus, path, parentId, grade, sort, version, logo, redirectType, redirectApi, hasToken, redirectUrl, iosUrl, androidUrl, prompt, permission, lightLogo, systemTag)
VALUES ('价格主数据导入', NULL, NULL, 'AISEP-vehiclePriceHq-import', 'AISEP-vehiclePriceHq-import', NULL, '2026-07-10 08:30:35', NULL, '0', '1', '2', '0', CONCAT(CONCAT(CONCAT(@sales_id, ',', @id_AISEX00001), ',', @id_AISEX00001001), ',', @id_AISEX_vehiclePriceHq), @id_AISEX_vehiclePriceHq, 5, 6, 0, NULL, 1, '', 1, '', NULL, NULL, NULL, 'web:nexus:vehiclePriceHq:import', NULL, NULL);
SET @id_AISEP_vehiclePriceHq_import = LAST_INSERT_ID();

-- 修复保函主表提前失效 resourceDesc（原为 batchCancel）
-- UPDATE resource_info SET resourceDesc = 'AISEP-exportVehicleLetterGuarantee-expireEarly'
-- WHERE resourceTag = 'AISEP-exportVehicleLetterGuarantee-expireEarly';
