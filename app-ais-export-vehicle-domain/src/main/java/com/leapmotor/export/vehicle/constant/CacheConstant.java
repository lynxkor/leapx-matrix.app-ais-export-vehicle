package com.leapmotor.export.vehicle.constant;

public interface CacheConstant {

    String CUSTOMER_INFO_REDIS_KEY = "AIS:EXPORT:VEHICLE:SYNC_TASK:customer_info";
    /*  分布式锁 key 前缀  */
    String LOCK_KEY_LETTER_GUARANTEE_PREFIX = "AIS:LETTER_GUARANTEE:LOCK";
}
