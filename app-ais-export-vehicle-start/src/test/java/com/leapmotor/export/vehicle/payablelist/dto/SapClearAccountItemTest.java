package com.leapmotor.export.vehicle.payablelist.dto;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * SapClearAccountItem 请求结构单元测试
 *
 * <p>验证 SAP 清账回调的实际报文（顶层数组、小写键名 paynr/porder/sorder/vhvin/augdt）
 * 能被 Jackson（@RequestBody 反序列化）与 fastjson2 正确映射到行项目对象。
 *
 * @author huangtao
 * @since 2026-09-11
 */
@DisplayName("SapClearAccountItem 请求结构单元测试")
public class SapClearAccountItemTest {

    /** SAP 实际回调报文：顶层数组，键名为小写，同一清单下多条 SO */
    private static final String SAP_REQUEST = "["
            + "{\"paynr\":\"PL20260801001\",\"porder\":\"HQ20260801001\",\"sorder\":\"SO20260801001\","
            + "\"vhvin\":\"LGWEF4A52JJ123456\",\"augdt\":\"2026-07-31\"},"
            + "{\"paynr\":\"PL20260801001\",\"porder\":\"HQ20260801001\",\"sorder\":\"SO20260801002\","
            + "\"vhvin\":\"LGWEF4A52JJ123457\",\"augdt\":\"2026-07-31\"}"
            + "]";

    @Test
    @DisplayName("Jackson解析 — 实际报文的五个小写键名应完整映射")
    public void testJacksonDeserialize() throws Exception {
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<SapClearAccountItem> list = mapper.readValue(
                SAP_REQUEST, new TypeReference<List<SapClearAccountItem>>() {
                });

        assertEquals(2, list.size(), "清账行数应为 2");
        SapClearAccountItem item = list.get(0);
        assertEquals("PL20260801001", item.getPaynr(), "paynr 应映射为应付清单编号");
        assertEquals("HQ20260801001", item.getPorder(), "porder 应映射为PO订单号");
        assertEquals("SO20260801001", item.getSorder(), "sorder 应映射为SO订单号");
        assertEquals("LGWEF4A52JJ123456", item.getVhvin(), "vhvin 应映射为车辆VIN号");
        assertEquals(LocalDate.of(2026, 7, 31), item.getAugdt(),
                "augdt 应按 yyyy-MM-dd 解析为清账日期");
        assertEquals("SO20260801002", list.get(1).getSorder(), "第二条 sorder 应正确映射");
    }

    @Test
    @DisplayName("fastjson2解析 — 实际报文的五个小写键名应完整映射")
    public void testFastjson2Deserialize() {
        List<SapClearAccountItem> list = JSON.parseArray(SAP_REQUEST, SapClearAccountItem.class);

        assertNotNull(list, "解析结果不应为空");
        assertEquals(2, list.size(), "清账行数应为 2");
        SapClearAccountItem item = list.get(0);
        assertEquals("PL20260801001", item.getPaynr(), "paynr 应映射");
        assertEquals("HQ20260801001", item.getPorder(), "porder 应映射");
        assertEquals("SO20260801001", item.getSorder(), "sorder 应映射");
        assertEquals("LGWEF4A52JJ123456", item.getVhvin(), "vhvin 应映射");
        assertEquals(LocalDate.of(2026, 7, 31), item.getAugdt(), "augdt 应映射");
    }
}
