package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author rbx
 * @title
 * @Create 2023-02-27 18:20
 * @Description
 */
@Service
public class TestServiceImpl implements TestService {
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Override
    public void testLock() {
        String num = (String) redisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(num)) {
            return;
        }
        int i = Integer.parseInt(num);
        redisTemplate.opsForValue().set("num", String.valueOf(++i));
    }

    /**
     * 采用SpringDataRedis实现分布式锁
     * 原理：执行业务方法前先尝试获取锁（setnx存入key val），如果获取锁成功再执行业务代码，业务执行完毕后将锁释放(del key)
     */
    @Override
    public void testLock2() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", uuid,3, TimeUnit.SECONDS);
        if (flag){
            String value = redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(value)){
                return;
            }
            int i = Integer.parseInt(value);
            redisTemplate.opsForValue().set("num", String.valueOf(++i));
            /*if (uuid.equals(redisTemplate.opsForValue().get("lock"))){
                redisTemplate.delete("lock");
            }*/
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
        }else {
            try {
                Thread.sleep(1000);
                this.testLock2();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 开发步骤：
     *  * 1.使用RedissonClient客户端对象 创建锁对象
     *  * 2.调用获取锁方法
     *  * 3.执行业务逻辑
     *  * 4.将锁释放
     */
    @Override
    public void testLock3() {
        RLock lock = redissonClient.getLock("lock");
        try {
            lock.tryLock(2,40,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String value = redisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(value)) {
            return;
        }
        int i = Integer.parseInt(value);
        redisTemplate.opsForValue().set("num", String.valueOf(++i));
        lock.unlock();
    }
}
