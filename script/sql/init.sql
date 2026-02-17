CREATE DATABASE IF NOT EXISTS `simple_admin` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `simple_admin`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `id`           bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
    `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '配置值',
    `config_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '配置名称',
    `remark`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '备注',
    `status`       tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
    `del_flag`     tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_by`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '创建者',
    `create_time`  datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '更新者',
    `update_time`  datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_config_key` (`config_key` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config`
VALUES (1, 'captcha.login.enabled', 'true', '登录验证码开关', '控制登录时是否需要验证码验证', 1, 0, NULL,
        '2025-12-22 10:00:00', NULL, '2025-12-22 10:00:00');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`
(
    `id`          bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '字典名称',
    `dict_code`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '字典编码',
    `sort`        int                                                           NOT NULL DEFAULT 0 COMMENT '排序',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '备注',
    `status`      tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
    `del_flag`    tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_dict_code` (`dict_code` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict`
VALUES (1, '性别', 'sex', 0, '性别字典', 1, 0, '2025-12-11 14:20:18', '2025-12-11 14:20:18');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`
(
    `id`          bigint UNSIGNED                                              NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_id`     bigint UNSIGNED                                              NOT NULL COMMENT '字典ID',
    `item_text`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典项文本',
    `item_value`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典项值',
    `sort`        int                                                          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      tinyint                                                      NOT NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
    `create_time` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_item_dict_id` (`dict_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典项表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item`
VALUES (1, 1, '男', '0', 0, 1, '2025-12-11 14:20:18', '2025-12-11 14:20:18');
INSERT INTO `sys_dict_item`
VALUES (2, 1, '女', '1', 1, 1, '2025-12-11 14:20:18', '2025-12-11 14:20:18');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `job_id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT '任务名称',
    `job_group`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
    `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT '' COMMENT 'cron执行表达式',
    `misfire_policy`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci      NULL     DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci      NULL     DEFAULT '0' COMMENT '状态（0正常 1暂停）',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`     datetime                                                      NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`     datetime                                                      NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT '' COMMENT '备注信息',
    PRIMARY KEY (`job_id`, `job_group`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job`
VALUES (1, '系统默认任务', 'DEFAULT', 'task.noParams', '0/10 * * * * ?', '3', '1', '0', 'admin', '2025-12-11 14:20:17',
        '', NULL, '有参测试任务');
INSERT INTO `sys_job`
VALUES (2, '系统默认任务(有参)', 'DEFAULT', 'task.params(\'test\')', '0/15 * * * * ?', '3', '1', '0', 'admin',
        '2025-12-11 14:20:17', '', NULL, '有参测试任务');
INSERT INTO `sys_job`
VALUES (3, '系统默认任务(多参)', 'DEFAULT', 'task.params(\'test1\', \'test2\')', '0/20 * * * * ?', '3', '1', '0',
        'admin', '2025-12-11 14:20:17', '', NULL, '有参测试任务');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `job_log_id`     bigint                                                         NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '任务名称',
    `job_group`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '调用目标字符串',
    `job_message`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '日志信息',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci       NULL     DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT '' COMMENT '异常信息',
    `create_time`    datetime                                                       NULL     DEFAULT NULL COMMENT '创建时间',
    `stop_time`      datetime                                                       NULL     DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`
(
    `id`         bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`    bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '用户ID',
    `username`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '登录账号',
    `ip`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '登录IP',
    `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '浏览器UA',
    `status`     tinyint                                                       NOT NULL DEFAULT 1 COMMENT '登录状态 1 成功 0 失败',
    `msg`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '登录信息',
    `login_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_log_user_id` (`user_id` ASC) USING BTREE,
    INDEX `idx_log_username` (`username` ASC) USING BTREE,
    INDEX `idx_log_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 126
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录日志表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`          bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`   bigint UNSIGNED                                               NOT NULL DEFAULT 0 COMMENT '父ID，0=顶级',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '菜单名称',
    `path`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '路由路径/外链',
    `component`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '组件路径',
    `redirect`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '重定向地址',
    `icon`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '图标',
    `sort`        int                                                           NOT NULL DEFAULT 0 COMMENT '排序',
    `hidden`      tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否隐藏 0 显示 1 隐藏',
    `external`    tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否外链 0 内部 1 外链',
    `perms`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '权限标识',
    `status`      tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
    `del_flag`    tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_by`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '创建者',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '更新者',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_menu_parent_id` (`parent_id` ASC) USING BTREE,
    INDEX `idx_menu_status_del` (`status` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 76
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu`
VALUES (1, 0, '仪表盘', '/dashboard', 'Dashboard', NULL, 'dashboard', 0, 0, 0, NULL, 1, 0, NULL, '2025-12-11 14:20:17',
        NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (2, 0, '系统管理', '/system', NULL, NULL, 'system', 1, 0, 0, NULL, 1, 0, NULL, '2025-12-11 14:20:17', NULL,
        '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (3, 2, '用户管理', '/system/user', 'system/user/index', NULL, 'user', 0, 0, 0, NULL, 1, 0, NULL,
        '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (4, 2, '角色管理', '/system/role', 'system/role/index', NULL, 'role', 1, 0, 0, NULL, 1, 0, NULL,
        '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (5, 2, '菜单管理', '/system/menu', 'system/menu/index', NULL, 'menu', 2, 0, 0, NULL, 1, 0, NULL,
        '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (6, 2, '字典管理', '/system/dict', 'system/dict/index', NULL, 'dict', 3, 0, 0, NULL, 1, 0, NULL,
        '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (7, 2, '权限管理', '/system/permission', 'system/permission/index', NULL, 'permission', 4, 0, 0, NULL, 1, 0,
        NULL, '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (8, 24, 'API文档', '/system-tools/api-doc', 'system/apiDoc/index', NULL, 'api', 0, 0, 0, NULL, 1, 0, NULL,
        '2025-12-16 15:45:00', NULL, '2025-12-16 15:45:00');
INSERT INTO `sys_menu`
VALUES (9, 0, '监控管理', '/monitor', NULL, NULL, 'monitoring', 2, 0, 0, NULL, 1, 0, NULL, '2025-12-22 10:00:00', NULL,
        '2025-12-22 10:00:00');
INSERT INTO `sys_menu`
VALUES (10, 9, '系统监控', '/monitor/system', 'system/monitor/index', NULL, 'monitor', 0, 0, 0, NULL, 1, 0, NULL,
        '2025-12-17 14:00:00', NULL, '2025-12-17 14:00:00');
INSERT INTO `sys_menu`
VALUES (11, 2, '日志管理', '/system/log', NULL, NULL, 'log', 6, 0, 0, NULL, 1, 0, NULL, '2025-12-22 10:00:00', NULL,
        '2025-12-22 10:00:00');
INSERT INTO `sys_menu`
VALUES (12, 11, '登录日志', '/system/log/login', 'system/loginLog/index', NULL, 'login-log', 0, 0, 0, NULL, 1, 0, NULL,
        '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_menu`
VALUES (13, 11, '操作日志', '/system/log/oper', 'system/operLog/index', NULL, 'oper-log', 1, 0, 0, NULL, 1, 0, NULL,
        '2025-12-18 10:00:00', NULL, '2025-12-18 10:00:00');
INSERT INTO `sys_menu`
VALUES (14, 9, '在线用户', '/system/log/online', 'system/online/index', NULL, 'online', 2, 0, 0, NULL, 1, 0, NULL,
        '2025-12-22 10:00:00', NULL, '2025-12-22 13:56:46');
INSERT INTO `sys_menu`
VALUES (15, 0, '通知公告', '/notice', NULL, NULL, 'notification', 4, 0, 0, NULL, 1, 0, NULL, '2025-12-31 00:00:00',
        NULL, '2025-12-31 00:00:00');
INSERT INTO `sys_menu`
VALUES (16, 15, '公告管理', '/notice/list', 'system/notice/index', NULL, 'file-text', 0, 0, 0, NULL, 1, 0, NULL,
        '2025-12-31 00:00:00', NULL, '2025-12-31 00:00:00');
INSERT INTO `sys_menu`
VALUES (17, 15, '我的通知', '/notice/my', 'system/notice/my', NULL, 'bell', 1, 0, 0, NULL, 1, 0, NULL,
        '2025-12-31 00:00:00', NULL, '2025-12-31 00:00:00');
INSERT INTO `sys_menu`
VALUES (18, 2, '定时任务', '/system/job', NULL, NULL, 'clock-circle', 7, 0, 0, NULL, 1, 0, NULL, '2025-01-05 00:00:00',
        NULL, '2025-01-05 00:00:00');
INSERT INTO `sys_menu`
VALUES (19, 18, '任务管理', '/system/job/list', 'system/job/index', NULL, 'setting', 0, 0, 0, NULL, 1, 0, NULL,
        '2025-01-05 00:00:00', NULL, '2025-01-05 00:00:00');
INSERT INTO `sys_menu`
VALUES (20, 18, '任务日志', '/system/job-log/list', 'system/jobLog/index', NULL, 'file-text', 1, 0, 0, NULL, 1, 0, NULL,
        '2025-01-05 00:00:00', NULL, '2025-01-05 00:00:00');
INSERT INTO `sys_menu`
VALUES (21, 9, '缓存监控', '/monitor/cache', 'system/cacheMonitor/index', NULL, 'database', 3, 0, 0, NULL, 1, 0, NULL,
        '2026-01-08 00:00:00', NULL, '2026-01-08 00:00:00');
INSERT INTO `sys_menu`
VALUES (22, 9, '缓存列表', '/monitor/cache/list', 'system/cacheList/index', NULL, 'unordered-list', 4, 0, 0, NULL, 1, 0,
        NULL, '2026-01-08 00:00:00', NULL, '2026-01-08 00:00:00');
INSERT INTO `sys_menu`
VALUES (23, 2, '文件管理', '/system/file', 'system/File/index', NULL, 'file', 8, 0, 0, NULL, 1, 0, NULL,
        '2026-01-08 00:00:00', NULL, '2026-01-08 00:00:00');
INSERT INTO `sys_menu`
VALUES (24, 0, '系统工具', '/system-tools', NULL, NULL, 'tool', 3, 0, 0, NULL, 1, 0, NULL, '2026-01-14 00:00:00',
        NULL, '2026-01-14 00:00:00');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `id`             bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '公告内容',
    `type`           tinyint                                                       NOT NULL DEFAULT 1 COMMENT '公告类型 1=系统公告 2=活动通知',
    `target_type`    tinyint                                                       NOT NULL DEFAULT 1 COMMENT '发布范围 1=全部用户 2=指定角色',
    `target_roles`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '指定角色ID列表(JSON数组)',
    `priority`       tinyint                                                       NOT NULL DEFAULT 1 COMMENT '优先级 1=普通 2=重要 3=紧急',
    `start_time`     datetime                                                      NULL     DEFAULT NULL COMMENT '生效开始时间',
    `end_time`       datetime                                                      NULL     DEFAULT NULL COMMENT '生效结束时间',
    `status`         tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态 1=草稿 2=已发布 3=已撤回',
    `publish_time`   datetime                                                      NULL     DEFAULT NULL COMMENT '发布时间',
    `publisher_id`   bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '发布者ID',
    `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '发布者姓名',
    `read_count`     int UNSIGNED                                                  NOT NULL DEFAULT 0 COMMENT '已读人数',
    `total_count`    int UNSIGNED                                                  NOT NULL DEFAULT 0 COMMENT '目标总人数',
    `del_flag`       tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_by`      varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '创建者',
    `create_time`    datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '更新者',
    `update_time`    datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_notice_status` (`status` ASC) USING BTREE,
    INDEX `idx_notice_type` (`type` ASC) USING BTREE,
    INDEX `idx_notice_publish_time` (`publish_time` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_read
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read`
(
    `id`        bigint UNSIGNED                                              NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `notice_id` bigint UNSIGNED                                              NOT NULL COMMENT '通知ID',
    `user_id`   bigint UNSIGNED                                              NOT NULL COMMENT '用户ID',
    `username`  varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
    `nickname`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户昵称',
    `read_time` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_notice_user` (`notice_id` ASC, `user_id` ASC) USING BTREE,
    INDEX `idx_read_notice_id` (`notice_id` ASC) USING BTREE,
    INDEX `idx_read_user_id` (`user_id` ASC) USING BTREE,
    INDEX `idx_read_time` (`read_time` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知已读记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_read
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`
(
    `id`             bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '模块标题',
    `business_type`  int                                                           NULL     DEFAULT 0 COMMENT '业务类型 0=其它 1=新增 2=修改 3=删除 4=授权 5=导出 6=导入 7=清空',
    `method`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '方法名称',
    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '请求方式',
    `operator_type`  int                                                           NULL     DEFAULT 0 COMMENT '操作类别 0=其它 1=后台用户 2=手机端用户',
    `oper_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '操作人员',
    `oper_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '请求URL',
    `oper_ip`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '主机地址',
    `oper_param`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '请求参数',
    `json_result`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '返回参数',
    `status`         int                                                           NULL     DEFAULT 0 COMMENT '操作状态 0=正常 1=异常',
    `error_msg`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '错误消息',
    `oper_time`      datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `cost_time`      bigint                                                        NULL     DEFAULT 0 COMMENT '消耗时间(毫秒)',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_oper_log_title` (`title` ASC) USING BTREE,
    INDEX `idx_oper_log_business_type` (`business_type` ASC) USING BTREE,
    INDEX `idx_oper_log_status` (`status` ASC) USING BTREE,
    INDEX `idx_oper_log_oper_time` (`oper_time` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`
(
    `id`      bigint UNSIGNED                                              NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `perm`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识',
    `name`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
    `menu_id` bigint UNSIGNED                                              NULL DEFAULT NULL COMMENT '关联菜单ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_perm` (`perm` ASC) USING BTREE,
    INDEX `idx_perm_menu_id` (`menu_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 76
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission`
VALUES (1, 'user:list', '查询用户', 3);
INSERT INTO `sys_permission`
VALUES (2, 'user:add', '新增用户', 3);
INSERT INTO `sys_permission`
VALUES (3, 'user:edit', '编辑用户', 3);
INSERT INTO `sys_permission`
VALUES (4, 'user:delete', '删除用户', 3);
INSERT INTO `sys_permission`
VALUES (5, 'user:import', '导入用户', 3);
INSERT INTO `sys_permission`
VALUES (6, 'user:export', '导出用户', 3);
INSERT INTO `sys_permission`
VALUES (7, 'role:list', '查询角色', 4);
INSERT INTO `sys_permission`
VALUES (8, 'role:add', '新增角色', 4);
INSERT INTO `sys_permission`
VALUES (9, 'role:edit', '编辑角色', 4);
INSERT INTO `sys_permission`
VALUES (10, 'role:delete', '删除角色', 4);
INSERT INTO `sys_permission`
VALUES (11, 'role:import', '导入角色', 4);
INSERT INTO `sys_permission`
VALUES (12, 'role:export', '导出角色', 4);
INSERT INTO `sys_permission`
VALUES (13, 'menu:list', '查询菜单', 5);
INSERT INTO `sys_permission`
VALUES (14, 'menu:add', '新增菜单', 5);
INSERT INTO `sys_permission`
VALUES (15, 'menu:edit', '编辑菜单', 5);
INSERT INTO `sys_permission`
VALUES (16, 'menu:delete', '删除菜单', 5);
INSERT INTO `sys_permission`
VALUES (17, 'menu:import', '导入菜单', 5);
INSERT INTO `sys_permission`
VALUES (18, 'menu:export', '导出菜单', 5);
INSERT INTO `sys_permission`
VALUES (19, 'dict:list', '查询字典', 6);
INSERT INTO `sys_permission`
VALUES (20, 'dict:add', '新增字典', 6);
INSERT INTO `sys_permission`
VALUES (21, 'dict:edit', '编辑字典', 6);
INSERT INTO `sys_permission`
VALUES (22, 'dict:delete', '删除字典', 6);
INSERT INTO `sys_permission`
VALUES (23, 'dict:import', '导入字典', 6);
INSERT INTO `sys_permission`
VALUES (24, 'dict:export', '导出字典', 6);
INSERT INTO `sys_permission`
VALUES (25, 'system:cache:clear', '清除缓存', 6);
INSERT INTO `sys_permission`
VALUES (26, 'system:cache:warm-up', '预热缓存', 6);
INSERT INTO `sys_permission`
VALUES (27, 'system:cache:refresh', '刷新缓存', 6);
INSERT INTO `sys_permission`
VALUES (28, 'system:monitor:test', 'MQ测试', 6);
INSERT INTO `sys_permission`
VALUES (29, 'log:list', '查询日志', 7);
INSERT INTO `sys_permission`
VALUES (30, 'log:delete', '删除日志', 7);
INSERT INTO `sys_permission`
VALUES (31, 'log:export', '导出日志', 7);
INSERT INTO `sys_permission`
VALUES (33, 'permission:list', '查询权限', 8);
INSERT INTO `sys_permission`
VALUES (34, 'permission:add', '新增权限', 8);
INSERT INTO `sys_permission`
VALUES (35, 'permission:edit', '编辑权限', 8);
INSERT INTO `sys_permission`
VALUES (36, 'permission:delete', '删除权限', 8);
INSERT INTO `sys_permission`
VALUES (37, 'permission:import', '导入权限', 8);
INSERT INTO `sys_permission`
VALUES (38, 'permission:export', '导出权限', 8);
INSERT INTO `sys_permission`
VALUES (39, 'system:status', '查看服务器状态', 1);
INSERT INTO `sys_permission`
VALUES (40, 'api-doc:view', '查看API文档', 8);
INSERT INTO `sys_permission`
VALUES (41, 'monitor:view', '查看系统监控', 10);
INSERT INTO `sys_permission`
VALUES (42, 'monitor:health', '查看健康状态', 10);
INSERT INTO `sys_permission`
VALUES (43, 'monitor:metrics', '查看系统指标', 10);
INSERT INTO `sys_permission`
VALUES (44, 'monitor:jvm', '查看JVM信息', 10);
INSERT INTO `sys_permission`
VALUES (45, 'system:config', '系统配置管理', 2);
INSERT INTO `sys_permission`
VALUES (46, 'online:list', '查询在线用户', 14);
INSERT INTO `sys_permission`
VALUES (47, 'online:kickout', '强踢用户', 14);
INSERT INTO `sys_permission`
VALUES (48, 'online:batch:kickout', '批量强踢', 14);
INSERT INTO `sys_permission`
VALUES (49, 'monitor:system', '查看系统信息', 10);
INSERT INTO `sys_permission`
VALUES (50, 'operlog:list', '查询操作日志', 13);
INSERT INTO `sys_permission`
VALUES (51, 'operlog:delete', '删除操作日志', 13);
INSERT INTO `sys_permission`
VALUES (52, 'operlog:export', '导出操作日志', 13);
INSERT INTO `sys_permission`
VALUES (53, 'notice:list', '查询公告', 16);
INSERT INTO `sys_permission`
VALUES (54, 'notice:add', '新增公告', 16);
INSERT INTO `sys_permission`
VALUES (55, 'notice:edit', '编辑公告', 16);
INSERT INTO `sys_permission`
VALUES (56, 'notice:delete', '删除公告', 16);
INSERT INTO `sys_permission`
VALUES (57, 'notice:publish', '发布公告', 16);
INSERT INTO `sys_permission`
VALUES (58, 'notice:revoke', '撤回公告', 16);
INSERT INTO `sys_permission`
VALUES (59, 'notice:my:list', '查询我的通知', 17);
INSERT INTO `sys_permission`
VALUES (60, 'notice:my:read', '标记已读', 17);
INSERT INTO `sys_permission`
VALUES (61, 'notice:my:batch-read', '批量标记已读', 17);
INSERT INTO `sys_permission`
VALUES (62, 'job:list', '查询定时任务', 19);
INSERT INTO `sys_permission`
VALUES (63, 'job:add', '新增定时任务', 19);
INSERT INTO `sys_permission`
VALUES (64, 'job:edit', '编辑定时任务', 19);
INSERT INTO `sys_permission`
VALUES (65, 'job:delete', '删除定时任务', 19);
INSERT INTO `sys_permission`
VALUES (66, 'job:changeStatus', '修改定时任务状态', 19);
INSERT INTO `sys_permission`
VALUES (67, 'job:run', '执行定时任务', 19);
INSERT INTO `sys_permission`
VALUES (68, 'joblog:list', '查询任务日志', 20);
INSERT INTO `sys_permission`
VALUES (69, 'joblog:delete', '删除任务日志', 20);
INSERT INTO `sys_permission`
VALUES (70, 'joblog:export', '导出任务日志', 20);
INSERT INTO `sys_permission`
VALUES (71, 'joblog:clean', '清空任务日志', 20);
INSERT INTO `sys_permission`
VALUES (72, 'cache:monitor:view', '查看缓存监控', 21);
INSERT INTO `sys_permission`
VALUES (73, 'cache:list:view', '查看缓存列表', 22);
INSERT INTO `sys_permission`
VALUES (74, 'cache:key:view', '查看缓存键值', 22);
INSERT INTO `sys_permission`
VALUES (75, 'cache:key:delete', '删除缓存键', 22);
INSERT INTO `sys_permission`
VALUES (76, 'cache:key:setTtl', '设置过期时间', 22);
INSERT INTO `sys_permission`
VALUES (77, 'cache:clear', '清空缓存', 22);
INSERT INTO `sys_permission`
VALUES (78, 'cache:list:clear', '清空数据库', 22);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint UNSIGNED                                              NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
    `code`        varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
    `sort`        int                                                          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      tinyint                                                      NOT NULL DEFAULT 1 COMMENT '状态 1 启用 0 禁用',
    `del_flag`    tinyint                                                      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_by`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '创建者',
    `create_time` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '更新者',
    `update_time` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_role_code` (`code` ASC) USING BTREE,
    INDEX `idx_role_status_del` (`status` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, '超级管理员', 'super_admin', 0, 1, 0, NULL, '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_role`
VALUES (2, '普通管理员', 'admin', 1, 1, 0, NULL, '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');
INSERT INTO `sys_role`
VALUES (3, '用户', 'user', 2, 1, 0, NULL, '2025-12-11 14:20:17', NULL, '2025-12-11 14:20:17');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
    `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`) USING BTREE,
    INDEX `idx_rm_menu_id` (`menu_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu`
VALUES (1, 1);
INSERT INTO `sys_role_menu`
VALUES (2, 1);
INSERT INTO `sys_role_menu`
VALUES (3, 1);
INSERT INTO `sys_role_menu`
VALUES (1, 2);
INSERT INTO `sys_role_menu`
VALUES (1, 3);
INSERT INTO `sys_role_menu`
VALUES (2, 3);
INSERT INTO `sys_role_menu`
VALUES (1, 4);
INSERT INTO `sys_role_menu`
VALUES (2, 4);
INSERT INTO `sys_role_menu`
VALUES (1, 5);
INSERT INTO `sys_role_menu`
VALUES (2, 5);
INSERT INTO `sys_role_menu`
VALUES (1, 6);
INSERT INTO `sys_role_menu`
VALUES (2, 6);
INSERT INTO `sys_role_menu`
VALUES (1, 7);
INSERT INTO `sys_role_menu`
VALUES (1, 8);
INSERT INTO `sys_role_menu`
VALUES (1, 9);
INSERT INTO `sys_role_menu`
VALUES (2, 9);
INSERT INTO `sys_role_menu`
VALUES (1, 10);
INSERT INTO `sys_role_menu`
VALUES (2, 10);
INSERT INTO `sys_role_menu`
VALUES (1, 11);
INSERT INTO `sys_role_menu`
VALUES (2, 11);
INSERT INTO `sys_role_menu`
VALUES (1, 12);
INSERT INTO `sys_role_menu`
VALUES (2, 12);
INSERT INTO `sys_role_menu`
VALUES (1, 13);
INSERT INTO `sys_role_menu`
VALUES (2, 13);
INSERT INTO `sys_role_menu`
VALUES (1, 14);
INSERT INTO `sys_role_menu`
VALUES (2, 14);
INSERT INTO `sys_role_menu`
VALUES (1, 15);
INSERT INTO `sys_role_menu`
VALUES (2, 15);
INSERT INTO `sys_role_menu`
VALUES (3, 15);
INSERT INTO `sys_role_menu`
VALUES (1, 16);
INSERT INTO `sys_role_menu`
VALUES (2, 16);
INSERT INTO `sys_role_menu`
VALUES (1, 17);
INSERT INTO `sys_role_menu`
VALUES (2, 17);
INSERT INTO `sys_role_menu`
VALUES (3, 17);
INSERT INTO `sys_role_menu`
VALUES (1, 18);
INSERT INTO `sys_role_menu`
VALUES (2, 18);
INSERT INTO `sys_role_menu`
VALUES (1, 19);
INSERT INTO `sys_role_menu`
VALUES (2, 19);
INSERT INTO `sys_role_menu`
VALUES (1, 20);
INSERT INTO `sys_role_menu`
VALUES (2, 20);
INSERT INTO `sys_role_menu`
VALUES (1, 21);
INSERT INTO `sys_role_menu`
VALUES (2, 21);
INSERT INTO `sys_role_menu`
VALUES (1, 22);
INSERT INTO `sys_role_menu`
VALUES (2, 22);
INSERT INTO `sys_role_menu`
VALUES (1, 23);
INSERT INTO `sys_role_menu`
VALUES (2, 23);
INSERT INTO `sys_role_menu`
VALUES (1, 24);
INSERT INTO `sys_role_menu`
VALUES (2, 24);

-- ----------------------------
-- Table structure for sys_role_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_perm`;
CREATE TABLE `sys_role_perm`
(
    `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
    `perm_id` bigint UNSIGNED NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`role_id`, `perm_id`) USING BTREE,
    INDEX `idx_rp_perm_id` (`perm_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_perm
-- ----------------------------
INSERT INTO `sys_role_perm`
VALUES (1, 1);
INSERT INTO `sys_role_perm`
VALUES (2, 1);
INSERT INTO `sys_role_perm`
VALUES (3, 1);
INSERT INTO `sys_role_perm`
VALUES (1, 2);
INSERT INTO `sys_role_perm`
VALUES (2, 2);
INSERT INTO `sys_role_perm`
VALUES (1, 3);
INSERT INTO `sys_role_perm`
VALUES (2, 3);
INSERT INTO `sys_role_perm`
VALUES (1, 4);
INSERT INTO `sys_role_perm`
VALUES (2, 4);
INSERT INTO `sys_role_perm`
VALUES (1, 5);
INSERT INTO `sys_role_perm`
VALUES (2, 5);
INSERT INTO `sys_role_perm`
VALUES (1, 6);
INSERT INTO `sys_role_perm`
VALUES (2, 6);
INSERT INTO `sys_role_perm`
VALUES (1, 7);
INSERT INTO `sys_role_perm`
VALUES (2, 7);
INSERT INTO `sys_role_perm`
VALUES (3, 7);
INSERT INTO `sys_role_perm`
VALUES (1, 8);
INSERT INTO `sys_role_perm`
VALUES (2, 8);
INSERT INTO `sys_role_perm`
VALUES (1, 9);
INSERT INTO `sys_role_perm`
VALUES (2, 9);
INSERT INTO `sys_role_perm`
VALUES (1, 10);
INSERT INTO `sys_role_perm`
VALUES (2, 10);
INSERT INTO `sys_role_perm`
VALUES (1, 11);
INSERT INTO `sys_role_perm`
VALUES (2, 11);
INSERT INTO `sys_role_perm`
VALUES (1, 12);
INSERT INTO `sys_role_perm`
VALUES (2, 12);
INSERT INTO `sys_role_perm`
VALUES (1, 13);
INSERT INTO `sys_role_perm`
VALUES (2, 13);
INSERT INTO `sys_role_perm`
VALUES (3, 13);
INSERT INTO `sys_role_perm`
VALUES (1, 14);
INSERT INTO `sys_role_perm`
VALUES (2, 14);
INSERT INTO `sys_role_perm`
VALUES (1, 15);
INSERT INTO `sys_role_perm`
VALUES (2, 15);
INSERT INTO `sys_role_perm`
VALUES (1, 16);
INSERT INTO `sys_role_perm`
VALUES (2, 16);
INSERT INTO `sys_role_perm`
VALUES (1, 17);
INSERT INTO `sys_role_perm`
VALUES (2, 17);
INSERT INTO `sys_role_perm`
VALUES (1, 18);
INSERT INTO `sys_role_perm`
VALUES (2, 18);
INSERT INTO `sys_role_perm`
VALUES (1, 19);
INSERT INTO `sys_role_perm`
VALUES (2, 19);
INSERT INTO `sys_role_perm`
VALUES (3, 19);
INSERT INTO `sys_role_perm`
VALUES (1, 20);
INSERT INTO `sys_role_perm`
VALUES (2, 20);
INSERT INTO `sys_role_perm`
VALUES (1, 21);
INSERT INTO `sys_role_perm`
VALUES (2, 21);
INSERT INTO `sys_role_perm`
VALUES (1, 22);
INSERT INTO `sys_role_perm`
VALUES (2, 22);
INSERT INTO `sys_role_perm`
VALUES (1, 23);
INSERT INTO `sys_role_perm`
VALUES (2, 23);
INSERT INTO `sys_role_perm`
VALUES (1, 24);
INSERT INTO `sys_role_perm`
VALUES (2, 24);
INSERT INTO `sys_role_perm`
VALUES (1, 25);
INSERT INTO `sys_role_perm`
VALUES (1, 26);
INSERT INTO `sys_role_perm`
VALUES (1, 27);
INSERT INTO `sys_role_perm`
VALUES (1, 28);
INSERT INTO `sys_role_perm`
VALUES (1, 29);
INSERT INTO `sys_role_perm`
VALUES (2, 29);
INSERT INTO `sys_role_perm`
VALUES (3, 29);
INSERT INTO `sys_role_perm`
VALUES (1, 30);
INSERT INTO `sys_role_perm`
VALUES (2, 30);
INSERT INTO `sys_role_perm`
VALUES (1, 31);
INSERT INTO `sys_role_perm`
VALUES (2, 31);
INSERT INTO `sys_role_perm`
VALUES (3, 31);
INSERT INTO `sys_role_perm`
VALUES (1, 32);
INSERT INTO `sys_role_perm`
VALUES (1, 33);
INSERT INTO `sys_role_perm`
VALUES (1, 34);
INSERT INTO `sys_role_perm`
VALUES (1, 35);
INSERT INTO `sys_role_perm`
VALUES (2, 35);
INSERT INTO `sys_role_perm`
VALUES (1, 36);
INSERT INTO `sys_role_perm`
VALUES (2, 36);
INSERT INTO `sys_role_perm`
VALUES (1, 37);
INSERT INTO `sys_role_perm`
VALUES (2, 37);
INSERT INTO `sys_role_perm`
VALUES (3, 37);
INSERT INTO `sys_role_perm`
VALUES (1, 38);
INSERT INTO `sys_role_perm`
VALUES (2, 38);
INSERT INTO `sys_role_perm`
VALUES (1, 39);
INSERT INTO `sys_role_perm`
VALUES (2, 39);
INSERT INTO `sys_role_perm`
VALUES (1, 40);
INSERT INTO `sys_role_perm`
VALUES (2, 40);
INSERT INTO `sys_role_perm`
VALUES (1, 41);
INSERT INTO `sys_role_perm`
VALUES (2, 41);
INSERT INTO `sys_role_perm`
VALUES (1, 42);
INSERT INTO `sys_role_perm`
VALUES (2, 42);
INSERT INTO `sys_role_perm`
VALUES (1, 43);
INSERT INTO `sys_role_perm`
VALUES (2, 43);
INSERT INTO `sys_role_perm`
VALUES (1, 44);
INSERT INTO `sys_role_perm`
VALUES (2, 44);
INSERT INTO `sys_role_perm`
VALUES (1, 45);
INSERT INTO `sys_role_perm`
VALUES (2, 45);
INSERT INTO `sys_role_perm`
VALUES (1, 46);
INSERT INTO `sys_role_perm`
VALUES (2, 46);
INSERT INTO `sys_role_perm`
VALUES (1, 47);
INSERT INTO `sys_role_perm`
VALUES (2, 47);
INSERT INTO `sys_role_perm`
VALUES (1, 48);
INSERT INTO `sys_role_perm`
VALUES (2, 48);
INSERT INTO `sys_role_perm`
VALUES (1, 49);
INSERT INTO `sys_role_perm`
VALUES (2, 49);
INSERT INTO `sys_role_perm`
VALUES (1, 50);
INSERT INTO `sys_role_perm`
VALUES (2, 50);
INSERT INTO `sys_role_perm`
VALUES (1, 51);
INSERT INTO `sys_role_perm`
VALUES (2, 51);
INSERT INTO `sys_role_perm`
VALUES (1, 52);
INSERT INTO `sys_role_perm`
VALUES (2, 52);
INSERT INTO `sys_role_perm`
VALUES (1, 53);
INSERT INTO `sys_role_perm`
VALUES (2, 53);
INSERT INTO `sys_role_perm`
VALUES (1, 54);
INSERT INTO `sys_role_perm`
VALUES (2, 54);
INSERT INTO `sys_role_perm`
VALUES (1, 55);
INSERT INTO `sys_role_perm`
VALUES (2, 55);
INSERT INTO `sys_role_perm`
VALUES (1, 56);
INSERT INTO `sys_role_perm`
VALUES (2, 56);
INSERT INTO `sys_role_perm`
VALUES (1, 57);
INSERT INTO `sys_role_perm`
VALUES (2, 57);
INSERT INTO `sys_role_perm`
VALUES (3, 57);
INSERT INTO `sys_role_perm`
VALUES (1, 58);
INSERT INTO `sys_role_perm`
VALUES (2, 58);
INSERT INTO `sys_role_perm`
VALUES (3, 58);
INSERT INTO `sys_role_perm`
VALUES (1, 59);
INSERT INTO `sys_role_perm`
VALUES (2, 59);
INSERT INTO `sys_role_perm`
VALUES (3, 59);
INSERT INTO `sys_role_perm`
VALUES (1, 60);
INSERT INTO `sys_role_perm`
VALUES (2, 60);
INSERT INTO `sys_role_perm`
VALUES (1, 61);
INSERT INTO `sys_role_perm`
VALUES (2, 61);
INSERT INTO `sys_role_perm`
VALUES (1, 62);
INSERT INTO `sys_role_perm`
VALUES (2, 62);
INSERT INTO `sys_role_perm`
VALUES (1, 63);
INSERT INTO `sys_role_perm`
VALUES (2, 63);
INSERT INTO `sys_role_perm`
VALUES (1, 64);
INSERT INTO `sys_role_perm`
VALUES (2, 64);
INSERT INTO `sys_role_perm`
VALUES (1, 65);
INSERT INTO `sys_role_perm`
VALUES (2, 65);
INSERT INTO `sys_role_perm`
VALUES (1, 66);
INSERT INTO `sys_role_perm`
VALUES (2, 66);
INSERT INTO `sys_role_perm`
VALUES (1, 67);
INSERT INTO `sys_role_perm`
VALUES (2, 67);
INSERT INTO `sys_role_perm`
VALUES (1, 68);
INSERT INTO `sys_role_perm`
VALUES (2, 68);
INSERT INTO `sys_role_perm`
VALUES (1, 69);
INSERT INTO `sys_role_perm`
VALUES (2, 69);
INSERT INTO `sys_role_perm`
VALUES (1, 70);
INSERT INTO `sys_role_perm`
VALUES (2, 70);
INSERT INTO `sys_role_perm`
VALUES (1, 71);
INSERT INTO `sys_role_perm`
VALUES (2, 71);
INSERT INTO `sys_role_perm`
VALUES (1, 72);
INSERT INTO `sys_role_perm`
VALUES (2, 72);
INSERT INTO `sys_role_perm`
VALUES (1, 73);
INSERT INTO `sys_role_perm`
VALUES (2, 73);
INSERT INTO `sys_role_perm`
VALUES (1, 74);
INSERT INTO `sys_role_perm`
VALUES (2, 74);
INSERT INTO `sys_role_perm`
VALUES (1, 75);
INSERT INTO `sys_role_perm`
VALUES (2, 75);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '登录账号',
    `password`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt 密文',
    `nickname`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '显示名',
    `avatar`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '头像URL',
    `email`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '邮箱',
    `phone`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '手机号',
    `gender`      tinyint                                                       NULL     DEFAULT NULL COMMENT '性别 0=男 1=女',
    `status`      tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态 1 正常 0 禁用',
    `del_flag`    tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_by`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '创建者',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '更新者',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_username` (`username` ASC) USING BTREE,
    UNIQUE INDEX `uk_user_email` (`email` ASC) USING BTREE,
    INDEX `idx_user_status_del` (`status` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, 'admin', '$2a$10$mBAVjhDP../Gj73C1CDJDuby6jThtE5kjup2XPEatE5OipNnneKXi', '超级管理员', NULL,
        'admin@example.com', '18898785687', 0, 1, 0, NULL, '2025-12-11 14:20:17', NULL, '2025-12-17 09:02:54');
INSERT INTO `sys_user`
VALUES (2, 'user1', '$2a$10$sIx3oKbZuySFsK5/Hf9kHOEBY7qKvuxszMkshGquVCzzAXq.fV6G6', '普通用户1', NULL,
        'user@example.email', '18898785687', 1, 1, 0, NULL, '2025-12-17 09:28:50', NULL, '2025-12-17 09:29:48');
INSERT INTO `sys_user`
VALUES (3, 'admin1', '$2a$10$tFPUoRM937v9XCEKOzpchueW08qXMMxMhg51SpgKlATIHru7IGyUC', '普通管理员1', NULL,
        'admin1@example.email', '18898785687', 0, 1, 0, NULL, '2025-12-17 09:32:33', NULL, '2025-12-17 12:47:35');

-- ----------------------------
-- Table structure for sys_user_online
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_online`;
CREATE TABLE `sys_user_online`
(
    `id`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '会话编号',
    `user_id`     bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '用户ID',
    `username`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '登录账号',
    `nickname`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '显示名称',
    `ip`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '登录IP',
    `location`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '登录地点',
    `browser`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '浏览器类型',
    `os`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '操作系统',
    `status`      varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT 'online' COMMENT '在线状态 on_line在线 off_line离线',
    `start_time`  datetime                                                      NOT NULL COMMENT '会话创建时间',
    `last_time`   datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后访问时间',
    `expire_time` datetime                                                      NOT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_online_user_id` (`user_id` ASC) USING BTREE,
    INDEX `idx_online_username` (`username` ASC) USING BTREE,
    INDEX `idx_online_status` (`status` ASC) USING BTREE,
    INDEX `idx_online_last_time` (`last_time` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '在线用户表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_online
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
    INDEX `idx_ur_role_id` (`role_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role`
VALUES (1, 1);
INSERT INTO `sys_user_role`
VALUES (3, 2);
INSERT INTO `sys_user_role`
VALUES (2, 3);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`
(
    `id`               bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名（存储在对象存储中的名称）',
    `original_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
    `file_suffix`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '文件后缀',
    `file_path`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件路径',
    `file_url`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '文件访问URL',
    `file_size`        bigint UNSIGNED                                               NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
    `file_type`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '文件类型（MIME类型）',
    `file_category`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT 'other' COMMENT '文件类别（image图片、video视频、audio音频、document文档、other其他）',
    `storage_provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT 'minio' COMMENT '存储服务商（minio、oss、cos等）',
    `bucket_name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '存储桶名称',
    `upload_user_id`   bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '上传人ID',
    `upload_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '上传人姓名',
    `del_flag`         tinyint                                                       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_time`      datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_file_upload_user` (`upload_user_id` ASC) USING BTREE,
    INDEX `idx_file_provider` (`storage_provider` ASC) USING BTREE,
    INDEX `idx_file_category` (`file_category` ASC) USING BTREE,
    INDEX `idx_file_create_time` (`create_time` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件管理表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`
(
    `table_id`          bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '表ID',
    `table_name`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '表名称',
    `table_comment`     varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '表描述',
    `class_name`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '实体类名称',
    `tpl_category`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT 'crud' COMMENT '使用的模板（crud单表 tree树表）',
    `package_name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '生成包路径',
        `function_author`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '生成功能作者',
    `gen_type`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
    `gen_path`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
    `form_layout`       char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT '1' COMMENT '表单布局（1单列 2双列 3三列）',
    `options`           varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '其它生成选项',
    `parent_menu_id`    bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '父级菜单ID（0=根菜单）',
    `create_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '创建者',
    `create_time`       datetime                                                      NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '更新者',
    `update_time`       datetime                                                      NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`            varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成业务表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`
(
    `column_id`         bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '列ID',
    `table_id`          bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '归属表ID',
    `column_name`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '列名称',
    `column_comment`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '列描述',
    `column_type`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '列类型',
    `java_type`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT 'Java类型',
    `java_field`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT 'Java字段名',
    `is_pk`             char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否主键（1是）',
    `is_increment`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否自增（1是）',
    `is_required`       char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否必填（1是）',
    `is_insert`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否为插入字段（1是）',
    `is_edit`           char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否编辑字段（1是）',
    `is_list`           char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否列表字段（1是）',
    `is_query`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NULL     DEFAULT NULL COMMENT '是否查询字段（1是）',
    `query_type`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
    `html_type`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT 'input' COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    `dict_type`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '字典类型',
    `sort`              int                                                           NULL     DEFAULT NULL COMMENT '排序',
    `create_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '创建者',
    `create_time`       datetime                                                      NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '更新者',
    `update_time`       datetime                                                      NULL     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`column_id`) USING BTREE,
    INDEX `idx_table_id` (`table_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成业务表字段'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 插入代码生成器菜单
-- ----------------------------
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `redirect`, `icon`, `sort`, `hidden`, `external`, `perms`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (24, '代码生成', '/system-tools/code-gen', 'system/codeGen/index', NULL, 'code', 2, 0, 0, NULL, 1, 0, NULL, '2026-01-15 00:00:00', NULL, '2026-01-15 00:00:00');

-- 获取刚插入的菜单ID
SET @menu_id = LAST_INSERT_ID();

-- ----------------------------
-- 插入代码生成器权限
-- ----------------------------
INSERT INTO `sys_permission` (`perm`, `name`, `menu_id`)
VALUES 
    ('gen:list', '查询代码生成', @menu_id),
    ('gen:import', '导入表结构', @menu_id),
    ('gen:edit', '修改代码生成配置', @menu_id),
    ('gen:delete', '删除代码生成', @menu_id),
    ('gen:preview', '预览生成代码', @menu_id),
    ('gen:code', '生成代码', @menu_id);

-- 获取权限ID范围（假设从76开始）
SET @perm_start = 76;

-- ----------------------------
-- 插入角色菜单关联（将代码生成器菜单分配给超级管理员和普通管理员）
-- ----------------------------
-- 超级管理员(role_id=1)和普通管理员(role_id=2)可以访问代码生成器菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES 
    (1, @menu_id),
    (2, @menu_id);

-- ----------------------------
-- 插入角色权限关联（将代码生成器权限分配给超级管理员和普通管理员）
-- ----------------------------
-- 超级管理员(role_id=1)拥有所有代码生成器权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
VALUES 
    (1, @perm_start),
    (1, @perm_start + 1),
    (1, @perm_start + 2),
    (1, @perm_start + 3),
    (1, @perm_start + 4),
    (1, @perm_start + 5);

-- 普通管理员(role_id=2)拥有所有代码生成器权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
VALUES 
    (2, @perm_start),
    (2, @perm_start + 1),
    (2, @perm_start + 2),
    (2, @perm_start + 3),
    (2, @perm_start + 4),
    (2, @perm_start + 5);

-- ----------------------------
-- Table structure for sys_storage_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_storage_config`;
CREATE TABLE `sys_storage_config`
(
    `id`                BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`        VARCHAR(50) NOT NULL COMMENT '配置key',
    `endpoint`          VARCHAR(255) NOT NULL COMMENT '访问站点',
    `custom_domain`     VARCHAR(255) DEFAULT NULL COMMENT '自定义域名',
    `access_key`        VARCHAR(255) NOT NULL COMMENT '访问密钥',
    `secret_key`        VARCHAR(255) NOT NULL COMMENT '密钥',
    `bucket_name`       VARCHAR(100) NOT NULL COMMENT '桶名称',
    `prefix`            VARCHAR(100) DEFAULT NULL COMMENT '前缀',
    `region`            VARCHAR(100) DEFAULT NULL COMMENT '域',
    `storage_provider`  VARCHAR(20) NOT NULL DEFAULT 'minio' COMMENT '存储服务商（minio、oss、cos等）',
    `bucket_permission` VARCHAR(20) NOT NULL DEFAULT 'private' COMMENT '桶权限类型（private私有、public-read公开读、public-read-write公开读写）',
    `is_https`          TINYINT NOT NULL DEFAULT 0 COMMENT '是否HTTPS（0否 1是）',
    `is_default`        TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认（0否 1是）',
    `remark`            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `del_flag`          TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0 正常 1 删除',
    `create_by`         VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_config_key` (`config_key`),
    KEY `idx_storage_provider` (`storage_provider`),
    KEY `idx_is_default` (`is_default`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '对象存储配置表';

-- ----------------------------
-- Records of sys_storage_config
-- ----------------------------
INSERT INTO `sys_storage_config` (`config_key`, `endpoint`, `custom_domain`, `access_key`, `secret_key`, `bucket_name`, `prefix`, `region`, `storage_provider`, `bucket_permission`, `is_https`, `is_default`, `remark`)
VALUES ('minio', '172.16.29.222:9000', NULL, 'admin', '12345678', 'avatars', NULL, NULL, 'minio', 'private', 0, 1, '本地MinIO配置');

SET FOREIGN_KEY_CHECKS = 1;
