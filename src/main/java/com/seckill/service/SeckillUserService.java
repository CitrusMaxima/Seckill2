package com.seckill.service;

import com.seckill.dao.SeckillUserDao;
import com.seckill.domain.SeckillUser;
import com.seckill.exception.GlobalException;
import com.seckill.redis.RedisService;
import com.seckill.redis.SeckillUserKey;
import com.seckill.result.CodeMsg;
import com.seckill.util.MD5Util;
import com.seckill.util.UUIDUtil;
import com.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    public SeckillUser getById(long id) {
        return seckillUserDao.getById(id);
    }

    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);

        // 延长有效期
        if (user != null) {
            addCookie(response, user);
        }
        return user;
    }
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        // 判断手机号是否存在
        SeckillUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        // 验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        // 生成cookie
        addCookie(response, user);

        return true;
    }

    private void addCookie(HttpServletResponse response, SeckillUser user) {
        String token = UUIDUtil.uuid();
        redisService.set(SeckillUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}