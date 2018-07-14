package com.seckill.service;

import com.seckill.domain.OrderInfo;
import com.seckill.domain.SeckillOrder;
import com.seckill.domain.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.redis.SeckillKey;
import com.seckill.util.MD5Util;
import com.seckill.util.UUIDUtil;
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

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo doSeckill(SeckillUser seckillUser, GoodsVo goods) {
        // 执行秒杀，减库存
        if (goodsService.reduceStock(goods)) {
            // 创建秒杀订单
            return orderService.createOrder(seckillUser, goods);
        }
        setGoodsOver(goods.getId());
        return null;
    }

    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        // 秒杀成功
        if (order != null)
            return order.getOrderId();
        else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver)
                return -1;
            else
                return 0;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
    }

    public boolean checkPath(String path, SeckillUser seckillUser, long goodsId) {
        if (seckillUser == null || path == null)
            return false;
        String pathOld = redisService.get(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createSeckillPath(SeckillUser seckillUser, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "e3wf4edte4");
        redisService.set(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId, str);
        return str;
    }
}
