package com.leapmotor.export.vehicle.payablelist.convertor;

import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListDetailResp;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应付清单明细DTO转换工具
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Mapper
public interface PayableListDetailDTOStructMapper {

    PayableListDetailDTOStructMapper INSTANCE = Mappers.getMapper(PayableListDetailDTOStructMapper.class);

    /**
     * 领域对象转响应对象
     */
    PayableListDetailResp domainToResp(PayableListDetailDo source);

    /**
     * 领域对象列表转响应对象列表
     */
    List<PayableListDetailResp> domainToResps(List<PayableListDetailDo> sources);

    /**
     * 分页领域对象转分页响应对象
     */
    PageResult<PayableListDetailResp> domainsToRespPage(PageResult<PayableListDetailDo> sources);

    /**
     * 分页参数转查询对象
     */
    PayableListDetailQo paramToQuery(PayableListDetailPageParam source);

    /**
     * 分页参数转查询对象（别名方法）
     */
    default PayableListDetailQo detailPageParamToQo(PayableListDetailPageParam source) {
        return paramToQuery(source);
    }

    /**
     * 分页领域对象转分页响应对象（别名方法）
     */
    default PageResult<PayableListDetailResp> pageDoToResp(PageResult<PayableListDetailDo> source) {
        return domainsToRespPage(source);
    }

    /**
     * 领域对象转下拉选项
     */
    LpOptionalResult domainToOptional(PayableListDetailResp source);

    /**
     * 领域对象列表转下拉选项列表
     */
    List<LpOptionalResult> domainsToOptionals(List<PayableListDetailResp> sources);
}
