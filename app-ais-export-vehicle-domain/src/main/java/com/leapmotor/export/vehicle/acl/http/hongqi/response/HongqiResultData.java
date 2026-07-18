package com.leapmotor.export.vehicle.acl.http.hongqi.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 红旗响应 data（VDO53002/VDO53003 标准响应体：receiveflag + errormessage）。
 */
@Data
public class HongqiResultData {

    /** 是否接收成功：Y=成功 / N=失败 */
    @JsonProperty("receiveflag")
    @JSONField(name = "receiveflag")
    private String receiveFlag;

    /** 错误消息（成功时为空） */
    @JsonProperty("errormessage")
    @JSONField(name = "errormessage")
    private String errorMessage;
}
