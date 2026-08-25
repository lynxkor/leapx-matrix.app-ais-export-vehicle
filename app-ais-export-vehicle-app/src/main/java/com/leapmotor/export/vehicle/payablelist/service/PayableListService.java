package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.req.*;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.paltform.ais.common.model.excel.in.ExcelBaseResult;
import com.leapmotor.paltform.ais.common.model.excel.in.ImportExcelDTO;
import com.leapmotor.paltform.ais.common.model.response.PageResult;

import java.util.List;

/**
 * 应付清单服务接口
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public interface PayableListService {

    /**
     * 分页查询应付清单
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<PayableListResp> pagePayableList(PayableListPageParam param);

    /**
     * 根据ID查询应付清单详情
     *
     * @param id 主键ID
     * @return 应付清单详情
     */
    PayableListResp getPayableListById(String id);

    /**
     * 提交付应付清单
     *
     * @param req 提交请求
     * @return 结果
     */
    String submitPayableList(PayableListSubmitReq req);

    /**
     * 更新应付清单基础信息（客户编码、客户名称）
     *
     * @param req 更新请求
     * @return 应付清单ID
     */
    String updatePayableList(PayableListUpdateReq req);

    /**
     * 批量提交应付清单
     *
     * @param req 批量提交请求
     * @return 成功的ID列表
     */
    List<String> batchSubmitPayableList(PayableListBatchSubmitReq req);

    /**
     * 导入最新价格
     *
     * @param dto 导入请求
     * @return 导入结果
     */
    ExcelBaseResult importPrice(ImportExcelDTO dto);

    /**
     * 删除应付清单
     *
     * @param ids ID列表
     * @return 结果
     */
    boolean deletePayableListByIds(List<String> ids);

    /**
     * 查询可添加车辆（分页）
     *
     * @param req 查询请求
     * @return 可添加车辆分页结果
     */
    PageResult<SoOrderForPayableDto> queryAvailableVehicles(PayableListQueryAvailableVehiclesReq req);

    /**
     * 添加车辆
     *
     * @param req 添加请求
     * @return 结果
     */
    String addVehicles(PayableListAddVehiclesReq req);

    /**
     * 删除车辆
     *
     * @param req 删除请求
     * @return 结果
     */
    String deleteVehicles(PayableListDeleteVehicleReq req);
}
