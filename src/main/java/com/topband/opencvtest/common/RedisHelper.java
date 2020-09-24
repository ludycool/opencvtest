package com.topband.opencvtest.common;

/**
 * @author ludi
 * @version 1.0
 * @date 2019/11/27 14:08
 * @remark
 */
public class RedisHelper {
    private static final ThreadLocal<RedisUtils<byte[]>> redisUtilsByte_Context = new InheritableThreadLocal<>();
    private static final ThreadLocal<RedisUtils<String>> redisUtilsString_Context = new InheritableThreadLocal<>();
    private static final ThreadLocal<RedisUtils<Object>> redisUtilsObject_Context = new InheritableThreadLocal<>();

    /**
     * 取得实例
     *
     * @return
     */
    public static RedisUtils<byte[]> getByteUtil() {
        RedisUtils<byte[]> redisUtils = redisUtilsByte_Context.get();
        if (redisUtils == null) {
            synchronized (RedisHelper.class) {
                if (redisUtils == null) {
                    redisUtils = SpringContextBeanService.getBean("redisUtilsByte");
                    redisUtilsByte_Context.set(redisUtils);
                }
            }
        }
        return redisUtils;
    }
    /**
     * 取得实例
     *
     * @return
     */
    public static RedisUtils<String> getStringUtil() {
        RedisUtils<String> redisUtils = redisUtilsString_Context.get();
        if (redisUtils == null) {
            synchronized (RedisHelper.class) {
                if (redisUtils == null) {
                    redisUtils = SpringContextBeanService.getBean("redisUtilsString");
                    redisUtilsString_Context.set(redisUtils);
                }
            }
        }
        return redisUtils;
    }

    /**
     * 取得实例
     *
     * @return
     */
    public static RedisUtils<Object> getObjectUtil() {
        RedisUtils<Object> redisUtils = redisUtilsObject_Context.get();
        if (redisUtils == null) {
            synchronized (RedisHelper.class) {
                if (redisUtils == null) {
                    redisUtils = SpringContextBeanService.getBean("redisUtilsObject");
                    redisUtilsObject_Context.set(redisUtils);
                }
            }
        }
        return redisUtils;
    }
}
