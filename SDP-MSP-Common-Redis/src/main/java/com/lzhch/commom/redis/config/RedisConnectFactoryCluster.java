package com.lzhch.commom.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @packageName： com.lzhch.commom.redis.config
 * @className: RedisConnectFactoryCluster
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-09-03 10:20
 */
@Configuration
@ConditionalOnProperty(prefix = "edw.sdp.spring.redis", name = "isCluster", havingValue = "true")
public class RedisConnectFactoryCluster {

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private RedisServer redisServer;

    /**
     *  集群配置, 分割节点
     * @return
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        String[] serverArray = redisConfig.getClusterNodes().split(",");
        Set<RedisNode> nodes = new HashSet<RedisNode>();
        for (String ipPort : serverArray) {
            String[] ipAndPort = ipPort.split(":");
            nodes.add(new RedisNode(ipAndPort[0].trim(), Integer.valueOf(ipAndPort[1])));
        }
        redisClusterConfiguration.setClusterNodes(nodes);
        redisClusterConfiguration.setMaxRedirects(redisConfig.getMmaxRedirectsac());
        return redisClusterConfiguration;
    }

    /**
     * 集群配置
     * @param redisClusterConfiguration
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration) {
        JedisConnectionFactory jedisConnectionFactory =
                new JedisConnectionFactory(redisClusterConfiguration, redisServer.jedisPoolConfig());
        // 连接池
        jedisConnectionFactory.setPoolConfig(redisServer.jedisPoolConfig());
        // IP地址
        jedisConnectionFactory.setHostName(redisConfig.getHostName());
        // 端口号
        jedisConnectionFactory.setPort(redisConfig.getPort());
        // 如果Redis设置有密码
        jedisConnectionFactory.setPassword(redisConfig.getPassword());
        // 客户端超时时间单位是毫秒
        jedisConnectionFactory.setTimeout(redisConfig.getTimeout());
        return jedisConnectionFactory;
    }

}
