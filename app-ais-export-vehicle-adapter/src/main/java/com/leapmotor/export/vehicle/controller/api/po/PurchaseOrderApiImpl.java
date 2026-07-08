package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.PurchaseOrderApi;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderResponse;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * PO订单主表 Feign Api 实现类
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "FeignAPI PO订单主表实现类", description = "PO订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/po/purchaseOrder")
public class PurchaseOrderApiImpl implements PurchaseOrderApi {

    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<PurchaseOrderResponse>> list(@RequestBody PurchaseOrderRequest purchaseOrderRequest) {
        PurchaseOrderParam param = PurchaseOrderDTOStructMapper.INSTANCE.requestToParam(purchaseOrderRequest);
        List<PurchaseOrderResp> respList = purchaseOrderService.listPurchaseOrder(param);
        List<PurchaseOrderResponse> dtoList = PurchaseOrderDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<PurchaseOrderResponse>> page(@RequestBody PurchaseOrderRequest purchaseOrderRequest) {
        PurchaseOrderParam param = PurchaseOrderDTOStructMapper.INSTANCE.requestToParam(purchaseOrderRequest);
        PageResult<PurchaseOrderResp> pageResult = purchaseOrderService.pagePurchaseOrder(param);
        PageResult<PurchaseOrderResponse> dtoPage = PurchaseOrderDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<PurchaseOrderResponse> getPurchaseOrderById(@RequestBody String id) {
        PurchaseOrderResp resp = purchaseOrderService.getPurchaseOrderById(id);
        PurchaseOrderResponse dto = PurchaseOrderDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody PurchaseOrderRequest purchaseOrderRequest) {
        PurchaseOrderAddOrUpdateReq req = PurchaseOrderDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderRequest);
        return AjaxResult.success(purchaseOrderService.addOrUpdatePurchaseOrder(req));
    }

}