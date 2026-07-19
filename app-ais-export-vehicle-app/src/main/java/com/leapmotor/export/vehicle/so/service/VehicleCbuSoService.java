package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoIssueReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoCancelReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailAggregateResp;

import java.util.List;

/**
 * <p>
 * CBU整车销售订单主表 服务类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoService {

    /**
     * 查询VehicleCbuSo
     *
     * @param id
     * @return VehicleCbuSo
     */
    VehicleCbuSoResp getVehicleCbuSoById(String id);

    /**
     * 查询VehicleCbuSo列表
     *
     * @param vehicleCbuSoParam
     * @return VehicleCbuSo集合
     */
    List<VehicleCbuSoResp> listVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam);

    /**
     * 分页查询VehicleCbuSo列表
     *
     * @param vehicleCbuSoParam
     * @return VehicleCbuSo集合
     */
    PageResult<VehicleCbuSoResp> pageVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam);

    /**
     * 新增/编辑VehicleCbuSo
     *
     * @param vehicleCbuSoAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateVehicleCbuSo(VehicleCbuSoAddOrUpdateReq vehicleCbuSoAddOrUpdateReq);

    /**
     * 删除VehicleCbuSo信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteVehicleCbuSoByIds(List<String> ids);

    /**
     * 启用/禁用VehicleCbuSo信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param);

    /**
     * 根据 PO 创建 CBU SO（一车一单）
     *
     * @param req
     * @return 生成的 SO 列表
     */
    List<SoResult.SoItem> createSo(SoCreateReq req);

    /**
     * 按 PO 刷新 SO 车辆价/冻结金额（PO 刷新价格分支B 联动 SO）。
     * 调用方需保证 PO 处于已审态(销售已提交/供需通过/产销通过)且 SO 无质检通过/已收款。
     *
     * @param req PO单号 + 明细新单价(按 poDetailId 匹配 SO)
     */
    void updatePrice(SoUpdatePriceReq req);

    /**
     * 根据 PO 单号查询 SO 列表（供 PO 侧内部调用）
     *
     * @param poNo
     * @return SO 领域对象列表
     */
    List<VehicleCbuSoDo> listVehicleCbuSoByPoNo(String poNo);

    /**
     * 下发生产（单条 / 批量 / 全量）：逐条独立子事务，失败项汇总。
     *
     * @param req 下发请求
     * @return 操作结果
     */
    VehicleCbuSoOperateResult issueToProduction(VehicleCbuSoIssueReq req);

    /**
     * 作废（单条 / 批量 / 按 PO）：逐条独立子事务，已作废幂等跳过。
     *
     * @param req 作废请求
     * @return 操作结果
     */
    VehicleCbuSoOperateResult cancel(VehicleCbuSoCancelReq req);

    /**
     * 查询 SO 聚合详情（订单信息 + 车辆明细）
     *
     * @param id SO 主键
     * @return SO 聚合详情
     */
    VehicleCbuSoDetailAggregateResp detail(String id);

    /**
     * 接收 APS 生产状态回传
     *
     * @param req APS 状态回传请求
     */
    void receiveProductionStatus(VehicleCbuSoReceiveStatusReq req);

    /**
     * 接收红旗质检结果回传
     *
     * @param req 红旗质检结果请求
     */
    void receiveQcResult(VehicleCbuSoReceiveQcReq req);

    /**
     * 供应付清单模块调用：按 SO 单号更新状态为已收款
     *
     * @param soNo SO 单号
     */
    void updateReceivedStatus(String soNo);

    /**
     * SAP 手动补推
     *
     * @param soNo SO 单号
     */
    void reissueSap(String soNo);

}
