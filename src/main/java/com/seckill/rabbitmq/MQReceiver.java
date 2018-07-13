package com.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    /**
     * Direct模式
     * @param message
     */
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
    }
}
