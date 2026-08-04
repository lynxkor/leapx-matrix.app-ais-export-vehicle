# APS 系统接口文档

> 本文档覆盖 `app-ais-export-vehicle` 中与 **APS 产销平台** 的对接接口。
> 涉及源码：
> - 入站（APS -> 我方）：`app-ais-export-vehicle-adapter/src/main/java/com/leapmotor/export/vehicle/controller/open/aps/ApsOpenController.java`
> - 出站（我方 -> APS）：`app-ais-export-vehicle-infrastructure/src/main/java/com/leapmotor/export/vehicle/infrastructure/acl/rpc/aps/client/ApsClientApi.java`
>
> YAPI 接口平台：<https://itdev-yapi.leapmotor.com/project/304/interface/api/130123>
>
> > 注：出站接口已对齐《海外销售订单接口文档》（`/api/abroad/batchOrder` 下发、`/api/abroad/cancelOrder` 作废），经出口网关 `/external/mom-cloud/aps` 前缀转发。

---

## 基础信息

### 内部访问地址

| 环境 | 地址 |
|------|------|
| 测试环境 | `https://mdmapitest.leapmotor.com` |
| 正式环境 | `https://mdmapi.leapmotor.com` |

### 通用请求头

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | 固定 `application/json` |
| api-caller | 是 | 接口平台分配（入站由网关校验） |
| api-token | 是 | 接口平台分配（入站由网关校验） |

### 通用响应体（我方平台 AjaxResult）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码：`200` 成功，非 `200` 失败 |
| msg | string | 提示信息 |
| data | object | 业务数据 |
| success | boolean | 是否成功：`true` / `false` |

---

## 一、入站接口（APS -> 我方）

### 1. 接收 APS 生产状态回传（批量）

#### 接口说明

| 项目 | 内容 |
|------|------|
| 接口名称 | 接收 APS 生产状态回传 |
| 功能描述 | APS 产销平台将车辆生产状态批量推送给销服，我方按 SO 单号 + 状态码逐条更新 SO 进度与车辆关键时间，单条失败不中断其余，返回批量处理结果 |
| 请求方式 | POST |
| 请求路径 | `/aisapi/vehicle/openApi/aps/vehicleCbuSo/receiveProductionStatus` |
| 鉴权方式 | 上层网关统一签名校验 |
| 批量上限 | 单次最多 100 条（可配 `aps.receive.batch-size-limit`），超限整批拒绝 |

#### 请求地址

```text
测试环境：https://mdmapitest.leapmotor.com/aisapi/vehicle/openApi/aps/vehicleCbuSo/receiveProductionStatus
正式环境：https://mdmapi.leapmotor.com/aisapi/vehicle/openApi/aps/vehicleCbuSo/receiveProductionStatus
```

#### 请求参数

请求体为 JSON 数组，每个元素字段如下：

| 字段名 | 类型 | 必填 | 格式 | 说明 |
|--------|------|------|------|------|
| soNo | string | 是 | - | SO 单号 |
| status | int | 是 | 1/2/3/4/5 | APS 外部状态码（见映射表） |
| productionIdentifyCode | string | 否 | - | 产销识别码，**status=1** 时由 APS 下发 |
| internalVehicleNo | string | 否 | - | 内部车辆号，**status=2/3/4/5** 时由 APS 下发 |
| vin | string | 否 | - | VIN 码，**status=4/5** 时由 APS 下发 |
| planOfflineDate | date | 否 | `yyyy-MM-dd` | 计划总装下线日期，**status=1** 时下发 |
| scheduleTime | datetime | 否 | `yyyy-MM-dd HH:mm:ss` | 排产时间，**status=1** 时下发 |
| barcodePrintTime | datetime | 否 | `yyyy-MM-dd HH:mm:ss` | 打印条码时间，**status=4/5** 时下发 |
| bodyInTime | datetime | 否 | `yyyy-MM-dd HH:mm:ss` | 焊装上线时间，对应状态必传 |
| paintInTime | datetime | 否 | `yyyy-MM-dd HH:mm:ss` | 涂装上线时间，对应状态必传 |
| assemblyInTime | datetime | 否 | `yyyy-MM-dd HH:mm:ss` | 总装上线时间，对应状态必传 |
| assemblyOutTime | datetime | 否 | `yyyy-MM-dd HH:mm:ss` | 总装下线时间，对应状态必传 |

#### APS 状态码映射

| APS status | 内部状态 | 含义 |
|------------|----------|------|
| 1 | 1003 | 排产中 |
| 2 | 1004 | 焊装上线 |
| 3 | 1005 | 涂装上线 |
| 4 | 1006 | 总装上线 |
| 5 | 1007 | 总装下线 |

#### 请求示例

批量回传（排产中 + 总装下线各一条）：

```json
[
  {
    "soNo": "SO202607150001",
    "status": 1,
    "productionIdentifyCode": "PIC202607150001",
    "planOfflineDate": "2026-08-15",
    "scheduleTime": "2026-07-15 09:00:00",
    "bodyInTime": "2026-08-01 08:00:00",
    "paintInTime": "2026-08-05 08:00:00",
    "assemblyInTime": "2026-08-10 08:00:00",
    "assemblyOutTime": "2026-08-15 18:00:00"
  },
  {
    "soNo": "SO202607150002",
    "status": 5,
    "internalVehicleNo": "IVN202607150002",
    "vin": "LSVAG2180E2100002",
    "barcodePrintTime": "2026-08-09 10:00:00",
    "bodyInTime": "2026-08-01 08:00:00",
    "paintInTime": "2026-08-05 08:00:00",
    "assemblyInTime": "2026-08-10 08:00:00",
    "assemblyOutTime": "2026-08-15 18:00:00"
  }
]
```

#### 响应参数

响应体 `data` 为批量处理结果 `VehicleCbuSoOperateResult`：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| totalCount | int | 总数量（= successCount + failCount） |
| successCount | int | 成功数量（含幂等/回退跳过，计入成功） |
| failCount | int | 失败数量 |
| failItems | array | 失败明细列表 |
| failItems[].soNo | string | 失败 SO 单号 |
| failItems[].reason | string | 失败原因 |

#### 响应示例

##### 成功（批量处理，含 1 条失败）

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalCount": 2,
    "successCount": 1,
    "failCount": 1,
    "failItems": [
      { "soNo": "SO202607150002", "reason": "SO[SO202607150002]不存在" }
    ]
  },
  "success": true
}
```

##### 失败（超过批量上限）

```json
{
  "code": 600,
  "msg": "单次最多100条，请分批调用",
  "data": null,
  "success": false
}
```

#### 业务校验

1. 请求体为 JSON 数组，单次条数不得超过 `aps.receive.batch-size-limit`（默认 100），超限整批拒绝。
2. 每条 `soNo`、`status` 必填；`status` 仅支持 `1/2/3/4/5`。
3. 逐条独立处理：单条失败（SO 不存在 / 状态码不支持等）计入 `failItems`，不中断其余条目。
4. 幂等/回退：目标状态 ≤ 当前状态（重复或回退）跳过处理，计入 `successCount`，不重复触发事件。
5. 各时间字段按状态按需下发，对应状态必传字段缺失时该条计入 `failItems`。

---

## 二、注意事项

1. **入站接口鉴权**：由上层网关统一签名/校验，调用方需在请求头中传入 `api-caller` 与 `api-token`。
2. **出站接口**：我方 -> APS 的出站接口（`ApsClientApi.soIssueSync` / `ApsClientApi.soCancelSync`）已对齐《海外销售订单接口文档》，经出口网关 `/external/mom-cloud/aps` 前缀转发至 APS `/api/abroad/batchOrder`（下发）、`/api/abroad/cancelOrder`（作废）。
3. **批量回传限流**：入站批量接口单次上限 100 条，APS 侧大批量回传请分批调用；上限可经 nacos `aps.receive.batch-size-limit` 调整。
