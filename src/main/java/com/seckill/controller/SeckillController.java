package com.seckill.controller;

import com.seckill.domain.OrderInfo;
import com.seckill.domain.SeckillOrder;
import com.seckill.domain.SeckillUser;
import com.seckill.result.CodeMsg;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillService;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/do_seckill")
    public String doSeckill(Model model, SeckillUser seckillUser,
                            @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", seckillUser);
        if (seckillUser == null) {
            return "login";
        }

        // 判断是否重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.SECKILL_REPEAT.getMsg());
            return "seckill_fail";
        }

        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }

        // 执行秒杀，减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = seckillService.doSeckill(seckillUser, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);

        return "order_detail";
    }


}
