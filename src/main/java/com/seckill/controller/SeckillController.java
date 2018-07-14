package com.seckill.controller;

import com.seckill.domain.SeckillOrder;
import com.seckill.domain.SeckillUser;
import com.seckill.rabbitmq.MQSender;
import com.seckill.rabbitmq.SeckillMessage;
import com.seckill.redis.GoodsKey;
import com.seckill.redis.RedisService;
import com.seckill.result.CodeMsg;
import com.seckill.result.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillService;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null)
            return;

        // 将库存数量写入Redis缓存
        for (GoodsVo goodsVo : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    @RequestMapping(value = "/{path}/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill(Model model, SeckillUser seckillUser,
                                     @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path) {
        model.addAttribute("user", seckillUser);
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 验证path
        boolean check = seckillService.checkPath(path, seckillUser, goodsId);
        if (!check)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);


        // 内存标记，减少redis访问
        boolean isOver = localOverMap.get(goodsId);
        if (isOver)
            return Result.error(CodeMsg.SECKILL_OVER);

        // 预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 判断是否重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.SECKILL_REPEAT);
        }

        // 入队
        SeckillMessage message = new SeckillMessage();
        message.setSeckillUser(seckillUser);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);

        return Result.success(0);   // 排队中


        /*
        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 判断是否重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.SECKILL_REPEAT);
        }

        // 执行秒杀，减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = seckillService.doSeckill(seckillUser, goods);
        if (orderInfo == null)
            return Result.error(CodeMsg.ORDER_NOT_EXIST);

        return Result.success(orderInfo);
        */

    }

    /**
     * orderId: 成功
     * -1: 秒杀失败
     * 0: 排队中
     *
     * @param model
     * @param seckillUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, SeckillUser seckillUser,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", seckillUser);
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = seckillService.getSeckillResult(seckillUser.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(Model model, SeckillUser seckillUser,
                                         @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", seckillUser);
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String path = seckillService.createSeckillPath(seckillUser, goodsId);
        return Result.success(path);
    }

}
