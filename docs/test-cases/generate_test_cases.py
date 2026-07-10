"""保函管理三个 Controller 的测试用例生成脚本。

设计依据:
- docs/保函管理-设计文档.md (§3 业务, §4 状态机, §8 接口, §10 异常)
- 控制器源码:
  * ExportVehicleLetterGuaranteeAppController (/app/basic/exportVehicleLetterGuarantee/*)
  * ExportVehicleLetterGuaranteeController (/web/basic/exportVehicleLetterGuarantee/*)
  * ExportVehicleLetterGuaranteeRecordController (/web/basic/exportVehicleLetterGuaranteeRecord/*)

输出:
  docs/test-cases/ExportVehicleLetterGuaranteeAppController_测试用例_<date>.xlsx
  docs/test-cases/ExportVehicleLetterGuaranteeController_测试用例_<date>.xlsx
  docs/test-cases/ExportVehicleLetterGuaranteeRecordController_测试用例_<date>.xlsx
"""
from openpyxl import Workbook
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter
from datetime import datetime
import os

HEADER_FILL = PatternFill("solid", start_color="D9E1F2")
HEADER_FONT = Font(bold=True, name="Microsoft YaHei", size=11)
HEADER_ALIGN = Alignment(horizontal="center", vertical="center", wrap_text=True)
CELL_ALIGN = Alignment(horizontal="left", vertical="center", wrap_text=True)
THIN_BORDER = Border(
    left=Side(style="thin", color="BFBFBF"),
    right=Side(style="thin", color="BFBFBF"),
    top=Side(style="thin", color="BFBFBF"),
    bottom=Side(style="thin", color="BFBFBF"),
)


def style_header_row(ws, row_idx, last_col):
    for col in range(1, last_col + 1):
        cell = ws.cell(row=row_idx, column=col)
        cell.fill = HEADER_FILL
        cell.font = HEADER_FONT
        cell.alignment = HEADER_ALIGN
        cell.border = THIN_BORDER


def write_data_row(ws, row_idx, values):
    for col_idx, value in enumerate(values, 1):
        cell = ws.cell(row=row_idx, column=col_idx, value=value)
        cell.alignment = CELL_ALIGN
        cell.border = THIN_BORDER


def autosize_columns(ws, min_width=14, max_width=55):
    for col in ws.columns:
        max_len = 0
        col_letter = get_column_letter(col[0].column)
        for cell in col:
            val = cell.value
            if val is None:
                continue
            length = max(len(str(line)) for line in str(val).split("\n"))
            max_len = max(max_len, length)
        ws.column_dimensions[col_letter].width = max(min(max_len + 2, max_width), min_width)


def add_overview_sheet(wb, module_name, scope, methods, total_cases):
    ws = wb.active
    ws.title = "概览"
    info = [
        ("项目", "内容"),
        ("功能模块", module_name),
        ("测试范围", scope),
        ("测试方法", methods),
        ("创建日期", datetime.now().strftime("%Y-%m-%d")),
        ("用例总数", total_cases),
        ("关联设计文档", "docs/保函管理-设计文档.md"),
    ]
    for row_idx, (k, v) in enumerate(info, 1):
        ws.cell(row=row_idx, column=1, value=k)
        ws.cell(row=row_idx, column=2, value=v)
    style_header_row(ws, 1, 2)
    ws.cell(row=2, column=1).font = Font(bold=True)
    for row_idx in range(2, len(info) + 1):
        ws.cell(row=row_idx, column=2).alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
    ws.column_dimensions["A"].width = 18
    ws.column_dimensions["B"].width = 70


def add_api_list_sheet(wb, apis):
    """apis: [(path, summary, role)]"""
    ws = wb.create_sheet("接口清单")
    headers = ["序号", "HTTP 方法", "接口路径", "接口说明", "权限标识", "备注"]
    for col_idx, header in enumerate(headers, 1):
        ws.cell(row=1, column=col_idx, value=header)
    style_header_row(ws, 1, len(headers))
    for row_idx, (idx, method, path, summary, perm, note) in enumerate(apis, 2):
        write_data_row(ws, row_idx, [idx, method, path, summary, perm, note])
    autosize_columns(ws, min_width=10, max_width=60)
    ws.column_dimensions["B"].width = 12
    ws.column_dimensions["C"].width = 55
    ws.column_dimensions["D"].width = 45
    ws.column_dimensions["E"].width = 50
    ws.column_dimensions["F"].width = 35


def add_scenario_sheet(wb, sheet_name, cases):
    """cases: [{name, pre, steps, expected, data, priority, note}]"""
    ws = wb.create_sheet(sheet_name)
    headers = ["用例ID", "场景名称", "前置条件", "操作步骤", "预期结果", "测试数据", "优先级", "备注"]
    for col_idx, header in enumerate(headers, 1):
        ws.cell(row=1, column=col_idx, value=header)
    style_header_row(ws, 1, len(headers))
    for row_idx, tc in enumerate(cases, 2):
        tc_id = f"TC{row_idx - 1:03d}"
        write_data_row(ws, row_idx, [
            tc_id,
            tc["name"],
            tc["pre"],
            tc["steps"],
            tc["expected"],
            tc["data"],
            tc.get("priority", "中"),
            tc.get("note", ""),
        ])
    autosize_columns(ws, min_width=12, max_width=70)
    ws.column_dimensions["A"].width = 10
    ws.column_dimensions["B"].width = 28
    ws.column_dimensions["G"].width = 8
    for row in range(2, ws.max_row + 1):
        ws.row_dimensions[row].height = 60


def add_state_machine_sheet(wb, cases):
    """状态转换用例: [{name, from, event, to, expected, data, priority, note}]"""
    ws = wb.create_sheet("状态转换")
    headers = ["用例ID", "场景名称", "起始状态", "触发事件/操作", "预期新状态", "预期结果/异常", "测试数据", "优先级", "备注"]
    for col_idx, header in enumerate(headers, 1):
        ws.cell(row=1, column=col_idx, value=header)
    style_header_row(ws, 1, len(headers))
    for row_idx, tc in enumerate(cases, 2):
        tc_id = f"TC{row_idx - 1:03d}"
        write_data_row(ws, row_idx, [
            tc_id,
            tc["name"],
            tc["from"],
            tc["event"],
            tc["to"],
            tc["expected"],
            tc["data"],
            tc.get("priority", "高"),
            tc.get("note", ""),
        ])
    autosize_columns(ws, min_width=12, max_width=60)
    ws.column_dimensions["A"].width = 10
    ws.column_dimensions["B"].width = 30
    ws.column_dimensions["C"].width = 18
    ws.column_dimensions["D"].width = 28
    ws.column_dimensions["E"].width = 18
    ws.column_dimensions["F"].width = 50
    ws.column_dimensions["G"].width = 40
    ws.column_dimensions["H"].width = 8
    for row in range(2, ws.max_row + 1):
        ws.row_dimensions[row].height = 70


def add_decision_table_sheet(wb, sheet_name, cases, conditions, actions):
    """决策表: [{id, scenario, conditions_combo, actions_combo, data, priority}]"""
    ws = wb.create_sheet(sheet_name)
    headers = ["用例ID", "场景名称"] + conditions + ["预期动作"] + ["预期结果"] + ["测试数据", "优先级"]
    for col_idx, header in enumerate(headers, 1):
        ws.cell(row=1, column=col_idx, value=header)
    style_header_row(ws, 1, len(headers))
    for row_idx, tc in enumerate(cases, 2):
        tc_id = f"TC{row_idx - 1:03d}"
        write_data_row(ws, row_idx, [
            tc_id,
            tc["scenario"],
            *tc["conditions"],
            tc["actions"],
            tc["expected"],
            tc["data"],
            tc.get("priority", "高"),
        ])
    autosize_columns(ws, min_width=12, max_width=50)
    ws.column_dimensions["A"].width = 10
    ws.column_dimensions["B"].width = 32
    for row in range(2, ws.max_row + 1):
        ws.row_dimensions[row].height = 75


# ============================================================
# File 1: AppController
# ============================================================
APP_APIS = [
    (1, "POST", "/app/basic/exportVehicleLetterGuarantee/validate", "资金校验 — PO 提交前置步骤", "(内部 HTTP,无 @ResourcePermission)", "设计文档 §3.4.2"),
    (2, "POST", "/app/basic/exportVehicleLetterGuarantee/freeze", "单 PO 冻结", "(内部 HTTP,无 @ResourcePermission)", "设计文档 §3.4.3"),
    (3, "POST", "/app/basic/exportVehicleLetterGuarantee/batchFreeze", "批量 PO 冻结", "(内部 HTTP,无 @ResourcePermission)", "每 PO 独立事务"),
    (4, "POST", "/app/basic/exportVehicleLetterGuarantee/unfreeze", "单 PO 解冻 — PO 驳回", "(内部 HTTP,无 @ResourcePermission)", "逐张全额释放"),
    (5, "POST", "/app/basic/exportVehicleLetterGuarantee/batchUnfreeze", "批量 PO 解冻", "(内部 HTTP,无 @ResourcePermission)", "每 PO 独立事务"),
    (6, "POST", "/app/basic/exportVehicleLetterGuarantee/transferToReceivable", "单 PO 转应收 — 质检通过", "(内部 HTTP,无 @ResourcePermission)", "record_type=30"),
    (7, "POST", "/app/basic/exportVehicleLetterGuarantee/releaseReceivable", "单 PO 释放应收 — SAP 清账", "(内部 HTTP,无 @ResourcePermission)", "record_type=40"),
    (8, "POST", "/app/basic/exportVehicleLetterGuarantee/adjustFreeze", "调增补冻 — PO 刷新价格(总金额上升)", "(内部 HTTP,无 @ResourcePermission)", "设计文档 §3.5.2"),
    (9, "POST", "/app/basic/exportVehicleLetterGuarantee/adjustUnfreeze", "调减退差 — PO 刷新价格(总金额下降)", "(内部 HTTP,无 @ResourcePermission)", "设计文档 §3.5.3"),
]

APP_SCENARIOS = [
    # -------- /validate --------
    {
        "name": "validate — 资金校验通过(可用保函总额 ≥ 订单总金额)",
        "pre": "客户 A 已审核通过 3 张有效保函,合计可用金额 370 万;订单总金额 200 万,创建时间 today",
        "steps": "1. 内部 HTTP 调用 POST /app/basic/exportVehicleLetterGuarantee/validate\n2. 传入 FreezeReq(customerId=A, orderType=CBU, orderId=O101, orderNo=PO101, requiredAmount=2000000, orderCreateTime=today)",
        "expected": "响应 success;data.passed=true;data.failCode=空;data.totalAvailable=3700000;data.candidates 含 3 张保函(失效日期升序、可用金额降序排序)",
        "data": "customerId=A, orderType=CBU, requiredAmount=2000000, orderCreateTime=today",
        "priority": "高",
        "note": "设计文档 §3.4.2 a~f 全通过",
    },
    {
        "name": "validate — 客户维度资金校验类型=1 无校验(不走保函)",
        "pre": "客户 A 资金校验类型=1(无);保函服务不被调用",
        "steps": "1. PO 模块前置读客户主数据,资金校验类型=1\n2. 直接跳过 /validate 调用",
        "expected": "无调用;不报错",
        "data": "客户 A, 资金校验类型=1",
        "priority": "中",
        "note": "设计文档 §3.4.2 前置",
    },
    {
        "name": "validate — 资金校验失败(a~e 任一不满足,可用保函为 0)",
        "pre": "客户 A 名下保函均未生效(lg_status≠20)或客户维度无保函",
        "steps": "1. 内部 HTTP 调用 POST /validate\n2. 传入 FreezeReq(customerId=A, orderType=CBU, orderId=O101, orderNo=PO101, requiredAmount=2000000, orderCreateTime=today)",
        "expected": "响应 success;data.passed=false;data.failCode=LG_NO_AVAILABLE;data.failMessage=提交失败,资金校验未通过,请联系商务;candidates 为空",
        "data": "客户 A 名下无可用保函",
        "priority": "高",
        "note": "failCode=LG_NO_AVAILABLE",
    },
    {
        "name": "validate — 资金校验失败(可用保函总额 < 订单总金额)",
        "pre": "客户 A 已审核通过 1 张有效保函,可用 50 万;订单总金额 200 万",
        "steps": "1. 内部 HTTP 调用 POST /validate\n2. 传入 requiredAmount=2000000",
        "expected": "响应 success;data.passed=false;data.failCode=LG_INSUFFICIENT_FUNDS;data.failMessage=提交失败,资金校验未通过,请联系商务;totalAvailable=500000 < requiredAmount=2000000",
        "data": "可用 50 万 < 订单 200 万",
        "priority": "高",
        "note": "f 项失败",
    },
    {
        "name": "validate — 订单类型与保函不匹配(LG_ORDER_TYPE_MISMATCH)",
        "pre": "客户 A 所有保函 orderType=SKD;传入 orderType=CBU",
        "steps": "1. 内部 HTTP 调用 POST /validate\n2. 传入 orderType=CBU",
        "expected": "data.passed=false;data.failCode=LG_ORDER_TYPE_MISMATCH;data.failMessage=订单类型与可用保函不匹配",
        "data": "保函 orderType=SKD,订单 orderType=CBU",
        "priority": "高",
        "note": "a 项失败",
    },
    {
        "name": "validate — 失效日期小于订单创建时间+coverDays 过滤掉",
        "pre": "保函 A 失效 2026-06-30,订单创建时间 2026-04-15,coverDays=100 → 阈值 2026-07-24",
        "steps": "1. 内部 HTTP 调用 POST /validate\n2. 传入 orderCreateTime=2026-04-15",
        "expected": "保函 A 不在 candidates 中;其余可用保函合计 ≥ requiredAmount 时通过,否则失败",
        "data": "失效日期 2026-06-30 < 阈值 2026-07-24",
        "priority": "高",
        "note": "e 项过滤",
    },
    # -------- /freeze --------
    {
        "name": "freeze — 单 PO 全量拆冻成功(命中优先级排序)",
        "pre": "保函 A 失效 2026-08-19,可用 120;保函 B 失效 2026-08-31,可用 150;保函 C 失效 2026-08-31,可用 100;订单总金额 200 万",
        "steps": "1. 内部 HTTP 调用 POST /freeze\n2. 传入 FreezeReq(customerId=A, orderType=CBU, orderId=O101, orderNo=PO101, poNo=PO101, requiredAmount=2000000, orderCreateTime=2026-04-15)",
        "expected": "响应 success;data.success=true;data.totalFrozenAmount=2000000;frozenItems 命中保函 A 120 万 + 保函 B 80 万(失效升序 → 金额降序);3 张保函主表占用/可用金额同步更新;3 条冻结流水 record_type=10 写入",
        "data": "PO101, requiredAmount=2000000",
        "priority": "高",
        "note": "设计文档 §3.4.7 示例",
    },
    {
        "name": "freeze — 单 PO 冻结成功(单张保函即可覆盖)",
        "pre": "客户 A 有 1 张可用保函,可用 500 万 ≥ 订单 200 万",
        "steps": "1. 内部 HTTP 调用 POST /freeze\n2. 传入 requiredAmount=2000000",
        "expected": "data.success=true;frozenItems 仅 1 项(该保函 200 万);主表 available-=200 万、occupied+=200 万;写 1 条 record_type=10 流水",
        "data": "PO101, requiredAmount=2000000",
        "priority": "高",
        "note": "逐步拆冻命中首张即达标",
    },
    {
        "name": "freeze — 任一保函冻结失败整体回滚(all-or-nothing)",
        "pre": "保函 A 可用 120 万、保函 B 可用 80 万,前 2 张冻结成功,第 3 张保函 C 在事务中段因并发被锁定失败",
        "steps": "1. 模拟并发:保函 C 在拆冻过程中被另一事务锁定\n2. 内部 HTTP 调用 POST /freeze\n3. requiredAmount=3000000",
        "expected": "data.success=false;failCode=LG_BATCH_FREEZE_FAILED;事务回滚,保函 A、B 主表与流水还原(available/occupied 未变,无 record_type=10 写入)",
        "data": "PO101, requiredAmount=3000000",
        "priority": "高",
        "note": "@Transactional(rollbackFor=Exception.class)",
    },
    {
        "name": "freeze — 保函可用金额不足(LG_INSUFFICIENT_FUNDS)",
        "pre": "候选保函合计可用 50 万 < 订单 200 万",
        "steps": "1. 内部 HTTP 调用 POST /freeze\n2. 传入 requiredAmount=2000000",
        "expected": "data.success=false;failCode=LG_INSUFFICIENT_FUNDS;failMessage=提交失败,资金校验未通过,请联系商务;事务整体回滚",
        "data": "可用 50 万 < 订单 200 万",
        "priority": "高",
        "note": "validate 已通过但 freeze 阶段可用被消耗/状态变更的兜底",
    },
    {
        "name": "freeze — 必填字段校验(customerId/orderType/requiredAmount/orderId/orderNo 为空)",
        "pre": "入参 FreezeReq 各字段为空或 null",
        "steps": "1. 内部 HTTP 调用 POST /freeze\n2. customerId/orderType/requiredAmount/orderId/orderNo 任一为空",
        "expected": "HTTP 400;由 @Schema(requiredMode=REQUIRED) 触发校验失败,返回参数错误",
        "data": "FreezeReq 各字段为空",
        "priority": "中",
        "note": "必填项校验",
    },
    # -------- /batchFreeze --------
    {
        "name": "batchFreeze — 多 PO 全部冻结成功",
        "pre": "3 个 PO 单(PO101/PO102/PO103),各自客户/金额合法;客户保函充足",
        "steps": "1. 内部 HTTP 调用 POST /batchFreeze\n2. 传入 List<FreezeReq> 含 3 个 PO",
        "expected": "响应 success;data 为 List<FreezeResp>,每个元素 success=true;totalFrozenAmount 与 frozenItems 逐 PO 独立返回",
        "data": "PO101/PO102/PO103 各 100 万",
        "priority": "高",
        "note": "每 PO 独立事务",
    },
    {
        "name": "batchFreeze — 部分 PO 失败,其他 PO 仍成功(每 PO 独立事务)",
        "pre": "3 个 PO,PO102 客户保函不足",
        "steps": "1. 内部 HTTP 调用 POST /batchFreeze\n2. 传入 3 个 FreezeReq",
        "expected": "data[0].success=true(PO101),data[1].success=false, failCode=LG_INSUFFICIENT_FUNDS(PO102),data[2].success=true(PO103);PO102 事务回滚,PO101/PO103 数据生效",
        "data": "PO101/PO102/PO103, PO102 客户保函不足",
        "priority": "高",
        "note": "PO 独立事务隔离",
    },
    {
        "name": "batchFreeze — 入参 List 为空",
        "pre": "List<FreezeReq> 为空集合",
        "steps": "1. 内部 HTTP 调用 POST /batchFreeze\n2. 传入 []",
        "expected": "响应 success;data 为空列表 [];不报错",
        "data": "[]",
        "priority": "低",
        "note": "边界场景",
    },
    # -------- /unfreeze --------
    {
        "name": "unfreeze — PO 驳回全量解冻(逐张全额)",
        "pre": "PO101 在保函 A、B 上分别冻结 120 万、80 万,占用金额合计 200 万",
        "steps": "1. 内部 HTTP 调用 POST /unfreeze\n2. 传入 UnfreezeReq(poNo=PO101, orderId=O101, reason=PO 驳回)",
        "expected": "响应 success;保函 A 占用-=120 万、可用+=120 万;保函 B 占用-=80 万、可用+=80 万;2 条 record_type=20 解冻流水写入(transactionAmount 与冻结时一致)",
        "data": "poNo=PO101, orderId=O101, reason=PO 驳回",
        "priority": "高",
        "note": "全额解冻,不解冻单张",
    },
    {
        "name": "unfreeze — PO 解冻失败整体回滚(任一张保函解冻失败)",
        "pre": "PO101 在保函 A、B 上有冻结;模拟保函 B 主表行被另一事务长期持有锁",
        "steps": "1. 模拟并发:保函 B 行被锁\n2. 内部 HTTP 调用 POST /unfreeze",
        "expected": "HTTP 5xx 或业务失败;事务整体回滚,保函 A 数据还原;2 条 record_type=20 流水均回滚",
        "data": "poNo=PO101, orderId=O101",
        "priority": "高",
        "note": "all-or-nothing",
    },
    {
        "name": "unfreeze — PO 单号无任何冻结记录",
        "pre": "poNo=PO_NOT_EXIST 主表无冻结明细",
        "steps": "1. 内部 HTTP 调用 POST /unfreeze\n2. 传入 poNo=PO_NOT_EXIST",
        "expected": "BizException;无可解冻记录(交易明细表无 record_type=10 流水)",
        "data": "poNo=PO_NOT_EXIST",
        "priority": "中",
        "note": "空候选集",
    },
    # -------- /batchUnfreeze --------
    {
        "name": "batchUnfreeze — 多 PO 全部解冻成功",
        "pre": "PO101/PO102/PO103 各自有冻结明细;客户账户正常",
        "steps": "1. 内部 HTTP 调用 POST /batchUnfreeze\n2. 传入 List<UnfreezeReq> 3 个 PO",
        "expected": "data 为 List<UnfreezeResp>,3 个元素 success=true;3 个 PO 各自的冻结明细逐张解冻并写 record_type=20 流水",
        "data": "PO101/PO102/PO103",
        "priority": "高",
        "note": "批量驳回",
    },
    {
        "name": "batchUnfreeze — 部分 PO 失败不影响其他 PO",
        "pre": "PO102 客户账户异常导致解冻失败",
        "steps": "1. 内部 HTTP 调用 POST /batchUnfreeze\n2. 传入 3 个 PO",
        "expected": "data[1].success=false, failCode 非空;PO101/PO103 数据生效;PO102 事务回滚",
        "data": "PO101/PO102/PO103",
        "priority": "高",
        "note": "每 PO 独立事务",
    },
    # -------- /transferToReceivable --------
    {
        "name": "transferToReceivable — 质检通过成功转应收",
        "pre": "PO101/SO_S001 已冻结 200 万(在保函 A 120 万 + 保函 B 80 万),VIN 已生成",
        "steps": "1. 内部 HTTP 调用 POST /transferToReceivable\n2. 传入 TransferReq(poNo=PO101, soNo=SO_S001, orderId=O101, frozenAmount=2000000, vin=LV..., reason=质检通过)",
        "expected": "响应 success;占用→应收:保函 A occupied-=120 万、receivable+=120 万;保函 B occupied-=80 万、receivable+=80 万;2 条 record_type=30 转应收流水(含 before/after receivable)",
        "data": "poNo=PO101, soNo=SO_S001, frozenAmount=2000000, vin=LV...",
        "priority": "高",
        "note": "record_type=30",
    },
    {
        "name": "transferToReceivable — PO 下无冻结明细(fail)",
        "pre": "PO_NO_FREEZE 在 record_type=10 中无任何流水",
        "steps": "1. 内部 HTTP 调用 POST /transferToReceivable\n2. 传入 poNo=PO_NO_FREEZE",
        "expected": "BizException;无可转应收记录",
        "data": "poNo=PO_NO_FREEZE, soNo=SO_X",
        "priority": "中",
        "note": "边界场景",
    },
    {
        "name": "transferToReceivable — 必填字段校验(poNo/soNo/orderId/frozenAmount/vin)",
        "pre": "TransferReq 任一必填字段为 null 或空",
        "steps": "1. 内部 HTTP 调用 POST /transferToReceivable\n2. 必填项缺失",
        "expected": "HTTP 400;@Schema(requiredMode=REQUIRED) 校验失败",
        "data": "TransferReq 必填项缺失",
        "priority": "中",
        "note": "必填校验",
    },
    # -------- /releaseReceivable --------
    {
        "name": "releaseReceivable — SAP 清账释放应收",
        "pre": "PO101/SO_S001 已转应收 200 万(在保函 A 120 + 保函 B 80),SAP 已完成清账",
        "steps": "1. 内部 HTTP 调用 POST /releaseReceivable\n2. 传入 ReleaseReq(poNo=PO101, soNo=SO_S001, orderId=O101, reason=SAP清账单号 XYZ)",
        "expected": "响应 success;保函 A/B receivable-=对应金额(核销完毕);2 条 record_type=40 释放应收流水写入",
        "data": "poNo=PO101, soNo=SO_S001, reason=SAP清账单号 XYZ",
        "priority": "高",
        "note": "record_type=40",
    },
    {
        "name": "releaseReceivable — 释放失败整体回滚(任一张保函释放失败)",
        "pre": "保函 B 在事务中段被锁",
        "steps": "1. 模拟并发\n2. 内部 HTTP 调用 POST /releaseReceivable",
        "expected": "BizException;事务整体回滚,保函 A 数据还原,record_type=40 流水不写入",
        "data": "poNo=PO101, soNo=SO_S001",
        "priority": "高",
        "note": "all-or-nothing",
    },
    # -------- /adjustFreeze --------
    {
        "name": "adjustFreeze — 调增补冻(差额拆冻,单 PO,可能多张保函)",
        "pre": "PO101 已冻结 200 万;新价格导致总金额上升 50 万;客户保函合计可用 ≥ 50 万",
        "steps": "1. 内部 HTTP 调用 POST /adjustFreeze\n2. 传入 AdjustFreezeReq(customerId, orderType=CBU, orderId, orderNo, poNo=PO101, amountDiff=500000, reason=刷新价格调增)",
        "expected": "响应 success;data.success=true;totalFrozenAmount=500000;frozenItems 按失效升序+可用降序拆冻到 1~N 张保函;主表可用-=占用+=;对应 record_type=10 流水(冻结类型,因本质是补冻)",
        "data": "poNo=PO101, amountDiff=500000",
        "priority": "高",
        "note": "设计文档 §3.5.2",
    },
    {
        "name": "adjustFreeze — amountDiff 必须大于 0",
        "pre": "amountDiff=0 或 amountDiff<0",
        "steps": "1. 内部 HTTP 调用 POST /adjustFreeze\n2. 传入 amountDiff=0",
        "expected": "BizException;amountDiff 必须大于 0",
        "data": "amountDiff=0",
        "priority": "中",
        "note": "前置校验",
    },
    {
        "name": "adjustFreeze — 候选保函可用合计 < amountDiff",
        "pre": "客户可用保函合计 10 万 < amountDiff 50 万",
        "steps": "1. 内部 HTTP 调用 POST /adjustFreeze\n2. 传入 amountDiff=500000",
        "expected": "BizException;failCode=LG_INSUFFICIENT_FUNDS;事务整体回滚",
        "data": "amountDiff=500000,可用 10 万",
        "priority": "高",
        "note": "复用 /freeze 候选查询",
    },
    # -------- /adjustUnfreeze --------
    {
        "name": "adjustUnfreeze — 调减退差(单张保函,失效最晚优先)",
        "pre": "PO101 在保函 A(失效 2026-08-19)冻结 120 万、保函 B(失效 2026-08-31)冻结 80 万;amountDiff=50 万",
        "steps": "1. 内部 HTTP 调用 POST /adjustUnfreeze\n2. 传入 AdjustUnfreezeReq(poNo=PO101, orderId=O101, amountDiff=500000, reason=刷新价格调减)",
        "expected": "响应 success;data.success=true;解冻保函 B 50 万(B 失效日期最晚,且 B 上 PO 占用 80 万 > 50 万);主表 B occupied-=50 万、available+=50 万;写 1 条 record_type=20 解冻流水",
        "data": "poNo=PO101, amountDiff=500000",
        "priority": "高",
        "note": "设计文档 §3.5.3;选失效最晚那张",
    },
    {
        "name": "adjustUnfreeze — 候选均不满足 poOccupied > amountDiff(降级失败)",
        "pre": "PO101 在保函 A(占用 10 万)、保函 B(占用 10 万);amountDiff=50 万(超过任何单张)",
        "steps": "1. 内部 HTTP 调用 POST /adjustUnfreeze\n2. 传入 amountDiff=500000",
        "expected": "BizException;failCode=LG_NO_ADJUST_LG;无可解冻保函满足金额差;事务整体回滚",
        "data": "amountDiff=500000,任何单张占用 ≤ 50 万",
        "priority": "高",
        "note": "设计文档 §3.5.3 第 7 步",
    },
    {
        "name": "adjustUnfreeze — 降级规则:首张不满足 → 次晚保函(降级成功)",
        "pre": "保函 B(失效 2026-08-31)占用 30 万、保函 A(失效 2026-08-19)占用 100 万;amountDiff=50 万",
        "steps": "1. 内部 HTTP 调用 POST /adjustUnfreeze\n2. amountDiff=500000",
        "expected": "data.success=true;保函 B 占用 30 万 ≤ 50 万 被过滤,降级到保函 A 解冻 50 万(A 失效最晚的候选)",
        "data": "B 占用 30 万(被过滤),A 占用 100 万(命中)",
        "priority": "高",
        "note": "降级规则;严格按失效日期 DESC,不随机",
    },
    {
        "name": "adjustUnfreeze — amountDiff 必须大于 0",
        "pre": "amountDiff<=0",
        "steps": "1. 内部 HTTP 调用 POST /adjustUnfreeze\n2. 传入 amountDiff=0",
        "expected": "BizException;amountDiff 必须大于 0",
        "data": "amountDiff=0",
        "priority": "中",
        "note": "前置校验",
    },
    # -------- 通用 --------
    {
        "name": "AppController — 内部 HTTP 鉴权依赖同集群内部服务间互信",
        "pre": "调用方服务与本服务在同集群 Nacos 注册",
        "steps": "1. 集群内 RestTemplate 调用 /freeze 等任一接口\n2. 检查是否依赖 token 透传",
        "expected": "调用成功;无外网访问",
        "data": "同集群内部 HTTP",
        "priority": "中",
        "note": "设计文档 §6.2 / §8.5;当前未挂 @ResourcePermission",
    },
    {
        "name": "AppController — 服务不可达(HTTP 5xx/408)处理",
        "pre": "保函服务实例全部宕机或超时",
        "steps": "1. PO 端调用 /freeze,等待 HTTP 响应\n2. 检查返回状态",
        "expected": "HTTP 5xx 或 408;PO 端业务失败提示\"系统繁忙,请稍后重试\";允许重试(最大 3 次)",
        "data": "服务不可达",
        "priority": "中",
        "note": "设计文档 §8.5.5",
    },
]


# ============================================================
# File 2: 主表 Controller
# ============================================================
MAIN_APIS = [
    (1, "POST", "/web/basic/exportVehicleLetterGuarantee/list", "保函主表列表查询", "web:basic:exportVehicleLetterGuarantee:list", "无条件返回可选列表"),
    (2, "POST", "/web/basic/exportVehicleLetterGuarantee/page", "保函主表分页查询", "web:basic:exportVehicleLetterGuarantee:page", "标准分页"),
    (3, "POST", "/web/basic/exportVehicleLetterGuarantee/get", "保函主表详情", "web:basic:exportVehicleLetterGuarantee:get", "传 id"),
    (4, "POST", "/web/basic/exportVehicleLetterGuarantee/addOrUpdate", "草稿新增/编辑", "web:basic:exportVehicleLetterGuarantee:addOrUpdate", "id 为空=新增"),
    (5, "POST", "/web/basic/exportVehicleLetterGuarantee/delete", "删除(仅草稿)", "web:basic:exportVehicleLetterGuarantee:delete", "传 ids"),
    (6, "POST", "/web/basic/exportVehicleLetterGuarantee/enableOrDisable", "启用/禁用", "web:basic:exportVehicleLetterGuarantee:enableOrDisable", "LpBaseParam.status 校验"),
    (7, "POST", "/web/basic/exportVehicleLetterGuarantee/submit", "提交(草稿→已提交)", "web:basic:exportVehicleLetterGuarantee:submit", "状态机"),
    (8, "POST", "/web/basic/exportVehicleLetterGuarantee/approve", "审核(已提交→通过/驳回)", "web:basic:exportVehicleLetterGuarantee:approve", "状态机"),
    (9, "POST", "/web/basic/exportVehicleLetterGuarantee/cancel", "作废(仅草稿/驳回)", "web:basic:exportVehicleLetterGuarantee:cancel", "状态机"),
    (10, "POST", "/web/basic/exportVehicleLetterGuarantee/expireEarly", "提前失效(审核通过)", "web:basic:exportVehicleLetterGuarantee:expireEarly", "状态机"),
]

MAIN_SCENARIOS = [
    # -------- CRUD --------
    {
        "name": "list — 列表查询(无条件返回)",
        "pre": "数据库已录入若干保函主数据",
        "steps": "1. POST /web/basic/exportVehicleLetterGuarantee/list\n2. 传空 ExportVehicleLetterGuaranteeParam",
        "expected": "data 为 List<LpOptionalResult>,字段含 id/保函编号/客户/状态/金额等可选字段",
        "data": "Param 为空对象 {}",
        "priority": "中",
        "note": "返回 LpOptionalResult",
    },
    {
        "name": "list — 按保函编号模糊查询",
        "pre": "数据库已录入 LG2026 / LG2027 等保函",
        "steps": "1. POST /list\n2. param.lgCode=LG2026",
        "expected": "data 仅含 lgCode LIKE '%LG2026%' 的记录",
        "data": "lgCode=LG2026",
        "priority": "中",
        "note": "模糊匹配",
    },
    {
        "name": "page — 分页查询(pageNum=1, pageSize=10)",
        "pre": "数据库有 25 条保函主数据",
        "steps": "1. POST /page\n2. param.pageNum=1, pageSize=10",
        "expected": "PageResult.total=25;records.size=10;默认按 status DESC + create_time DESC 排序",
        "data": "pageNum=1, pageSize=10",
        "priority": "中",
        "note": "LpBaseDao.buildBasicQueryWrapper 默认排序",
    },
    {
        "name": "page — 多条件组合(客户 + 类型 + 状态)",
        "pre": "数据库有不同 customer/lgType/approvalStatus 的保函",
        "steps": "1. POST /page\n2. param.customerId=A, lgType=PREPAYMENT, approvalStatus=30",
        "expected": "data 仅返回客户 A、类型预付款、审核通过的保函分页结果",
        "data": "customerId=A, lgType=PREPAYMENT, approvalStatus=30",
        "priority": "中",
        "note": "DaoImpl.buildQueryWrapper 多条件拼接",
    },
    {
        "name": "get — 详情查询(存在记录)",
        "pre": "保函 id=LG_001 已存在",
        "steps": "1. POST /get\n2. 传 \"LG_001\"",
        "expected": "data 为 ExportVehicleLetterGuaranteeResp 全字段",
        "data": "id=LG_001",
        "priority": "中",
        "note": "标准详情",
    },
    {
        "name": "get — 详情查询(记录不存在)",
        "pre": "id=LG_NOT_EXIST 不存在",
        "steps": "1. POST /get\n2. 传 \"LG_NOT_EXIST\"",
        "expected": "data 为 null 或抛\"保函不存在\"",
        "data": "id=LG_NOT_EXIST",
        "priority": "中",
        "note": "BizException 保函不存在",
    },
    {
        "name": "addOrUpdate — 草稿新增(成功)",
        "pre": "id 为空,客户/类型/银行/金额/比例/日期等字段合法",
        "steps": "1. POST /addOrUpdate\n2. 传 AddOrUpdateReq(bankName=中国工商银行杭州武林支行, totalAmount=1000000, conversionRatio=1.0, effectiveDate=2026-07-01, expiryDate=2026-12-31, lgType=PREPAYMENT, orderType=CBU)",
        "expected": "data 为新增 id(非空);主表入库,approval_status=10 草稿、lg_status=10 未生效;available_amount = totalAmount*conversionRatio=1000000;occupied_amount=0",
        "data": "bankName=中国工商银行杭州武林支行, totalAmount=1000000",
        "priority": "高",
        "note": "设计文档 §5.1 / §7.1",
    },
    {
        "name": "addOrUpdate — 草稿编辑(更新非金额字段)",
        "pre": "保函 id=LG_001 已存在且为草稿(approval_status=10)",
        "steps": "1. POST /addOrUpdate\n2. 传 AddOrUpdateReq(id=LG_001, bankName=中国建设银行北京中关村支行, ...)",
        "expected": "data=LG_001;主表更新非金额字段(银行/日期/比例等);available/occupied/receivable 不变(因状态仍为草稿)",
        "data": "id=LG_001, bankName=中国建设银行北京中关村支行",
        "priority": "高",
        "note": "编辑场景",
    },
    {
        "name": "addOrUpdate — 必填字段校验(bankName/totalAmount/conversionRatio/effectiveDate/expiryDate)",
        "pre": "AddOrUpdateReq 缺失 bankName 或 totalAmount 为 0",
        "steps": "1. POST /addOrUpdate\n2. bankName=空 / totalAmount=null",
        "expected": "HTTP 400;@NotBlank / @NotNull / @DecimalMin 触发校验失败",
        "data": "缺失 bankName / totalAmount",
        "priority": "中",
        "note": "必填项校验",
    },
    {
        "name": "addOrUpdate — conversionRatio 越界(>1 或 ≤0)",
        "pre": "conversionRatio=1.5 或 conversionRatio=0",
        "steps": "1. POST /addOrUpdate\n2. 传 conversionRatio=1.5",
        "expected": "HTTP 400;@DecimalMax(1.0) / @DecimalMin(0.000001) 校验失败",
        "data": "conversionRatio=1.5",
        "priority": "中",
        "note": "比例越界",
    },
    {
        "name": "addOrUpdate — 生效日期 ≥ 失效日期",
        "pre": "effectiveDate=2026-12-31, expiryDate=2026-06-30",
        "steps": "1. POST /addOrUpdate\n2. 日期顺序颠倒",
        "expected": "BizException;生效日期必须早于失效日期",
        "data": "effectiveDate=2026-12-31, expiryDate=2026-06-30",
        "priority": "高",
        "note": "业务校验",
    },
    {
        "name": "delete — 批量删除(全部为草稿)",
        "pre": "ids=[LG_D1, LG_D2] 均为草稿(approval_status=10)",
        "steps": "1. POST /delete\n2. 传 [\"LG_D1\", \"LG_D2\"]",
        "expected": "响应 success;2 条记录被逻辑删除(delete_status=1)",
        "data": "ids=[LG_D1, LG_D2]",
        "priority": "中",
        "note": "仅草稿可删",
    },
    {
        "name": "delete — 删除已提交/审核通过的保函(失败)",
        "pre": "ids=[LG_APPROVED] 审核通过",
        "steps": "1. POST /delete\n2. 传 [\"LG_APPROVED\"]",
        "expected": "BizException;非草稿不可删除",
        "data": "ids=[LG_APPROVED]",
        "priority": "中",
        "note": "状态门禁",
    },
    {
        "name": "delete — 入参 ids 为空",
        "pre": "ids=[]",
        "steps": "1. POST /delete\n2. 传 []",
        "expected": "BizException 或静默成功(空集合);不允许静默删除",
        "data": "ids=[]",
        "priority": "低",
        "note": "边界场景",
    },
    {
        "name": "enableOrDisable — 启用(idSet + status 合法)",
        "pre": "LpBaseParam.idSet=[LG_E1], status=1",
        "steps": "1. POST /enableOrDisable\n2. 传 LpBaseParam(idSet=[LG_E1], status=1)",
        "expected": "响应 success;LG_E1.status 置为 1(status_desc 写\"启用\")",
        "data": "idSet=[LG_E1], status=1",
        "priority": "中",
        "note": "BooleanEnum.getByCode 校验通过",
    },
    {
        "name": "enableOrDisable — 禁用",
        "pre": "LpBaseParam.idSet=[LG_E1], status=0",
        "steps": "1. POST /enableOrDisable\n2. 传 status=0",
        "expected": "响应 success;LG_E1.status 置为 0(status_desc 写\"禁用\")",
        "data": "idSet=[LG_E1], status=0",
        "priority": "中",
        "note": "禁用操作",
    },
    {
        "name": "enableOrDisable — idSet 为空",
        "pre": "LpBaseParam.idSet 为空",
        "steps": "1. POST /enableOrDisable\n2. idSet=[]",
        "expected": "AjaxResult.fail;请选择要操作的记录",
        "data": "idSet=[]",
        "priority": "中",
        "note": "Controller 内显式 fail",
    },
    {
        "name": "enableOrDisable — status 为 null/不合法",
        "pre": "status=null 或 status=99",
        "steps": "1. POST /enableOrDisable\n2. status=null",
        "expected": "AjaxResult.fail;请选择要操作的状态 / 状态参数错误",
        "data": "status=null / status=99",
        "priority": "中",
        "note": "StatusEnum.matchByStatus 校验",
    },
]

MAIN_STATE_MACHINE = [
    # -------- submit --------
    {
        "name": "submit — 草稿 → 已提交",
        "from": "草稿(approval_status=10)",
        "event": "/submit(id=LG_001)",
        "to": "已提交(approval_status=20)",
        "expected": "成功;submitter_id/submitter_name/submit_time 写入;approval_status=20;lg_status 仍 10",
        "data": "id=LG_001, creator=金融专员 U001",
        "priority": "高",
        "note": "设计文档 §4.1",
    },
    {
        "name": "submit — 已提交重复提交(禁止)",
        "from": "已提交(approval_status=20)",
        "event": "/submit(id=LG_001)",
        "to": "—",
        "expected": "BizException;无效的审核状态转换: 20 → 20",
        "data": "id=LG_001(已提交)",
        "priority": "高",
        "note": "状态机越权",
    },
    {
        "name": "submit — 审核通过再次提交(禁止)",
        "from": "审核通过(approval_status=30)",
        "event": "/submit(id=LG_001)",
        "to": "—",
        "expected": "BizException;无效的审核状态转换: 30 → 20",
        "data": "id=LG_001(审核通过)",
        "priority": "高",
        "note": "终态不可越权",
    },
    {
        "name": "submit — 作废后再次提交(禁止)",
        "from": "作废(approval_status=50)",
        "event": "/submit(id=LG_001)",
        "to": "—",
        "expected": "BizException;无效的审核状态转换: 50 → 20",
        "data": "id=LG_001(作废)",
        "priority": "高",
        "note": "终态不可越权",
    },
    {
        "name": "submit — id 不存在",
        "from": "—",
        "event": "/submit(id=LG_NOT_EXIST)",
        "to": "—",
        "expected": "BizException;保函不存在",
        "data": "id=LG_NOT_EXIST",
        "priority": "中",
        "note": "通用前置",
    },
    # -------- approve --------
    {
        "name": "approve — 已提交 → 审核通过(approved=true)",
        "from": "已提交(approval_status=20)",
        "event": "/approve(id=LG_001, approved=true, approveRemark=同意)",
        "to": "审核通过(approval_status=30, lg_status=20 有效)",
        "expected": "成功;approval_status=30、lg_status=20;approver_id/name/approve_time 写入;write approve_remark=同意",
        "data": "id=LG_001, approved=true, approveRemark=同意",
        "priority": "高",
        "note": "审核通过联动 lg_status:10→20",
    },
    {
        "name": "approve — 已提交 → 驳回(approved=false)",
        "from": "已提交(approval_status=20)",
        "event": "/approve(id=LG_001, approved=false, approveRemark=材料不全)",
        "to": "驳回(approval_status=40, lg_status=10 未生效)",
        "expected": "成功;approval_status=40、lg_status 仍 10;approver 字段写入;approve_remark=材料不全",
        "data": "id=LG_001, approved=false, approveRemark=材料不全",
        "priority": "高",
        "note": "驳回不联动 lg_status",
    },
    {
        "name": "approve — 草稿直接审核(禁止)",
        "from": "草稿(approval_status=10)",
        "event": "/approve(id=LG_001)",
        "to": "—",
        "expected": "BizException;无效的审核状态转换: 10 → 30/40",
        "data": "id=LG_001(草稿)",
        "priority": "高",
        "note": "必须先提交",
    },
    {
        "name": "approve — 审核通过再次审核(禁止)",
        "from": "审核通过(approval_status=30)",
        "event": "/approve(id=LG_001)",
        "to": "—",
        "expected": "BizException;无效的审核状态转换: 30 → 30",
        "data": "id=LG_001(已审核通过)",
        "priority": "高",
        "note": "不可重复审核",
    },
    {
        "name": "approve — @EnumValue 校验 approved 仅允许 30/40",
        "from": "—",
        "event": "/approve(id=LG_001, approved=99)",
        "to": "—",
        "expected": "HTTP 400;@EnumValue 校验失败",
        "data": "approved=99",
        "priority": "中",
        "note": "枚举校验",
    },
    # -------- cancel --------
    {
        "name": "cancel — 草稿作废(无校验)",
        "from": "草稿(approval_status=10)",
        "event": "/cancel(id=LG_001, remark=录入错误)",
        "to": "作废(approval_status=50, lg_status=10 未生效)",
        "expected": "成功;approval_status=50;lg_status 保持 10(从未启用);remark 写入",
        "data": "id=LG_001, creator=U001, remark=录入错误",
        "priority": "高",
        "note": "5a 草稿作废",
    },
    {
        "name": "cancel — 驳回作废(无校验)",
        "from": "驳回(approval_status=40)",
        "event": "/cancel(id=LG_001, remark=放弃修改)",
        "to": "作废(approval_status=50, lg_status=10 未生效)",
        "expected": "成功;approval_status=50;lg_status 保持 10;remark 写入",
        "data": "id=LG_001, creator=U001, remark=放弃修改",
        "priority": "高",
        "note": "5b 驳回作废",
    },
    {
        "name": "cancel — 已提交作废(禁止 — 在审中)",
        "from": "已提交(approval_status=20)",
        "event": "/cancel(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函在审核中,不可作废",
        "data": "id=LG_001(已提交)",
        "priority": "高",
        "note": "5c 在审不可作废",
    },
    {
        "name": "cancel — 审核通过作废(禁止 — 走提前失效)",
        "from": "审核通过(approval_status=30)",
        "event": "/cancel(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函已审核通过,不可作废,终止请走\"提前失效\"",
        "data": "id=LG_001(审核通过)",
        "priority": "高",
        "note": "设计文档 §4.1 重点禁止项",
    },
    {
        "name": "cancel — 已作废再次作废(禁止)",
        "from": "作废(approval_status=50)",
        "event": "/cancel(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函已作废,不可重复作废",
        "data": "id=LG_001(已作废)",
        "priority": "高",
        "note": "终态不可越权",
    },
    # -------- expireEarly --------
    {
        "name": "expireEarly — 审核通过 → 失效(lg_status: 20→30)",
        "from": "审核通过(approval_status=30, lg_status=20)",
        "event": "/expireEarly(id=LG_001, reason=银行撤销)",
        "to": "lg_status=30(失效), approval_status 仍 30",
        "expected": "成功;lg_status:20→30;若 available>0 置 0;occupied/receivable 保留;approval_status 不变;写 1 条 record_type=50 失效流水",
        "data": "id=LG_001, creator=U001, reason=银行撤销",
        "priority": "高",
        "note": "设计文档 §3.5.1 / §4.2",
    },
    {
        "name": "expireEarly — 失效后既有占用可继续走完流程",
        "from": "失效(lg_status=30)",
        "event": "AppController /unfreeze(poNo=PO101)",
        "to": "lg_status=30(保持)",
        "expected": "解冻成功;失效后的占用/应收可继续冻结/解冻/转应收/释放应收",
        "data": "lg_status=30 后调用 /unfreeze",
        "priority": "高",
        "note": "设计文档 §4.2 重要语义",
    },
    {
        "name": "expireEarly — 草稿提前失效(禁止)",
        "from": "草稿(approval_status=10)",
        "event": "/expireEarly(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函未审核通过,不可提前失效",
        "data": "id=LG_001(草稿)",
        "priority": "高",
        "note": "状态门禁",
    },
    {
        "name": "expireEarly — 驳回提前失效(禁止)",
        "from": "驳回(approval_status=40)",
        "event": "/expireEarly(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函未审核通过,不可提前失效",
        "data": "id=LG_001(驳回)",
        "priority": "高",
        "note": "状态门禁",
    },
    {
        "name": "expireEarly — 已提交提前失效(禁止)",
        "from": "已提交(approval_status=20)",
        "event": "/expireEarly(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函未审核通过,不可提前失效",
        "data": "id=LG_001(已提交)",
        "priority": "高",
        "note": "状态门禁",
    },
    {
        "name": "expireEarly — 已作废提前失效(禁止)",
        "from": "作废(approval_status=50)",
        "event": "/expireEarly(id=LG_001)",
        "to": "—",
        "expected": "BizException;已作废保函不可提前失效",
        "data": "id=LG_001(已作废)",
        "priority": "高",
        "note": "终态门禁",
    },
    {
        "name": "expireEarly — 已失效再失效(禁止)",
        "from": "lg_status=30",
        "event": "/expireEarly(id=LG_001)",
        "to": "—",
        "expected": "BizException;保函已失效,无需重复失效",
        "data": "id=LG_001(lg_status=30)",
        "priority": "高",
        "note": "幂等保护",
    },
    {
        "name": "expireEarly — @NotBlank reason 校验",
        "from": "—",
        "event": "/expireEarly(id=LG_001, reason=\"\")",
        "to": "—",
        "expected": "HTTP 400;@NotBlank 校验失败",
        "data": "reason=空",
        "priority": "中",
        "note": "必填校验",
    },
    {
        "name": "expireEarly — id 不存在",
        "from": "—",
        "event": "/expireEarly(id=LG_NOT_EXIST)",
        "to": "—",
        "expected": "BizException;保函不存在",
        "data": "id=LG_NOT_EXIST",
        "priority": "中",
        "note": "通用前置",
    },
]


# 通用鉴权场景(移入 MAIN_SCENARIOS)
_EXTRA_AUTH_SCENARIOS = [
    {
        "name": "资源权限校验 — @ResourcePermission 缺失 token",
        "pre": "无登录态/无 token",
        "steps": "1. POST 任意 web 接口\n2. 不携带 Authorization",
        "expected": "HTTP 401;平台鉴权拦截",
        "data": "无 token",
        "priority": "中",
        "note": "Web 端通用鉴权",
    },
    {
        "name": "资源权限校验 — 角色无权限",
        "pre": "红旗商务角色登录,尝试 /approve",
        "steps": "1. 红旗商务 token 调用 /approve\n2. 检查平台 RBAC",
        "expected": "HTTP 403;无权限(红旗商务仅查看)",
        "data": "redflag_business token",
        "priority": "中",
        "note": "设计文档 §9 角色分工",
    },
]
MAIN_SCENARIOS = MAIN_SCENARIOS + _EXTRA_AUTH_SCENARIOS


# ============================================================
# File 3: RecordController
# ============================================================
RECORD_APIS = [
    (1, "POST", "/web/basic/exportVehicleLetterGuaranteeRecord/list", "保函交易明细列表(按保函ID筛选)", "web:basic:exportVehicleLetterGuaranteeRecord:list", "追加写表"),
    (2, "POST", "/web/basic/exportVehicleLetterGuaranteeRecord/page", "保函交易明细分页", "web:basic:exportVehicleLetterGuaranteeRecord:page", "标准分页"),
    (3, "POST", "/web/basic/exportVehicleLetterGuaranteeRecord/get", "保函交易明细详情", "web:basic:exportVehicleLetterGuaranteeRecord:get", "传 id"),
    (4, "POST", "/web/basic/exportVehicleLetterGuaranteeRecord/addOrUpdate", "新增/编辑", "web:basic:exportVehicleLetterGuaranteeRecord:addOrUpdate", "id 为空=新增"),
    (5, "POST", "/web/basic/exportVehicleLetterGuaranteeRecord/delete", "删除", "web:basic:exportVehicleLetterGuaranteeRecord:delete", "设计文档 §11.6 仅追加禁止改/删"),
    (6, "POST", "/web/basic/exportVehicleLetterGuaranteeRecord/enableOrDisable", "启用/禁用", "web:basic:exportVehicleLetterGuaranteeRecord:enableOrDisable", "LpBaseParam.status 校验"),
]

RECORD_SCENARIOS = [
    {
        "name": "list — 按保函 ID 筛选明细列表",
        "pre": "保函 LG_001 下有 10 条 record_type=10/20/30/40/50 流水",
        "steps": "1. POST /web/basic/exportVehicleLetterGuaranteeRecord/list\n2. param.lgId=LG_001",
        "expected": "data 为 List<LpOptionalResult>,含 10 条流水的可选字段(交易类型/金额/前后值/PO 号等)",
        "data": "lgId=LG_001",
        "priority": "高",
        "note": "按保函维度查询",
    },
    {
        "name": "list — 按 recordType(流水类型)筛选",
        "pre": "保函 LG_001 下有冻结 3 条、解冻 2 条、转应收 1 条",
        "steps": "1. POST /list\n2. param.lgId=LG_001, transactionType=10",
        "expected": "data 仅含 record_type=10 冻结流水(3 条)",
        "data": "lgId=LG_001, transactionType=10",
        "priority": "中",
        "note": "流水类型过滤",
    },
    {
        "name": "list — 按 PO 单号反查该 PO 在各保函的流水",
        "pre": "PO101 在保函 A、B 上各产生 1 条冻结流水",
        "steps": "1. POST /list\n2. param.poNo=PO101",
        "expected": "data 含 2 条冻结流水(分别对应保函 A、B)",
        "data": "poNo=PO101",
        "priority": "中",
        "note": "idx_lg_id / idx_po_no 索引覆盖",
    },
    {
        "name": "page — 分页查询(pageNum=1, pageSize=20)",
        "pre": "保函 LG_001 下有 45 条流水",
        "steps": "1. POST /page\n2. param.lgId=LG_001, pageNum=1, pageSize=20",
        "expected": "PageResult.total=45;records.size=20;默认按 create_time DESC 排序(最新在前)",
        "data": "lgId=LG_001, pageNum=1, pageSize=20",
        "priority": "中",
        "note": "分页 + 时间倒序",
    },
    {
        "name": "page — 时间范围筛选(创建时间起止)",
        "pre": "保函 LG_001 在 2026-04 月有 30 条流水",
        "steps": "1. POST /page\n2. param.lgId=LG_001, createTimeStart=2026-04-01, createTimeEnd=2026-04-30",
        "expected": "data 仅含 2026-04 月的流水",
        "data": "lgId=LG_001, 时间范围",
        "priority": "低",
        "note": "时间筛选",
    },
    {
        "name": "get — 详情查询(存在记录)",
        "pre": "record id=R001 已存在",
        "steps": "1. POST /get\n2. 传 \"R001\"",
        "expected": "data 为 ExportVehicleLetterGuaranteeRecordResp 全字段(含 lg_record_code、transaction_type、before/after available/occupied/receivable)",
        "data": "id=R001",
        "priority": "中",
        "note": "标准详情",
    },
    {
        "name": "get — 详情查询(记录不存在)",
        "pre": "id=R_NOT_EXIST 不存在",
        "steps": "1. POST /get\n2. 传 \"R_NOT_EXIST\"",
        "expected": "data 为 null 或抛业务异常",
        "data": "id=R_NOT_EXIST",
        "priority": "中",
        "note": "边界场景",
    },
    {
        "name": "addOrUpdate — 手工新增流水(非业务自动写入)",
        "pre": "id 为空,流水字段齐全",
        "steps": "1. POST /addOrUpdate\n2. 传 AddOrUpdateReq(lgId=LG_001, lgCode=LG2026..., transactionType=10, transactionAmount=100000, beforeAvailable=200000, afterAvailable=100000, beforeOccupied=0, afterOccupied=100000, poNo=PO101)",
        "expected": "data 为新增 record id;流水入库;lg_record_code 按 BK+yyyyMMdd+HHmmss+5位流水 生成且全库唯一",
        "data": "lgId=LG_001, transactionType=10, amount=100000",
        "priority": "中",
        "note": "lg_record_code 唯一性",
    },
    {
        "name": "addOrUpdate — 必填字段校验(transactionType/transactionAmount 为空)",
        "pre": "AddOrUpdateReq 缺失 transactionType 或 transactionAmount",
        "steps": "1. POST /addOrUpdate\n2. 必填项为空",
        "expected": "HTTP 400;@NotNull / @NotBlank 校验失败",
        "data": "缺失必填项",
        "priority": "中",
        "note": "必填校验",
    },
    {
        "name": "delete — 业务约束(交易明细仅追加,禁止删除)",
        "pre": "ids=[R001]",
        "steps": "1. POST /delete\n2. 传 [\"R001\"]",
        "expected": "BizException 或接口设计为禁止;设计文档 §11.6 明示\"交易明细表仅追加,不允许修改/删除\";数据库层也收紧 UPDATE/DELETE 权限",
        "data": "ids=[R001]",
        "priority": "高",
        "note": "审计追溯完整性",
    },
    {
        "name": "delete — 批量删除传入 ids 为空",
        "pre": "ids=[]",
        "steps": "1. POST /delete\n2. 传 []",
        "expected": "BizException;不允许空集合删除",
        "data": "ids=[]",
        "priority": "低",
        "note": "边界场景",
    },
    {
        "name": "enableOrDisable — 启用(idSet + status=1)",
        "pre": "LpBaseParam.idSet=[R001], status=1",
        "steps": "1. POST /enableOrDisable\n2. 传 LpBaseParam(idSet=[R001], status=1)",
        "expected": "响应 success;R001.status 置为 1",
        "data": "idSet=[R001], status=1",
        "priority": "中",
        "note": "启用操作",
    },
    {
        "name": "enableOrDisable — 禁用",
        "pre": "LpBaseParam.idSet=[R001], status=0",
        "steps": "1. POST /enableOrDisable\n2. 传 status=0",
        "expected": "响应 success;R001.status 置为 0",
        "data": "idSet=[R001], status=0",
        "priority": "中",
        "note": "禁用操作",
    },
    {
        "name": "enableOrDisable — idSet 为空",
        "pre": "LpBaseParam.idSet 为空",
        "steps": "1. POST /enableOrDisable\n2. idSet=[]",
        "expected": "AjaxResult.fail;请选择要操作的记录",
        "data": "idSet=[]",
        "priority": "中",
        "note": "Controller 内显式 fail",
    },
    {
        "name": "enableOrDisable — status 为 null",
        "pre": "LpBaseParam.status=null",
        "steps": "1. POST /enableOrDisable\n2. status=null",
        "expected": "AjaxResult.fail;请选择要操作的状态",
        "data": "status=null",
        "priority": "中",
        "note": "BooleanEnum.getByCode 校验",
    },
    {
        "name": "流水一致性 — 业务自动写入与手动新增互不干扰",
        "pre": "保函 LG_001 由 AppController /freeze 自动写 1 条冻结;手动 addOrUpdate 也写 1 条冻结",
        "steps": "1. AppController /freeze 触发自动写流水\n2. PC 端 addOrUpdate 手动新增 1 条冻结\n3. 查询 /list 确认 2 条都存在且 lg_record_code 唯一",
        "expected": "2 条冻结流水均可查询;lg_record_code 唯一;before/after 金额一致",
        "data": "LG_001, 2 条冻结",
        "priority": "中",
        "note": "自动写入与手动新增共存",
    },
    {
        "name": "流水一致性 — before/after 金额守恒",
        "pre": "保函 LG_001 在冻结 100 之前 available=200、occupied=0",
        "steps": "1. AppController /freeze 冻结 100\n2. 查询 /get 流水\n3. 校验 before_available=200, after_available=100, before_occupied=0, after_occupied=100",
        "expected": "before/after 与主表更新严格一致;before_receivable/after_receivable=NULL(冻结不涉及应收)",
        "data": "before_available=200, after_available=100",
        "priority": "高",
        "note": "设计文档 §3.4.5 / §7.2",
    },
]


# ============================================================
# Decision Table (validate 资金校验规则 a~f)
# ============================================================
VALIDATE_CONDITIONS = [
    "a. 保函订单类型 = 订单订单类型",
    "b. lg_status = 20 有效",
    "c. approval_status = 30 审核通过",
    "d. effective_date ≤ orderCreateTime",
    "e. expiry_date ≥ orderCreateTime + coverDays",
    "f. Σ(available_amount) ≥ requiredAmount",
]
VALIDATE_ACTIONS = [
    "LG_ORDER_TYPE_MISMATCH",
    "LG_NO_AVAILABLE",
    "LG_NO_AVAILABLE",
    "LG_NO_AVAILABLE",
    "LG_NO_AVAILABLE",
    "LG_INSUFFICIENT_FUNDS",
    "passed=true",
]


def derive_action(conds):
    """conds: [a, b, c, d, e, f] (True=通过, False=不通过)"""
    a, b, c, d, e, f = conds
    if not a:
        return "failCode=LG_ORDER_TYPE_MISMATCH"
    if not (b and c and d and e):
        return "failCode=LG_NO_AVAILABLE"
    if not f:
        return "failCode=LG_INSUFFICIENT_FUNDS"
    return "passed=true"


VALIDATE_DECISION_CASES = [
    {
        "scenario": "全规则通过 — 标准通过",
        "conditions": ["✓", "✓", "✓", "✓", "✓", "✓"],
        "actions": derive_action([True] * 6),
        "expected": "passed=true;data.totalAvailable≥requiredAmount;candidates 排序后返回",
        "data": "可用保函总额 370 万 ≥ 订单 200 万;订单类型一致;在生效/失效范围内",
        "priority": "高",
    },
    {
        "scenario": "a 不通过 — 订单类型不匹配",
        "conditions": ["✗", "✓", "✓", "✓", "✓", "✓"],
        "actions": derive_action([False, True, True, True, True, True]),
        "expected": "failCode=LG_ORDER_TYPE_MISMATCH;passed=false",
        "data": "保函 orderType=SKD,订单 orderType=CBU",
        "priority": "高",
    },
    {
        "scenario": "b 不通过 — 保函未生效(草稿/已提交/驳回/作废)",
        "conditions": ["✓", "✗", "✓", "✓", "✓", "✓"],
        "actions": derive_action([True, False, True, True, True, True]),
        "expected": "failCode=LG_NO_AVAILABLE;passed=false;candidates 为空(已过滤 lg_status≠20)",
        "data": "保函 lg_status=10 未生效",
        "priority": "高",
    },
    {
        "scenario": "c 不通过 — 审核未通过(草稿/已提交/驳回)",
        "conditions": ["✓", "✓", "✗", "✓", "✓", "✓"],
        "actions": derive_action([True, True, False, True, True, True]),
        "expected": "failCode=LG_NO_AVAILABLE;passed=false;candidates 为空",
        "data": "保函 approval_status=10/20/40",
        "priority": "高",
    },
    {
        "scenario": "d 不通过 — 保函尚未生效",
        "conditions": ["✓", "✓", "✓", "✗", "✓", "✓"],
        "actions": derive_action([True, True, True, False, True, True]),
        "expected": "failCode=LG_NO_AVAILABLE;passed=false",
        "data": "保函 effective_date=2026-12-31,订单 orderCreateTime=2026-04-15",
        "priority": "高",
    },
    {
        "scenario": "e 不通过 — 失效日期过早",
        "conditions": ["✓", "✓", "✓", "✓", "✗", "✓"],
        "actions": derive_action([True, True, True, True, False, True]),
        "expected": "failCode=LG_NO_AVAILABLE;passed=false;candidates 为空",
        "data": "保函 expiry_date=2026-06-30, orderCreateTime=2026-04-15, coverDays=100 → 阈值 2026-07-24",
        "priority": "高",
    },
    {
        "scenario": "f 不通过 — 可用保函总额 < 订单总金额",
        "conditions": ["✓", "✓", "✓", "✓", "✓", "✗"],
        "actions": derive_action([True, True, True, True, True, False]),
        "expected": "failCode=LG_INSUFFICIENT_FUNDS;passed=false;data.totalAvailable < requiredAmount",
        "data": "可用 50 万 < 订单 200 万",
        "priority": "高",
    },
    {
        "scenario": "多条件同时不通过 — a/b 同时不满足",
        "conditions": ["✗", "✗", "✓", "✓", "✓", "✓"],
        "actions": derive_action([False, False, True, True, True, True]),
        "expected": "failCode=LG_ORDER_TYPE_MISMATCH(优先于 LG_NO_AVAILABLE)",
        "data": "订单类型不匹配 且 保函未生效",
        "priority": "中",
    },
    {
        "scenario": "a 通过 + b~e 通过 + f 不足 — 业务可用但金额不足",
        "conditions": ["✓", "✓", "✓", "✓", "✓", "✗"],
        "actions": derive_action([True, True, True, True, True, False]),
        "expected": "failCode=LG_INSUFFICIENT_FUNDS;data.totalAvailable < requiredAmount;candidates 非空(展示给 PO 端)",
        "data": "可用 100 万 < 订单 200 万;订单类型/状态/日期均一致",
        "priority": "高",
    },
    {
        "scenario": "边界 — Σ(available)=requiredAmount 恰好等于(临界)",
        "conditions": ["✓", "✓", "✓", "✓", "✓", "✓"],
        "actions": derive_action([True] * 6),
        "expected": "passed=true(严格 ≥,临界值通过)",
        "data": "可用合计 = requiredAmount",
        "priority": "中",
    },
]


# ============================================================
# 生成 Excel
# ============================================================
OUTPUT_DIR = "docs/test-cases"
os.makedirs(OUTPUT_DIR, exist_ok=True)
date_str = datetime.now().strftime("%Y%m%d")

# File 1
wb1 = Workbook()
add_overview_sheet(wb1, "ExportVehicleLetterGuaranteeAppController",
                   "AppController 内部 HTTP 接口(供 PO/SO/SAP 集成调用,非 Feign)",
                   "场景测试 + 决策表(/validate 资金校验规则 a~f)",
                   len(APP_SCENARIOS) + len(VALIDATE_DECISION_CASES))
add_api_list_sheet(wb1, APP_APIS)
add_scenario_sheet(wb1, "场景测试用例", APP_SCENARIOS)
add_decision_table_sheet(wb1, "决策表-validate校验规则", VALIDATE_DECISION_CASES,
                         VALIDATE_CONDITIONS, VALIDATE_ACTIONS)
file1 = os.path.join(OUTPUT_DIR, f"ExportVehicleLetterGuaranteeAppController_测试用例_{date_str}.xlsx")
wb1.save(file1)
print(f"[OK] {file1}")
print(f"  - API list: {len(APP_APIS)} rows")
print(f"  - Scenario: {len(APP_SCENARIOS)} cases")
print(f"  - Decision table (validate): {len(VALIDATE_DECISION_CASES)} rows")

# File 2
wb2 = Workbook()
add_overview_sheet(wb2, "ExportVehicleLetterGuaranteeController",
                   "Web 端保函主表 PC 接口(金融专员/金融管理/红旗商务 三角色)",
                   "场景测试(CRUD + enableOrDisable) + 状态转换(submit/approve/cancel/expireEarly)",
                   len(MAIN_SCENARIOS) + len(MAIN_STATE_MACHINE))
add_api_list_sheet(wb2, MAIN_APIS)
add_scenario_sheet(wb2, "场景测试用例", MAIN_SCENARIOS)
add_state_machine_sheet(wb2, MAIN_STATE_MACHINE)
file2 = os.path.join(OUTPUT_DIR, f"ExportVehicleLetterGuaranteeController_测试用例_{date_str}.xlsx")
wb2.save(file2)
print(f"[OK] {file2}")
print(f"  - API list: {len(MAIN_APIS)} rows")
print(f"  - Scenario: {len(MAIN_SCENARIOS)} cases")
print(f"  - State machine: {len(MAIN_STATE_MACHINE)} cases")

# File 3
wb3 = Workbook()
add_overview_sheet(wb3, "ExportVehicleLetterGuaranteeRecordController",
                   "Web 端保函交易明细(追加写) PC 接口",
                   "场景测试",
                   len(RECORD_SCENARIOS))
add_api_list_sheet(wb3, RECORD_APIS)
add_scenario_sheet(wb3, "场景测试用例", RECORD_SCENARIOS)
file3 = os.path.join(OUTPUT_DIR, f"ExportVehicleLetterGuaranteeRecordController_测试用例_{date_str}.xlsx")
wb3.save(file3)
print(f"[OK] {file3}")
print(f"  - API list: {len(RECORD_APIS)} rows")
print(f"  - Scenario: {len(RECORD_SCENARIOS)} cases")

print(f"\nAll files generated, date: {date_str}")