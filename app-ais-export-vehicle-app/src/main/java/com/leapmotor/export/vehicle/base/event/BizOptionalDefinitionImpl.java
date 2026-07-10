package com.leapmotor.export.vehicle.base.event;

import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.HongqiThirdTargetPlatformEnum;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.BizOptionalDefinition;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 事件/平台下拉框定义（覆盖框架默认空实现，供前端下拉选）
 */
@Service
public class BizOptionalDefinitionImpl implements BizOptionalDefinition {

    @Override
    public List<LpOptionalResult> findEventTypeList() {
        return mergeEnumLists(HongqiEventTypeEnum.values());
    }

    @Override
    public List<LpOptionalResult> findThirdTargetPlatformList() {
        return Arrays.stream(HongqiThirdTargetPlatformEnum.values())
                .map(e -> {
                    LpOptionalResult result = new LpOptionalResult();
                    result.setName(e.getDesc());
                    result.setCode(e.getPlatform());
                    return result;
                })
                .toList();
    }

    private List<LpOptionalResult> mergeEnumLists(BizEventType[]... enumArrays) {
        return Stream.of(enumArrays)
                .flatMap(Arrays::stream)
                .map(this::convertToOptionalResult)
                .toList();
    }

    private LpOptionalResult convertToOptionalResult(BizEventType eventType) {
        LpOptionalResult result = new LpOptionalResult();
        result.setName(eventType.getDesc());
        result.setCode(eventType.getType());
        return result;
    }
}
