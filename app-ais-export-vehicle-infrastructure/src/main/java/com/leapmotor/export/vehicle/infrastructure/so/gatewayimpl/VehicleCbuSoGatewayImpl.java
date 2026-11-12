package com.leapmotor.export.vehicle.infrastructure.so.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public int batchAddVehicleCbuSo(List<VehicleCbuSoDo> vehicleCbuSoDos) {
        List<VehicleCbuSo> vehicleCbuSos = VehicleCbuSoStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoDos);
        // 显式填充 owner_user_id / owner_org_id 默认值，避免 NOT NULL 字段报错
        for (VehicleCbuSo entity : vehicleCbuSos) {
            if (entity.getOwnerUserId() == null) {
                entity.setOwnerUserId(0L);
            }
            if (entity.getOwnerOrgId() == null) {
                entity.setOwnerOrgId(0L);
            }
        }
        int result = vehicleCbuSoDao.batchSaveSomeColumn(vehicleCbuSos);
        // 回写主键与 SO 单号到领域对象
        for (int i = 0; i < vehicleCbuSos.size(); i++) {
            VehicleCbuSoDo domain = vehicleCbuSoDos.get(i);
            VehicleCbuSo entity = vehicleCbuSos.get(i);
            domain.setId(entity.getId());
            domain.setSoNo(entity.getSoNo());
        }
        return result;
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
    public int updateBatchById(List<VehicleCbuSoDo> vehicleCbuSoDos) {
        if (CollectionUtils.isEmpty(vehicleCbuSoDos)) {
            return 0;
        }
        List<VehicleCbuSo> vehicleCbuSos = VehicleCbuSoStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoDos);
        return vehicleCbuSoDao.updateBatchById(vehicleCbuSos) ? vehicleCbuSoDos.size() : 0;
    }

    @Override
    public VehicleCbuSoDo getVehicleCbuSoById(String id) {
        VehicleCbuSo vehicleCbuSo = vehicleCbuSoDao.getById(id);
        return VehicleCbuSoStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSo);
    }

    @Override
    public VehicleCbuSoDo getVehicleCbuSoBySoNo(String soNo) {
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setSoNo(soNo);
        VehicleCbuSo vehicleCbuSo = vehicleCbuSoDao.getOneForceLimit(qo);
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
    public List<VehicleCbuSoDo> listSoList(VehicleCbuSoQo vehicleCbuSoQo) {
        return vehicleCbuSoMapper.listSoList(vehicleCbuSoQo);
    }

    @Override
    public PageResult<VehicleCbuSoDo> pageSoList(VehicleCbuSoQo vehicleCbuSoQo) {
        Page<VehicleCbuSoDo> page = new Page<>(vehicleCbuSoQo.getPageNum(), vehicleCbuSoQo.getPageSize());
        Page<VehicleCbuSoDo> result = vehicleCbuSoMapper.pageSoList(page, vehicleCbuSoQo);
        return PageUtils.transferPage(r -> r, result);
    }

    @Override
    public VehicleCbuSoDo getSoDetailById(String id) {
        return vehicleCbuSoMapper.getSoDetailById(id);
    }

    @Override
    public int batchUpdateStatus(List<String> ids, Integer orderStatus,
                                 LocalDateTime issueTime, LocalDateTime cancelTime) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        LambdaUpdateWrapper<VehicleCbuSo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(VehicleCbuSo::getId, ids).set(VehicleCbuSo::getOrderStatus, orderStatus);
        if (issueTime != null) {
            updateWrapper.set(VehicleCbuSo::getIssueTime, issueTime);
        }
        if (cancelTime != null) {
            updateWrapper.set(VehicleCbuSo::getCancelTime, cancelTime);
        }
        return vehicleCbuSoMapper.update(null, updateWrapper);
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

