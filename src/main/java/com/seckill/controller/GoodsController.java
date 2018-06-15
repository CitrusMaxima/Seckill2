package com.seckill.controller;

import com.seckill.domain.SeckillUser;
import com.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    SeckillUserService seckillUserService;

    @RequestMapping("/to_list")
    public String toList(Model model, SeckillUser seckillUser) {
        model.addAttribute("user", seckillUser);
        return "goods_list";
    }

}
