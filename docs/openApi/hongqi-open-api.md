# 红旗系统开放接口文档

> 本目录文档对应 `app-ais-export-vehicle-adapter/src/main/java/com/leapmotor/export/vehicle/controller/open/hongqi/HongqiOpenController.java`。
> 所有接口均为红旗系统向我方（零跑 AIS 出口平台）发起入站调用，鉴权/签名由上层网关统一处理。

---

## 基础信息

### 公网地址

| 环境 | 地址前缀 |
|------|----------|
| 测试环境 | `https://mdmapitest-external.leapmotor.com/aisapi` |
| 正式环境 | `https://mdmapi-external.leapmotor.com/aisapi` |

### 通用请求头

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | 固定 `application/json` |
| api-caller | 是 | 接口平台分配（测试和线上一致） |
| api-token | 是 | 接口平台分配（测试和线上不一致） |

### 通用响应体

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码：`200` 成功，非 `200` 失败 |
| msg | string | 提示信息 |
| data | object | 业务数据，具体见各接口 |
| success | boolean | 是否成功：`true` / `false` |

---

## 1. 红旗 CBU 订单采购申请（创建草稿 PO 订单）

### 接口说明

| 项目 | 内容 |
|------|------|
| 接口名称 | 红旗 CBU 订单采购申请 |
| 功能描述 | 红旗系统将 CBU 采购申请同步给我方，创建草稿 PO 订单（主表 + 明细联合保存） |
| 请求方式 | POST |
| 请求路径 | `/vehicle/openApi/hongqi/purchaseOrder/sync` |

### 请求地址

```text
测试环境：https://mdmapitest-external.leapmotor.com/aisapi/vehicle/openApi/hongqi/purchaseOrder/sync
正式环境：https://mdmapi-external.leapmotor.com/aisapi/vehicle/openApi/hongqi/purchaseOrder/sync
```

### 请求参数

#### 请求体（Body）

| 字段名 | 类型 | 必填 | 长度/格式 | 说明 |
|--------|------|------|-----------|------|
| poNo | string | 是 | ≤30 | PO 单号，全局唯一 |
| customerCode | string | 是 | ≤30 | 客户编码：`HQ01` 红旗股份 / `HQ02` 红旗进出口（外部客户编码） |
| planVersion | string | 是 | ≤30 | 计划版本号，例 `20260305V1` |
| orderDetailList | array | 是 | 非空 | 订单明细列表 |

#### 订单明细对象 orderDetailList

| 字段名 | 类型 | 必填 | 长度/格式 | 说明 |
|--------|------|------|-----------|------|
| detailNumber | string | 是 | ≤30 | 订单明细编号，同一 PO 内唯一 |
| materialCode | string | 是 | ≤18 | 18 位零跑物料编码（同步物料编码） |
| demandQuantity | decimal | 是 | - | 需求数量 |
| vinYear | string | 否 | ≤10 | VIN 码年份（YYYY） |
| expectedOfflineDate | string | 是 | ≤10 | 期望下线日期（YYYYMMDD） |

### 请求示例

```json
{
  "poNo": "PO-HQ-20260305001",
  "customerCode": "HQ01",
  "planVersion": "20260305V1",
  "orderDetailList": [
    {
      "detailNumber": "D001",
      "materialCode": "34022021A05001005",
      "demandQuantity": 10,
      "vinYear": "2026",
      "expectedOfflineDate": "20260715"
    },
    {
      "detailNumber": "D002",
      "materialCode": "34022021A05001006",
      "demandQuantity": 5,
      "vinYear": "2026",
      "expectedOfflineDate": "20260720"
    }
  ]
}
```

### 响应示例

#### 成功

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "abc123def456ghi789",
  "success": true
}
```

#### 失败（参数校验失败）

```json
{
  "code": 600,
  "msg": "PO单号不能为空",
  "data": null,
  "success": false
}
```

### 业务校验

1. `poNo`、`customerCode`、`planVersion` 必填。
2. `orderDetailList` 非空，且每条明细中 `detailNumber`、`materialCode`、`demandQuantity`、`expectedOfflineDate` 必填。
3. `poNo` 全局唯一，重复提交按业务幂等策略处理。

---

## 2. 接收红旗质检结果回传

### 接口说明

| 项目 | 内容 |
|------|------|
| 接口名称 | 接收红旗质检结果回传 |
| 功能描述 | 红旗系统将 CBU 车辆的质检结果回传给我方，我方根据 VIN 码更新 SO 质检状态 |
| 请求方式 | POST |
| 请求路径 | `/vehicle/openApi/hongqi/vehicleCbuSo/receiveQcResult` |

### 请求地址

```text
测试环境：https://mdmapitest-external.leapmotor.com/aisapi/vehicle/openApi/hongqi/vehicleCbuSo/receiveQcResult
正式环境：https://mdmapi-external.leapmotor.com/aisapi/vehicle/openApi/hongqi/vehicleCbuSo/receiveQcResult
```

### 请求参数

#### 请求体（Body）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| vin | string | 是 | VIN 码 |
| result | int | 是 | 质检结果：`10` 通过 / `20` 不通过 / `30` 未收到车辆 |
| remark | string | 否 | 质检说明 |
| qcTime | string | 否 | 质检反馈时间，格式 `yyyy-MM-dd HH:mm:ss` |

#### result 取值说明

| result | 含义 |
|--------|------|
| 10 | 通过 |
| 20 | 不通过 |
| 30 | 未收到车辆 |

### 请求示例

#### 质检通过

```json
{
  "vin": "LSVAG2180E2100001",
  "result": 10,
  "remark": "外观、底盘、路试均合格",
  "qcTime": "2026-07-17 14:30:00"
}
```

#### 质检不通过

```json
{
  "vin": "LSVAG2180E2100002",
  "result": 20,
  "remark": "右侧漆面划伤，需返修",
  "qcTime": "2026-07-17 15:00:00"
}
```

#### 未收到车辆

```json
{
  "vin": "LSVAG2180E2100003",
  "result": 30,
  "remark": "物流未到店",
  "qcTime": "2026-07-17 16:00:00"
}
```

### 响应示例

#### 成功

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null,
  "success": true
}
```

#### 失败（VIN 不存在）

```json
{
  "code": 600,
  "msg": "车辆不存在",
  "data": null,
  "success": false
}
```

### 业务校验

1. `vin`、`result` 必填。
2. `result` 仅支持 `10`、`20`、`30` 三个值，其他值会被拒绝。
3. 根据 `vin` 匹配对应 SO 车辆明细，更新质检结果、质检说明与质检反馈时间。

---

## 注意事项

1. 所有接口均为开放接口，鉴权/签名由上层网关统一处理，调用方需在请求头中传入接口平台分配的 `api-caller` 与 `api-token`。
2. 测试环境与正式环境的 `api-token` 不一致，请按部署环境替换。
3. 当前采购申请接口契约按技术评审 01 节点 §三 接口字段定义实现，若红旗侧契约调整，请求字段可能同步微调。
4. 建议红旗侧按车辆逐条回传质检结果，我方按 VIN 逐笔处理。
