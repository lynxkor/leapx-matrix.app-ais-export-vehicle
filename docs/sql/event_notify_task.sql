-- ============================================================
-- 事件通知任务表（ais-event 框架本地消息表，推红旗可靠投递）
-- 来源：ais-event 框架 EventNotifyTask 实体（@TableName("event_notify_task")）
-- 状态码 execute_result：0=待处理 8=已完成 9=已取消 10=最终失败
-- 建到 export-vehicle 主库（app_ais_exportvehicle_sit 或对应环境库）
-- ============================================================

CREATE TABLE `event_notify_task` (
    `id`                    VARCHAR(64)   NOT NULL                  COMMENT '主键(UUID)',
    `event_notify_id`       VARCHAR(64)   DEFAULT NULL              COMMENT '事件任务ID',
    `biz_no`                VARCHAR(64)   DEFAULT NULL              COMMENT '业务编号(PO单号)',
    `scene`                 VARCHAR(255)  DEFAULT NULL              COMMENT '事件场景描述',
    `type`                  VARCHAR(64)   DEFAULT NULL              COMMENT '事件类型(PUSH_CBU_APPROVAL_RESULT)',
    `data_isolation_index`  VARCHAR(64)   DEFAULT NULL              COMMENT '数据隔离索引',
    `event_at`              DATETIME      DEFAULT NULL              COMMENT '事件发生时间',
    `related_no`            VARCHAR(64)   DEFAULT NULL              COMMENT '业务关联单据',
    `target`                VARCHAR(32)   DEFAULT NULL              COMMENT '目标平台(HONGQI)',
    `failed_count`          BIGINT        DEFAULT 0                 COMMENT '失败次数',
    `failed_reason`         VARCHAR(1000) DEFAULT NULL              COMMENT '失败原因',
    `execute_result`        INT           DEFAULT 0                 COMMENT '执行结果 0待处理 8已完成 9已取消 10最终失败',
    `data_param`            TEXT          DEFAULT NULL              COMMENT '事件数据快照(请求体JSON)',
    `extend1`               VARCHAR(255)  DEFAULT NULL              COMMENT '拓展字段1',
    `extend2`               VARCHAR(255)  DEFAULT NULL              COMMENT '拓展字段2',
    -- LpBaseExtendEntity
    `status`                INT           DEFAULT 1                 COMMENT '启用状态 0禁用 1启用',
    `status_desc`           VARCHAR(255)  DEFAULT NULL              COMMENT '状态变更描述',
    `remark`                VARCHAR(500)  DEFAULT NULL              COMMENT '备注',
    `version`               BIGINT        DEFAULT 0                 COMMENT '版本号(乐观锁 @Version)',
    -- LpBaseEntity
    `creator`               BIGINT        DEFAULT NULL              COMMENT '创建者',
    `creator_name`          VARCHAR(64)   DEFAULT NULL              COMMENT '创建者名称',
    `create_time`           DATETIME      DEFAULT NULL              COMMENT '创建时间',
    `modifier`              BIGINT        DEFAULT NULL              COMMENT '修改者',
    `modifier_name`         VARCHAR(64)   DEFAULT NULL              COMMENT '修改者名称',
    `update_time`           DATETIME      DEFAULT NULL              COMMENT '修改时间',
    `delete_status`         BIGINT        DEFAULT 0                 COMMENT '删除状态 0未删 1已删(逻辑删除)',
    PRIMARY KEY (`id`),
    KEY `idx_biz_type_target` (`biz_no`, `type`, `target`),
    KEY `idx_execute_result` (`execute_result`, `update_time`),
    KEY `idx_create_time` (`create_time`)
) DEFAULT CHARSET=utf8mb4 COMMENT='事件通知任务表';
