package com.leapmotor.export.vehicle.so.req;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * APS 产销平台同步状态给销服请求
 *
 * <p>APS 外部状态码 {@code status} 映射：
 * 1->1003 排产中、2->1004 焊装上线、3->1005 涂装上线、4->1006 总装上线、5->1007 总装下线。
 *
 * <p>字段取值参考接口文档《产销平台同步状态给销服》：
 * <ul>
 *   <li>产销识别码：状态 1 时会传；</li>
 *   <li>内部车辆号：状态 2/3/4/5 时会传；</li>
 *   <li>VIN：状态 4/5 时会传；</li>
 *   <li>计划总装下线日期：状态 1 时会传；</li>
 *   <li>排产时间：状态 1 会传；</li>
 *   <li>打印条码时间：状态 4/5 会传；</li>
 *   <li>焊装/涂装/总装上线时间、总装下线时间：对应状态必传。</li>
 * </ul>
 */
@Data
public class VehicleCbuSoReceiveStatusReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SO 单号
     */
    private String soNo;

    /**
     * APS 外部状态码：1/2/3/4/5
     */
    private Integer status;

    /**
     * 产销识别码，状态 1 时从 APS 获取
     */
    private String productionIdentifyCode;

    /**
     * 内部车辆号，状态 2/3/4/5 时从 APS 获取
     */
    private String internalVehicleNo;

    /**
     * VIN 码，状态 4/5 时从 APS 获取
     */
    private String vin;

    /**
     * 计划总装下线日期，状态 1 时从 APS 获取
     */
    private LocalDate planOfflineDate;

    /**
     * 排产时间
     */
    private LocalDateTime scheduleTime;

    /**
     * 打印条码时间
     */
    private LocalDateTime barcodePrintTime;

    /**
     * 焊装上线时间
     */
    private LocalDateTime bodyInTime;

    /**
     * 涂装上线时间
     */
    private LocalDateTime paintInTime;

    /**
     * 总装上线时间
     */
    private LocalDateTime assemblyInTime;

    /**
     * 总装下线时间
     */
    private LocalDateTime assemblyOutTime;
}
