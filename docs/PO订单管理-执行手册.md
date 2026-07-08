# PO订单管理 - 执行手册

> 配合《PO订单管理-开发任务.md》（设计层：目标/产出/验收/依赖）使用。本手册是**操作层**：按 T01~T11 列出每一步具体操作，标注执行方与完成标志。
>
> 执行方标注：
> - **[用户]** = 你在 IDEA / 数据库 / Nacos 操作
> - **[agent]** = 我来写代码、改配置
> - **[一起]** = 一起验证、联调
>
> 编码约束权威：`PO订单技术评审/rules/backend-coding-constraints.md`（后端，含完整示例）；前端阶段用 `frontend-coding-constraints.md`。所有 [agent] 步骤的代码写法以 rules 为准。
>
> 节奏：一个任务的"完成标志"达成后再进下一个。当前进度见文末「进度跟踪」。

---

## T01 前置准备（建表 / 生成骨架 / 状态枚举 / 验证）

### 已完成（agent）
- [x] 验证：`id-type` 实体级 `ASSIGN_UUID` 覆盖全局 `auto`；事件机制在独立制品 `com.leapmotor.platform:ais-event`；`LpBaseEntity` 字段（id String/creator Long/deleteStatus Long/时间自动填充）
- [x] 建表 SQL：`docs/sql/po_order_tables.sql`（三表完整，含基础字段 + 唯一键）
- [x] 生成器配置：`generator.properties` → `business.name=po`、`generate.table.name=purchase_order`
- [x] 状态枚举：`client/.../po/enums/PurchaseOrderStatusEnum.java`（7 态 + 状态机注释）

### 待做
1. **[用户] 建表**：把 `docs/sql/po_order_tables.sql` 执行到 `generator.properties` 里那个库（`app_ais_datacenter` @ oceanbase 测试库）。生成器读表结构就指这个 url，建别处它读不到。
   - 检查：`SHOW TABLES LIKE 'purchase_order%';` 返回三张表；`DESC purchase_order;` 字段齐全。
2. **[用户] 跑生成器（主表骨架）**：IDEA 打开 `app-ais-export-vehicle-generator/.../DddCodeGenerator.java`，右键 Run `main`。
   - 检查：`com.leapmotor.export.vehicle.po` 包下生成 `PurchaseOrder` 的各层类（Entity/Mapper/XML/Dao/Gateway/Service/Controller 等）。
3. **[用户] 改表名再跑两次**（明细 + 关联）：`generator.properties` 的 `generate.table.name` 改成 `purchase_order_detail` 跑一次，再改成 `purchase_order_guarantee` 跑一次。
   - 检查：`PurchaseOrderDetail`、`PurchaseOrderGuarantee` 各层类生成。
4. **[一起] 编译验证**：`mvn clean compile -pl app-ais-export-vehicle-start -am`（用 IDEA bundled Maven + JBR，JDK17），确认生成代码能编译。

### 完成标志
三表骨架生成、枚举就位、编译通过。→ 进 T02。

---

## T02 三表 CRUD 骨架落地（主子表联存联查）

> 前置：T01 生成器已跑完，三表骨架代码就位。

### 待做（agent，在生成骨架上调整）
1. **[agent]** 三表 Entity 字段对齐 DDL：补 `ownerUserId` / `ownerOrgId` / `status` / `statusDesc` 显式 `@TableField`；`orderStatus` 留 String（运行时与 `PurchaseOrderStatusEnum` 互转）；检查生成字段类型/注释是否齐全。
2. **[agent]** Mapper XML 补齐：`BaseResultMap` + `Base_Column_List` + 批量 `batchInsert`（明细、关联表用，参考 maintain `ServiceProxyMapper.xml`）。
3. **[agent]** DaoImpl 补 `buildQueryWrapper`（评审列表筛选条件 8 项：客户名称/PO单号/订单状态/提交日期/创建日期/产销平衡通过日期/供需审核通过日期/销售公司）。
4. **[agent]** GatewayImpl 分页方法加 `@DataPermission`（按 `owner_org_id` 过滤）。
5. **[agent]** CmdExe 主子表联保存：主表 insert/update + 明细批量 + 关联表批量，同一 `@Transactional(rollbackFor=Exception.class)`。
6. **[agent]** Controller（`PurchaseOrderController`）端点加 `@ResourcePermission("web:po:purchaseOrder:page|detail|addOrUpdate")`。
7. **[agent]** 单元测试（继承 `BaseServiceTest<T>`）：主子表联存联查、分页带数据权限。

### 待做（一起）
8. **[一起]** 编译 + 跑测试，确认 CRUD 跑通。

### rules 要点（写代码时遵守 backend-coding-constraints）
- `deleteStatus` 走 `@TableLogic`（LpBaseEntity 自带）；基础字段 creator/createTime 等平台自动填充，勿手写。
- 明细 `po_detail_number` 用 `@AutoCode(prefix="POD")`；若格式不符退 `NoGenerator`。
- 请求 DTO 枚举字段 `@EnumValue`、响应 DTO 枚举字段 `@DictEnumType`。
- GatewayImpl 分页 `@DataPermission(ownerOrgIdField="owner_org_id", tables="purchase_order")`。

### 完成标志
三表一起增改查、分页带数据权限、单元测试通过。→ T03/T04/T05 可并行。

---

## T03 事件机制基础设施（推红旗可靠投递，对齐 rules §15）

> 前置：T01（ais-event 已确认完整可用）。可与 T04/T05 并行。

### 待做（agent）
1. **[agent]** 加依赖：`com.leapmotor.platform:ais-event`（版本由 ais-parent 管理）。
2. **[agent]** 本地消息表：建 `event_notify_task`（ais-event 自带实体，DDL 从 ais-event 制品/maintain 找）。
3. **[agent]** 事件类型：`HongqiEventTypeEnum implements BizEventType`（审批结果反馈等，type/desc）。
4. **[agent]** 目标平台：本项目 `ThirdTargetPlatformEnum implements ThirdTargetPlatform`（含 HONGQI，code/desc）。
5. **[agent]** 注册事件↔平台：`BizOptionalDefinitionImpl implements BizOptionalDefinition`，`findEventTypeList()` 绑定事件↔HONGQI。
6. **[agent]** 通知服务：`HongqiNotifyServiceImpl extends AbstractThirdNotifyService`，`support()`+`doInvoke()`（调 HongqiClient）。
7. **[agent]** 外部客户端：`domain/acl/http/hongqi/HongqiClient`（@FeignClient）+ request/response。
8. **[agent]** 配置：Nacos event 配置（参考 maintain `app-ais-maintain-event.yaml`）+ 注册 `NotifyEventJob` 到 XXL-Job。

### 待做（一起）
9. **[一起]** 发测试事件 → 验证 `event_notify_task` 落记录、`HongqiNotifyServiceImpl.doInvoke` 调通 `HongqiClient`（红旗可先 mock）、失败 XXL-Job 重试。

### 完成标志
事件可发布、`event_notify_task` 落库、消费端调通、XXL-Job 重试生效。→ 解锁 T06、T09。

---

## T04 4.1 红旗订单同步创建（入站 + 匹配）

> 前置：T02。依赖：红旗 CBU 入站接口契约、客户/整编/价格主数据查询接口归属。

### 待做（agent，契约到位后）
1. **[agent]** 入站 Controller（红旗 → PO，接收采购订单推送）。
2. **[agent]** `PurchaseOrderSyncCmdExe`：PO单号唯一性判断 → 草稿创建；客户编码匹配客户主数据；物料匹配整编主数据；单价按「物料编码+创建时间+订单类型」匹配价格主数据（多条取审批通过时间最新）。匹配不到留空。
3. **[agent]** 单元测试：匹配命中/未命中留空、重复 PO单号不重建。

### 完成标志
红旗推送 → 草稿落库、重复不重建、匹配留空不报错、单测通过。

---

## T05 4.2 订单编辑&保存

> 前置：T02。可与 T04 并行。

### 待做（agent）
1. **[agent]** `PurchaseOrderController.save` + CmdExe：仅 `sales_company`（抬头）+ 明细 `production_remark` 可改。
2. **[agent]** 草稿态校验 + 乐观锁（`@DistributeLock` 或 version 字段）。
3. **[agent]** 单元测试：非草稿不可编辑、非可编辑字段防篡改（后端校验）、并发乐观锁。

### 完成标志
仅草稿可编辑、防篡改、乐观锁生效、单测通过。

---

## T06 4.3 销售提交 / 驳回（保函冻结 / 推红旗）

> 前置：T02、T03。

### 待做（agent）
1. **[agent]** 提交 `submit` CmdExe，4 步编排：①数据校验（状态/销售公司/客户编码兜底匹配/明细物料兜底匹配）→ ②价格匹配（补单价+金额+总金额）→ ③资金校验+保函冻结 → ④完成（状态→销售已提交、记 `submit_time`）。
2. **[agent]** 保函消费：调本项目保函模块内部接口 `/validate` + `/batchFreeze`（入参 po_number + 冻结金额=总金额），返回 list 写 `purchase_order_guarantee`；空 list 视为冻结失败、状态不变。
3. **[agent]** 职责链：`PurchaseOrderChainFactory` + 校验器（状态/权限）+ 处理器（保函冻结/状态推进）。
4. **[agent]** 驳回 `reject`：状态→销售已驳回、记驳回信息；发事件推红旗（T03）。
5. **[agent]** 单元测试：各步失败整体回滚、保函冻结成功/失败、驳回推红旗。

### 完成标志
提交 4 步跑通、任一步失败整体回滚、驳回推红旗、单测通过。→ 进 T07。

---

## T07 4.4 供需审核通过 / 驳回（SO 创建 / 保函解冻）

> 前置：T06。依赖：SO 创建/作废接口契约。

### 待做（agent）
1. **[agent]** `SoClient`（`domain/acl/http/so`）：创建/作废/状态查询/价格更新。
2. **[agent]** 通过 `supplyApprove`：调 SO 创建（一车一单，SO 侧生成单号/行号，PO 侧传参）+ 状态流转 + 记审核人/时间。
3. **[agent]** 驳回 `supplyReject`：保函解冻（po_number）+ 关联表 `po_current_occupied_amount=0` + 推红旗。
4. **[agent]** 单元测试。

### 完成标志
通过生成 SO、驳回解冻+清零+推红旗、整体事务、单测通过。→ 进 T08。

---

## T08 4.5 产销平衡审核通过 / 驳回（SO 作废 + APS + 保函解冻）

> 前置：T07。依赖：SO 作废、APS 同步接口契约。

### 待做（agent）
1. **[agent]** `ApsClient`（`domain/acl/http/aps`）。
2. **[agent]** 通过 `balanceApprove`：状态→产销平衡通过（终态）+ 推红旗。
3. **[agent]** 驳回 `balanceReject`：SO 状态校验（焊装上线及之后不可驳回）→ SO 全作废 → 按需调 APS「SO订单作废同步」→ 保函解冻 → 状态→产销平衡驳回（终态）→ 推红旗。
4. **[agent]** 单元测试：SO 状态分支、外部调用编排、事务边界（SO/保函强一致、APS/红旗最终一致）。

### 完成标志
通过终态+推红旗、驳回按分支作废+APS+解冻+推红旗、单测通过。→ 解锁 T10。

---

## T09 4.6 刷新订单价格（联动保函 / SO）

> 前置：T02、T03。可与审批链并行。

### 待做（agent）
1. **[agent]** `refreshPrice` CmdExe，按状态分支：分支 A（草稿/各驳回态：重算单价+金额+总金额，不动保函/SO）；分支 B（销售已提交/供需通过/产销平衡通过：重算 + 差额调保函 `adjustFreeze`/`adjustUnfreeze` + 更新 SO 车辆价格/冻结）。
2. **[agent]** 单元测试：两分支各测、保函/SO 联动。

### 完成标志
两分支重算正确、分支 B 联动生效、单测通过。

---

## T10 3.8 订单明细列表 + 批量操作

> 前置：T02、T06/T07/T08。

### 待做（agent）
1. **[agent]** `PurchaseOrderDetailController`：明细列表 `page` + 导出。
2. **[agent]** 批量端点（批量提交/销售驳回/供需通过/驳回/产销平衡通过/驳回）：套 `runBatch`，单条独立事务 + 成功/失败汇总。
3. **[agent]** 单元测试：单条失败不影响其他、汇总正确。

### 完成标志
明细筛选/导出、批量汇总正确、单测通过。

---

## T11 权限矩阵 + 数据权限 + 联调收尾 + 测试覆盖

> 前置：T01~T10。

### 待做（agent）
1. **[agent]** 所有 Controller 端点补 `@ResourcePermission`（6 角色：供需管理/汽车财务/红旗项目商务/金融专员/金融管理/销售运营，按评审 §八矩阵）。
2. **[agent]** GatewayImpl 分页方法补 `@DataPermission`。
3. **[agent]** 补全全模块单元测试，覆盖率达标（核心业务分支 100%、整体 ≥80%）；金额/数量断言对齐业务展示口径。

### 待做（一起）
4. **[一起]** 红旗 CBU 入站/出站联调。
5. **[一起]** 保函 / SO / APS 对接联调。
6. **[一起]** 全链路验收（三级审批端到端 + 批量 + 刷新价格）。

### 完成标志
权限按矩阵生效、三级审批全链路打通、外部接口收发报文正确、单元测试覆盖率达标。

---

## 进度跟踪

- [x] T01 前置（建表/生成骨架/状态枚举/验证）— 全完成
- [x] T02 三表 CRUD 骨架（编译过；筛选对齐/数据权限/主子表联查/单测待）
- [x] T03 事件机制基础设施（代码完成；XXL-Job + 链路测试待）
- [x] T04 4.1 红旗同步创建（代码完成；红旗入站契约/联调待）
- [x] T05 4.2 编辑保存（代码完成；单测待）
- [x] T06 4.3 销售提交/驳回（代码完成；保函/红旗联调 + 单测待）
- [x] T07 4.4 供需审核（代码完成；SO 契约/联调 + 单测待）
- [x] T08 4.5 产销平衡审核（代码完成；SO/APS 契约/联调 + 单测待）
- [ ] T09 4.6 刷新价格
- [ ] T10 3.8 明细列表 + 批量
- [ ] T11 权限 + 数据权限 + 联调收尾 + 单测覆盖

## 遗留清单（2026-07-08 暂停整理）

> 三级审批核心（T04–T08）代码全打通、编译过；下列为占位/外部契约/待补项。

### A. 占位待团队真代码
- 主数据查询 `PlaceholderCustomer/Material/PriceDataGateway`（返回 null）→ 客户/整编/价格主数据表 + 真 Gateway 实现
- SO 接口 `SoClient`（createSo/queryStatus/cancel）→ SO 微服务契约
- APS 接口 `ApsClient.soCancelSync` → APS 契约 + 改事件机制（评审要求最终一致，当前同步占位）

### B. 外部契约待定（推动外部团队）
- 红旗 CBU 入站（T04 Request 字段/路径）
- 红旗 CBU 出站 审批结果反馈（T03 `CbuApprovalResultReq` + `HongqiClient` URL/服务名）
- SO 接口（创建/作废/查询/价格更新）
- APS 接口
- 客户/整编/价格主数据表名（同库，待 `SHOW TABLES` 确认）

### C. T03 事件机制待收尾
- XXL-Job `notifyEventJob` 任务配置（推红旗轮询消费，T06 推红旗前配）
- T03 链路实测（reject → `event_notify_task` 落表 → 消费 → HongqiClient，红旗先 mock）
- `HongqiClient` 红旗反馈契约

### D. 各任务未完项
- T02：筛选条件对齐评审 8 项（前端联调时）、`@DataPermission`（推 T11）、主子表联查（详情）、单测
- T04–T08：单测全未写、外部联调待契约
- T09 / T10 / T11：未开始

### E. 代码 TODO
- `generatePoDetailNumber`（Random 占位）→ `@AutoCode`/`NoGenerator`（防并发冲突）
- `freeze.customerId`（暂用 customerCode，待保函侧确认）
- 数据来源/订单类型硬编码（"红旗"/"CBU"）→ 建 `DataSourceEnum`/`OrderTypeEnum`
- `reject_user` 存 userId（Long→String），若要姓名改 `LoginUserUtils.getCurrentUser()`
