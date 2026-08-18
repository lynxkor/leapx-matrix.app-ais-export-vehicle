package com.leapmotor.export.vehicle.job;

import com.leapmotor.export.nexus.customer.api.CustomerApi;
import com.leapmotor.export.nexus.customer.request.CustomerRequest;
import com.leapmotor.export.nexus.customer.response.CustomerResponse;
import com.leapmotor.export.vehicle.cache.CustomerInfoCache;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 客户信息本地缓存定时刷新任务
 *
 * <p>模式 2:定时任务全量预热(Feign → Redis Hash)。
 * Feign 调 app-ais-export-nexus CustomerApi.list 拉全量客户,写 Redis Hash。
 * 查询侧按 customerId hget,O(1) 命中。
 *
 * <p>XXL-JOB handler key: {@code refreshCustomerInfoCacheJob}
 * <p>{@link ApplicationReadyEvent} 触发应用启动后立即执行一次预热,避免首次请求冷启动。
 *
 * @author wangkai2527148
 * @since 2026-07-11
 */
@Slf4j
@Component
@AllArgsConstructor
public class RefreshCustomerInfoCacheJob {

    private CustomerApi customerApi;
    private CustomerInfoCache customerInfoCache;

    @EventListener(ApplicationReadyEvent.class)
    public void onAppReady() {
        log.info("[启动预热] 服务启动,执行客户信息缓存首次刷新");
        refresh();
    }

    @XxlJob("refreshCustomerInfoCacheJob")
    public void refresh() {
        log.info("客户信息缓存刷新开始");
        long start = System.currentTimeMillis();
        try {
            CustomerRequest req = new CustomerRequest();
            AjaxResult<List<CustomerResponse>> ret = customerApi.list(req);
            List<CustomerResponse> list = (ret != null) ? ret.getData() : null;
            if (list == null || list.isEmpty()) {
                log.warn("CustomerApi 返回空,仅清空缓存");
                customerInfoCache.refreshAll(Collections.emptyList());
                return;
            }
            List<CustomerInfoCache.Item> items = new ArrayList<>(list.size());
            for (CustomerResponse c : list) {
                if (c == null || c.getId() == null) {
                    continue;
                }
                CustomerInfoCache.Item item = new CustomerInfoCache.Item();
                item.setCustomerId(c.getId());
                item.setCustomerCode(c.getCustomerCode());
                item.setCustomerName(c.getCustomerName());
                items.add(item);
            }
            customerInfoCache.refreshAll(items);
            log.info("客户信息缓存刷新完成: 共 {} 条,耗时 {} ms", items.size(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("客户信息缓存刷新失败,耗时 {} ms, err={}", System.currentTimeMillis() - start, e.getMessage(), e);
        }
    }
}
