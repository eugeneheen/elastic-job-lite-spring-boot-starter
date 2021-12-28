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
import java.util.Map;

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
    @Value("${elasticjob.regCenter.namespace}")
    private String namespace;
    @Value("${elasticjob.regCenter.serverLists}")
    private String serverLists;
    private Map<String, String> listeners;
    @Autowired
    private DataSource dataSource;

    /**
     * Zookeeper注册中心配置
     * @return ZookeeperConfiguration
     */
    @Order(1)
    @Bean
    public ZookeeperConfiguration zookeeperConfiguration() {
        return new ZookeeperConfiguration(this.serverLists, this.namespace);
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
        return new ElasticJobBeanPostProcessor(center, this.listeners, this.dataSource);
    }
}
