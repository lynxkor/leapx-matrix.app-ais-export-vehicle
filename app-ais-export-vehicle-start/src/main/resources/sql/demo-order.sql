-- ============================================================
-- app-ais-export-vehicle 整车订单示例表（DemoOrder）
-- 仅作 demo 演示用，业务字段以 LpBaseEntity 约定的公共字段为准
-- ============================================================
DROP TABLE IF EXISTS `demo_order`;
CREATE TABLE `demo_order`
(
    `id`          VARCHAR(64)  NOT NULL COMMENT '主键ID',
    `order_no`    VARCHAR(255)     NULL COMMENT '订单号',
    `order_type`  VARCHAR(64)      NULL COMMENT '订单类型（CBU/SKD/CKD）',
    `remark`      VARCHAR(500)     NULL COMMENT '备注',
    `status`      TINYINT          NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `create_by`   VARCHAR(64)      NULL COMMENT '创建人',
    `create_time` DATETIME         NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)      NULL COMMENT '更新人',
    `update_time` DATETIME         NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='整车订单示例表';
