package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 提前失效(审核通过 → lg_status=30 失效)
 */
@Data
public class ExportVehicleLetterGuaranteeExpireEarlyReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "失效原因(可选,不填则流水 transaction_remark 留空)")
    private String remark;
}
