package com.leapmotor.export.vehicle.controller.web.payablelist;

import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListDetailResp;
import com.leapmotor.export.vehicle.payablelist.service.PayableListDetailService;
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

/**
 * 应付清单明细 PC端接口
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Tag(name = "PC端 应付清单明细", description = "应付清单明细的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/payableList/payableListDetail")
public class PayableListDetailController {

    private PayableListDetailService payableListDetailService;

    @Operation(summary = "应付清单明细分页查询")
    @ResourcePermission(value = "web:payableList:payableListDetail:page", desc = "应付清单明细分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<PayableListDetailResp>> page(@RequestBody PayableListDetailPageParam param) {
        return AjaxResult.success(payableListDetailService.pagePayableListDetail(param));
    }
}
