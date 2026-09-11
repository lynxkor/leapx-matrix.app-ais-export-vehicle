package com.leapmotor.export.vehicle.controller.open.sap;

import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountItem;
import com.leapmotor.export.vehicle.payablelist.executor.integration.SapClearAccountProcessor;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * SAP开放接口清账回调单元测试
 *
 * <p>控制器直接接收 SAP 顶层数组报文，封装为请求对象后委托处理器：
 * 成功返回 AjaxResult.success；业务/系统异常原样冒泡，由公共组件 GlobalExceptionHandler
 * 统一转换为带明确提示的失败响应（BizException → 业务错误码 400；
 * HttpMessageNotReadableException 等报文格式错误 → badRequest 400 类提示）。
 *
 * @author huangtao
 * @since 2026-09-11
 */
@DisplayName("SAP开放接口清账回调单元测试")
@ExtendWith(MockitoExtension.class)
public class SapOpenControllerTest {

    @Mock
    private SapClearAccountProcessor sapClearAccountProcessor;

    @Test
    @DisplayName("业务成功 — 顶层数组原样封装后委托处理器，返回成功响应")
    public void testClearCallback_success() {
        SapOpenController controller = new SapOpenController(sapClearAccountProcessor);
        SapClearAccountItem item = new SapClearAccountItem();
        item.setSorder("SO20260801001");
        List<SapClearAccountItem> clearAccountList = Collections.singletonList(item);

        AjaxResult<Void> result = controller.clearCallback(clearAccountList);

        verify(sapClearAccountProcessor).process(clearAccountList);
        assertNotNull(result);
        assertEquals(200, result.getCode(), "成功响应 code 应为 200");
        assertTrue(result.isSuccess(), "响应体应标记为成功");
    }

    @Test
    @DisplayName("空数组 — 原样透传交由处理器给出明确提示")
    public void testClearCallback_emptyList() {
        SapOpenController controller = new SapOpenController(sapClearAccountProcessor);

        controller.clearCallback(Collections.emptyList());

        verify(sapClearAccountProcessor).process(Collections.emptyList());
    }

    @Test
    @DisplayName("业务失败 — 异常冒泡交由全局异常处理器返回明确提示")
    public void testClearCallback_exceptionPropagates() {
        SapOpenController controller = new SapOpenController(sapClearAccountProcessor);
        BizException bizException = new BizException("保函应收释放失败，需人工核查：sorder=SO0001, porder=PO0001");
        doThrow(bizException).when(sapClearAccountProcessor).process(any());
        SapClearAccountItem item = new SapClearAccountItem();

        BizException thrown = assertThrows(BizException.class,
                () -> controller.clearCallback(Collections.singletonList(item)));

        assertSame(bizException, thrown, "业务异常应原样冒泡给全局异常处理器");
        assertEquals(400, thrown.getCode(), "BizException 默认业务错误码为 400（BAD_REQUEST）");
    }
}
