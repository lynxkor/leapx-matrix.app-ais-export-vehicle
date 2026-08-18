package com.leapmotor.export.vehicle.acl.rpc.hongqi.response;

import lombok.Data;

/**
 * 红旗接口返回壳（VDO53002/VDO53003：code/message/data/success/result）。
 *
 * <p>data 为泛型，各接口返回结构不同：
 * <ul>
 *   <li>{@code pushCbuApprovalResult}：data = {@link HongqiResultData}（receiveflag/errormessage）</li>
 *   <li>{@code pushProductionNodeSync}：data = {@code List<HongqiProductionNodeResultData>}（poNo/soNo/lpMtlNo/vin/isSign/errMsg）</li>
 * </ul>
 *
 * <p>业务真正接收成功需 success=true 且 data 内接收标识为 Y
 * （VDO53002 receiveflag=Y / VDO53003 每条 isSign=Y）。
 * 成功响应含 result=true（与 code 互斥；失败响应无 result 有 code）。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@Data
public class HongqiResult<T> {

    private int code;

    private String message;

    private T data;

    private boolean success;

    /**
     * VDO53003 成功响应含 result=true（与 code 互斥）
     */
    private boolean result;
}
