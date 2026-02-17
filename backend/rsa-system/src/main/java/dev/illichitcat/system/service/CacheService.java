package dev.illichitcat.system.service;

import dev.illichitcat.system.model.vo.CacheInfoVO;
import dev.illichitcat.system.model.vo.KeyDetailVO;

import java.util.List;

/**
 * 缓存监控服务接口
 *
 * @author Illichitcat
 * @since 2026/01/08
 */
public interface CacheService {

    /**
     * 获取缓存监控信息
     *
     * @return 缓存监控信息
     */
    CacheInfoVO getCacheInfo();

    /**
     * 获取缓存键列表
     *
     * @param pattern 键模式，默认为 "*"
     * @return 键列表
     */
    List<String> getKeys(String pattern);

    /**
     * 获取键值
     *
     * @param key 键名
     * @return 键值
     */
    Object getValue(String key);

    /**
     * 获取键详细信息
     *
     * @param key 键名
     * @return 键详细信息
     */
    KeyDetailVO getKeyDetail(String key);

    /**
     * 删除键
     *
     * @param key 键名
     */
    void deleteKey(String key);

    /**
     * 设置过期时间
     *
     * @param key 键名
     * @param ttl 过期时间(秒)
     */
    void setTtl(String key, Long ttl);

    /**
     * 清空数据库
     */
    void clearDb();
}