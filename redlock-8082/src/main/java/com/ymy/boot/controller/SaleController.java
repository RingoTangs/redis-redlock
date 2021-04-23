package com.ymy.boot.controller;

import com.ymy.boot.service.SaleService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ringo
 * @date 2021/4/21 22:00
 */
@RestController
public class SaleController {

    @Resource
    private SaleService saleService;

    @Resource
    private RedisTemplate redisTemplate;

    // Redis 锁
    public static final String REDIS_LOCK = "REDIS_LOCK";

    // 初始代码
    /*@GetMapping("/sale")
    public String saleShop() {
        return saleService.sale();
    }*/

    // v1.0 单机版没有加锁（synchronized）
/*    @GetMapping("/sale")
    public String saleShop() {
        synchronized (this) {
            return saleService.sale();
        }
    }*/

    // v1.1. 单机版没有加锁 ReentrantLock
/*    private final Lock lock = new ReentrantLock();

    @GetMapping("/sale")
    public String saleShop() throws Exception {
        if (lock.tryLock(3L, TimeUnit.SECONDS)) {
            try {
                return saleService.sale();
            } finally {
                lock.unlock();
            }
        } else {
            String msg = "没有抢到锁, 稍后再试";
            System.out.println(msg);
            return msg;
        }
    }*/

    // v2.0 单机锁不能解决分布式项目数据一致性问题: 分布式锁
/*    @GetMapping("/sale")
    public String saleShop() {
        String current = UUID.randomUUID().toString() + Thread.currentThread().getName();
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, current); // setNX

        if (!flag) {
            System.out.println("没有抢到锁!");
            return "没有抢到锁!";
        }
        try {
            return saleService.sale();
        } finally {
            redisTemplate.delete(REDIS_LOCK);
        }
    }*/

    // v3.0 释放锁不能直接释放, 需要判断
/*    @GetMapping("/sale")
    public String saleShop() {
        String current = UUID.randomUUID().toString() + Thread.currentThread().getName();
        System.out.println(current);
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, current, 10L, TimeUnit.SECONDS); // setNX

        if (!flag) {
            System.out.println("没有抢到锁!");
            return "没有抢到锁!";
        }
        try {
            System.out.println(current + "\t 买到了");
            return saleService.sale();
        } finally {
            System.out.println(current);
            if (redisTemplate.opsForValue().get(REDIS_LOCK).equals(current)) {
                redisTemplate.delete(REDIS_LOCK);
            }
        }
    }*/

    // v4.0 Redisson 分布式锁
    @Resource
    private Redisson redisson;

    @GetMapping("/sale")
    public String saleShop() {
        RLock lock = redisson.getLock(REDIS_LOCK);
        lock.lock();
        try {
            return saleService.sale();
        } finally {
            if (lock.isHeldByCurrentThread()) // 注意细节
                lock.unlock();
        }
    }
}
