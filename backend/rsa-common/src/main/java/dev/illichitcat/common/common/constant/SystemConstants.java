package dev.illichitcat.common.common.constant;

/**
 * 系统常量定义
 * 统一管理系统状态、标记等常量
 *
 * @author Illichitcat
 * @since 2026/01/14
 */
public final class SystemConstants {

    private SystemConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 用户状态常量
     */
    public static final class UserStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }

    /**
     * 逻辑删除标记常量
     */
    public static final class DelFlag {
        /**
         * 正常
         */
        public static final Integer NORMAL = 0;
        /**
         * 已删除
         */
        public static final Integer DELETED = 1;
    }

    /**
     * 通知发布范围常量
     */
    public static final class NoticeTargetType {
        /**
         * 全部用户
         */
        public static final Integer ALL_USERS = 1;
        /**
         * 指定角色
         */
        public static final Integer SPECIFIED_ROLES = 2;
    }

    /**
     * 通知状态常量
     */
    public static final class NoticeStatus {
        /**
         * 草稿
         */
        public static final Integer DRAFT = 1;
        /**
         * 已发布
         */
        public static final Integer PUBLISHED = 2;
        /**
         * 已撤销
         */
        public static final Integer REVOKED = 3;
    }

    /**
     * 通知类型常量
     */
    public static final class NoticeType {
        /**
         * 系统通知
         */
        public static final Integer SYSTEM_NOTICE = 1;
        /**
         * 活动通知
         */
        public static final Integer ACTIVITY_NOTICE = 2;
    }

    /**
     * 通知优先级常量
     */
    public static final class NoticePriority {
        /**
         * 普通
         */
        public static final Integer NORMAL = 1;
        /**
         * 重要
         */
        public static final Integer IMPORTANT = 2;
        /**
         * 紧急
         */
        public static final Integer URGENT = 3;
    }

    /**
     * 菜单状态常量
     */
    public static final class MenuStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }

    /**
     * 菜单显示状态常量
     */
    public static final class MenuHidden {
        /**
         * 显示
         */
        public static final Integer SHOW = 0;
        /**
         * 隐藏
         */
        public static final Integer HIDDEN = 1;
    }

    /**
     * 菜单外链状态常量
     */
    public static final class MenuExternal {
        /**
         * 内部链接
         */
        public static final Integer INTERNAL = 0;
        /**
         * 外部链接
         */
        public static final Integer EXTERNAL = 1;
    }

    /**
     * 菜单类型常量
     */
    public static final class MenuType {
        /**
         * 目录
         */
        public static final String DIRECTORY = "M";
        /**
         * 菜单
         */
        public static final String MENU = "C";
        /**
         * 按钮
         */
        public static final String BUTTON = "F";
    }

    /**
     * 角色状态常量
     */
    public static final class RoleStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }

    /**
     * 角色权限范围常量
     */
    public static final class RoleDataScope {
        /**
         * 全部数据权限
         */
        public static final String ALL = "1";
        /**
         * 自定义数据权限
         */
        public static final String CUSTOM = "2";
        /**
         * 本部门数据权限
         */
        public static final String DEPT = "3";
        /**
         * 本部门及以下数据权限
         */
        public static final String DEPT_AND_CHILD = "4";
        /**
         * 仅本人数据权限
         */
        public static final String SELF = "5";
    }

    /**
     * 字典状态常量
     */
    public static final class DictStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }

    /**
     * 在线用户状态常量
     */
    public static final class UserOnlineStatus {
        /**
         * 在线
         */
        public static final String ONLINE = "online";
        /**
         * 离线
         */
        public static final String OFFLINE = "offline";
    }

    /**
     * 登录状态常量
     */
    public static final class LoginStatus {
        /**
         * 失败
         */
        public static final Integer FAILURE = 0;
        /**
         * 成功
         */
        public static final Integer SUCCESS = 1;
    }

    /**
     * 定时任务状态常量
     */
    public static final class JobStatus {
        /**
         * 正常
         */
        public static final String NORMAL = "0";
        /**
         * 暂停
         */
        public static final String PAUSE = "1";
    }

    /**
     * 定时任务并发策略常量
     */
    public static final class JobConcurrent {
        /**
         * 允许
         */
        public static final String ALLOWED = "0";
        /**
         * 禁止
         */
        public static final String FORBIDDEN = "1";
    }

    /**
     * 定时任务执行状态常量
     */
    public static final class JobLogStatus {
        /**
         * 正常
         */
        public static final String NORMAL = "0";
        /**
         * 失败
         */
        public static final String FAILURE = "1";
    }

    /**
     * 定时任务错过执行策略常量
     */
    public static final class JobMisfirePolicy {
        /**
         * 默认
         */
        public static final String DEFAULT = "0";
        /**
         * 立即执行
         */
        public static final String IGNORE_MISFIRES = "1";
        /**
         * 执行一次
         */
        public static final String FIRE_AND_PROCEED = "2";
        /**
         * 放弃执行
         */
        public static final String DO_NOTHING = "3";
    }

    /**
     * 操作日志状态常量
     */
    public static final class OperLogStatus {
        /**
         * 正常
         */
        public static final Integer NORMAL = 0;
        /**
         * 异常
         */
        public static final Integer ABNORMAL = 1;
    }

    /**
     * 配置状态常量
     */
    public static final class ConfigStatus {
        /**
         * 禁用
         */
        public static final Integer DISABLED = 0;
        /**
         * 启用
         */
        public static final Integer ENABLED = 1;
    }

    /**
     * 存储配置默认状态常量
     */
    public static final class StorageConfigDefault {
        /**
         * 否
         */
        public static final Integer NO = 0;
        /**
         * 是
         */
        public static final Integer YES = 1;
    }

    /**
     * 顶级父ID常量
     */
    public static final class TopParentId {
        /**
         * 顶级父ID
         */
        public static final Long TOP = 0L;
    }

    /**
     * 缓存预热来源常量
     */
    public static final class CacheSource {
        /**
         * 手动触发
         */
        public static final String MANUAL = "MANUAL";
        /**
         * 定时任务触发
         */
        public static final String SCHEDULED = "SCHEDULED";
    }
}