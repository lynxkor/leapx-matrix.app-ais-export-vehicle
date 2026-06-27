package com.leapmotor.export.vehicle.demo.api.fallback;

import com.leapmotor.export.vehicle.demo.api.DemoOrderApi;
import com.leapmotor.export.vehicle.demo.dto.DemoOrderDto;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 整车订单示例 Feign客户端降级工厂
 */
@Component("DemoOrderApiFallbackFactory")
public class DemoOrderApiFallbackFactory implements FallbackFactory<DemoOrderApi> {

    private static final Logger log = LoggerFactory.getLogger(DemoOrderApiFallbackFactory.class);

    @Override
    public DemoOrderApi create(Throwable cause) {
        return new DemoOrderApi() {

            @Override
            public AjaxResult<java.util.List<DemoOrderDto>> list(DemoOrderDto demoOrderDto) {
                log.error("整车订单示例列表查询服务降级，参数：{}，异常：{}", demoOrderDto, cause.getMessage(), cause);
                return AjaxResult.fail("demoOrder-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<DemoOrderDto>> page(DemoOrderDto demoOrderDto) {
                log.error("整车订单示例分页查询服务降级，参数：{}，异常：{}", demoOrderDto, cause.getMessage(), cause);
                PageResult<DemoOrderDto> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("demoOrder-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<DemoOrderDto> getDemoOrderById(String id) {
                log.error("整车订单示例详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("demoOrder-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<Boolean> addOrUpdate(DemoOrderDto demoOrderDto) {
                log.error("整车订单示例新增/编辑服务降级，参数：{}，异常：{}", demoOrderDto, cause.getMessage(), cause);
                return AjaxResult.fail("demoOrder-service服务异常：" + cause.getMessage());
            }
        };
    }
}
