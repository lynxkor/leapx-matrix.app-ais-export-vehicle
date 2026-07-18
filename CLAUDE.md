# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

智能服务系统--整车订单业务服务（app-ais-export-vehicle），覆盖 CBU/SKD/CKD 整车订单主体（LPMI/SVZ/G117/more）。干净的 DDD 框架，保留通用基础设施。

## 构建与运行

> **必须用 JDK 17。** 系统 `mvn` 默认可能绑定低于 17 的 JDK（本机为 JDK 8），裸 `mvn` 会报 `无效的目标发行版: 17`。构建时需将 `JAVA_HOME` 指向本地 JDK 17 并将其 `bin` 置于 `PATH` 前部（各开发者 JDK 安装路径不同，请按本机实际路径替换）。

```bash
# 全量构建（内联 JDK 17，路径按本机实际替换）
JAVA_HOME="<本机JDK17路径>" PATH="<本机JDK17路径>/bin:$PATH" mvn clean package -DskipTests

# 离线复验（依赖已缓存时，规避偶发的元数据下载卡顿）
JAVA_HOME="<本机JDK17路径>" mvn clean compile -DskipTests -o

# 单模块构建（含依赖模块）
mvn clean install -pl app-ais-export-vehicle-app -am -DskipTests

# 本地运行（需先在 Nacos 建好配置文件，见下）
cd app-ais-export-vehicle-start && mvn spring-boot:run
```

测试：仅有抽象基类 `BaseServiceTest<T>`（start 模块 src/test），通过反射泛型自动注入被测 `service`。新增集成测试继承它并用 `printJson(result)` 输出。运行单个测试类：`mvn test -pl app-ais-export-vehicle-start -Dtest=XxxTest`。

## 模块架构（DDD 分层）

| 模块 | 职责 |
|------|------|
| **client** | Feign API 接口、DTO、降级工厂(FallbackFactory) |
| **adapter** | REST 控制器(Controller) |
| **app** | 应用服务 + CmdExe/QryExe 执行器 + 配置类 + 定时任务 |
| **domain** | 领域对象(Do)、查询对象(Qo)、网关接口(Gateway) |
| **infrastructure** | Entity/Mapper/Dao/GatewayImpl + 缓存配置 |
| **generator** | DDD 代码生成器（`DddCodeGenerator.main()`，配置见 `generator.properties`） |
| **start** | 启动入口 `ExportVehicleApplication` + bootstrap.yml |

模块依赖方向：`client ← domain ← infrastructure`；`client ← app ← adapter`；`start` 聚合 `adapter`+`infrastructure`。**Controller 不直接访问 infrastructure，必须经 app 层 Service。**

## 关键架构模式

- **CQRS**：`executor/cmd/*CmdExe`（写，`@Transactional(rollbackFor=Exception.class)`）与 `executor/query/*QryExe`（读）分离；`service/*Service` 编排两者。
- **Gateway 模式**：接口在 `domain/gateway`，实现在 `infrastructure/gatewayimpl`；DB 链路 `Entity → Mapper(extends LpBaseMapper) → Dao(extends LpBaseDao) → GatewayImpl`。`LpBaseDao` 提供 `buildBasicQueryWrapper`/`findPage`/`queryList` 等，子类覆写 `buildQueryWrapper` 拼业务条件，用 `STATUS_COLUMN`/`CREATED_TIME_COLUMN` 排序。
- **MapStruct 双转换器**：`app/convertor/*DTOStructMapper`（domain↔req/resp）+ `infra/convertor/*StructMapper`（domain↔entity），均 `@Mapper` + `Mappers.getMapper(X.class)` 的 `INSTANCE` 单例。

## 包路径

根包 `com.leapmotor.export.vehicle`，包与目录严格一致：
- `base.config` / `base.constant`：通用配置（Async/Rest/Swagger）与常量
- `job`：定时任务（`@XxlJob`，新建业务时按需新增 `XxxJob` 类）
- mapper XML：`infrastructure/src/main/resources/mapper/**/*.xml`（`mapper-locations` 为 `classpath*:/mapper/**/*.xml`）

> 平台响应/基类包名是 `com.leapmotor.paltform.ais.common.model.*`（`paltform` 为平台历史拼写，**非笔误**）；`permission`/`mybatis` 等在正确拼写 `com.leapmotor.platform.ais.common.*`。按现有业务模块的 import 照抄。

## 服务配置

- **服务名 / Nacos 配置**：`app-ais-export-vehicle`（需在 Nacos group=ais 新建 `app-ais-export-vehicle.yaml`、`app-ais-export-vehicle-datasource.yaml`，否则启动配置加载失败；redis/job/common/portal-jwt 为公共配置）
- **端口**：8084，context-path `/vehicle`；Actuator 18084，base-path `/vehicle/actuator`
- **Nacos**：`leapx-nacos-test.leapmotor.com`，namespace `483de891-cc67-4c6b-80b6-53727bba98db`

### 本地启动环境变量

本地启动前需配置以下环境变量覆盖 Nacos 连接，并将 `namespace` 改为**个人 namespace**（勿用代码默认的公共 namespace，避免互相影响）：

```
spring.cloud.nacos.discovery.server-addr=https://leapx-nacos-test.leapmotor.com
spring.cloud.nacos.discovery.namespace=<个人namespace>
spring.cloud.nacos.discovery.username=nAdmin
spring.cloud.nacos.discovery.password=<密码>
```

个人 namespace 申请方式见内网 Wiki：https://wiki-intra.leapmotor.com/pages/viewpage.action?pageId=118531368 。这些变量同时作用于 `discovery` 与 `config`（`bootstrap.yml` 已用 `${spring.cloud.nacos.discovery.*}` 引用）。

## start 模块 resources 静态文件

`app-ais-export-vehicle-start/src/main/resources/` 下的静态资源：

- `bootstrap.yml` — Nacos 引导：服务名、discovery/config 地址、namespace，及多份 nacos 配置导入（redis/job/common/portal-jwt/export-vehicle/export-vehicle-datasource）。
- `application.yml` — 本地应用配置：端口、context-path、mybatis-plus（mapper-locations、typeAliasesPackage）、actuator、springdoc。
- `logback-spring.xml` — 日志配置：按 `${spring.application.name}` 分目录，info/error 分文件，单文件 100MB、保留 7 天、总量 1GB，含 `traceId` 占位。
- `i18n/messages*.properties` — 国际化消息资源（默认 + `zh_CN`），含校验/登录/权限等提示文案，供 `MessageSource` 取用。
- `properties/common_*.properties` — 业务国际化语言包（`zh_CN`/`en_US`）。

> 注意：`application.yml` 的 `spring.cache.ehcache.config: classpath:ehcache.xml` 引用了一项目前不存在的 `ehcache.xml`（模板遗留的悬空引用）。若启用本地 ehcache 缓存需补建该文件，否则可移除该引用。

## 新业务模块起步

新业务优先使用 `ais-generator`（配置见 `app-ais-export-vehicle-generator/src/main/resources/generator.properties`，当前已配为 `business.name=so` / `generate.table.name=export_vehicle_cbu_so_interface_sync`）生成 DDD 全分层代码，再补业务逻辑。Generator 输出 Client/Adapter/App/Domain/Infrastructure 完整闭环，覆盖 CQRS+Gateway+MapStruct 模板；具体约束详见根目录 `.claude/rules/backend-coding-constraints.md`。

## 参考文档（内网 Wiki）

- 框架介绍：https://wiki-intra.leapmotor.com/pages/viewpage.action?pageId=90433541
- 本地启动 Nacos 个人 namespace 配置：https://wiki-intra.leapmotor.com/pages/viewpage.action?pageId=118531368
