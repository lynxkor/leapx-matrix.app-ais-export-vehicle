package com.leapmotor.export.vehicle.infrastructure.so.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.so.convertor.VehicleCbuSoStructMapper;
import com.leapmotor.export.vehicle.infrastructure.so.dao.VehicleCbuSoDao;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * CBU整车销售订单主表领域网关实现
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoGatewayImpl implements VehicleCbuSoGateway {

    private VehicleCbuSoMapper vehicleCbuSoMapper;
    private VehicleCbuSoDao vehicleCbuSoDao;

    @Override
    public String addVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo) {
        VehicleCbuSo vehicleCbuSo = VehicleCbuSoStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoDo);
        int result = vehicleCbuSoMapper.insert(vehicleCbuSo);

        vehicleCbuSoDo.setId(vehicleCbuSo.getId());

        if (result > 0) {
            return vehicleCbuSo.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addBatchVehicleCbuSo(List<VehicleCbuSoDo> vehicleCbuSoDos) {
        List<VehicleCbuSo> vehicleCbuSos = VehicleCbuSoStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoDos);

        return vehicleCbuSoDao.batchSaveSomeColumn(vehicleCbuSos);
    }

    @Override
    public String updateVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo) {
        VehicleCbuSo vehicleCbuSo = VehicleCbuSoStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoDo);
        int result = vehicleCbuSoMapper.updateById(vehicleCbuSo);
        if (result > 0) {
            return vehicleCbuSo.getId();
        } else {
            return null;
        }
    }

    @Override
    public VehicleCbuSoDo getVehicleCbuSoById(String id) {
        VehicleCbuSo vehicleCbuSo = vehicleCbuSoDao.getById(id);
        return VehicleCbuSoStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSo);
    }

    @Override
    public VehicleCbuSoDo getOneVehicleCbuSo(VehicleCbuSoQo vehicleCbuSoQo) {
        VehicleCbuSo vehicleCbuSo = vehicleCbuSoDao.getOneForceLimit(vehicleCbuSoQo);
        return VehicleCbuSoStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSo);
    }

    @Override
    public List<VehicleCbuSoDo> listVehicleCbuSo(VehicleCbuSoQo vehicleCbuSoQo) {
        List<VehicleCbuSo> vehicleCbuSos = vehicleCbuSoDao.queryList(vehicleCbuSoQo);
        return VehicleCbuSoStructMapper.INSTANCE.dataObjectsToDomains(vehicleCbuSos);
    }

    @Override
    public PageResult<VehicleCbuSoDo> pageVehicleCbuSo(VehicleCbuSoQo vehicleCbuSoQo) {
        Page<VehicleCbuSo> page = vehicleCbuSoDao.findPage(vehicleCbuSoQo);
        return PageUtils.transferPage(VehicleCbuSoStructMapper.INSTANCE::dataObjectsToDomains, page);
    }

    @Override
    public boolean deleteVehicleCbuSoById(String id) {
        return vehicleCbuSoDao.removeById(id);
    }

    @Override
    public boolean deleteVehicleCbuSoByIds(Collection<String> ids) {
        return vehicleCbuSoDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param) {
        VehicleCbuSo vehicleCbuSo = new VehicleCbuSo();
        vehicleCbuSo.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            vehicleCbuSo.setId(id);
            vehicleCbuSoDao.updateById(vehicleCbuSo);
        }
        return true;
    }
}

