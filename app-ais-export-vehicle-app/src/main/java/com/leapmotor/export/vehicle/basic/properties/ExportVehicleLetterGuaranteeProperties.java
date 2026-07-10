package com.leapmotor.export.vehicle.basic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 保函管理 Nacos 配置项
 *
 * <p>Nacos 路径 {@code letterGuarantee.*} 或 kebab-case {@code letter-guarantee.*} 均可(Spring relaxed binding)
 * <pre>
 * letterGuarantee:
 *   coverDays: 100  # 资金校验覆盖天数(失效日期 ≥ 订单创建时间 + 此天数)
 * </pre>
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Data
@Component
@ConfigurationProperties(prefix = "letter-guarantee")
public class ExportVehicleLetterGuaranteeProperties {

    /**
     * 资金校验覆盖天数(失效日期 ≥ 订单创建时间 + 此天数)
     * 默认 100 天
     */
    private Long coverDays = 100L;
}
