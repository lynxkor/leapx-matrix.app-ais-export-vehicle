package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListQueryAvailableVehiclesReq;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询可添加车辆执行器
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListAvailableVehicleQryExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    /**
     * 执行查询可添加车辆（分页）
     *
     * @param req 查询请求
     * @return 可添加车辆分页结果
     */
    public PageResult<SoOrderForPayableDto> execute(PayableListQueryAvailableVehiclesReq req) {
        // 查询应付清单
        PayableListDo payableList = payableListGateway.getPayableListById(req.getPayableListId());
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        // 查询所有已绑定到应付清单的SO订单ID（SQL 层排除）
        List<String> boundSoIds = payableListDetailGateway.listAllBoundSoIds();

        // 分页查询质检通过的车辆（主表+明细联表查询；VIN/SO 单值模糊、已绑定 SO 排除）
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setQcResult(VehicleCbuSoQcResultEnum.PASSED.getCode());
        qo.setCustomerCode(payableList.getCustomerCode());
        qo.setVinLike(req.getVin());
        qo.setSoNoLike(req.getSoNo());
        qo.setExcludeIds(boundSoIds);
        qo.setPageNum(req.getPageNum());
        qo.setPageSize(req.getPageSize());
        PageResult<VehicleCbuSoDo> page = vehicleCbuSoGateway.pageSoList(qo);

        // 转换当前页数据
        List<SoOrderForPayableDto> records = page.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        log.info("查询可添加车辆成功，payableListId={}, customerCode={}, total={}",
                req.getPayableListId(), payableList.getCustomerCode(), page.getTotal());

        PageResult<SoOrderForPayableDto> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        return result;
    }

    /**
     * 将 VehicleCbuSoDo 转换为 SoOrderForPayableDto
     *
     * @param soDo SO 领域对象
     * @return DTO
     */
    private SoOrderForPayableDto convertToDto(VehicleCbuSoDo soDo) {
        SoOrderForPayableDto dto = new SoOrderForPayableDto();
        dto.setSoId(soDo.getId());
        dto.setSoNo(soDo.getSoNo());
        dto.setPoNo(soDo.getPoNo());
        dto.setVin(soDo.getVin());
        dto.setMaterialCode(soDo.getMaterialCode());
        dto.setMaterialDesc(soDo.getMaterialDescription());
        dto.setCustomerCode(soDo.getCustomerCode());
        dto.setCustomerName(soDo.getCustomerName());
        dto.setExternalCustomerCode(soDo.getExternalCustomerCode());
        dto.setVehiclePrice(soDo.getVehiclePrice());
        dto.setFrozenAmount(soDo.getFrozenAmount());
        dto.setReceivableAmount(soDo.getReceivableAmount());
        dto.setQcPassTime(soDo.getQcResultTime());
        dto.setPoCreateTime(soDo.getReceiveOrderTime());
        dto.setSaleMarket(soDo.getSalesMarket());
        dto.setModelYear(soDo.getModelYear());
        dto.setOptionPackage(soDo.getOptionalPackage());
        return dto;
    }
}
