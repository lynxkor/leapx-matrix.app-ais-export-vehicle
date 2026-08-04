package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * SO车辆明细表 服务类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoDetailService {

    /**
     * 查询VehicleCbuSoDetail
     *
     * @param id
     * @return VehicleCbuSoDetail
     */
    VehicleCbuSoDetailResp getVehicleCbuSoDetailById(String id);

    /**
     * 查询VehicleCbuSoDetail列表
     *
     * @param vehicleCbuSoDetailParam
     * @return VehicleCbuSoDetail集合
     */
    List<VehicleCbuSoDetailResp> listVehicleCbuSoDetail(VehicleCbuSoDetailParam vehicleCbuSoDetailParam);

    /**
     * 分页查询VehicleCbuSoDetail列表
     *
     * @param vehicleCbuSoDetailParam
     * @return VehicleCbuSoDetail集合
     */
    PageResult<VehicleCbuSoDetailResp> pageVehicleCbuSoDetail(VehicleCbuSoDetailParam vehicleCbuSoDetailParam);

    /**
     * 新增/编辑VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateVehicleCbuSoDetail(VehicleCbuSoDetailAddOrUpdateReq vehicleCbuSoDetailAddOrUpdateReq);


    /**
     * 删除VehicleCbuSoDetail信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteVehicleCbuSoDetailByIds(List<String> ids);


    /**
     * 启用/禁用VehicleCbuSoDetail信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableVehicleCbuSoDetailByIds(LpBaseParam param);

}
