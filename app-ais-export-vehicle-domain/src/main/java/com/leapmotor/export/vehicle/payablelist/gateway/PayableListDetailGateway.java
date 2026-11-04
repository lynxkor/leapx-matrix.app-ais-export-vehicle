package com.leapmotor.export.vehicle.payablelist.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
     * 批量按ID修改应付清单明细（仅更新入参非null字段，避免全量回写覆盖并发变更）
     *
     * @param detailDos 待更新领域对象列表（须携带ID与待更新字段）
     * @return 影响行数
     */
    int updateBatchById(List<PayableListDetailDo> detailDos);

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

    /**
     * 查询所有已绑定到应付清单的SO订单ID列表
     *
     * @return 已绑定的SO订单ID列表
     */
    List<String> listAllBoundSoIds();

    /**
     * 根据应付清单ID统计车辆数量
     *
     * @param payableListId 应付清单ID
     * @return 车辆数量
     */
    int countByPayableListId(String payableListId);

    /**
     * 根据应付清单ID统计金额合计
     *
     * @param payableListId 应付清单ID
     * @return 金额合计
     */
    BigDecimal sumAmountByPayableListId(String payableListId);

    /**
     * 根据应付清单ID批量更新明细表冗余字段
     *
     * 只更新本次业务实际变更的字段：入参为 null 表示本次不涉及该字段，明细表对应列保持原值。
     * 如提交场景只传提交日期、确认/驳回场景只传确认时间，二者互不覆盖。
     *
     * @param payableListId 应付清单ID
     * @param billStatus    单据状态（null 表示不更新）
     * @param submitDate    提交日期（null 表示不更新）
     * @param confirmTime   确认时间（null 表示不更新）
     * @return 影响行数
     */
    int updateBillStatusByPayableListId(String payableListId, String billStatus, LocalDateTime submitDate, LocalDateTime confirmTime);

    /**
     * 根据应付清单ID同步明细表冗余字段（仅更新非 null 字段）
     *
     * <p>抬头字段变更后用于明细冗余列对齐，入参为 null 表示本次不涉及该字段，明细表对应列保持原值。
     * 支持的冗余列：单据状态、客户编码/名称、生成月份、提交日期、确认时间。
     *
     * @param payableListId   应付清单ID
     * @param redundantFields 待同步的冗余字段载体（仅使用上述字段）
     * @return 影响行数
     */
    int updateRedundantFieldsByPayableListId(String payableListId, PayableListDetailDo redundantFields);

    /**
     * 根据应付清单ID按状态编码同步明细冗余单据状态
     *
     * 仅当目标状态编码大于明细当前状态时才更新，用于清账等并发统计场景，
     * 避免明细冗余单据状态回退（如已收款60 被后算出的部分收款50 覆盖）。
     *
     * @param payableListId 应付清单ID
     * @param billStatus    目标单据状态
     * @return 影响行数（0 表示明细状态均不小于目标状态，未更新）
     */
    int advanceBillStatusByPayableListId(String payableListId, String billStatus);

    /**
     * 根据明细ID更新收款状态与收款时间
     *
     * 只更新收款维度的两个字段，避免全量回写快照覆盖明细表的其它列（尤其是冗余列）；
     * 收款时间为 null 表示不更新该字段。
     *
     * @param id          明细ID
     * @param isReceived  是否收款：0=否/1=是
     * @param receiveTime 收款时间（null 表示不更新）
     * @return 影响行数
     */
    int updateReceiveStatusById(String id, Integer isReceived, LocalDateTime receiveTime);
}