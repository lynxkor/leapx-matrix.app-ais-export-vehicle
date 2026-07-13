package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;

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

}
