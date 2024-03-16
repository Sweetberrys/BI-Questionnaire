package com.questionnaire.service;


import com.questionnaire.common.enums.ResultCodeEnum;
import com.questionnaire.exception.CustomException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供RedisLimiter 限流基础服务（提供了通用的能力）
 */
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     * @param key 区分不同的限流器，比如不同的用户ID，应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //每1秒最多访问 2 次
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new CustomException(ResultCodeEnum.TOO_MANY_REQUEST);
        }

    }
}