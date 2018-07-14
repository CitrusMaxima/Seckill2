package com.seckill.rabbitmq;

import com.seckill.domain.SeckillOrder;
import com.seckill.domain.SeckillUser;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillService;
import com.seckill.util.CommonUtil;
import com.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);

        SeckillMessage seckillMessage = CommonUtil.stringToBean(message, SeckillMessage.class);
        SeckillUser seckillUser = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }

        // 判断是否重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (order != null) {
            return;
        }

        // 执行秒杀，减库存，下订单，写入秒杀订单
        seckillService.doSeckill(seckillUser, goods);
    }

    /*
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("topic queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("topic queue2 message:" + message);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void receiveHeaders(byte[] message) {
        log.info("headers queue message:" + new String(message));
    }
    */
}
