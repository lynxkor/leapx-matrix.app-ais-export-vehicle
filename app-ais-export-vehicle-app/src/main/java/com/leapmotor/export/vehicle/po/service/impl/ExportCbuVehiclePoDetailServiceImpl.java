package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportCbuVehiclePoDetailCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportCbuVehiclePoDetailQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoDetailGateway;
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
public class ExportCbuVehiclePoDetailServiceImpl implements ExportCbuVehiclePoDetailService {


    private ExportCbuVehiclePoDetailCmdExe purchaseOrderDetailCmdExe;
    private ExportCbuVehiclePoDetailQryExe purchaseOrderDetailQryExe;
    private ExportCbuVehiclePoDetailGateway purchaseOrderDetailGateway;

    @Override
    public ExportCbuVehiclePoDetailResp getExportCbuVehiclePoDetailById(String id) {
        ExportCbuVehiclePoDetailDo purchaseOrderDetailDo = purchaseOrderDetailGateway.getExportCbuVehiclePoDetailById(id);
        return ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainToResp(purchaseOrderDetailDo);
    }

    @Override
    public List<ExportCbuVehiclePoDetailResp> listExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailParam purchaseOrderDetailParam) {
    ExportCbuVehiclePoDetailQo purchaseOrderDetailQo = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderDetailParam);
        List<ExportCbuVehiclePoDetailDo> purchaseOrderDetailDoList =  purchaseOrderDetailGateway.listExportCbuVehiclePoDetail(purchaseOrderDetailQo);
        return ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderDetailDoList);
    }

    @Override
    public PageResult<ExportCbuVehiclePoDetailResp> pageExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailParam purchaseOrderDetailParam) {
        PageResult<ExportCbuVehiclePoDetailDo> page = purchaseOrderDetailQryExe.pageExportCbuVehiclePoDetail(purchaseOrderDetailParam);
        return ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq) {
        ExportCbuVehiclePoDetailDo purchaseOrderDetailDo = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderDetailAddOrUpdateReq);
        return purchaseOrderDetailCmdExe.addOrUpdateExportCbuVehiclePoDetail(purchaseOrderDetailDo);
    }

    @Override
    public boolean deleteExportCbuVehiclePoDetailByIds(List<String> ids) {
        return purchaseOrderDetailCmdExe.deleteExportCbuVehiclePoDetailByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportCbuVehiclePoDetailByIds(LpBaseParam param) {
        return purchaseOrderDetailCmdExe.enableOrDisableExportCbuVehiclePoDetailByIds(param);
    }
}
