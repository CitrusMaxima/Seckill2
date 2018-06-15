package com.seckill.controller;

import com.seckill.domain.SeckillUser;
import com.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    SeckillUserService seckillUserService;

    @RequestMapping("/to_list")
    public String toList(Model model,
                         @CookieValue(value = SeckillUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                         @RequestParam(value = SeckillUserService.COOKIE_NAME_TOKEN, required = false) String paramToken) {

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        SeckillUser seckillUser = seckillUserService.getByToken(token);
        model.addAttribute("user", seckillUser);
        return "goods_list";
    }

}
