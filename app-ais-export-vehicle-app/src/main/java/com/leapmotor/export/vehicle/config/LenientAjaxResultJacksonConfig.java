package com.leapmotor.export.vehicle.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 * nexus Feign 响应里 AjaxResult.timestamp 带完整时间(+纳秒)，
 * 平台 DynamicTimeZoneLocalDateTimeDeserializer 调 LocalDate.parse(yyyy-MM-dd) 只认日期 → 反序列化失败。
 * 这里用 MixIn 覆盖 AjaxResult.timestamp 的反序列化，认完整 LocalDateTime（yyyy-MM-dd HH:mm:ss + 可选 0-9 位纳秒）。
 */
@Configuration
public class LenientAjaxResultJacksonConfig {

    static final DateTimeFormatter LENIENT_LDT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter(Locale.CHINA);

    /** MixIn：覆盖 AjaxResult.timestamp 字段的 @JsonDeserialize */
    abstract static class AjaxResultTimestampMixin {
        @JsonDeserialize(using = LenientLocalDateTimeDeserializer.class)
        private LocalDateTime timestamp;
    }

    /** 认 yyyy-MM-dd HH:mm:ss + 可选任意位小数(纳秒) 的 LocalDateTime 反序列化器 */
    public static class LenientLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String str = p.getValueAsString();
            if (str == null || str.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(str, LENIENT_LDT);
        }
    }

    /** 注册 MixIn 到全局 ObjectMapper（Feign Decoder / MVC 共用） */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer ajaxResultTimestampMixInCustomizer() {
        return builder -> builder.mixIn(AjaxResult.class, AjaxResultTimestampMixin.class);
    }
}
