package com.leapmotor.export.vehicle.demo.api;

import com.leapmotor.export.vehicle.demo.api.fallback.DemoOrderApiFallbackFactory;
import com.leapmotor.export.vehicle.demo.dto.DemoOrderDto;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 整车订单示例 Feign客户端
 */
@FeignClient(name = "app-ais-export-vehicle", contextId = "DemoOrderApi", path = "/api/vehicle/demoOrder", fallbackFactory = DemoOrderApiFallbackFactory.class)
public interface DemoOrderApi {

    /**
     * 列表查询
     */
    @PostMapping("/list")
    AjaxResult<List<DemoOrderDto>> list(@RequestBody DemoOrderDto demoOrderDto);

    /**
     * 分页查询
     */
    @PostMapping("/page")
    AjaxResult<PageResult<DemoOrderDto>> page(@RequestBody DemoOrderDto demoOrderDto);

    /**
     * 详情查询
     */
    @PostMapping("/get")
    AjaxResult<DemoOrderDto> getDemoOrderById(@RequestBody String id);

    /**
     * 新增/编辑
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<Boolean> addOrUpdate(@RequestBody DemoOrderDto demoOrderDto);
}
