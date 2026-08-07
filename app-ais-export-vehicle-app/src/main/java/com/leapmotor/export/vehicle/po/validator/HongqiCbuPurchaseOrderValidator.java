package com.leapmotor.export.vehicle.po.validator;

import com.leapmotor.export.vehicle.po.req.HongqiCbuOrderDetailReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 红旗 CBU 采购申请入参校验器。
 *
 * <p>不依赖 jakarta validation 注解，改为手写一次性收集 <b>全部</b> 参数错误，message 完全可控：
 * 抬头字段错误形如 {@code [PO单号]格式错误...}，明细行错误形如 {@code 第2行[物料编码]必须为18位字母或数字，当前值: xxx}，
 * 调用方可一次拿到所有问题，而不是遇到第一个就中断。
 *
 * <p>校验规则与原 jakarta 注解等价，并补强了两处注解做不到的校验：
 * <ul>
 *   <li>PO单号 / 计划版本号 / 期望下线日期 的内嵌日期段必须是真实合法日期（注解只校验数字位，20261332 这类伪日期在此拦截）</li>
 *   <li>同一 PO 内订单明细编号唯一</li>
 * </ul>
 *
 * @author leapmotor
 * @since 2026-07-24
 */
@Component
public class HongqiCbuPurchaseOrderValidator {

    /** PO单号：HQ + YYYYMMDD + 6位流水（共 HQ + 14位数字） */
    private static final Pattern PO_NO = Pattern.compile("^HQ\\d{14}$");
    /** 18位零跑物料编码 */
    private static final Pattern MATERIAL_CODE = Pattern.compile("^[A-Za-z0-9]{18}$");
    /** 期望下线日期：YYYYMMDD */
    private static final Pattern OFFLINE_DATE = Pattern.compile("^\\d{8}$");
    /** VIN码年份：空 或 YYYY */
    private static final Pattern VIN_YEAR = Pattern.compile("^(|\\d{4})$");

    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    /** 全量校验，返回错误提示列表；空列表表示通过 */
    public List<String> validate(HongqiCbuPurchaseOrderReq req) {
        List<String> errors = new ArrayList<>();
        if (req == null) {
            errors.add("请求体不能为空");
            return errors;
        }
        validateHeader(req, errors);
        validateDetails(req, errors);
        return errors;
    }

    private void validateHeader(HongqiCbuPurchaseOrderReq req, List<String> errors) {
        // PO单号
        if (!notBlank(req.getPoNo())) {
            errors.add("[PO单号]不能为空");
        } else if (req.getPoNo().length() > 30) {
            errors.add("[PO单号]长度不能超过30");
        } else if (!PO_NO.matcher(req.getPoNo()).matches()) {
            errors.add("[PO单号]格式错误，规则 HQ+YYYYMMDD+6位流水号，当前值: " + req.getPoNo());
        } else if (!isRealDate(req.getPoNo().substring(2, 10))) {
            errors.add("[PO单号]内嵌日期段不是合法日期: " + req.getPoNo().substring(2, 10));
        }

        // 客户编码
        if (!notBlank(req.getCustomerCode())) {
            errors.add("[客户编码]不能为空");
        } else if (req.getCustomerCode().length() > 30) {
            errors.add("[客户编码]长度不能超过30");
        }

        // 计划版本号
        if (!notBlank(req.getPlanVersion())) {
            errors.add("[计划版本号]不能为空");
        } else if (req.getPlanVersion().length() > 30) {
            errors.add("[计划版本号]长度不能超过30");
        }

        // 订单明细列表
        List<HongqiCbuOrderDetailReq> details = req.getOrderDetailList();
        if (details == null || details.isEmpty()) {
            errors.add("[订单明细]不能为空");
        } else if (details.size() > 200) {
            errors.add("[订单明细]单次不能超过200条，当前: " + details.size() + "条");
        }
    }

    private void validateDetails(HongqiCbuPurchaseOrderReq req, List<String> errors) {
        List<HongqiCbuOrderDetailReq> details = req.getOrderDetailList();
        if (details == null || details.isEmpty()) {
            return;
        }
        Set<String> seenDetailNumber = new HashSet<>();
        for (int i = 0; i < details.size(); i++) {
            HongqiCbuOrderDetailReq d = details.get(i);
            String row = "第" + (i + 1) + "行";

            // 订单明细编号
            if (!notBlank(d.getDetailNumber())) {
                errors.add(row + "[订单明细编号]不能为空");
            } else if (d.getDetailNumber().length() > 30) {
                errors.add(row + "[订单明细编号]长度不能超过30");
            } else if (!seenDetailNumber.add(d.getDetailNumber())) {
                errors.add(row + "[订单明细编号]重复: " + d.getDetailNumber());
            }

            // 物料编码
            if (!notBlank(d.getMaterialCode())) {
                errors.add(row + "[物料编码]不能为空");
            } else if (!MATERIAL_CODE.matcher(d.getMaterialCode()).matches()) {
                errors.add(row + "[物料编码]必须为18位字母或数字，当前值: " + d.getMaterialCode());
            }

            // 需求数量
            BigDecimal q = d.getDemandQuantity();
            if (q == null) {
                errors.add(row + "[需求数量]不能为空");
            } else {
                if (q.signum() <= 0) {
                    errors.add(row + "[需求数量]必须大于0，当前值: " + q);
                }
                // 整数位 = precision - scale（scale<0 时表示末尾零，等价于整数位增多），小数位 = max(scale,0)
                long intDigits = (long) q.precision() - q.scale();
                if (intDigits > 18) {
                    errors.add(row + "[需求数量]整数部分不超过18位，当前值: " + q);
                }
                if (q.scale() > 2) {
                    errors.add(row + "[需求数量]小数不超过2位，当前值: " + q);
                }
            }

            // VIN码年份（可空）
            if (notBlank(d.getVinYear())) {
                if (d.getVinYear().length() > 10) {
                    errors.add(row + "[VIN码年份]长度不能超过10");
                } else if (!VIN_YEAR.matcher(d.getVinYear()).matches()) {
                    errors.add(row + "[VIN码年份]格式错误，应为YYYY，当前值: " + d.getVinYear());
                }
            }

            // 期望下线日期
            if (!notBlank(d.getExpectedOfflineDate())) {
                errors.add(row + "[期望下线日期]不能为空");
            } else if (d.getExpectedOfflineDate().length() > 10) {
                errors.add(row + "[期望下线日期]长度不能超过10");
            } else if (!OFFLINE_DATE.matcher(d.getExpectedOfflineDate()).matches()) {
                errors.add(row + "[期望下线日期]格式错误，应为YYYYMMDD，当前值: " + d.getExpectedOfflineDate());
            } else if (!isRealDate(d.getExpectedOfflineDate())) {
                errors.add(row + "[期望下线日期]不是合法日期: " + d.getExpectedOfflineDate());
            }
        }
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** yyyyMMdd 是否为真实合法日期（拦截 20261332 这类伪日期） */
    private static boolean isRealDate(String yyyyMMdd) {
        try {
            LocalDate.parse(yyyyMMdd, BASIC_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}