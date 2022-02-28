package com.cloudathon.cloudathondemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
public class RedisConfig {

    @Value(value = "${spring.redis.host}")
    private String servers;

    @Value(value = "${spring.redis.password}")
    private String password;


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        List<String> nodes = Arrays.stream(servers.split(",")).collect(Collectors.toList());
        JedisConnectionFactory jedisConFactory
                = new JedisConnectionFactory(new RedisClusterConfiguration(nodes));
        //jedisConFactory.setHostName("localhost");
        jedisConFactory.setPort(6379);
        jedisConFactory.setPassword(password);
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

}
