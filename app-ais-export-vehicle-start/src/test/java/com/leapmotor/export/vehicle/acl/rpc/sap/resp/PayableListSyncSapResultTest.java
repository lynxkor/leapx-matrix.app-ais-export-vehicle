package com.leapmotor.export.vehicle.acl.rpc.sap.resp;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult.Body;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult.EsRet;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult.RetTabItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PayableListSyncSapResult 返回结构单元测试
 *
 * <p>验证 SAP ABAP Gateway 大写 Key 返回（ES_RET 总状态 + RET_TAB 明细表格）
 * 能被 Jackson（Feign 响应解码）与 fastjson2 正确反序列化。
 *
 * @author huangtao
 * @since 2026-09-08
 */
@DisplayName("PayableListSyncSapResult 返回结构单元测试")
public class PayableListSyncSapResultTest {

    /**
     * SAP 实际返回形态：body 下并列 ES_RET 与 RET_TAB（大写 Key）。
     */
    private static final String SAP_RESPONSE = "{\"code\":\"0\",\"message\":\"操作成功\",\"success\":true,"
            + "\"body\":{"
            + "\"ES_RET\":{\"RET_CODE\":\"E\",\"RET_REASON\":\"存在无效车辆\"},"
            + "\"RET_TAB\":["
            + "{\"PAYNR\":\"YF202606000001\",\"PORDER\":\"PO0001\",\"SORDER\":\"SO0001\","
            + "\"VHVIN\":\"LFZJ9X4K8PGH65270U\",\"RET_CODE\":\"E\",\"RET_REASON\":\"无效的车辆编号LFZJ9X4K8PGH65270U\"},"
            + "{\"PAYNR\":\"YF202606000001\",\"PORDER\":\"PO0002\",\"SORDER\":\"SO0002\","
            + "\"VHVIN\":\"LFZJ9X4K8PGH65271U\",\"RET_CODE\":\"S\",\"RET_REASON\":\"成功\"}"
            + "]}}";

    @Test
    @DisplayName("Jackson解析 — ES_RET与RET_TAB应完整映射")
    public void testJacksonDeserialize() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PayableListSyncSapResult result = mapper.readValue(SAP_RESPONSE, PayableListSyncSapResult.class);

        assertNotNull(result.getBody(), "body 不应为空");
        // ES_RET 总状态
        assertEquals("E", result.getBody().getEsRet().getRetCode(), "总状态 RET_CODE 应映射为 E");
        assertEquals("存在无效车辆", result.getBody().getEsRet().getRetReason(), "总状态 RET_REASON 应映射");
        // RET_TAB 明细表格
        List<RetTabItem> retTab = result.getBody().getRetTab();
        assertNotNull(retTab, "RET_TAB 不应为空");
        assertEquals(2, retTab.size(), "RET_TAB 行数应为 2");

        RetTabItem first = retTab.get(0);
        assertEquals("YF202606000001", first.getPaynr(), "PAYNR 应映射");
        assertEquals("PO0001", first.getPorder(), "PORDER 应映射");
        assertEquals("SO0001", first.getSorder(), "SORDER 应映射");
        assertEquals("LFZJ9X4K8PGH65270U", first.getVhvin(), "VHVIN 应映射");
        assertEquals("E", first.getRetCode(), "明细 RET_CODE 应映射");
        assertEquals("无效的车辆编号LFZJ9X4K8PGH65270U", first.getRetReason(), "明细 RET_REASON 应映射");

        assertEquals("S", retTab.get(1).getRetCode(), "成功明细状态应为 S");
    }

    @Test
    @DisplayName("fastjson2解析 — ES_RET与RET_TAB应完整映射")
    public void testFastjson2Deserialize() {
        PayableListSyncSapResult result = JSON.parseObject(SAP_RESPONSE, PayableListSyncSapResult.class);

        assertNotNull(result.getBody(), "body 不应为空");
        assertEquals("E", result.getBody().getEsRet().getRetCode(), "总状态 RET_CODE 应映射为 E");
        assertEquals("存在无效车辆", result.getBody().getEsRet().getRetReason(), "总状态 RET_REASON 应映射");

        List<RetTabItem> retTab = result.getBody().getRetTab();
        assertNotNull(retTab, "RET_TAB 不应为空");
        assertEquals(2, retTab.size(), "RET_TAB 行数应为 2");
        assertEquals("LFZJ9X4K8PGH65270U", retTab.get(0).getVhvin(), "VHVIN 应映射");
        assertEquals("E", retTab.get(0).getRetCode(), "明细 RET_CODE 应映射");
        assertEquals("S", retTab.get(1).getRetCode(), "成功明细状态应为 S");
    }

    @Test
    @DisplayName("序列化 — 组装对象可输出大写Key JSON（ES_RET+RET_TAB）")
    public void testSerializeUppercaseKeys() {
        PayableListSyncSapResult result = new PayableListSyncSapResult();
        result.setSuccess(true);
        Body body = new Body();

        EsRet esRet = new EsRet();
        esRet.setRetCode("E");
        esRet.setRetReason("存在无效车辆");
        body.setEsRet(esRet);

        RetTabItem item = new RetTabItem();
        item.setPaynr("YF202606000001");
        item.setPorder("PO0001");
        item.setSorder("SO0001");
        item.setVhvin("LFZJ9X4K8PGH65270U");
        item.setRetCode("E");
        item.setRetReason("无效的车辆编号LFZJ9X4K8PGH65270U");
        body.setRetTab(List.of(item));
        result.setBody(body);

        String json = JSON.toJSONString(result);

        assertTrue(json.contains("\"ES_RET\""), "序列化应输出大写 ES_RET");
        assertTrue(json.contains("\"RET_TAB\""), "序列化应输出大写 RET_TAB");
        assertTrue(json.contains("\"PAYNR\":\"YF202606000001\""), "行项目应输出大写 PAYNR");
        assertTrue(json.contains("\"VHVIN\":\"LFZJ9X4K8PGH65270U\""), "行项目应输出大写 VHVIN");
        assertTrue(json.contains("\"RET_CODE\":\"E\""), "行项目应输出大写 RET_CODE");
    }
}
