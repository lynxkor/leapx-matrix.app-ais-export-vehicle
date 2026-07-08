package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.PurchaseOrderDetailApi;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderDetailRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderDetailResponse;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderDetailResp;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderDetailService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * PO订单明细表 Feign Api 实现类
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "FeignAPI PO订单明细表实现类", description = "PO订单明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/po/purchaseOrderDetail")
public class PurchaseOrderDetailApiImpl implements PurchaseOrderDetailApi {

    private PurchaseOrderDetailService purchaseOrderDetailService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<PurchaseOrderDetailResponse>> list(@RequestBody PurchaseOrderDetailRequest purchaseOrderDetailRequest) {
        PurchaseOrderDetailParam param = PurchaseOrderDetailDTOStructMapper.INSTANCE.requestToParam(purchaseOrderDetailRequest);
        List<PurchaseOrderDetailResp> respList = purchaseOrderDetailService.listPurchaseOrderDetail(param);
        List<PurchaseOrderDetailResponse> dtoList = PurchaseOrderDetailDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<PurchaseOrderDetailResponse>> page(@RequestBody PurchaseOrderDetailRequest purchaseOrderDetailRequest) {
        PurchaseOrderDetailParam param = PurchaseOrderDetailDTOStructMapper.INSTANCE.requestToParam(purchaseOrderDetailRequest);
        PageResult<PurchaseOrderDetailResp> pageResult = purchaseOrderDetailService.pagePurchaseOrderDetail(param);
        PageResult<PurchaseOrderDetailResponse> dtoPage = PurchaseOrderDetailDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<PurchaseOrderDetailResponse> getPurchaseOrderDetailById(@RequestBody String id) {
        PurchaseOrderDetailResp resp = purchaseOrderDetailService.getPurchaseOrderDetailById(id);
        PurchaseOrderDetailResponse dto = PurchaseOrderDetailDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody PurchaseOrderDetailRequest purchaseOrderDetailRequest) {
        PurchaseOrderDetailAddOrUpdateReq req = PurchaseOrderDetailDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderDetailRequest);
        return AjaxResult.success(purchaseOrderDetailService.addOrUpdatePurchaseOrderDetail(req));
    }

}