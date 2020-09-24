package com.topband.opencvtest.common;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonHelper {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 001.json转换成对象
     *
     * @param c
     * @param jsonStr
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> T fromJson(Class<T> c, String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//忽略未知字段
        mapper.setDateFormat(formatter);
        try {
            return mapper.readValue(jsonStr, c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> listFromJson(Class<T> c, String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//忽略未知字段
        try {
            if (jsonStr != null && !jsonStr.equals("")) {
                CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, c);
                return mapper.readValue(jsonStr, listType);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复杂转换 例如： sonHelper.fromJson(new TypeReference<List<T>>(){},str);
     *
     * @param c
     * @param jsonStr
     * @param <T>
     * @return
     */
    public static <T> T fromJson(TypeReference<T> c, String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//忽略未知字段
        try {
            if (jsonStr != null && !jsonStr.equals("")) {
                return mapper.readValue(jsonStr, c);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 002.对象转换成json
     * @param:传入对象
     * @return:json字符串
     */
    public static String toJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 不序列化null的属性
        mapper.setDateFormat(formatter);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
        }
        return "";
    }

    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(formatter);
        Map<String, Object> map = new HashMap<>();
        try {
            String jsonStr = mapper.writeValueAsString(obj);
            map = mapper.readValue(jsonStr, map.getClass());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Map转成实体对象
     *
     * @param map map实体对象包含属性
     * @param
     * @return
     */
    public static <T> T map2bject(Class<T> c, Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//忽略未知字段
        mapper.setDateFormat(formatter);
        try {
            String jsonStr = mapper.writeValueAsString(map);
            return mapper.readValue(jsonStr, c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将两个JSONObject 交集 组合成为一个JSONObject ，如果 key相同,则覆盖
     *
     * @param oldFieldData 旧的数据
     * @param newFieldData 新数据
     * @return
     */
    public static JSONObject fieldDataCover(JSONObject oldFieldData, JSONObject newFieldData) {

        Set<String> st = newFieldData.keySet();
        Iterator<String> it = st.iterator();
        while (it.hasNext()) {
            // 获得key
            String key = it.next();
            String value = newFieldData.getString(key);
            oldFieldData.put(key, value);//新增 或替换
        }
        return oldFieldData;
    }


}