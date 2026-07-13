package com.leapmotor.export.vehicle.so.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.executor.cmd.VehicleCbuSoDetailCmdExe;
import com.leapmotor.export.vehicle.so.executor.query.VehicleCbuSoDetailQryExe;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;

/**
 * <p>
 * SO车辆明细表 服务实现类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoDetailServiceImpl implements VehicleCbuSoDetailService {

    private VehicleCbuSoDetailCmdExe vehicleCbuSoDetailCmdExe;
    private VehicleCbuSoDetailQryExe vehicleCbuSoDetailQryExe;
    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;

    @Override
    public VehicleCbuSoDetailResp getVehicleCbuSoDetailById(String id) {
        VehicleCbuSoDetailDo vehicleCbuSoDetailDo = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(id);
        return VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainToResp(vehicleCbuSoDetailDo);
    }

    @Override
    public List<VehicleCbuSoDetailResp> listVehicleCbuSoDetail(VehicleCbuSoDetailParam vehicleCbuSoDetailParam) {
        VehicleCbuSoDetailQo vehicleCbuSoDetailQo = VehicleCbuSoDetailDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoDetailParam);
        List<VehicleCbuSoDetailDo> vehicleCbuSoDetailDoList = vehicleCbuSoDetailGateway.listVehicleCbuSoDetail(vehicleCbuSoDetailQo);
        return VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainsToResps(vehicleCbuSoDetailDoList);
    }

    @Override
    public PageResult<VehicleCbuSoDetailResp> pageVehicleCbuSoDetail(VehicleCbuSoDetailParam vehicleCbuSoDetailParam) {
        PageResult<VehicleCbuSoDetailDo> page = vehicleCbuSoDetailQryExe.pageVehicleCbuSoDetail(vehicleCbuSoDetailParam);
        return VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateVehicleCbuSoDetail(VehicleCbuSoDetailAddOrUpdateReq vehicleCbuSoDetailAddOrUpdateReq) {
        VehicleCbuSoDetailDo vehicleCbuSoDetailDo = VehicleCbuSoDetailDTOStructMapper.INSTANCE.addCmdToDomain(vehicleCbuSoDetailAddOrUpdateReq);
        return vehicleCbuSoDetailCmdExe.addOrUpdateVehicleCbuSoDetail(vehicleCbuSoDetailDo);
    }

    @Override
    public boolean deleteVehicleCbuSoDetailByIds(List<String> ids) {
        return vehicleCbuSoDetailCmdExe.deleteVehicleCbuSoDetailByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoDetailByIds(LpBaseParam param) {
        return vehicleCbuSoDetailCmdExe.enableOrDisableVehicleCbuSoDetailByIds(param);
    }
}
