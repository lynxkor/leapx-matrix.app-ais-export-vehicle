package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoBalanceRejectReq;

import java.util.List;

/**
 * <p>
 * PO订单主表 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoService {

    ExportCbuVehiclePoResp getExportCbuVehiclePoById(String id);

    List<ExportCbuVehiclePoResp> listExportCbuVehiclePo(ExportCbuVehiclePoParam purchaseOrderParam);

    PageResult<ExportCbuVehiclePoResp> pageExportCbuVehiclePo(ExportCbuVehiclePoParam purchaseOrderParam);

    String addOrUpdateExportCbuVehiclePo(ExportCbuVehiclePoAddOrUpdateReq purchaseOrderAddOrUpdateReq);

    boolean deleteExportCbuVehiclePoByIds(List<String> ids);

    boolean enableOrDisableExportCbuVehiclePoByIds(LpBaseParam param);

    /** 4.1 红旗CBU推送创建草稿订单 */
    String createFromHongqi(HongqiCbuPurchaseOrderReq req);

    /** 4.2 草稿态编辑保存 */
    void saveExportCbuVehiclePo(ExportCbuVehiclePoSaveReq req);

    /** 4.3 销售提交 */
    void submitExportCbuVehiclePo(ExportCbuVehiclePoSubmitReq req);

    /** 4.3 销售驳回 */
    void rejectExportCbuVehiclePo(ExportCbuVehiclePoRejectReq req);

    /** 4.4 供需审核通过（生成 SO 单） */
    void supplyApprove(ExportCbuVehiclePoSupplyApproveReq req);

    /** 4.4 供需审核驳回（解冻保函 + 推红旗） */
    void supplyReject(ExportCbuVehiclePoSupplyRejectReq req);

    /** 4.5 产销平衡审核通过（终态 + 推红旗） */
    void balanceApprove(ExportCbuVehiclePoBalanceApproveReq req);

    /** 4.5 产销平衡审核驳回（SO 作废 + APS + 保函解冻 + 推红旗） */
    void balanceReject(ExportCbuVehiclePoBalanceRejectReq req);

}
