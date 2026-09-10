package com.leapmotor.export.vehicle.payablelist.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;

import java.util.Collection;
import java.util.List;

/**
 * 应付清单抬头领域网关
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public interface PayableListGateway {

    /**
     * 新增应付清单
     *
     * @param payableListDo 应付清单领域对象
     * @return 主键ID
     */
    String addPayableList(PayableListDo payableListDo);

    /**
     * 批量新增应付清单
     *
     * @param payableListDos 应付清单领域对象列表
     * @return 影响行数
     */
    int addBatchPayableList(List<PayableListDo> payableListDos);

    /**
     * 修改应付清单
     *
     * @param payableListDo 应付清单领域对象
     * @return 主键ID
     */
    String updatePayableList(PayableListDo payableListDo);

    /**
     * 根据ID查询应付清单
     *
     * @param id 主键ID
     * @return 应付清单领域对象
     */
    PayableListDo getPayableListById(String id);

    /**
     * 根据应付清单编号查询
     *
     * @param payableListNo 应付清单编号
     * @return 应付清单领域对象
     */
    PayableListDo getPayableListByNo(String payableListNo);

    /**
     * 唯一查询应付清单
     *
     * @param payableListQo 查询条件
     * @return 应付清单领域对象
     */
    PayableListDo getOnePayableList(PayableListQo payableListQo);

    /**
     * 列表查询应付清单
     *
     * @param payableListQo 查询条件
     * @return 应付清单领域对象列表
     */
    List<PayableListDo> listPayableList(PayableListQo payableListQo);

    /**
     * 分页查询应付清单
     *
     * @param payableListQo 查询条件
     * @return 分页结果
     */
    PageResult<PayableListDo> pagePayableList(PayableListQo payableListQo);

    /**
     * 根据ID列表批量更新
     *
     * @param ids    ID列表
     * @param update 更新对象
     * @return 影响行数
     */
    int updateBatchByIds(Collection<String> ids, PayableListDo update);

}
