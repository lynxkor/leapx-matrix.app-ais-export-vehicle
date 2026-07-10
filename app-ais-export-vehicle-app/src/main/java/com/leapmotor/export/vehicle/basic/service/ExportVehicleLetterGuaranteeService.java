package com.leapmotor.export.vehicle.basic.service;


import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeAdjustUnfreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeFreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeUnfreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeBatchStateMachineResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeValidateResp;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;

import java.util.List;

/**
 * <p>
 * 保函主表 服务类
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
public interface ExportVehicleLetterGuaranteeService {

    /**
     * 查询ExportVehicleLetterGuarantee
     */
    ExportVehicleLetterGuaranteeResp getExportVehicleLetterGuaranteeById(String id);

    /**
     * 列表
     */
    List<ExportVehicleLetterGuaranteeResp> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam);

    /**
     * 分页
     */
    PageResult<ExportVehicleLetterGuaranteeResp> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam);

    /**
     * 新增/编辑
     */
    String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeAddOrUpdateReq exportVehicleLetterGuaranteeAddOrUpdateReq);

    /**
     * 删除
     */
    boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids);

    /**
     * 启停
     */
    boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param);

    // ============== 状态机 ==============

    /**
     * 提交(草稿 → 已提交) — 单条/批量统一入口
     *
     * <p>req 传 {@code id} 表示单条,传 {@code ids} 表示批量,二选一。
     * <p>批量场景每条独立事务,响应中逐条返回成功/失败;单条场景响应中只有 1 条结果。
     */
    ExportVehicleLetterGuaranteeBatchStateMachineResp submit(ExportVehicleLetterGuaranteeSubmitReq req);

    /**
     * 审核(已提交 → 通过/驳回) — 单条/批量统一入口
     *
     * <p>req 传 {@code id} 表示单条,传 {@code ids} 表示批量,二选一。
     */
    ExportVehicleLetterGuaranteeBatchStateMachineResp approve(ExportVehicleLetterGuaranteeApproveReq req);

    /**
     * 作废(草稿/驳回 → 作废) — 单条/批量统一入口
     *
     * <p>req 传 {@code id} 表示单条,传 {@code ids} 表示批量,二选一。
     */
    ExportVehicleLetterGuaranteeBatchStateMachineResp cancel(ExportVehicleLetterGuaranteeCancelReq req);

    /**
     * 提前失效(审核通过 → lg_status 失效) — 仅单条
     */
    boolean expireEarly(ExportVehicleLetterGuaranteeExpireEarlyReq req);

    // ============== 资金流(由 PO/APP 内部调用) ==============

    /**
     * 单 PO 冻结(单个 PO 提交用,§8.5.3)
     * <p>服务端自动选保函,逐步拆冻至覆盖 requiredAmount,任一保函冻结失败整体回滚。
     */
    ExportVehicleLetterGuaranteeFreezeResp freeze(ExportVehicleLetterGuaranteeFreezeReq req);

    /**
     * 批量 PO 冻结(多个 PO 提交用)
     * <p>每个 PO 独立事务,返回每 PO 的冻结结果。
     */
    List<ExportVehicleLetterGuaranteeFreezeResp> batchFreeze(List<ExportVehicleLetterGuaranteeFreezeReq> list);

    /**
     * 解冻(PO 驳回用)
     */
    boolean unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq req);

    /**
     * 批量 PO 解冻(多个 PO 驳回用)
     * <p>每个 PO 独立事务,返回每 PO 的解冻结果。
     */
    List<ExportVehicleLetterGuaranteeUnfreezeResp> batchUnfreeze(List<ExportVehicleLetterGuaranteeUnfreezeReq> list);

    /**
     * 转应收(质检通过)
     */
    boolean transferToReceivable(ExportVehicleLetterGuaranteeTransferReq req);

    /**
     * 释放应收(SAP 清账)
     */
    boolean releaseReceivable(ExportVehicleLetterGuaranteeReleaseReq req);

    // ============== PO 刷新订单价格联动(§3.5) ==============

    /**
     * 刷新价格-调增(补冻金额差,§3.5.2)
     * <p>与 {@link #freeze} 算法相同(失效日期 ASC → 可用 DESC → 随机),仅入参语义差异;
     * 入参 amountDiff 复用为 requiredAmount 走 freeze 流程。
     */
    ExportVehicleLetterGuaranteeFreezeResp adjustFreeze(ExportVehicleLetterGuaranteeAdjustFreezeReq req);

    /**
     * 刷新价格-调减(退差金额差,§3.5.3)
     * <p>部分解冻:从该 PO 已冻结保函中选失效日期最晚 + PO 当前累计占用 > amountDiff 的单张保函,
     * 解冻金额差;不满足条件降级或抛错。
     */
    ExportVehicleLetterGuaranteeAdjustUnfreezeResp adjustUnfreeze(ExportVehicleLetterGuaranteeAdjustUnfreezeReq req);

    // ============== 查询:获取已有 AppController 校验所需保函 ==============

    /**
     * PO 资金校验(给 PO 模块调用,§8.5.1)
     */
    ExportVehicleLetterGuaranteeValidateResp validateForPo(ExportVehicleLetterGuaranteeFreezeReq req);
}
