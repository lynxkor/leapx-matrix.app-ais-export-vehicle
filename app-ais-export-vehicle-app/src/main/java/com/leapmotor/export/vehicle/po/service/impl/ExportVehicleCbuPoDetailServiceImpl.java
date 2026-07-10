package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportVehicleCbuPoDetailCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportVehicleCbuPoDetailQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
/**
 * <p>
 * PO订单明细表 服务实现类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class ExportVehicleCbuPoDetailServiceImpl implements ExportVehicleCbuPoDetailService {


    private ExportVehicleCbuPoDetailCmdExe purchaseOrderDetailCmdExe;
    private ExportVehicleCbuPoDetailQryExe purchaseOrderDetailQryExe;
    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;

    @Override
    public ExportVehicleCbuPoDetailResp getExportVehicleCbuPoDetailById(String id) {
        ExportVehicleCbuPoDetailDo purchaseOrderDetailDo = purchaseOrderDetailGateway.getExportVehicleCbuPoDetailById(id);
        return ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainToResp(purchaseOrderDetailDo);
    }

    @Override
    public List<ExportVehicleCbuPoDetailResp> listExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailParam purchaseOrderDetailParam) {
    ExportVehicleCbuPoDetailQo purchaseOrderDetailQo = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderDetailParam);
        List<ExportVehicleCbuPoDetailDo> purchaseOrderDetailDoList =  purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(purchaseOrderDetailQo);
        return ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderDetailDoList);
    }

    @Override
    public PageResult<ExportVehicleCbuPoDetailResp> pageExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailParam purchaseOrderDetailParam) {
        PageResult<ExportVehicleCbuPoDetailDo> page = purchaseOrderDetailQryExe.pageExportVehicleCbuPoDetail(purchaseOrderDetailParam);
        return ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq) {
        ExportVehicleCbuPoDetailDo purchaseOrderDetailDo = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderDetailAddOrUpdateReq);
        return purchaseOrderDetailCmdExe.addOrUpdateExportVehicleCbuPoDetail(purchaseOrderDetailDo);
    }

    @Override
    public boolean deleteExportVehicleCbuPoDetailByIds(List<String> ids) {
        return purchaseOrderDetailCmdExe.deleteExportVehicleCbuPoDetailByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleCbuPoDetailByIds(LpBaseParam param) {
        return purchaseOrderDetailCmdExe.enableOrDisableExportVehicleCbuPoDetailByIds(param);
    }
}
