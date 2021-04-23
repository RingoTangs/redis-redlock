package com.ymy.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Ringo
 * @date 2021/4/22 16:02
 */
@Service
public class SaleService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServerProperties serverProperties;

    public String sale() {
        String result = stringRedisTemplate.opsForValue().get("stock");

        int goodNum = result == null ? 0 : Integer.parseInt(result);

        String msg = serverProperties.getPort() + "";

        if (goodNum > 0) {
            int stock = goodNum - 1;
            stringRedisTemplate.opsForValue().set("stock", String.valueOf(stock));
            msg += "\t" + "成功买到商品, 还剩【" + stock + "】件";
            System.out.println(msg);
            return msg;
        }
        msg += "\t" + "库存不足";
        System.out.println(msg);
        return msg;
    }

}
