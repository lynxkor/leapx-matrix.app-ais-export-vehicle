package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.PurchaseOrderGuaranteeApi;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderGuaranteeResponse;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderGuaranteeResp;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderGuaranteeService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * PO-保函关联表(一个PO多保函) Feign Api 实现类
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "FeignAPI PO-保函关联表(一个PO多保函)实现类", description = "PO-保函关联表(一个PO多保函)的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/po/purchaseOrderGuarantee")
public class PurchaseOrderGuaranteeApiImpl implements PurchaseOrderGuaranteeApi {

    private PurchaseOrderGuaranteeService purchaseOrderGuaranteeService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<PurchaseOrderGuaranteeResponse>> list(@RequestBody PurchaseOrderGuaranteeRequest purchaseOrderGuaranteeRequest) {
        PurchaseOrderGuaranteeParam param = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.requestToParam(purchaseOrderGuaranteeRequest);
        List<PurchaseOrderGuaranteeResp> respList = purchaseOrderGuaranteeService.listPurchaseOrderGuarantee(param);
        List<PurchaseOrderGuaranteeResponse> dtoList = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<PurchaseOrderGuaranteeResponse>> page(@RequestBody PurchaseOrderGuaranteeRequest purchaseOrderGuaranteeRequest) {
        PurchaseOrderGuaranteeParam param = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.requestToParam(purchaseOrderGuaranteeRequest);
        PageResult<PurchaseOrderGuaranteeResp> pageResult = purchaseOrderGuaranteeService.pagePurchaseOrderGuarantee(param);
        PageResult<PurchaseOrderGuaranteeResponse> dtoPage = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<PurchaseOrderGuaranteeResponse> getPurchaseOrderGuaranteeById(@RequestBody String id) {
        PurchaseOrderGuaranteeResp resp = purchaseOrderGuaranteeService.getPurchaseOrderGuaranteeById(id);
        PurchaseOrderGuaranteeResponse dto = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody PurchaseOrderGuaranteeRequest purchaseOrderGuaranteeRequest) {
        PurchaseOrderGuaranteeAddOrUpdateReq req = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderGuaranteeRequest);
        return AjaxResult.success(purchaseOrderGuaranteeService.addOrUpdatePurchaseOrderGuarantee(req));
    }

}