package com.leapmotor.export.vehicle.so.executor.cmd;

/**
 * 单条下发结果（SUCCESS/SKIP/FAIL + soNo + reason）。
 *
 * <p>用于 {@link VehicleCbuSoCmdExe#issueToProduction} 并行下发时每条 SO 的处理结果，
 * 供 {@code allOf} 汇总后按状态分组填充 {@code VehicleCbuSoOperateResult}。
 *
 * @author leapmotor
 * @since 2026-07-22
 */
public class IssueItemResult {

    private final IssueStatus status;
    private final String soNo;
    private final String reason;

    private IssueItemResult(IssueStatus status, String soNo, String reason) {
        this.status = status;
        this.soNo = soNo;
        this.reason = reason;
    }

    /**
     * 下发成功。
     */
    public static IssueItemResult success(String soNo) {
        return new IssueItemResult(IssueStatus.SUCCESS, soNo, null);
    }

    /**
     * 跳过（锁内二次校验状态非未下发生产，如并发跳过）。
     */
    public static IssueItemResult skip(String soNo, String reason) {
        return new IssueItemResult(IssueStatus.SKIP, soNo, reason);
    }

    /**
     * 失败（处理过程抛异常，如 APS 远程失败）。
     */
    public static IssueItemResult fail(String soNo, String reason) {
        return new IssueItemResult(IssueStatus.FAIL, soNo, reason);
    }

    public IssueStatus getStatus() {
        return status;
    }

    public String getSoNo() {
        return soNo;
    }

    public String getReason() {
        return reason;
    }
}
