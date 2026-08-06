package com.leapmotor.export.vehicle.base.util;

import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;

/**
 * 应付清单状态校验工具类
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public class PayableListStatusChecker {

    /**
     * 校验是否可提交（仅草稿/驳回状态可提交）
     */
    public static void checkCanSubmit(PayableListDo payableList) {
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        String status = payableList.getBillStatus();
        if (!PayableListStatusEnum.DRAFT.getCode().equals(status)
                && !PayableListStatusEnum.REJECTED.getCode().equals(status)) {
            throw new BizException(ResultEnum.BAD_REQUEST, "仅草稿/驳回状态的单据可提交");
        }
    }

    /**
     * 校验是否可编辑（仅草稿/驳回状态可编辑）
     */
    public static void checkCanEdit(PayableListDo payableList) {
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        String status = payableList.getBillStatus();
        if (!PayableListStatusEnum.DRAFT.getCode().equals(status)
                && !PayableListStatusEnum.REJECTED.getCode().equals(status)) {
            throw new BizException(ResultEnum.BAD_REQUEST, "仅草稿/驳回状态的单据可编辑");
        }
    }

    /**
     * 校验是否可编辑（仅草稿/驳回状态可编辑）
     * 别名方法，与 checkCanEdit 功能相同
     */
    public static void checkEditable(PayableListDo payableList) {
        checkCanEdit(payableList);
    }

    /**
     * 校验版本号（乐观锁）
     */
    public static void checkVersion(PayableListDo payableList, Integer reqVersion) {
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        if (reqVersion == null || !reqVersion.equals(payableList.getVersion())) {
            throw new BizException(ResultEnum.BAD_REQUEST, "数据已被他人修改，请刷新后重试");
        }
    }
}
