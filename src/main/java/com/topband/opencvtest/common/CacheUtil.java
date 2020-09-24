package com.topband.opencvtest.common;

import com.topband.opencvtest.config.AppConfig;
import com.topband.opencvtest.mode.User;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/24 11:05
 * @remark
 */
public class CacheUtil {

    static String userKey = AppConfig.proje_prefix + "user_";


    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    public static User userGet(String id) {
        String res = RedisHelper.getStringUtil().hGet(userKey, id);
        if (!StringUtils.isEmpty(res)) {
            User item = JsonHelper.fromJson(User.class, res);
            return item;
        } else {
            return null;
        }
    }


    /**
     * 保存用户
     *
     * @param item
     * @return
     */
    public static void userSet(User item) {

        // String cahcedid = DeviceLoginKey + mac;
        RedisHelper.getStringUtil().hPut(userKey, item.getId(), JsonHelper.toJson(item));
    }

}
