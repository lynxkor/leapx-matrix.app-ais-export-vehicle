package com.leapmotor.export.vehicle.base.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Swagger / OpenAPI 文档配置
 */
@Configuration
@Profile({"dev"})
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("app-ais-export-vehicle--整车订单业务 API")
                        .version("1.0.0")
                        .description("整车订单业务（CBU/SKD/CKD）服务 API 文档"));
    }
}
