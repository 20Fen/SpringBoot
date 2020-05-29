package com.example.demo.system.util;

import io.goeasy.GoEasy;
import io.goeasy.publish.GoEasyError;
import io.goeasy.publish.PublishListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@Slf4j
public class GoEasyUtil {

    @Value("${goeasy.common-key}")
    private String common_key;
    @Value("${goeasy.rest-host}")
    private String rest_host;

    private static GoEasy goEasy;
//    private static GoeasyMessageService goeasyMessageServiceStatic;
//
//    @Autowired
//    private GoeasyMessageService goeasyMessageService;

    @PostConstruct
    public void init() {
        goEasy = new GoEasy(rest_host, common_key);
//        goeasyMessageServiceStatic = goeasyMessageService;
    }

    private GoEasyUtil() {}

    /**
     * @param title 标题
     * @param content 内容
     * @param channel
     * @param grade 等级
     */
    public static void publish(String title, String content, String channel, int grade, Map<String, String> params
            ,Integer type, Long orderViewId, Long tenantId, Integer read) {

        Map<String, String> map = new HashMap<>(4);
        map.put("title", title);
        map.put("content", content);
        map.put("grade", String.valueOf(grade));
        String jsonString = JSONUtil.toJSONString(params);
        map.put("params", jsonString);

        goEasy.publish(channel, JSONUtils.toJSONString(map), new PublishListener(){
            @Override
            public void onSuccess() {
                log.info("消息发布成功===="+title+",orderViewId:"+orderViewId+",adminUserId:"+channel);
                try {
                    insertGoeasyMessage(title, content, channel, grade, 1, jsonString, "ok",type,orderViewId,tenantId,read);
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("推送异常");
                }
            }
            @Override
            public void onFailed(GoEasyError error) {
                log.error("消息发布失败===="+title+", 错误编码：" + error.getCode() + " 错误信息： " + error.getContent());
                try {
                    insertGoeasyMessage(title, content, channel, grade, 1, jsonString, "错误编码:" + error.getCode() + " 错误信息:" + error.getContent(), type, orderViewId, tenantId, read);
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("推送异常");
                }
            }
        });

    }

//    private static void insertGoeasyMessage(String title, String content, String channel, int grade, int status, String params, String errorReason,Integer type, Long orderViewId, Long tenantId, Integer ifRead) {
//        GoeasyMessage goeasyMessage = new GoeasyMessage();
//        goeasyMessage.setChannel(channel);
//        goeasyMessage.setContent(content);
//        goeasyMessage.setGrade(grade);
//        goeasyMessage.setTitle(title);
//        goeasyMessage.setStatus(status);
//        goeasyMessage.setErrorReason(errorReason);
//        goeasyMessage.setParams(params);
//        goeasyMessage.setType(type);
//        goeasyMessage.setTenantId(tenantId);
//        goeasyMessage.setOrderViewId(orderViewId);
//        goeasyMessage.setIfRead(ifRead);
//        goeasyMessageServiceStatic.insert(goeasyMessage);
//    }
}
