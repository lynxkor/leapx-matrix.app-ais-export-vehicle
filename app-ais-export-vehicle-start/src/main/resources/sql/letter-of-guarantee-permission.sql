-- ============================================================
-- 保函管理 权限点初始化
-- 适用: G117 红旗项目 - app-ais-export-vehicle 服务
-- 关联文档: docs/保函管理-设计文档.md §8.1 / §8.5 / §8.6
-- 前置: 字典导入 SQL (letter-of-guarantee-dict.sql) 先执行
-- ============================================================

-- 注:权限表 schema 以平台实际为准,以下为标准 sys_menu/sys_role_menu 模板示例;
-- 实际执行前请按集团权限系统字段调整 (menu_id/parent_id/perms 等)。

-- ---------- Web 端 (PC 端用户使用) ----------
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
VALUES
('保函管理', 0, 50, 'exportVehicleLetterGuarantee', null, 1, 0, 'M', '0', '0', null, '#', 'admin', NOW(), '保函管理目录'),
('保函主表', @parentId, 1, 'exportVehicleLetterGuarantee', 'basic/exportVehicleLetterGuarantee/index', 1, 0, 'C', '0', '0', 'web:basic:exportVehicleLetterGuarantee:list', '#', 'admin', NOW(), '保函主表菜单');

-- Web 端权限点 — 操作级别 (按钮权限,与菜单路由解耦)
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT * FROM (
  SELECT '保函列表'    AS menu_name, @lgMenuId AS parent_id, 1 AS order_num, '#' AS path, null AS component, 1 AS is_frame, 0 AS is_cache, 'F' AS menu_type, '0' AS visible, '0' AS status, 'web:basic:exportVehicleLetterGuarantee:list'            AS perms, '#' AS icon, 'admin' AS create_by, NOW() AS create_time, '保函列表查询' AS remark UNION ALL
  SELECT '保函分页'    AS menu_name, @lgMenuId,            2,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:page'            , '#',          'admin',         NOW(),                '保函分页查询'  UNION ALL
  SELECT '保函详情'    AS menu_name, @lgMenuId,            3,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:get'             , '#',          'admin',         NOW(),                '保函详情查询'  UNION ALL
  SELECT '保函新增'    AS menu_name, @lgMenuId,            4,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:addOrUpdate'     , '#',          'admin',         NOW(),                '保函新增/编辑'  UNION ALL
  SELECT '保函删除'    AS menu_name, @lgMenuId,            5,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:delete'          , '#',          'admin',         NOW(),                '保函删除'      UNION ALL
  SELECT '保函启停'    AS menu_name, @lgMenuId,            6,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:enableOrDisable' , '#',          'admin',         NOW(),                '保函启停'      UNION ALL
  SELECT '保函提交'    AS menu_name, @lgMenuId,            7,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:submit'          , '#',          'admin',         NOW(),                '保函提交'      UNION ALL
  SELECT '保函审核'    AS menu_name, @lgMenuId,            8,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:approve'         , '#',          'admin',         NOW(),                '保函审核'      UNION ALL
  SELECT '保函作废'    AS menu_name, @lgMenuId,            9,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:cancel'          , '#',          'admin',         NOW(),                '保函作废'      UNION ALL
  SELECT '保函提前失效' AS menu_name, @lgMenuId,           10,            '#', null,             1,             0,             'F',              '0',           '0',        'web:basic:exportVehicleLetterGuarantee:expireEarly'     , '#',          'admin',         NOW(),                '保函提前失效'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = tmp.perms);

-- /submit /approve /cancel 接口已合并单条与批量,通过 req 的 id/ids 字段路由;
-- 共用 :submit /:approve /:cancel 权限码,不区分单/批授权。

-- ---------- App 端 (内部 HTTP 调用,无菜单) ----------
-- /app/basic/exportVehicleLetterGuarantee/validate
-- /app/basic/exportVehicleLetterGuarantee/batchFreeze
-- /app/basic/exportVehicleLetterGuarantee/unfreeze
-- /app/basic/exportVehicleLetterGuarantee/transferToReceivable
-- /app/basic/exportVehicleLetterGuarantee/releaseReceivable
-- 内部接口由服务间调用约定,不暴露给前端菜单;权限沿用 Web 端同一角色 (例 web:basic:exportVehicleLetterGuarantee:list 已隐含)。

-- 注: 字典导入 (letter-of-guarantee-dict.sql) + 本脚本 + 建表 (letter-guarantee.sql)
-- 三者执行顺序: 建表 → 字典 → 权限
