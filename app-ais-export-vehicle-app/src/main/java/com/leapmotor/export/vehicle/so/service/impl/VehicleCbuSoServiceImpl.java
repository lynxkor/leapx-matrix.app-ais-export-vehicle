package com.leapmotor.export.vehicle.so.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.executor.cmd.VehicleCbuSoCmdExe;
import com.leapmotor.export.vehicle.so.executor.query.VehicleCbuSoQryExe;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDTOStructMapper;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import org.springframework.stereotype.Service;

import java.util.List;

import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;

/**
 * <p>
 * CBU整车销售订单主表 服务实现类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoServiceImpl implements VehicleCbuSoService {

    private VehicleCbuSoCmdExe vehicleCbuSoCmdExe;
    private VehicleCbuSoQryExe vehicleCbuSoQryExe;
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    @Override
    public VehicleCbuSoResp getVehicleCbuSoById(String id) {
        VehicleCbuSoDo vehicleCbuSoDo = vehicleCbuSoGateway.getVehicleCbuSoById(id);
        return VehicleCbuSoDTOStructMapper.INSTANCE.domainToResp(vehicleCbuSoDo);
    }

    @Override
    public List<VehicleCbuSoResp> listVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam) {
        VehicleCbuSoQo vehicleCbuSoQo = VehicleCbuSoDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoParam);
        List<VehicleCbuSoDo> vehicleCbuSoDoList = vehicleCbuSoGateway.listVehicleCbuSo(vehicleCbuSoQo);
        return VehicleCbuSoDTOStructMapper.INSTANCE.domainsToResps(vehicleCbuSoDoList);
    }

    @Override
    public PageResult<VehicleCbuSoResp> pageVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam) {
        PageResult<VehicleCbuSoDo> page = vehicleCbuSoQryExe.pageVehicleCbuSo(vehicleCbuSoParam);
        return VehicleCbuSoDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateVehicleCbuSo(VehicleCbuSoAddOrUpdateReq vehicleCbuSoAddOrUpdateReq) {
        VehicleCbuSoDo vehicleCbuSoDo = VehicleCbuSoDTOStructMapper.INSTANCE.addCmdToDomain(vehicleCbuSoAddOrUpdateReq);
        return vehicleCbuSoCmdExe.addOrUpdateVehicleCbuSo(vehicleCbuSoDo);
    }

    @Override
    public boolean deleteVehicleCbuSoByIds(List<String> ids) {
        return vehicleCbuSoCmdExe.deleteVehicleCbuSoByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param) {
        return vehicleCbuSoCmdExe.enableOrDisableVehicleCbuSoByIds(param);
    }
}
