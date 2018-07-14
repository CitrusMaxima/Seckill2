package com.seckill.controller;

import com.seckill.domain.User;
import com.seckill.rabbitmq.MQSender;
import com.seckill.redis.RedisService;
import com.seckill.redis.UserKey;
import com.seckill.result.CodeMsg;
import com.seckill.result.Result;
import com.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "CitrusMaxima");
        return "hello";
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        // sender.send("hello, CitrusMaxima");
        return Result.success("hello, CitrusMaxima");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
        // sender.sendTopic("hello, CitrusMaxima");
        return Result.success("hello, CitrusMaxima");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
        // sender.sendFanout("hello, CitrusMaxima");
        return Result.success("hello, CitrusMaxima");
    }

    @RequestMapping("/mq/headers")
    @ResponseBody
    public Result<String> headers() {
        // sender.sendHeaders("hello, CitrusMaxima");
        return Result.success("hello, CitrusMaxima");
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello, CitrusMaxima");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(1);
        user.setName("11111");
        redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(true);
    }
}
