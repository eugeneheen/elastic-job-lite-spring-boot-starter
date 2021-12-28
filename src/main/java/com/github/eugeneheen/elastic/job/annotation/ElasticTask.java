package com.github.eugeneheen.elastic.job.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 定义一个Elastic中的Simple类型Job注解
 *
 * @author ChenZhiHeng
 * @date 2021-12-22
 */
@Component
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticTask {
    /**
     * 定义任务名称，默认为""
     * @return 实际设置的名称
     */
    String jobName() default "";

    /**
     * 定义Cron时间表达式，默认为""
     * @return 实际设置的表达式
     */
    String cron() default "";

    /**
     * 定义任务描述信息，默认为""
     * @return 实际设置的名称
     */
    String description() default "";

    /**
     * 定义任务参数信息，默认为""
     * @return 实际设置的任务参数信息
     */
    String jobParamter() default "";

    /**
     * 定义任务分片数，默认为1
     * @return 实际设置的分任务片数
     */
    int shardingTotalCount() default 1;

    /**
     * 定义任务分片参数，默认为""
     * @return 实际设置的任务分片参数
     */
    String shardingItemParameters() default "";

    /**
     * 设置是否禁用任务分片功能，默认值为false
     * @return 实际设置的值
     */
    boolean disabled() default false;

    /**
     * 配置分片策略，默认为""
     * @return 实际设置的策略值
     */
    String jobShardingStrategyClass() default "";

    /**
     * 配置是否故障转移功能，默认值为false
     * @return 实际设置的值
     */
    boolean failover() default false;

    /**
     * 配置是否在运行重启时，重置任务定义信息（包括Cron时间片设置），默认值为true
     * @return 实际设置的值
     */
    boolean overwrite() default true;

    /**
     * 配置是否开启错误任务重新执行，默认值为true
     * @return 实际设置的值
     */
    boolean misfire() default true;

    /**
     * 配置监听器，默认为""
     * @return 匹配监听器实现类，getType方法的返回值
     */
    String listener() default "";
}
