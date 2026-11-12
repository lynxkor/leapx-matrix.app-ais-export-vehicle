package com.leapmotor.export.vehicle.controller.web.pricechangerecord;

import com.leapmotor.export.vehicle.pricechangerecord.executor.query.PriceChangeRecordQryExe;
import com.leapmotor.export.vehicle.pricechangerecord.req.PriceChangeRecordParam;
import com.leapmotor.export.vehicle.pricechangerecord.resp.PriceChangeRecordResp;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PC端 价格变更记录查询", description = "价格变更记录查询的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/priceChangeRecord")
public class PriceChangeRecordController {

    private final PriceChangeRecordQryExe priceChangeRecordQryExe;

    @Operation(summary = "价格变更记录分页查询")
    @ResourcePermission(value = "web:priceChangeRecord:page", desc = "价格变更记录分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<PriceChangeRecordResp>> page(@RequestBody PriceChangeRecordParam param) {
        return AjaxResult.success(priceChangeRecordQryExe.pagePriceChangeRecord(param));
    }
}
