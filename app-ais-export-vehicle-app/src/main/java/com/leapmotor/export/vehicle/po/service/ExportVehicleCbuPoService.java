package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailFullResp;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRefreshPriceReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoBatchResp;

import java.util.List;

/**
 * <p>
 * PO订单主表 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoService {

    ExportVehicleCbuPoResp getExportVehicleCbuPoById(String id);

    /** 主子表联查：PO + 明细列表 + 保函关联列表 */
    ExportVehicleCbuPoDetailFullResp getExportVehicleCbuPoDetailById(String id);

    List<ExportVehicleCbuPoResp> listExportVehicleCbuPo(ExportVehicleCbuPoParam purchaseOrderParam);

    PageResult<ExportVehicleCbuPoResp> pageExportVehicleCbuPo(ExportVehicleCbuPoParam purchaseOrderParam);

    String addOrUpdateExportVehicleCbuPo(ExportVehicleCbuPoAddOrUpdateReq purchaseOrderAddOrUpdateReq);

    boolean deleteExportVehicleCbuPoByIds(List<String> ids);

    boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param);

    /** 4.1 红旗CBU推送创建草稿订单 */
    String createFromHongqi(HongqiCbuPurchaseOrderReq req);

    /** 4.2 草稿态编辑保存 */
    void saveExportVehicleCbuPo(ExportVehicleCbuPoSaveReq req);

    /** 4.3 销售提交 */
    void submitExportVehicleCbuPo(ExportVehicleCbuPoSubmitReq req);

    /** 4.3 销售驳回 */
    void rejectExportVehicleCbuPo(ExportVehicleCbuPoRejectReq req);

    /** 4.4 供需审核通过（生成 SO 单） */
    void supplyApprove(ExportVehicleCbuPoSupplyApproveReq req);

    /** 4.4 供需审核驳回（解冻保函 + 推红旗） */
    void supplyReject(ExportVehicleCbuPoSupplyRejectReq req);

    /** 4.5 产销平衡审核通过（终态 + 推红旗） */
    void balanceApprove(ExportVehicleCbuPoBalanceApproveReq req);

    /** 4.5 产销平衡审核驳回（SO 作废 + 保函解冻 + 推红旗，APS 同步由 SO 侧负责） */
    void balanceReject(ExportVehicleCbuPoBalanceRejectReq req);

    /** 4.6 刷新订单价格（联动保函 / SO） */
    void refreshPrice(ExportVehicleCbuPoRefreshPriceReq req);

    /** 批量提交 */
    ExportVehicleCbuPoBatchResp batchSubmit(ExportVehicleCbuPoBatchReq req);

    /** 批量销售驳回 */
    ExportVehicleCbuPoBatchResp batchReject(ExportVehicleCbuPoBatchReq req);

    /** 批量供需审核通过 */
    ExportVehicleCbuPoBatchResp batchSupplyApprove(ExportVehicleCbuPoBatchReq req);

    /** 批量供需审核驳回 */
    ExportVehicleCbuPoBatchResp batchSupplyReject(ExportVehicleCbuPoBatchReq req);

    /** 批量产销平衡审核通过 */
    ExportVehicleCbuPoBatchResp batchBalanceApprove(ExportVehicleCbuPoBatchReq req);

    /** 批量产销平衡审核驳回 */
    ExportVehicleCbuPoBatchResp batchBalanceReject(ExportVehicleCbuPoBatchReq req);

}
