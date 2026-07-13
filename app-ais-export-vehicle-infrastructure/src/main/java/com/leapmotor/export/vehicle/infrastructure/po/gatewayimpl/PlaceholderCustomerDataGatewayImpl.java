package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.leapmotor.export.nexus.customer.api.CustomerApi;
import com.leapmotor.export.nexus.customer.request.CustomerRequest;
import com.leapmotor.export.nexus.customer.response.CustomerResponse;
import com.leapmotor.export.vehicle.po.gateway.PlaceholderCustomerDataGateway;
import com.leapmotor.export.vehicle.po.model.PlaceholderCustomerDataDo;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户主数据查询：Feign 调 app-ais-export-nexus CustomerApi（已对接）。
 * 用 getByExternalCode 精确单查（nexus 侧 Gateway getCustomerByExternalCode，非 list 模糊）。
 * nexus fundVerifyType 1010=无/1020=保函 → PO 内部"无"/"保函"（与 submitExportVehicleCbuPo 判断一致）。
 */
@Slf4j
@Service
public class PlaceholderCustomerDataGatewayImpl implements PlaceholderCustomerDataGateway {

    private final CustomerApi customerApi;

    public PlaceholderCustomerDataGatewayImpl(CustomerApi customerApi) {
        this.customerApi = customerApi;
    }

    @Override
    public PlaceholderCustomerDataDo getByExternalCode(String externalCustomerCode) {
        if (externalCustomerCode == null || externalCustomerCode.isEmpty()) {
            return null;
        }
        try {
            CustomerRequest req = new CustomerRequest();
            req.setExternalCode(externalCustomerCode);
            AjaxResult<List<CustomerResponse>> ret = customerApi.list(req);
            List<CustomerResponse> list = (ret != null) ? ret.getData() : null;
            if (list == null || list.isEmpty()) {
                return null;
            }
            PlaceholderCustomerDataDo o = new PlaceholderCustomerDataDo();
            o.setCustomerCode(list.get(0).getCustomerCode());
            o.setCustomerName(list.get(0).getCustomerName());
            o.setFundCheckType(convertFundVerifyType(list.get(0).getFundVerifyType()));
            return o;
        } catch (Exception e) {
            log.error("客户主数据查询失败 externalCustomerCode={}", externalCustomerCode, e);
            return null;
        }
    }

    /**
     * nexus FundVerifyTypeEnum：1010=无 / 1020=保函 → PO 内部"无"/"保函"
     */
    private String convertFundVerifyType(Integer fundVerifyType) {
        if (fundVerifyType == null) {
            return null;
        }
        if (fundVerifyType == 1020) {
            return "保函";
        }
        if (fundVerifyType == 1010) {
            return "无";
        }
        return String.valueOf(fundVerifyType);
    }
}
