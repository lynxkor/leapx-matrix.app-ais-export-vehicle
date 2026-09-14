package com.leapmotor.export.vehicle.payablelist.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;

import java.time.LocalDateTime;
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

    /**
     * 按状态编码单调前进的方式更新单据状态（仅当目标状态编码大于当前状态时才更新）
     *
     * 用于清账等"读取统计后回写"的并发场景，避免后算出的低状态（如部分收款50）覆盖已落地的高状态（如已收款60）。
     * 状态编码为两位数（10/20/30/40/50/60），字典序与数值序一致。
     *
     * @param payableListId 应付清单ID
     * @param billStatus    目标单据状态
     * @return 影响行数（0 表示当前状态不小于目标状态，未更新）
     */
    int advanceBillStatus(String payableListId, String billStatus);

    /**
     * 按ID更新同步红旗结果
     *
     * 事件处理器回写同步结果时使用：只更新同步列。
     * 入参为 null 表示本次不更新该列。
     *
     * @param payableListId 应付清单ID
     * @param syncHqTime    同步红旗时间
     * @param syncHqResult  同步红旗结果：SUCCESS/FAIL
     * @param syncHqMessage 同步红旗接口返回报文
     * @return 影响行数
     */
    int updateSyncHqResultById(String payableListId, LocalDateTime syncHqTime, String syncHqResult, String syncHqMessage);

    /**
     * 按ID更新同步SAP结果
     *
     * 与红旗同步结果回写同理：只更新同步列，不影响并发变更的其它字段；入参为 null 表示本次不更新该列。
     *
     * @param payableListId  应付清单ID
     * @param syncSapTime    同步SAP时间
     * @param syncSapResult  同步SAP结果：SUCCESS/FAIL
     * @param syncSapMessage 同步SAP接口返回报文
     * @return 影响行数
     */
    int updateSyncSapResultById(String payableListId, LocalDateTime syncSapTime, String syncSapResult, String syncSapMessage);

}
