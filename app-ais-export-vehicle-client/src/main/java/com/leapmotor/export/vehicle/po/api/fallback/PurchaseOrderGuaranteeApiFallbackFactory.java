package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.PurchaseOrderGuaranteeApi;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderGuaranteeResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO-保函关联表(一个PO多保函) Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("PurchaseOrderGuaranteeApiFallbackFactory")
public class PurchaseOrderGuaranteeApiFallbackFactory implements FallbackFactory<PurchaseOrderGuaranteeApi> {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderGuaranteeApiFallbackFactory.class);

    @Override
    public PurchaseOrderGuaranteeApi create(Throwable cause) {
        return new PurchaseOrderGuaranteeApi() {

            @Override
            public AjaxResult<List<PurchaseOrderGuaranteeResponse>> list(PurchaseOrderGuaranteeRequest purchaseOrderGuaranteeRequest) {
                log.error("PO-保函关联表(一个PO多保函)列表查询服务降级，参数：{}，异常：{}", purchaseOrderGuaranteeRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderGuarantee-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<PurchaseOrderGuaranteeResponse>> page(PurchaseOrderGuaranteeRequest purchaseOrderGuaranteeRequest) {
                log.error("PO-保函关联表(一个PO多保函)分页查询服务降级，参数：{}，异常：{}", purchaseOrderGuaranteeRequest, cause.getMessage(), cause);
                PageResult<PurchaseOrderGuaranteeResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrderGuarantee-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<PurchaseOrderGuaranteeResponse> getPurchaseOrderGuaranteeById(String id) {
                log.error("PO-保函关联表(一个PO多保函)详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderGuarantee-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(PurchaseOrderGuaranteeRequest purchaseOrderGuaranteeRequest) {
                log.error("PO-保函关联表(一个PO多保函)新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderGuaranteeRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderGuarantee-service服务异常：" + cause.getMessage());
            }
        };
    }
}