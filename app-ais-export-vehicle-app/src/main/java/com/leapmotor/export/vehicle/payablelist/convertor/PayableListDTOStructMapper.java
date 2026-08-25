package com.leapmotor.export.vehicle.payablelist.convertor;

import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListPageParam;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应付清单DTO转换工具
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Mapper
public interface PayableListDTOStructMapper {

    PayableListDTOStructMapper INSTANCE = Mappers.getMapper(PayableListDTOStructMapper.class);

    // ==================== 抬头转换 ====================

    /**
     * 领域对象转响应对象
     */
    PayableListResp domainToResp(PayableListDo source);

    /**
     * 领域对象列表转响应对象列表
     */
    List<PayableListResp> domainToResps(List<PayableListDo> sources);

    /**
     * 分页领域对象转分页响应对象
     */
    PageResult<PayableListResp> domainsToRespPage(PageResult<PayableListDo> sources);

    /**
     * 分页参数转查询对象
     */
    PayableListQo paramToQo(PayableListPageParam source);

    /**
     * 分页参数转查询对象（别名方法）
     */
    default PayableListQo pageParamToQo(PayableListPageParam source) {
        return paramToQo(source);
    }

    /**
     * 分页领域对象转分页响应对象（别名方法）
     */
    default PageResult<PayableListResp> pageDoToResp(PageResult<PayableListDo> source) {
        return domainsToRespPage(source);
    }

    /**
     * 领域对象转下拉选项
     */
    LpOptionalResult domainToOptional(PayableListResp source);

    /**
     * 领域对象列表转下拉选项列表
     */
    List<LpOptionalResult> domainsToOptionals(List<PayableListResp> sources);
}
