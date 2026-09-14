package com.leapmotor.export.vehicle.controller.open.sap;

import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountItem;
import com.leapmotor.export.vehicle.payablelist.executor.integration.SapClearAccountProcessor;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SAP系统开放接口
 * 路径前缀 /openApi/sap，需签名校验。
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Tag(name = "SAP系统开放接口", description = "SAP系统开放接口")
@RestController
@AllArgsConstructor
@RequestMapping("/openApi/sap")
public class SapOpenController {

    private SapClearAccountProcessor sapClearAccountProcessor;

    @Operation(summary = "SAP清账结果同步")
    @PostMapping("/payableList/clearCallback")
    public AjaxResult<Void> clearCallback(@RequestBody List<SapClearAccountItem> clearAccountList) {
        sapClearAccountProcessor.process(clearAccountList);
        return AjaxResult.success();
    }
}
