package com.leapmotor.export.vehicle.cache;

import com.alibaba.fastjson.JSON;
import com.leapmotor.export.vehicle.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 客户信息本地缓存(Redis Hash 模式)
 *
 * <p>模式 2:定时任务全量预热(Feign → Redis Hash)。
 * key = customerId,value = JSON({customerCode,customerName})。
 * 写入前先 del 清空旧 key,避免脏数据;查询侧按 field 直接 hget,O(1) 命中。
 *
 * @author wangkai2527148
 * @since 2026-07-11
 */
@Slf4j
@Component
public class CustomerInfoCache {

    private final StringRedisTemplate stringRedisTemplate;

    public CustomerInfoCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 全量刷新:清空旧 key 后 hmset 写入新数据。
     *
     * @param items 全量客户信息(允许为 null/空,空则只清空)
     */
    public void refreshAll(List<Item> items) {
        stringRedisTemplate.delete(CacheConstant.CUSTOMER_INFO_REDIS_KEY);
        if (items == null || items.isEmpty()) {
            log.warn("客户信息全量刷新,数据为空,仅清空 Redis key={}", CacheConstant.CUSTOMER_INFO_REDIS_KEY);
            return;
        }
        Map<String, String> map = new HashMap<>(items.size() * 2);
        for (Item item : items) {
            if (item.getCustomerId() == null || item.getCustomerId().isEmpty()) {
                continue;
            }
            map.put(item.getCustomerId(), JSON.toJSONString(item));
        }
        if (!map.isEmpty()) {
            stringRedisTemplate.opsForHash().putAll(CacheConstant.CUSTOMER_INFO_REDIS_KEY, map);
        }
        log.info("客户信息全量刷新完成,共 {} 条,key={}", map.size(), CacheConstant.CUSTOMER_INFO_REDIS_KEY);
    }

    /**
     * 单查:按 customerId 取缓存,未命中返回 null。
     */
    public Item getById(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return null;
        }
        Object raw = stringRedisTemplate.opsForHash().get(CacheConstant.CUSTOMER_INFO_REDIS_KEY, customerId);
        if (raw == null) {
            return null;
        }
        try {
            return JSON.parseObject(raw.toString(), Item.class);
        } catch (Exception e) {
            log.error("客户信息缓存反序列化失败, customerId={}, raw={}", customerId, raw, e);
            return null;
        }
    }

    /**
     * 批量取:按 customerId 集合取,未命中不回填。
     */
    public Map<String, Item> getByIds(Collection<String> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object> fields = new ArrayList<>(customerIds);
        List<Object> values = stringRedisTemplate.opsForHash()
                .multiGet(CacheConstant.CUSTOMER_INFO_REDIS_KEY, fields);
        if (values == null) {
            return Collections.emptyMap();
        }
        Map<String, Item> result = new HashMap<>(values.size() * 2);
        int i = 0;
        for (Object v : values) {
            if (v == null) {
                i++;
                continue;
            }
            try {
                Item item = JSON.parseObject(v.toString(), Item.class);
                if (item != null && Objects.equals(item.getCustomerId(), fields.get(i))) {
                    result.put(item.getCustomerId(), item);
                }
            } catch (Exception e) {
                log.error("客户信息缓存批量反序列化失败, customerId={}, raw={}", fields.get(i), v, e);
            }
            i++;
        }
        return result;
    }

    /**
     * 单查:按 customerCode 取缓存,未命中返回 null。
     * <p>Redis Hash 仅以 customerId 为 field,无 customerCode 索引,
     * 内部遍历全部 value,O(N);命中第一条即返回。
     */
    public Item getByCustomerCode(String customerCode) {
        if (customerCode == null || customerCode.isEmpty()) {
            return null;
        }
        List<Object> values = stringRedisTemplate.opsForHash().values(CacheConstant.CUSTOMER_INFO_REDIS_KEY);
        if (values == null || values.isEmpty()) {
            return null;
        }
        for (Object v : values) {
            if (v == null) {
                continue;
            }
            try {
                Item item = JSON.parseObject(v.toString(), Item.class);
                if (item != null && Objects.equals(item.getCustomerCode(), customerCode)) {
                    return item;
                }
            } catch (Exception e) {
                log.error("客户信息缓存反序列化失败, customerCode={}, raw={}", customerCode, v, e);
            }
        }
        return null;
    }

    /**
     * 缓存项(value 序列化为 JSON 后存入 Redis Hash)
     */
    @Data
    public static class Item {
        private String customerId;
        private String customerCode;
        private String customerName;
    }
}
