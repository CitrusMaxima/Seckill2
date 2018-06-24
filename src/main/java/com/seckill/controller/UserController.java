package com.seckill.controller;

import com.seckill.domain.SeckillUser;
import com.seckill.result.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    SeckillUserService seckillUserService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser seckillUser) {
        return Result.success(seckillUser);
    }


}
