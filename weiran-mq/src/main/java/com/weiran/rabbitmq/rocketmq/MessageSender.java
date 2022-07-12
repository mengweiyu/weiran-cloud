package com.weiran.rabbitmq.rocketmq;

import com.weiran.rabbitmq.common.SeckillMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * 生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSender {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送可靠同步消息 ,可以拿到SendResult 返回数据
     * 同步发送是指消息发送出去后，会在收到mq发出响应之后才会发送下一个数据包的通讯方式。
     
     * 参数1:  消息体 可以为一个对象
     * 参数2： topic:tag
     */
    public void syncSend(SeckillMessage seckillMessage, String topicAndTag) {
        SendResult result = rocketMQTemplate.syncSend(topicAndTag, seckillMessage, 1000);
        log.info("" + result);
    }

    /**
     * 发送 可靠异步消息
     * 发送消息后，不等mq响应，接着发送下一个数据包。发送方通过设置回调接口接收服务器的响应，并可对响应结果进行处理。
     * 参数1:  消息体 可以为一个对象
     * 参数2： topic:tag
     * 参数3： 回调对象
     */
    public void asyncSend(SeckillMessage seckillMessage, String topicAndTag) {
        rocketMQTemplate.asyncSend(topicAndTag, seckillMessage, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
//                log.info("回调sendResult:" + sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.info("MQ发送失败" + e.getMessage());
            }
        });
//        }, 20 * 1000); // 防止 invokeAsync call timeout信息提示超时
    }

    /**
     * 发送单向消息，特点为只负责发送消息，不等待服务器回应且没有回调函数触发，即只发送请求不等待应答。
     * 此方式发送消息的过程耗时非常短，一般在微秒级别。应用场景：适用于某些耗时非常短，但对可靠性要求并
     * 不高的场景，例如日志收集。
     * 参数1:  消息体 可以为一个对象
     * 参数2： topic:tag
     */
    public void sendOneWay(SeckillMessage seckillMessage, String topicAndTag) {
        rocketMQTemplate.sendOneWay(topicAndTag, seckillMessage);
    }

    /**
     * 发送单向的顺序消息
     * 参数1:  消息体 可以为一个对象
     * 参数2： topic:tag
     */
    public void sendOneWayOrderly(SeckillMessage seckillMessage, String topicAndTag) {
        rocketMQTemplate.sendOneWayOrderly(topicAndTag, seckillMessage, "8888");
    }

}

