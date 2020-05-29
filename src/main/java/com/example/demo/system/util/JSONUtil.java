package com.example.demo.system.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;


@Slf4j
public class JSONUtil {


    public static final JSONObject EMPTY_JSONOBJECT = new JSONObject();
    public static final JSONArray EMPTY_JSONARRAY = new JSONArray();


    /**
     * 解析返回对象的list
     *
     * @param content
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parseList(String content, Class<T> clazz) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        List<T> results = null;
        try {
            results = JSON.parseArray(content, clazz);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return (results == null ? Collections.EMPTY_LIST : results);
    }

    /**
     * 解析返回对象
     *
     * @param content
     * @param clazz
     * @return
     */
    public static <T> T parse(String content, Class<T> clazz) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        T result = null;
        try {
            result = JSON.parseObject(content, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 解析JsonObject
     *
     * @param content
     * @return
     */
    public static JSONObject parseObject(String content) {
        if (StringUtils.isBlank(content)) {
            return EMPTY_JSONOBJECT;
        }
        JSONObject result = null;
        try {
            result = JSON.parseObject(content);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result == null ? EMPTY_JSONOBJECT : result;
    }

    /**
     * 解析JsonArray
     *
     * @param content
     * @return
     */
    public static JSONArray parseArray(String content) {
        if (StringUtils.isBlank(content)) {
            return EMPTY_JSONARRAY;
        }
        JSONArray result = null;
        try {
            result = JSON.parseArray(content);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result == null ? EMPTY_JSONARRAY : result;
    }

    /**
     * 将对象解析成JSON
     *
     * @return
     */
    public static String toJSONString(Object object) {
        if (object == null) {
            return EMPTY_JSONOBJECT.toJSONString();
        }
        String result = null;
        try {
            result = JSON.toJSONString(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result == null ? EMPTY_JSONOBJECT.toJSONString() : result;
    }

    /**根据object 返回json
     * @param object
     * @return
     */
    public static JSONObject toJSON(Object object) {

        if (object == null) {
            return null;
        }
        JSONObject result = null;
        try {
            result = (JSONObject)JSON.toJSON(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result= null;
        }
        return result;
    }


    /**创建一个空jsonobject
     * @return
     */
    public static JSONObject createNew(){
        return new JSONObject();
    }
}
