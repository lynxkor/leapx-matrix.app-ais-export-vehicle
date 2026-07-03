package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 校验响应(§8.5.1)
 */
@Data
public class ExportVehicleLetterGuaranteeValidateResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否通过")
    private Boolean passed;

    @Schema(description = "失败代码(i18n key 前缀,如 dict.letterGuarantee.validate.fail.insufficient)")
    private String failCode;

    @Schema(description = "失败文案(i18n)")
    private String failMessage;

    @Schema(description = "候选保函列表(供 PO 提交后批量冻结)")
    private List<ExportVehicleLetterGuaranteeListAvailableResp> candidates;

    @Schema(description = "本次申请所需金额")
    @BigDecimalFormat(pattern = "#.########")
    private java.math.BigDecimal requiredAmount;

    @Schema(description = "候选保函可用总金额")
    @BigDecimalFormat(pattern = "#.########")
    private java.math.BigDecimal totalAvailable;
}
