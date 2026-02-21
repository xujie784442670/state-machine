package cn.jasonone.sm.config;

import cn.jasonone.sm.core.RedisStateMachineManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfiguration
public class AutoRedisConfiguration {

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisStateMachineManager redisStateMachineManager(RedisTemplate<String, String> redisTemplate) {
        return new RedisStateMachineManager(redisTemplate);
    }
}
