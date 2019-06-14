CREATE DATABASE IF NOT EXISTS `zohar`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `zohar`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id`            BIGINT(20) UNSIGNED NOT NULL    AUTO_INCREMENT
  COMMENT '自增主键',
  `user_id`       VARCHAR(64)         NOT NULL
  COMMENT '用户ID',
  `username`      VARCHAR(128) CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci          NOT NULL
  COMMENT '用户名',
  `password`      VARCHAR(128) CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci          NOT NULL
  COMMENT '密码',
  `nickname`      VARCHAR(128) CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci          NOT NULL
  COMMENT '昵称',
  `mobile_number` VARCHAR(20) COLLATE utf8mb4_bin DEFAULT NULL
  COMMENT '手机号',
  `avatar`        VARCHAR(255) CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci                      DEFAULT NULL
  COMMENT '头像',
  `email`         VARCHAR(56) COLLATE utf8mb4_bin DEFAULT NULL
  COMMENT '邮箱地址',
  `is_deleted`    TINYINT(4) UNSIGNED NOT NULL    DEFAULT 0
  COMMENT '是否已删除，0未删除；1已删除',
  `create_time`   DATETIME(0)         NOT NULL    DEFAULT current_timestamp()
  COMMENT '创建时间',
  `update_time`   DATETIME(0)         NOT NULL    DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(
      0)
  COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
)
  ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '用户表'
  ROW_FORMAT = DYNAMIC;


SET FOREIGN_KEY_CHECKS = 1;
