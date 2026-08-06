package com.leapmotor.export.vehicle.payablelist.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;

import java.util.Collection;
import java.util.List;

/**
 * 应付清单明细领域网关
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public interface PayableListDetailGateway {

    /**
     * 新增应付清单明细
     *
     * @param detailDo 应付清单明细领域对象
     * @return 主键ID
     */
    String addPayableListDetail(PayableListDetailDo detailDo);

    /**
     * 批量新增应付清单明细
     *
     * @param detailDos 应付清单明细领域对象列表
     * @return 影响行数
     */
    int addBatchPayableListDetail(List<PayableListDetailDo> detailDos);

    /**
     * 修改应付清单明细
     *
     * @param detailDo 应付清单明细领域对象
     * @return 主键ID
     */
    String updatePayableListDetail(PayableListDetailDo detailDo);

    /**
     * 根据ID查询应付清单明细
     *
     * @param id 主键ID
     * @return 应付清单明细领域对象
     */
    PayableListDetailDo getPayableListDetailById(String id);

    /**
     * 列表查询应付清单明细
     *
     * @param qo 查询条件
     * @return 应付清单明细领域对象列表
     */
    List<PayableListDetailDo> listPayableListDetail(PayableListDetailQo qo);

    /**
     * 分页查询应付清单明细
     *
     * @param qo 查询条件
     * @return 分页结果
     */
    PageResult<PayableListDetailDo> pagePayableListDetail(PayableListDetailQo qo);

    /**
     * 根据应付清单ID查询明细列表
     *
     * @param payableListId 应付清单ID
     * @return 明细列表
     */
    List<PayableListDetailDo> listByPayableListId(String payableListId);

    /**
     * 根据ID列表批量更新
     *
     * @param ids    ID列表
     * @param update 更新对象
     * @return 影响行数
     */
    int updateBatchByIds(Collection<String> ids, PayableListDetailDo update);

    /**
     * 根据ID删除（软删除）
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(String id);

    /**
     * 根据ID列表批量删除（软删除）
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatchByIds(Collection<String> ids);

    /**
     * 根据SO订单号查询明细
     *
     * @param soNo SO订单号
     * @return 应付清单明细领域对象
     */
    PayableListDetailDo getBySoNo(String soNo);

    /**
     * 根据SO订单号列表批量查询明细
     *
     * @param soNos SO订单号列表
     * @return 应付清单明细领域对象列表
     */
    List<PayableListDetailDo> listBySoNos(Collection<String> soNos);
}