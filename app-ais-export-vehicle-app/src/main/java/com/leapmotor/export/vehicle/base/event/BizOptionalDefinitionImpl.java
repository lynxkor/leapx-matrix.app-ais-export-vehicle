package com.leapmotor.export.vehicle.base.event;

import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.BizOptionalDefinition;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return mergeEnumLists(HongqiEventTypeEnum.values(), SapEventTypeEnum.values());
    }

    @Override
    public List<LpOptionalResult> findThirdTargetPlatformList() {
        List<LpOptionalResult> result = new ArrayList<>();
        Arrays.stream(ThirdTargetPlatformEnum.values())
                .forEach(e -> result.add(convertPlatformToOptional(e.getDesc(), e.getPlatform())));
        return result;
    }

    private LpOptionalResult convertPlatformToOptional(String desc, String code) {
        LpOptionalResult result = new LpOptionalResult();
        result.setName(desc);
        result.setCode(code);
        return result;
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
