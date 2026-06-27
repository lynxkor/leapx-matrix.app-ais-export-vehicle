package com.leapmotor.export.vehicle.demo.controller;

import com.leapmotor.export.vehicle.demo.convertor.DemoOrderDTOStructMapper;
import com.leapmotor.export.vehicle.demo.req.DemoOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.demo.req.DemoOrderParam;
import com.leapmotor.export.vehicle.demo.resp.DemoOrderResp;
import com.leapmotor.export.vehicle.demo.service.DemoOrderService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 整车订单示例 PC端接口
 */
@Tag(name = "PC端 整车订单示例管理", description = "整车订单示例的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/vehicle/demoOrder")
public class DemoOrderController {

    private DemoOrderService demoOrderService;

    @Operation(summary = "整车订单示例列表查询")
    @ResourcePermission("web:vehicle:demoOrder:list")
    @PostMapping("/list")
    public AjaxResult<List<DemoOrderResp>> list(@RequestBody DemoOrderParam demoOrderParam) {
        return AjaxResult.success(demoOrderService.listDemoOrder(demoOrderParam));
    }

    @Operation(summary = "整车订单示例分页查询")
    @ResourcePermission("web:vehicle:demoOrder:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<DemoOrderResp>> page(@RequestBody DemoOrderParam demoOrderParam) {
        return AjaxResult.success(demoOrderService.pageDemoOrder(demoOrderParam));
    }

    @Operation(summary = "整车订单示例详情信息")
    @ResourcePermission("web:vehicle:demoOrder:get")
    @PostMapping("/get")
    public AjaxResult<DemoOrderResp> getDemoOrderById(@RequestBody String id) {
        return AjaxResult.success(demoOrderService.getDemoOrderById(id));
    }

    @Operation(summary = "整车订单示例新增/编辑")
    @ResourcePermission("web:vehicle:demoOrder:addOrUpdate")
    @PostMapping("/addOrUpdate")
    public AjaxResult<Void> addOrUpdate(@RequestBody @Validated DemoOrderAddOrUpdateReq demoOrderAddOrUpdateReq) {
        demoOrderService.addOrUpdateDemoOrder(demoOrderAddOrUpdateReq);
        return AjaxResult.success();
    }

    @Operation(summary = "整车订单示例删除")
    @ResourcePermission("web:vehicle:demoOrder:delete")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        demoOrderService.deleteDemoOrderByIds(ids);
        return AjaxResult.success();
    }
}
