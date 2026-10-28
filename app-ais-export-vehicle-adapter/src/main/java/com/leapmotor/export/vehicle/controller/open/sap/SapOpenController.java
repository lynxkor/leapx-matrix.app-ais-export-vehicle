package com.leapmotor.export.vehicle.controller.open.sap;

import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountItem;
import com.leapmotor.export.vehicle.payablelist.executor.integration.SapClearAccountProcessor;
import com.leapmotor.export.vehicle.payablelist.service.SapPayableListOpenService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    private SapPayableListOpenService sapPayableListOpenService;

    @Operation(summary = "SAP清账结果同步")
    @PostMapping("/payableList/clearCallback")
    public AjaxResult<Void> clearCallback(@RequestBody List<SapClearAccountItem> clearAccountList) {
        sapClearAccountProcessor.process(clearAccountList);
        return AjaxResult.success();
    }

    /**
     * 手动补推应付清单到 SAP：重发同步事件（立即异步执行），本接口不同步返回 SAP 结果
     *
     * @param body payableListNo 必传：应付清单编号；force 可选：已同步成功时是否强制重推（传 "true"）
     */
    @Operation(summary = "手动补推应付清单到SAP")
    @PostMapping("/payableList/sync")
    public AjaxResult<Object> syncPayableList(@RequestBody Map<String, String> body) {
        return AjaxResult.success(sapPayableListOpenService.syncPayableListByNo(
                body.get("payableListNo"), Boolean.parseBoolean(body.get("force"))));
    }

    /**
     * 直连补推应付清单到SAP：同步调用并原路返回SAP结果
     *
     * @param body payableListNo 必传：应付清单编号；force 可选：已同步成功时是否强制重推（传 "true"）
     */
    @Operation(summary = "直连补推应付清单到SAP（同步返回SAP结果）")
    @PostMapping("/payableList/syncDirect")
    public AjaxResult<Object> syncPayableListDirect(@RequestBody Map<String, String> body) {
        return AjaxResult.success(sapPayableListOpenService.syncPayableListDirect(
                body.get("payableListNo"), Boolean.parseBoolean(body.get("force"))));
    }
}
