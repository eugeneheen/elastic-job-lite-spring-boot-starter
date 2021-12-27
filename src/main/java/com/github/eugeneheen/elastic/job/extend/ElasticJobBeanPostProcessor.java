package com.github.eugeneheen.elastic.job.extend;

import com.github.eugeneheen.elastic.job.annotation.ElasticTask;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 完成ElasticJob的初始化
 *
 * @author ChenZhiHeng
 * @date 2021-12-22
 */
public class ElasticJobBeanPostProcessor implements BeanPostProcessor, DisposableBean {

    /**
     * Zookeeper注册器
     */
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    private List<ScheduleJobBootstrap> schedules = new ArrayList<>();

    public ElasticJobBeanPostProcessor(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        // 处理自定义注解
        if (!clazz.isAnnotationPresent(ElasticTask.class)) {
            return bean;
        }

        if (!(bean instanceof ElasticJob)) {
            return bean;
        }

        ElasticJob job = (ElasticJob) bean;
        ElasticTask annotation = clazz.getAnnotation(ElasticTask.class);
        String jobName = annotation.jobName();
        String cron = annotation.cron();
        String description = annotation.description();
        String jobParamter = annotation.jobParamter();
        int shardingTotalCount = annotation.shardingTotalCount();
        String shardingItemParameters = annotation.shardingItemParameters();
        boolean disabled = annotation.disabled();
        String jobShardingStrategyClass = annotation.jobShardingStrategyClass();
        boolean failover = annotation.failover();
        boolean overwrite = annotation.overwrite();
        boolean misfire = annotation.misfire();

        // 根据自定义注解配置ElasticJob任务的配置
        JobConfiguration jobConfiguration = JobConfiguration.newBuilder(jobName, shardingTotalCount)
                .cron(cron)
                .jobParameter(jobParamter)
                .overwrite(overwrite)
                .failover(failover)
                .misfire(misfire)
                .description(description)
                .shardingItemParameters(shardingItemParameters)
                .jobShardingStrategyType(jobShardingStrategyClass)
                .disabled(disabled)
                .build();

        // 创建任务调度对象
        ScheduleJobBootstrap scheduleJobBootstrap = new ScheduleJobBootstrap(this.zookeeperRegistryCenter, job, jobConfiguration);
        // 触发任务调度
        scheduleJobBootstrap.schedule();
        // 创建的任务调度对象放入集合，便于统一销毁
        this.schedules.add(scheduleJobBootstrap);

        return job;
    }

    @Override
    public void destroy() throws Exception {
        this.schedules.stream().forEach(scheduleJobBootstrap -> {
            scheduleJobBootstrap.shutdown();
        });
    }
}
