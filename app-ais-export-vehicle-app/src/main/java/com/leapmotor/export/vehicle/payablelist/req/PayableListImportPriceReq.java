package com.leapmotor.export.vehicle.payablelist.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 导入最新价格请求
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Data
public class PayableListImportPriceReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单ID
     */
    private String payableListId;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * Excel数据JSON字符串
     */
    private String excel;
}
