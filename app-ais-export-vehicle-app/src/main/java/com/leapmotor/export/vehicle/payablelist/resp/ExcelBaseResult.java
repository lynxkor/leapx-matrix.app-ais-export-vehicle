package com.leapmotor.export.vehicle.payablelist.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Excel导入基础返回结果
 *
 * @author 赵吴涯
 * @since 2026-07-27
 */
@Data
public class ExcelBaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功数量
     */
    private Integer successNum;

    /**
     * 失败数量
     */
    private Integer failNum;

    /**
     * 失败明细数据
     */
    private List<?> data;
}
