# PO订单管理 - 开发任务

> 本文档是 PO订单管理（红旗CBU采购订单）新模块的**开发任务拆解**，依据技术评审文档 + 新项目现状 + 老项目实现套路三者合成。
>
> - 技术评审文档：`C:/Users/2523113/TraeProjects/PO订单技术评审/`（总览 + 01~08 节点，需求与数据模型权威来源）
> - 开发分支：`develop-wk`（本地 + 远程均已就绪，当前已在此分支）
> - 参考老项目：`C:/Users/2523113/IdeaProjects3/app-ais-maintain`（同架构、同技术栈，照搬其 ServiceProxy + SupportApproval + EventHandler 套路）

## 目录

### 文档章节
1. 需求与范围
2. 技术底座（架构 / 复用基础设施 / maintain 参考映射）
3. 数据模型（三表 / 主键 / 状态枚举）
4. 任务拆解（T01~T11 详述，见下方执行清单）
5. 关键技术决策
6. 风险与待确认
7. 测试要求

### 任务执行清单（逐个推进，完成打勾）

- [ ] **T01 前置准备**（无依赖）— 建表 / 生成骨架 / 状态枚举 / leapx 验证
- [ ] **T02 三表 CRUD 骨架**（依赖 T01）— 主子表联存联查 + 数据权限
- [ ] **T03 事件机制**（依赖 T01）— 搬 maintain 推红旗可靠投递
- [ ] **T04 4.1 红旗同步创建**（依赖 T02）— 入站接口 + 客户/物料/单价匹配
- [ ] **T05 4.2 编辑保存**（依赖 T02）— 销售公司 + 生产备注 + 乐观锁
- [ ] **T06 4.3 销售提交/驳回**（依赖 T02、T03）— 4 步编排 + 保函冻结 + 推红旗
- [ ] **T07 4.4 供需审核**（依赖 T06）— SO 创建/作废 + 保函解冻
- [ ] **T08 4.5 产销平衡审核**（依赖 T07）— SO 作废 + APS + 保函解冻
- [ ] **T09 4.6 刷新价格**（依赖 T02、T03）— 重算 + 联动保函/SO
- [ ] **T10 3.8 明细列表 + 批量**（依赖 T02、T06、T07、T08）— 查询导出 + 批量整体事务
- [ ] **T11 权限 + 联调收尾 + 测试覆盖**（依赖 T01~T10）— 6 角色权限 + 全链路联调 + 单元测试全覆盖

> 执行顺序：T01 →（T02、T03 并行）→（T04、T05 并行；T06 需等 T03）→ T06 → T07 → T08；T09、T10 可与审批链并行；T11 收尾。

## 一、需求与范围

从【红旗CBU订单采购申请】接口同步采购订单，在系统内完成 **销售提交 → 供需审核 → 产销平衡审核** 三级流转审批，结果通过【红旗CBU采购订单审批结果反馈】接口回传红旗。订单明细关联保函，涉及保函额度冻结/解冻；供需审核通过生成 SO 单。

业务节点（对应评审文档章节）：

| 节点 | 评审章节 | 状态流转 |
|---|---|---|
| 红旗订单同步创建 | 4.1 | （新建）→ 草稿 |
| 订单编辑&保存 | 4.2 | 草稿 → 草稿 |
| 销售提交/驳回 | 4.3 | 草稿 → 销售已提交 / 销售已驳回 |
| 供需审核通过/驳回 | 4.4 | 销售已提交 → 供需审核通过 / 供需审核驳回 |
| 产销平衡审核通过/驳回 | 4.5 | 供需审核通过 → 产销平衡通过 / 产销平衡驳回 |
| 刷新订单价格 | 4.6 | 状态不变（重算 + 联动保函/SO） |
| 订单明细列表 | 3.8 | 查询/导出 |

驳回态（销售已驳回/供需审核驳回/产销平衡驳回）均为终态，红旗重推按 4.1 创建为新订单。

## 二、技术底座

> 编码约束权威：`PO订单技术评审/rules/backend-coding-constraints.md`（后端 19 节，含 DDD 分层/CQRS/事件/权限/锁/建表/生成器等完整示例）。本文档与之冲突时以 rules 为准。

### 2.1 架构与栈

本项目为 DDD 七模块（SB3.5 / JDK17 / MyBatis-Plus / Nacos / OpenFeign / MapStruct / JetCache）：

| 模块 | 职责 | PO 包路径 |
|---|---|---|
| client | Feign API + DTO + 枚举 | `com.leapmotor.export.vehicle.po.{enums,dto,request,response,api,api.fallback}` |
| domain | 领域对象 Do/Qo/Gateway + ACL 外部接口 | `com.leapmotor.export.vehicle.po.{model,query,gateway}` + `acl.http.{hongqi,so,aps}` |
| app | Service + CmdExe/QryExe + Convertor + Req/Resp + Event | `com.leapmotor.export.vehicle.po.{service,service.impl,executor.cmd,executor.query,convertor,req,resp,event}` + `base.event`（搬 maintain） |
| infrastructure | Entity/Mapper/Dao/GatewayImpl/Convertor + XML | `com.leapmotor.export.vehicle.infrastructure.po.{entity,mapper,dao,dao.impl,gatewayimpl,convertor}` + `resources/mapper/po/` |
| adapter | Controller（web/app/api） | `com.leapmotor.export.vehicle.controller.{web,app,api}.po` |
| generator | DDD 代码生成器 | 改 `generator.properties` 的 `business.name=po` |
| start | 启动入口 + 配置 | — |

### 2.2 复用基础设施（全部现成，直接用）

| 能力 | 类 / 注解 | 来源 |
|---|---|---|
| 实体基类（id/creator/creator_name/create_time/modifier/modifier_name/update_time/delete_status） | `LpBaseEntity` | `com.leapmotor.platform.ais.common.mybatis.support.entity` |
| 领域基类 / 查询基类 | `LpBaseModel` / `LpBaseQo` / `LpBaseParam` | `com.leapmotor.paltform.ais.common.model.base`（注意 `paltform` 为平台历史拼写） |
| Mapper / Dao 基类 | `LpBaseMapper` / `LpBaseDao` / `ServiceImpl` | leapx-model |
| 统一返回 / 分页 | `AjaxResult` / `PageResult` / `PageUtils.transferPage` | `com.leapmotor.paltform.ais.common.model.response` |
| 业务异常 | `BizException` | 平台 GlobalExceptionHandler 统一捕获 |
| 当前用户 | `LoginUserUtils.getCurrentUserOrSysDefault()` | `com.leapmotor.paltform.ais.common.util` |
| 按钮权限 | `@ResourcePermission("web:po:purchaseOrder:动作")` | `com.leapmotor.platform.ais.common.permission.annotation` |
| 数据权限（按 owner_org_id 过滤） | `@DataPermission`（标在 GatewayImpl 分页方法） | 同上 |
| 分布式锁 | `@DistributeLock` | `com.leapmotor.platform.ais.common.redis.annotation` |
| 参数校验 | `@Validated` + jakarta.validation | 平台 |
| Feign | `@FeignClient`（启动类已 `@EnableFeignClients`） | spring-cloud |

### 2.3 maintain 参考映射（照搬哪段抄哪段）

| PO 要做的事 | 抄 maintain 的 | 文件 |
|---|---|---|
| 主子表事务落库 | `ServiceProxyServiceImpl.addOrUpdateServiceProxy` + `ServiceProxyCmdExe` | `app/.../sp/service/impl/ServiceProxyServiceImpl.java:396`、`executor/cmd/ServiceProxyCmdExe.java:67,95` |
| 批量插入/更新 | `ServiceProxyDaoImpl`（batchInsert / batchUpdateByCaseWhen） | `infrastructure/.../sp/dao/impl/ServiceProxyDaoImpl.java:196` |
| 三级审批状态枚举 | `OrderStatusEnum` + `AuditStatusEnum`（带外部映射） | `client/.../sp/enums/OrderStatusEnum.java:26` |
| 推外部（红旗）可靠投递 | `EventHandlerHelper.publishAsync` + `AbstractEventHandler` 子类 `ServiceProxyToGms` | `app/.../base/event/EventHandlerHelper.java:80`、`sp/event/ServiceProxyToGms.java:56` |
| 外部 Feign 客户端 | `GmsClient`（domain 的 `acl.http.*`） | `domain/.../acl/http/gms/GmsClient.java:18` |
| 批量操作整体事务 | `runBatch`（单条独立事务 + 汇总） | 参见保函 `ExportVehicleLetterGuaranteeServiceImpl.runBatch` |
| Entity 注解范式 | `ServiceProxy`（@TableName/autoResultMap/@TableField/ownerOrgId） | `infrastructure/.../sp/entity/ServiceProxy.java:25` |
| 最干净的主子表范本 | `OwnersEquityOrder`（+Item +Parts） | `infrastructure/.../sp/entity/OwnersEquityOrder.java` |

## 三、数据模型

### 3.1 三张物理表（DDL 见评审文档 `00-总览` §3.1.1 / §3.2.1 / §3.3.1）

| 表 | 类名 | 说明 |
|---|---|---|
| `purchase_order` | `PurchaseOrder` | 主表，业务唯一键 po_number |
| `purchase_order_detail` | `PurchaseOrderDetail` | 明细，(po_number, po_detail_number) 组合唯一 |
| `purchase_order_guarantee` | `PurchaseOrderGuarantee` | PO-保函关联（一对多），(po_number, guarantee_id) 组合唯一 |

三表均带平台基础字段 11 个（creator/creator_name/create_time/modifier/modifier_name/update_time/delete_status/status/status_desc/owner_user_id/owner_org_id），其中 id/creator/create_time/modifier/update_time/delete_status 由 `LpBaseEntity` 提供，status/status_desc/owner_user_id/owner_org_id 在业务实体显式声明。

### 3.2 主键

已确认：`LpBaseEntity` 源码为 `@TableId(value = "id", type = IdType.ASSIGN_UUID) private String id`——实体级 ASSIGN_UUID 覆盖全局 `id-type: auto`，id 由 MyBatis-Plus 自动生成 UUID 字符串，无需手填、非数据库自增。评审 `VARCHAR(64)` 正确（UUID 36 位）。另：`creator`、`deleteStatus` 为 `Long`（建表 BIGINT）；`create_time`/`update_time` 由 `application.yml` auto-field 自动填充（dateTime）。

### 3.3 订单状态枚举（新建 `PurchaseOrderStatusEnum`，client 层）

草稿 / 销售已提交 / 销售已驳回 / 供需审核通过 / 供需审核驳回 / 产销平衡通过 / 产销平衡驳回（共 7 态，详见评审 §2.1）。实现 `IBaseEnum`，Integer code + 中文 message。

## 四、任务拆解

> 依赖链：T01 → T02 →（T03、T04、T05 并行）→ T06 → T07 → T08；T09、T10 可与审批链并行；T11 收尾。
>
> 每个任务的"产出类"按 DDD 分层列出，命名前缀 `PurchaseOrder`（主表）/ `PurchaseOrderDetail`（明细）/ `PurchaseOrderGuarantee`（关联）。

### T01 - 前置准备（建表 / 骨架生成 / 状态枚举 / leapx 验证）

- 目标：把数据库表、代码骨架、枚举、事件机制依赖一次性备齐。
- 产出：
  - 数据库：按评审 DDL 建三张表（purchase_order / purchase_order_detail / purchase_order_guarantee）。
  - 骨架：用 `app-ais-export-vehicle-generator` 的 `DddCodeGenerator`，设 `business.name=po`、`generate.table.name` 依次为三张表，生成全分层 CRUD 骨架；调整包名到 `po`、字段对齐评审 DDL。
  - 枚举（client）：`PurchaseOrderStatusEnum`。
  - 验证：确认 `id-type` 策略；确认 leapx 库是否含 `EventPublisher` / `EventNotifyTask`（`com.leapmotor.paltform.ais.common.event.core.*`），决定 T03 搬运范围。
- 参考：本项目保函模块生成结构、maintain `OrderStatusEnum`。
- 验收：三表建好；生成代码能编译；状态枚举可用。
- 依赖：无。

### T02 - 主表/明细/关联表 CRUD 骨架落地（联存联查）

- 目标：在生成的骨架上补齐主子表联保存、联查询、分页、详情。
- 产出（按层，三表各一套）：
  - infrastructure：`PurchaseOrder`(Detail/Guarantee) Entity 对齐 DDL 字段（含 ownerOrgId）、Mapper + XML（BaseResultMap / Base_Column_List / batchInsert）、Dao + DaoImpl（buildQueryWrapper）、GatewayImpl（分页方法标 `@DataPermission`）、StructMapper。
  - domain：Do / Qo / Gateway。
  - app：Service + ServiceImpl、CmdExe（主表 insert/update + 明细/关联批量同事务）、QryExe、DTOStructMapper、Req（`PurchaseOrderAddOrUpdateReq` 嵌 `List<PurchaseOrderDetailAddOrUpdateReq>`）、Resp。
  - adapter：`PurchaseOrderController`（page/detail/addOrUpdate，标 `@ResourcePermission`）。
- rules 要点：Entity `deleteStatus` 走 `@TableLogic`（LpBaseEntity 自带，勿重复填充基础字段）；明细 `po_detail_number` 用 `@AutoCode(prefix="POD")` 自动编号（格式不符退 `NoGenerator`）；请求 DTO 枚举字段 `@EnumValue(enumClass=PurchaseOrderStatusEnum.class, required=true)`、响应 DTO 枚举字段 `@DictEnumType(prefix="PurchaseOrderStatusEnum")`；GatewayImpl 分页 `@DataPermission(ownerOrgIdField="owner_org_id", tables="purchase_order")`。
- 参考：maintain `ServiceProxy` 主子表、`OwnersEquityOrder` 主子表范本。
- 验收：主表 + 明细 + 关联表能一起增改查；分页带数据权限。
- 依赖：T01。

### T03 - 事件机制基础设施（推红旗可靠投递，对齐平台事件驱动标准 rules §15）

- 目标：接入平台 `ais-event` 标准事件驱动（本地消息表 + XXL-Job 失败重试），实现推红旗 CBU 审批结果的可靠投递。
- 产出：
  - 依赖：新增 `com.leapmotor.platform:ais-event`（已确认完整含：`EventPublisher`/`NotifyEventSourceAdapter`/`AbstractThirdNotifyService`/`BizEventType`/`ThirdTargetPlatform`/`BizOptionalDefinition`/`EventNotifyTask` + Service/Manager/Mapper/Controller + `NotifyEventJob` XXL-Job 重试调度，包 `com.leapmotor.paltform.ais.common.event.*`）。
  - 本地消息表：建 `event_notify_task`（ais-event 自带实体，DDL 从 ais-event 制品/maintain 找）。
  - 事件类型：`HongqiEventTypeEnum implements BizEventType`（审批结果反馈等，字段 type/desc）。
  - 目标平台：本项目 `ThirdTargetPlatformEnum implements ThirdTargetPlatform`（含 HONGQI，字段 code/desc）。
  - 注册事件↔平台：`BizOptionalDefinitionImpl implements BizOptionalDefinition`，`findEventTypeList()` 绑定 `HongqiEventTypeEnum.XXX ↔ HONGQI`。
  - 通知服务：`HongqiNotifyServiceImpl extends AbstractThirdNotifyService`，实现 `support()`+`doInvoke()`，`doInvoke` 调 `HongqiClient` 推红旗。
  - 发布：业务侧 `eventPublisher.publishAsync(NotifyEventSourceAdapter.builder().eventType().thirdTargetPlatform().content().build())`。
  - domain/acl/http/hongqi：`HongqiClient`（@FeignClient 对接红旗 CBU 反馈）+ request/response。
  - 配置：nacos event 配置（参考 maintain `app-ais-maintain-event.yaml`）+ 注册 ais-event 的 `NotifyEventJob` 到 XXL-Job 调度。
- 参考：rules `backend-coding-constraints.md` §15（权威，含完整示例）、maintain `ServiceProxyToGms`。
- 验收：发测试事件 → `event_notify_task` 落记录 → `HongqiNotifyServiceImpl.doInvoke` 调通 `HongqiClient`（红旗可先 mock）→ 失败时 XXL-Job 重试。
- 依赖：T01。

### T04 - 4.1 红旗订单同步创建（入站接口 + 匹配逻辑）

- 目标：接收红旗 CBU 推送的采购订单，唯一性判断后创建草稿；客户/物料/单价匹配不到则留空（提交时强校验）。
- 产出：
  - adapter：入站 Controller（红旗 → PO，`/app/...` 或对接红旗回调路径）。
  - app：`PurchaseOrderSyncCmdExe`（或并入主 CmdExe）：PO单号唯一性判断、草稿创建、客户编码匹配客户主数据、物料匹配整编主数据、单价按「物料编码+创建时间+订单类型」匹配价格主数据（多条取审批通过时间最新）。
  - 依赖调用：客户主数据（app-account-api）、整编主数据、价格主数据查询接口（归属待确认，见评审 08 节点）。
- 参考：评审 01 节点、maintain 入站接口模式。
- 验收：红旗推送 → 草稿订单落库；重复 PO单号不重复创建；匹配类字段留空不报错。
- 依赖：T02。

### T05 - 4.2 订单编辑&保存

- 目标：草稿态编辑销售公司 + 明细生产备注并保存。
- 产出：`PurchaseOrderController.save` 端点；CmdExe 编辑逻辑（仅 sales_company 抬头可改、明细 production_remark 逐行改）；草稿态校验 + 乐观锁。
- 参考：评审 02 节点、maintain 编辑保存模式。
- 验收：仅草稿可编辑；非可编辑字段防篡改（后端校验）；并发乐观锁生效。
- 依赖：T02。

### T06 - 4.3 销售提交 / 驳回（保函冻结 / 推红旗）

- 目标：提交走 4 步编排；驳回走终态 + 推红旗。
- 产出：
  - 提交 `submit`：步骤1 数据校验（状态/销售公司/客户编码兜底匹配/明细物料兜底匹配）→ 步骤2 价格匹配（补单价+金额+总金额）→ 步骤3 资金校验+保函冻结 → 完成（状态→销售已提交、记 submit_time）。
  - 保函消费：调本项目保函模块内部接口 `/validate` + `/batchFreeze`（入参 po_number + 冻结金额=总金额），返回 list 写入 purchase_order_guarantee；空 list 视为冻结失败、状态不变。
  - 驳回 `reject`：状态→销售已驳回、记 reject_time/reject_user/reject_reason；发事件推红旗（T03）。
  - 职责链：`PurchaseOrderChainFactory` + 校验器（状态/权限）+ 处理器（保函冻结/状态推进）。
- 参考：评审 03 节点、保函模块 `ExportVehicleLetterGuaranteeAppController`、maintain `ServiceProxyChainFactory`。
- 验收：提交成功则保函冻结 + 关联表写入 + 状态流转；任一步失败整体回滚、状态不变；驳回推红旗走本地消息表。
- 依赖：T02、T03。

### T07 - 4.4 供需审核通过 / 驳回（SO 创建 / 保函解冻）

- 目标：通过则生成 SO 单；驳回则解冻保函 + 作废 SO（如有）+ 推红旗。
- 产出：
  - 通过 `supplyApprove`：状态→供需审核通过、记 supply_demand_approve_time/approver；调 SO 创建接口（一车一单，SO 侧生成单号/行号，PO 侧传参）。
  - 驳回 `supplyReject`：状态→供需审核驳回（终态）、记驳回信息；调保函解冻（po_number）、更新关联表 po_current_occupied_amount=0；推红旗。
  - SO 客户端：domain/acl/http/so `SoClient`（创建/作废/状态查询/价格更新）。
- 参考：评审 04 节点、maintain 外部接口编排。
- 验收：通过生成 SO；驳回解冻保函 + 关联表清零 + 推红旗；整体事务。
- 依赖：T06。

### T08 - 4.5 产销平衡审核通过 / 驳回（SO 作废 + APS + 保函解冻）

- 目标：通过为终态、推红旗；驳回则 SO 作废 + APS 同步 + 保函解冻 + 推红旗。
- 产出：
  - 通过 `balanceApprove`：状态→产销平衡通过（终态）、记 production_sales_balance_approve_time/approver；推红旗。
  - 驳回 `balanceReject`：校验 SO 状态（焊装上线及之后不可驳回）；SO 全作废；按需调 APS「SO订单作废同步」；保函解冻；状态→产销平衡驳回（终态）；推红旗。
  - SO 作废 + APS：`SoClient` 作废、`ApsClient`（domain/acl/http/aps）同步。
- 参考：评审 05 节点。
- 验收：通过终态 + 推红旗；驳回按 SO 状态分支、SO 作废 + APS + 保函解冻 + 推红旗；APS/红旗走事件最终一致，SO/保函走本地事务强一致。
- 依赖：T07。

### T09 - 4.6 刷新订单价格（联动保函 / SO）

- 目标：按订单状态分支重算价格，联动保函 adjustFreeze/adjustUnfreeze、SO 车辆价格/冻结金额更新。
- 产出：`refreshPrice` 端点 + CmdExe；分支 A（草稿/各驳回态：重算单价+金额+总金额，不动保函/SO）与分支 B（销售已提交/供需通过/产销平衡通过：重算 + 差额调保函 adjustFreeze/adjustUnfreeze + 更新 SO 价格/冻结）。
- 参考：评审 06 节点、保函 `/adjustFreeze` `/adjustUnfreeze`。
- 验收：两分支价格重算正确；分支 B 保函/SO 联动生效。
- 依赖：T02、T03。

### T10 - 3.8 订单明细列表 + 批量操作

- 目标：明细列表查询/导出；列表页批量提交/驳回/审核（整体事务）。
- 产出：`PurchaseOrderDetailController`（明细列表 page + 导出）；批量端点（批量提交/销售驳回/供需通过/驳回/产销平衡通过/驳回），套 `runBatch` 单条独立事务 + 汇总结果。
- 参考：评审 07 节点 + 总览 §4.3 批量操作、保函 `runBatch`。
- 验收：明细列表筛选/导出；批量操作每条独立事务、失败不影响其他条、返回成功/失败汇总。
- 依赖：T02、T06/T07/T08（复用各节点逻辑）。

### T11 - 权限矩阵 + 数据权限 + 联调收尾

- 目标：按评审 §八 权限矩阵配 6 角色按钮权限；全链路联调。
- 产出：所有 Controller 端点补 `@ResourcePermission`（供需管理/汽车财务/红旗项目商务/金融专员/金融管理/销售运营）；GatewayImpl 分页补 `@DataPermission`；红旗 CBU 入站/出站联调；保函/SO/APS 对接联调。
- 参考：评审 §八 权限矩阵、maintain 权限注解用法。
- 验收：各角色仅可见/可操作授权范围；三级审批全链路打通；外部接口收发报文正确；**全模块单元测试覆盖率达标（核心业务分支全覆盖）**。
- 依赖：T01~T10。

## 五、关键技术决策

| 项 | 决策 | 依据 |
|---|---|---|
| 主键 | LpBaseEntity String UUID（不手填、不自增） | 项目惯例 + 评审 VARCHAR(64) |
| 推红旗 | 平台事件驱动标准（ais-event：`event_notify_task` + `EventPublisher` + `AbstractThirdNotifyService` + XXL-Job 重试） | rules §15 + 评审"最终一致，状态不因推送回滚" |
| SO/保函 | 本地事务强一致（同微服务），失败整体回滚 | 评审通用规则 |
| APS/红旗 | 事件机制最终一致，不阻塞订单状态 | 评审通用规则 |
| 保函数据 | PO 只消费，不维护保函状态/生效日期（接口关联展示） | 保函模块已预留契约 |
| 审批人记录 | 复用主表各 approver/time 字段（评审已定），不单建审批记录表 | 评审主表字段 |
| 三级审批编排 | 职责链（校验器 + 处理器）+ CmdExe 事务 | maintain ServiceProxy 套路 |

## 六、风险与待确认

1. **外部接口契约**：红旗 CBU 入站/出站、SO、APS、客户/整编/价格主数据查询的契约待外部团队提供（评审 08 节点），T04/T06~T09 部分逻辑需契约到位才能完整联调。
2. **主数据归属**：客户信息/整编主数据/价格主数据查询接口归属待确认（app-account-api 还是其他服务）。
3. **保函关联表唯一键**：(po_number, guarantee_id) 组合唯一，前提是"同一 PO 不重复冻同一保函"，需业务确认。
4. **ehcache 悬空引用**：本项目 application.yml 的 ehcache.config 指向不存在文件（CLAUDE.md 已标），与 PO 无关，留意勿踩。

## 七、测试要求

**每个任务交付须含对应单元测试**，T11 收尾时全模块覆盖率达标。

- 测试基类：继承 `BaseServiceTest<T>`（start 模块 src/test，反射泛型自动注入被测 service）。
- 覆盖范围（各 CmdExe / ServiceImpl 业务方法重点测）：
  - 状态流转：7 态的合法跳转 + 非法跳转拦截（如非草稿不可编辑、产销平衡驳回的 SO 状态校验）。
  - 4.1 匹配：客户/物料/单价匹配的命中、未命中（留空）、多条取最新审批价。
  - 4.3 提交 4 步编排：各步失败的整体回滚（状态不变、保函不冻结、关联表不写入）。
  - 保函联动：冻结/解冻/刷新调额后关联表 po_current_occupied_amount 的正确性。
  - 刷新价格：分支 A（仅重算）与分支 B（重算 + 联动保函/SO）各测。
  - 批量操作：单条失败不影响其他条，返回成功/失败汇总。
- 外部依赖隔离：HongqiClient / SoClient / ApsClient / 保函内部接口 / 主数据查询用 `@MockBean` 或 Mockito mock，禁止真连外部。
- 数据层：Dao/Mapper 自定义 SQL（batchInsert / 复杂关联查询）配集成测试（连测试库）。
- 覆盖率目标：核心业务方法行覆盖 + 分支覆盖 ≥ 80%；审批/状态/金额计算类逻辑分支覆盖 100%。
- 金额/数量断言对齐业务展示口径（从明细聚合计算，勿用汇总字段等价替换），避免汇总字段与展示口径不一致的坑。
