package com.leapmotor.export.vehicle.payablelist.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 应付清单添加车辆请求
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Data
public class PayableListAddVehiclesReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单ID
     */
    @NotBlank(message = "应付清单ID不能为空")
    private String payableListId;

    /**
     * SO订单ID列表
     */
    @NotEmpty(message = "车辆ID列表不能为空")
    private List<String> vehicleIds;
}
