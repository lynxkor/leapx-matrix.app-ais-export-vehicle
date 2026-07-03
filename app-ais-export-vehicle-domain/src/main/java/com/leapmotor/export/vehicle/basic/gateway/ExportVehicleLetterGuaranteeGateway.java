package com.leapmotor.export.vehicle.basic.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 保函主表领域网关
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
public interface ExportVehicleLetterGuaranteeGateway  {

    /**
     * 新增ExportVehicleLetterGuarantee
     */
    String addExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo);

    /**
     * 批量新增
     */
    int addBatchExportVehicleLetterGuarantee(List<ExportVehicleLetterGuaranteeDo> exportVehicleLetterGuaranteeDos);

    /**
     * 修改
     */
    String updateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo);

    /**
     * 按ID查询
     */
    ExportVehicleLetterGuaranteeDo getExportVehicleLetterGuaranteeById(String id);

    /**
     * 唯一查询
     */
    ExportVehicleLetterGuaranteeDo getOneExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo);

    /**
     * 列表
     */
    List<ExportVehicleLetterGuaranteeDo> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo);

    /**
     * 分页
     */
    PageResult<ExportVehicleLetterGuaranteeDo> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo);

    /**
     * 单删
     */
    boolean deleteExportVehicleLetterGuaranteeById(String id);

    /**
     * 批量删除
     */
    boolean deleteExportVehicleLetterGuaranteeByIds(Collection<String> ids);

    /**
     * 启停
     */
    boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param);

    // ---- 状态机 + 资金流（按设计文档 §6.3.1 / §4）----

    /**
     * 提交(草稿 10 / 驳回 40 → 已提交 20)
     * 仅 approval_status=10(草稿)或 40(驳回)可提交;lg_status 保持 10
     * <p>驳回后再提交时:submitterId/Name/submitTime 更新为最新提交人,历史驳回记录由 approverId/Name/approveTime/approveRemark 保留作为审计
     * <p>操作人(提交人 ID/名称)由本方法内部通过 {@code LoginUserUtils.getCurrentUserOrSysDefault()} 获取
     */
    boolean submit(String id);

    /**
     * 审核(已提交 20 → 审核通过 30 / 驳回 40)
     * approved=true:approval_status 30,lg_status 10→20(有效)
     * approved=false:approval_status 40,lg_status 保持 10
     * <p>操作人(审核人 ID/名称)由本方法内部通过 {@code LoginUserUtils.getCurrentUserOrSysDefault()} 获取
     */
    boolean approve(String id, boolean approved, String approveRemark);

    /**
     * 作废(草稿 10 / 驳回 40 → 作废 50)
     * 审核通过(30)不可作废,终止走 expireEarly
     */
    boolean cancel(String id, String remark);

    /**
     * 提前失效(审核通过 30,lg_status 20 → 30)
     * 仅 lg_status=20 可手动失效;写失效流水 record_type=50
     * <p>操作人由内部通过 {@code LoginUserUtils.getCurrentUser()} 获取(写入流水 creator)
     */
    boolean expireEarly(String id, String reason);

    /**
     * 冻结(可用 → 占用,record_type=10)
     * 仅 lg_status=20 可冻结;available -= amount, occupied += amount
     * <p>操作人由内部通过 {@code LoginUserUtils.getCurrentUser()} 获取
     */
    boolean freeze(String lgId, BigDecimal amount, String orderId, String orderNo,
                   String poNo, String soNo, String vin, String remark);

    /**
     * 解冻(占用 → 可用,record_type=20)
     * 仅 lg_status=20 可解冻;available += amount, occupied -= amount
     * <p>操作人由内部通过 {@code LoginUserUtils.getCurrentUser()} 获取
     */
    boolean unfreeze(String lgId, BigDecimal amount, String orderId, String poNo, String reason);

    /**
     * 转应收(占用 → 应收,record_type=30)
     * 仅 lg_status=20 可转;occupied -= amount, receivable += amount
     * <p>操作人由内部通过 {@code LoginUserUtils.getCurrentUser()} 获取
     */
    boolean transferToReceivable(String lgId, BigDecimal amount, String orderId,
                                 String soNo, String vin, String reason);

    /**
     * 释放应收(应收清账,record_type=40)
     * receivable -= amount(核销完毕,保函不补回)
     * <p>操作人由内部通过 {@code LoginUserUtils.getCurrentUser()} 获取
     */
    boolean releaseReceivable(String lgId, BigDecimal amount, String orderId,
                              String soNo, String reason);

    /**
     * 列出指定客户+订单类型下可用的保函(可被锁定的,优先级按金额)
     * approval_status=30 AND lg_status=20 AND available>=amount
     */
    List<ExportVehicleLetterGuaranteeDo> listAvailableForCustomer(String customerId, String orderType,
                                                                  BigDecimal requiredAmount);

    /**
     * 列出到期需要失效的保函(凌晨任务用)
     * approval_status=30(审核通过) AND lg_status=20(有效) AND expiry_date < today
     */
    List<ExportVehicleLetterGuaranteeDo> listExpiredBefore(java.time.LocalDate date);
}
