package com.leapmotor.export.vehicle.so.helper;

import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.nexus.company.request.CompanyRequest;
import com.leapmotor.export.nexus.company.response.CompanyResponse;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 销售公司主数据查询工具类。
 *
 * <p>供 SO 展示/同步 APS 时根据 {@code salesCompanyCode} 获取销售公司中文名称。
 * 调用 {@link CompanyApi} 失败时直接抛 {@link BizException}，不降级展示编码。
 */
@Component
public class CompanyApiHelper {

    @Resource
    private CompanyApi companyApi;

    /**
     * 根据销售公司编码查询公司名称。
     *
     * @param salesCompanyCode 销售公司编码
     * @return 销售公司中文名称
     * @throws BizException 调用失败或未找到公司时抛出
     */
    public String getCompanyName(String salesCompanyCode) {
        if (StringUtils.isBlank(salesCompanyCode)) {
            throw new BizException("销售公司编码不能为空");
        }

        CompanyRequest request = new CompanyRequest();
        request.setCompanyCode(salesCompanyCode);

        AjaxResult<List<CompanyResponse>> result;
        try {
            result = companyApi.list(request);
        } catch (Exception e) {
            throw new BizException("查询销售公司信息失败：" + e.getMessage());
        }

        if (result == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new BizException("查询销售公司信息失败" + (result == null ? "" : ": " + result.getMsg()));
        }

        List<CompanyResponse> data = result.getData();
        if (CollectionUtils.isEmpty(data) || data.get(0) == null) {
            throw new BizException("销售公司不存在：" + salesCompanyCode);
        }

        String companyName = data.get(0).getCompanyName();
        if (StringUtils.isBlank(companyName)) {
            throw new BizException("销售公司名称为空：" + salesCompanyCode);
        }

        return companyName;
    }
}
