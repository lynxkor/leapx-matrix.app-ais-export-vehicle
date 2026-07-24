package com.leapmotor.export.vehicle.controller.open.aps;

import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * APS 系统开放接口（APS -> SO，入站）。
 * 路径前缀 /openApi/aps，不走 @ResourcePermission（系统间调用）。
 * 鉴权/签名由上层网关统一处理，本接口不配签名拦截器。
 *
 * @author lijianuo
 * @since 2026-07-17
 */
@Tag(name = "APS系统开放接口", description = "APS -> SO 入站接口")
@RestController
@AllArgsConstructor
@RequestMapping("/openApi/aps")
public class ApsOpenController {

    private VehicleCbuSoService vehicleCbuSoService;

    /**
     * 接收 APS 生产状态回传（批量，单次不超过配置上限 aps.receive.batch-size-limit，默认 100）
     */
    @Operation(summary = "接收APS生产状态回传")
    @PostMapping("/vehicleCbuSo/receiveProductionStatus")
    public AjaxResult<VehicleCbuSoOperateResult> receiveProductionStatus(
            @RequestBody @Valid List<VehicleCbuSoReceiveStatusReq> reqs) {
        return AjaxResult.success(vehicleCbuSoService.receiveProductionStatus(reqs));
    }
}
