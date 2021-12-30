package com.github.eugeneheen.elastic.job.config;

import com.github.eugeneheen.elastic.job.extend.ElasticJobBeanPostProcessor;
import lombok.Data;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * ElasticJob配置类
 *
 * @author ChenZhiHeng
 * @date 2021-12-22
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "elasticjob.regCenter", name = {"namespace", "serverLists"})
@ConfigurationProperties(prefix = "elasticjob")
public class ElasticJobConf {
    @Value("${elasticjob.regCenter.namespace:localhost:2181}")
    private String namespace;
    @Value("${elasticjob.regCenter.serverLists:elastic-job}")
    private String serverLists;
    @Value("${elasticjob.regCenter.baseSleepTimeMilliseconds:1000}")
    private int baseSleepTimeMilliseconds;
    @Value("${elasticjob.regCenter.maxSleepTimeMilliseconds:3000}")
    private int maxSleepTimeMilliseconds;
    @Value("${elasticjob.regCenter.maxRetries:3}")
    private int maxRetries;
    @Value("${elasticjob.regCenter.sessionTimeoutMilliseconds:0}")
    private int sessionTimeoutMilliseconds;
    @Value("${elasticjob.regCenter.connectionTimeoutMilliseconds:0}")
    private int connectionTimeoutMilliseconds;

    @Autowired
    private DataSource dataSource;

    /**
     * Zookeeper注册中心配置
     * @return ZookeeperConfiguration
     */
    @Order(1)
    @Bean
    public ZookeeperConfiguration zookeeperConfiguration() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(this.serverLists, this.namespace);
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(this.baseSleepTimeMilliseconds);
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(this.maxSleepTimeMilliseconds);
        zookeeperConfiguration.setMaxRetries(this.maxRetries);
        zookeeperConfiguration.setSessionTimeoutMilliseconds(this.sessionTimeoutMilliseconds);
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(this.connectionTimeoutMilliseconds);
        return zookeeperConfiguration;
    }

    /**
     * 初始化注册信息
     * @return CoordinatorRegistryCenter
     */
    @Order(2)
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(ZookeeperConfiguration configuration) {
        return new ZookeeperRegistryCenter(configuration);
    }

    /**
     * 配合处理自定义ElasticJob任务的处理逻辑类
     * @param center 注册信息
     * @return ElasticJobBeanPostProcessor
     */
    @Order(3)
    @Bean
    public ElasticJobBeanPostProcessor elasticJobBeanPostProcessor(ZookeeperRegistryCenter center) {
        return new ElasticJobBeanPostProcessor(center, this.dataSource);
    }
}
