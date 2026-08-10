package com.leapmotor.export.vehicle.base.util;

import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应付清单状态校验工具类测试
 *
 * @author 赵吴涯
 * @since 2026-08-07
 */
@DisplayName("应付清单状态校验工具类测试")
public class PayableListStatusCheckerTest {

    @Test
    @DisplayName("checkCanSubmit — 草稿状态应通过校验")
    public void testCheckCanSubmit_draft() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());

        // 不应抛出异常
        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanSubmit(payableList));
    }

    @Test
    @DisplayName("checkCanSubmit — 驳回状态应通过校验")
    public void testCheckCanSubmit_rejected() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.REJECTED.getCode());

        // 不应抛出异常
        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanSubmit(payableList));
    }

    @Test
    @DisplayName("checkCanSubmit — 已提交状态应抛出异常")
    public void testCheckCanSubmit_submitted() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.SUBMITTED.getCode());

        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanSubmit(payableList)
        );

        assertTrue(
                exception.getMessage().contains("仅草稿/驳回状态的单据可提交"),
                "异常消息应包含 '仅草稿/驳回状态的单据可提交'"
        );
    }

    @Test
    @DisplayName("checkCanSubmit — 已确认状态应抛出异常")
    public void testCheckCanSubmit_confirmed() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.CONFIRMED.getCode());

        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanSubmit(payableList)
        );

        assertTrue(
                exception.getMessage().contains("仅草稿/驳回状态的单据可提交"),
                "异常消息应包含 '仅草稿/驳回状态的单据可提交'"
        );
    }

    @Test
    @DisplayName("checkCanSubmit — null参数应抛出异常")
    public void testCheckCanSubmit_null() {
        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanSubmit(null)
        );

        assertTrue(
                exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'"
        );
    }

    @Test
    @DisplayName("checkCanEdit — 草稿状态应通过校验")
    public void testCheckCanEdit_draft() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());

        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanEdit(payableList));
    }

    @Test
    @DisplayName("checkCanEdit — 驳回状态应通过校验")
    public void testCheckCanEdit_rejected() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.REJECTED.getCode());

        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanEdit(payableList));
    }

    @Test
    @DisplayName("checkCanEdit — 已提交状态应抛出异常")
    public void testCheckCanEdit_submitted() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.SUBMITTED.getCode());

        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanEdit(payableList)
        );

        assertTrue(
                exception.getMessage().contains("仅草稿/驳回状态的单据可编辑"),
                "异常消息应包含 '仅草稿/驳回状态的单据可编辑'"
        );
    }

    @Test
    @DisplayName("checkCanEdit — null参数应抛出异常")
    public void testCheckCanEdit_null() {
        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanEdit(null)
        );

        assertTrue(
                exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'"
        );
    }

    @Test
    @DisplayName("checkEditable — 草稿状态应通过校验（别名方法）")
    public void testCheckEditable_draft() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());

        assertDoesNotThrow(() -> PayableListStatusChecker.checkEditable(payableList));
    }

    @Test
    @DisplayName("checkEditable — 已提交状态应抛出异常（别名方法）")
    public void testCheckEditable_submitted() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.SUBMITTED.getCode());

        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkEditable(payableList)
        );

        assertTrue(
                exception.getMessage().contains("仅草稿/驳回状态的单据可编辑"),
                "异常消息应包含 '仅草稿/驳回状态的单据可编辑'"
        );
    }

    @Test
    @DisplayName("checkVersion — 版本号匹配应通过校验")
    public void testCheckVersion_match() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(1);

        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, 1));
    }

    @Test
    @DisplayName("checkVersion — 版本号不匹配应抛出异常")
    public void testCheckVersion_mismatch() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(1);

        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkVersion(payableList, 2)
        );

        assertTrue(
                exception.getMessage().contains("数据已被他人修改，请刷新后重试"),
                "异常消息应包含 '数据已被他人修改，请刷新后重试'"
        );
    }

    @Test
    @DisplayName("checkVersion — null版本号应抛出异常")
    public void testCheckVersion_nullVersion() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(1);

        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkVersion(payableList, null)
        );

        assertTrue(
                exception.getMessage().contains("数据已被他人修改，请刷新后重试"),
                "异常消息应包含 '数据已被他人修改，请刷新后重试'"
        );
    }

    @Test
    @DisplayName("checkVersion — null参数应抛出异常")
    public void testCheckVersion_nullPayableList() {
        BizException exception = assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkVersion(null, 1)
        );

        assertTrue(
                exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'"
        );
    }

    @Test
    @DisplayName("checkVersion — 版本号为0时的边界测试")
    public void testCheckVersion_zeroVersion() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(0);

        // 版本号为0也是合法的
        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, 0));
    }

    @Test
    @DisplayName("checkVersion — 大版本号的边界测试")
    public void testCheckVersion_largeVersion() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(Integer.MAX_VALUE);

        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("checkVersion — 版本号从0到1的递增场景")
    public void testCheckVersion_versionIncrement() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(1);

        // 初始版本号为1，请求版本号也应为1
        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, 1));

        // 模拟更新后版本号为2，旧版本号1应校验失败
        payableList.setVersion(2);
        assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkVersion(payableList, 1)
        );
    }

    @Test
    @DisplayName("完整场景测试 — 草稿状态提交流程")
    public void testFullScenario_draftSubmit() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.DRAFT.getCode());
        payableList.setVersion(1);

        // 草稿状态可以提交
        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanSubmit(payableList));

        // 版本号校验通过
        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, 1));

        // 草稿状态可以编辑
        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanEdit(payableList));
    }

    @Test
    @DisplayName("完整场景测试 — 已提交状态不可再提交")
    public void testFullScenario_submittedCannotSubmit() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.SUBMITTED.getCode());
        payableList.setVersion(2);

        // 已提交状态不能提交
        assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanSubmit(payableList)
        );

        // 已提交状态不能编辑
        assertThrows(
                BizException.class,
                () -> PayableListStatusChecker.checkCanEdit(payableList)
        );

        // 版本号校验仍然有效
        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, 2));
    }

    @Test
    @DisplayName("完整场景测试 — 驳回状态可重新提交")
    public void testFullScenario_rejectedCanSubmit() {
        PayableListDo payableList = createPayableList(PayableListStatusEnum.REJECTED.getCode());
        payableList.setVersion(3);

        // 驳回状态可以提交
        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanSubmit(payableList));

        // 驳回状态可以编辑
        assertDoesNotThrow(() -> PayableListStatusChecker.checkCanEdit(payableList));

        // 版本号校验通过
        assertDoesNotThrow(() -> PayableListStatusChecker.checkVersion(payableList, 3));
    }

    /**
     * 创建应付清单领域对象
     */
    private PayableListDo createPayableList(String billStatus) {
        PayableListDo payableList = new PayableListDo();
        payableList.setId("test-id-001");
        payableList.setPayableListNo("YF20260700001");
        payableList.setBillStatus(billStatus);
        payableList.setCustomerCode("CUST001");
        payableList.setCustomerName("测试客户");
        payableList.setTotalVehicles(10);
        payableList.setTotalAmount(new BigDecimal("1500000.00"));
        payableList.setGenerateMonth("202607");
        payableList.setVersion(1);
        return payableList;
    }
}
