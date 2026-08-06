package com.leapmotor.export.vehicle.payablelist.gatewayimpl;

import com.leapmotor.export.vehicle.payablelist.dto.LgBindInfoDto;
import com.leapmotor.export.vehicle.payablelist.dto.LgReleaseReceivableReq;
import com.leapmotor.export.vehicle.payablelist.gateway.LetterGuaranteeGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 保函领域网关实现（同JVM调用保函模块）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Repository
@AllArgsConstructor
public class LetterGuaranteeGatewayImpl implements LetterGuaranteeGateway {

    // TODO: 注入保函模块 DomainGateway，同 JVM 调用
    // private LetterGuaranteeDomainGateway letterGuaranteeDomainGateway;

    @Override
    public List<LgBindInfoDto> queryBySoNo(String soNo) {
        // TODO: 调用 letterGuaranteeDomainGateway.queryBySoNo(soNo)
        log.warn("[LetterGuaranteeGatewayImpl] queryBySoNo 待实现，保函模块Gateway未注入");
        return Collections.emptyList();
    }

    @Override
    public boolean releaseReceivable(LgReleaseReceivableReq req) {
        // TODO: 调用 letterGuaranteeDomainGateway.releaseReceivable(req)
        log.warn("[LetterGuaranteeGatewayImpl] releaseReceivable 待实现，保函模块Gateway未注入");
        return false;
    }
}
