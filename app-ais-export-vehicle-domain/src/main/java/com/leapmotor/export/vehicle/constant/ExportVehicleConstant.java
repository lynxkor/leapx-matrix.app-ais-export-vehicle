package com.leapmotor.export.vehicle.constant;

/**
 * 整车出口模块全局常量
 */
public interface ExportVehicleConstant {

    // ============== 保函管理(letterGuarantee) ==============

    /**
     * PO 资金校验的保函覆盖天数(失效日期 ≥ 订单创建时间 + 该天数)
     * <p>默认 100;实际值应从 Nacos 配置 {@link LETTER_GUARANTEE_COVER_DAYS_CONFIG_KEY} 读取,
     * 本常量作为配置缺失时的兜底。
     */
    long LETTER_GUARANTEE_COVER_DAYS = 100L;

}
