package com.study.common.controller.rocketMq.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        consumerGroup = "myGroup",  // consumerGroup：消费者组名
        topic = "myTopic",                         // topic：订阅的主题
        selectorExpression = "*",               // selectorExpression：控制可以选择的消息，可以使用SelectorType.SQL92语法。设置为 * 时，表示全部。
        messageModel = MessageModel.CLUSTERING  // messageModel: 控制消息模式。MessageModel.CLUSTERING：负载均衡；MessageModel.BROADCASTING：广播模式
)
public class ConsumerListenerByTemplate implements RocketMQListener<JSONObject> {

    @Override
    public void onMessage(JSONObject msg) {
        System.out.println("监听到消费消息-" + msg.toJSONString());
    }
}
