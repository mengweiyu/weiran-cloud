package com.weiran.rabbitmq.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.weiran.rabbitmq.common.SeckillMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * rabbitmq demo-消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BasicConsumer {

    /**
     * 监听并接收消费队列中的消息-在这里采用单一容器工厂实例即可
     */
    @RabbitListener(queues = RabbitMqConstants.BASIC_QUEUE, containerFactory = "singleListenerContainer") // 设置消费者监听的队列以及监听的消息容器
    public void consumeMsg(SeckillMessage seckillMessage, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException {
        try {
            log.info("");
            log.warn("写入订单表失败: {}", JSONUtil.toJsonStr(seckillMessage));
            // 执行完业务逻辑后，手动进行确认消费，其中第一个参数为：消息的分发标识(全局唯一);第二个参数：是否允许批量确认消费
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 第二个参数requeue重新归入队列,true的话会重新归入队列,需要人为地处理此次异常消息,重新归入队列也会继续异常
            channel.basicAck(tag, true);
            log.error("rabbitmq demo-消费者-发生异常：", e);
        }
    }
}
