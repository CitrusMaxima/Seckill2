package com.seckill.service;

import com.seckill.domain.OrderInfo;
import com.seckill.domain.SeckillUser;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo doSeckill(SeckillUser seckillUser, GoodsVo goods) {
        // 执行秒杀，减库存
        if (goodsService.reduceStock(goods)) {
            // 创建秒杀订单
            return orderService.createOrder(seckillUser, goods);
        }

        return null;
    }
}
