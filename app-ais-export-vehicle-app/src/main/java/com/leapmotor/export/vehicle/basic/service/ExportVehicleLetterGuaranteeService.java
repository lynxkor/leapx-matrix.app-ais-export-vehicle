package com.leapmotor.export.vehicle.basic.service;


import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeBatchFreezeResp;
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
     * 单张冻结
     */
    boolean freeze(ExportVehicleLetterGuaranteeFreezeReq req);

    /**
     * 批量冻结(PO 提交用)
     */
    ExportVehicleLetterGuaranteeBatchFreezeResp batchFreeze(ExportVehicleLetterGuaranteeBatchFreezeReq req);

    /**
     * 解冻(PO 驳回用)
     */
    boolean unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq req);

    /**
     * 转应收(质检通过)
     */
    boolean transferToReceivable(ExportVehicleLetterGuaranteeTransferReq req);

    /**
     * 释放应收(SAP 清账)
     */
    boolean releaseReceivable(ExportVehicleLetterGuaranteeReleaseReq req);

    // ============== 查询:获取已有 AppController 校验所需保函 ==============

    /**
     * PO 资金校验(给 PO 模块调用,§8.5.1)
     */
    ExportVehicleLetterGuaranteeValidateResp validateForPo(ExportVehicleLetterGuaranteeValidateReq req);
}
