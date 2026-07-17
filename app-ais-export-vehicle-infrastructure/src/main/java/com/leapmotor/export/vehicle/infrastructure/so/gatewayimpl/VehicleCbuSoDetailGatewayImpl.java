package com.leapmotor.export.vehicle.infrastructure.so.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.so.convertor.VehicleCbuSoDetailStructMapper;
import com.leapmotor.export.vehicle.infrastructure.so.dao.VehicleCbuSoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoDetail;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * SO车辆明细表领域网关实现
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoDetailGatewayImpl implements VehicleCbuSoDetailGateway {

    private VehicleCbuSoDetailMapper vehicleCbuSoDetailMapper;
    private VehicleCbuSoDetailDao vehicleCbuSoDetailDao;

    @Override
    public String addVehicleCbuSoDetail(VehicleCbuSoDetailDo vehicleCbuSoDetailDo) {
        VehicleCbuSoDetail vehicleCbuSoDetail = VehicleCbuSoDetailStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoDetailDo);
        int result = vehicleCbuSoDetailMapper.insert(vehicleCbuSoDetail);

        vehicleCbuSoDetailDo.setId(vehicleCbuSoDetail.getId());

        if (result > 0) {
            return vehicleCbuSoDetail.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addBatchVehicleCbuSoDetail(List<VehicleCbuSoDetailDo> vehicleCbuSoDetailDos) {
        List<VehicleCbuSoDetail> vehicleCbuSoDetails =
                VehicleCbuSoDetailStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoDetailDos);

        return vehicleCbuSoDetailDao.batchSaveSomeColumn(vehicleCbuSoDetails);
    }

    @Override
    public int batchAddVehicleCbuSoDetail(List<VehicleCbuSoDetailDo> vehicleCbuSoDetailDos) {
        List<VehicleCbuSoDetail> vehicleCbuSoDetails =
                VehicleCbuSoDetailStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoDetailDos);
        int result = vehicleCbuSoDetailDao.batchSaveSomeColumn(vehicleCbuSoDetails);
        // 回写主键到领域对象
        for (int i = 0; i < vehicleCbuSoDetails.size(); i++) {
            vehicleCbuSoDetailDos.get(i).setId(vehicleCbuSoDetails.get(i).getId());
        }
        return result;
    }

    @Override
    public String updateVehicleCbuSoDetail(VehicleCbuSoDetailDo vehicleCbuSoDetailDo) {
        VehicleCbuSoDetail vehicleCbuSoDetail = VehicleCbuSoDetailStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoDetailDo);
        int result = vehicleCbuSoDetailMapper.updateById(vehicleCbuSoDetail);
        if (result > 0) {
            return vehicleCbuSoDetail.getId();
        } else {
            return null;
        }
    }

    @Override
    public VehicleCbuSoDetailDo getVehicleCbuSoDetailById(String id) {
        VehicleCbuSoDetail vehicleCbuSoDetail = vehicleCbuSoDetailDao.getById(id);
        return VehicleCbuSoDetailStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSoDetail);
    }

    @Override
    public VehicleCbuSoDetailDo getVehicleCbuSoDetailBySoId(String soId) {
        VehicleCbuSoDetailQo qo = new VehicleCbuSoDetailQo();
        qo.setSoId(soId);
        VehicleCbuSoDetail vehicleCbuSoDetail = vehicleCbuSoDetailDao.getOneForceLimit(qo);
        return VehicleCbuSoDetailStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSoDetail);
    }

    @Override
    public VehicleCbuSoDetailDo getVehicleCbuSoDetailByVin(String vin) {
        VehicleCbuSoDetailQo qo = new VehicleCbuSoDetailQo();
        qo.setVin(vin);
        VehicleCbuSoDetail vehicleCbuSoDetail = vehicleCbuSoDetailDao.getOneForceLimit(qo);
        return VehicleCbuSoDetailStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSoDetail);
    }

    @Override
    public VehicleCbuSoDetailDo getOneVehicleCbuSoDetail(VehicleCbuSoDetailQo vehicleCbuSoDetailQo) {
        VehicleCbuSoDetail vehicleCbuSoDetail = vehicleCbuSoDetailDao.getOneForceLimit(vehicleCbuSoDetailQo);
        return VehicleCbuSoDetailStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSoDetail);
    }

    @Override
    public List<VehicleCbuSoDetailDo> listVehicleCbuSoDetail(VehicleCbuSoDetailQo vehicleCbuSoDetailQo) {
        List<VehicleCbuSoDetail> vehicleCbuSoDetails = vehicleCbuSoDetailDao.queryList(vehicleCbuSoDetailQo);
        return VehicleCbuSoDetailStructMapper.INSTANCE.dataObjectsToDomains(vehicleCbuSoDetails);
    }

    @Override
    public PageResult<VehicleCbuSoDetailDo> pageVehicleCbuSoDetail(VehicleCbuSoDetailQo vehicleCbuSoDetailQo) {
        Page<VehicleCbuSoDetail> page = vehicleCbuSoDetailDao.findPage(vehicleCbuSoDetailQo);
        return PageUtils.transferPage(VehicleCbuSoDetailStructMapper.INSTANCE::dataObjectsToDomains, page);
    }

    @Override
    public boolean deleteVehicleCbuSoDetailById(String id) {
        return vehicleCbuSoDetailDao.removeById(id);
    }

    @Override
    public boolean deleteVehicleCbuSoDetailByIds(Collection<String> ids) {
        return vehicleCbuSoDetailDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoDetailByIds(LpBaseParam param) {
        VehicleCbuSoDetail vehicleCbuSoDetail = new VehicleCbuSoDetail();
        vehicleCbuSoDetail.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            vehicleCbuSoDetail.setId(id);
            vehicleCbuSoDetailDao.updateById(vehicleCbuSoDetail);
        }
        return true;
    }
}

