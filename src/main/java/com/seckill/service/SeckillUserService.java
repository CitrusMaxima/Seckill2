package com.seckill.service;

import com.seckill.dao.SeckillUserDao;
import com.seckill.domain.SeckillUser;
import com.seckill.exception.GlobalException;
import com.seckill.result.CodeMsg;
import com.seckill.util.MD5Util;
import com.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillUserService {

    @Autowired
    SeckillUserDao seckillUserDao;

    public SeckillUser getById(long id) {
        return seckillUserDao.getById(id);
    }

    public boolean login(LoginVo loginVo) {
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
        return true;
    }
}