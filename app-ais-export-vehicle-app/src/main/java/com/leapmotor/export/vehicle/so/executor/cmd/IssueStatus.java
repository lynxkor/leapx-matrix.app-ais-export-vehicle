package com.leapmotor.export.vehicle.so.executor.cmd;

/**
 * 单条下发结果状态。
 *
 * @author leapmotor
 * @since 2026-07-22
 */
public enum IssueStatus {

    /**
     * 成功
     */
    SUCCESS,

    /**
     * 跳过（并发跳过 / 状态已被他线程改）
     */
    SKIP,

    /**
     * 失败（处理过程抛异常）
     */
    FAIL
}
