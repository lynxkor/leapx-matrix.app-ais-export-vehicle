package com.leapmotor.export.vehicle.acl.rpc.hongqi.response;

import lombok.Data;

/**
 * 红旗接口返回（VDO53002/VDO53003 响应壳：code+message+data+success）。
 * 业务真正接收成功需 success=true 且 data.receiveflag=Y（code=200 但 receiveflag=N 表示红旗业务层拒绝）。
 */
@Data
public class HongqiResult {

    private int code;
    private String message;
    private HongqiResultData data;
    private boolean success;
}
