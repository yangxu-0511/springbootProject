package com.study.common.controller.rocketMq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductByTemplate {

    private final RocketMQTemplate rocketMQTemplate;

    public ProductByTemplate(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void sendMessage(String topic, JSONObject message) {
        rocketMQTemplate.convertAndSend(topic, message);
    }
}
