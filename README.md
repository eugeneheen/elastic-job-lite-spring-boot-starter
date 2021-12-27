# Getting Started

### Reference Documentation
For further reference, please consider the following sections:
- 引入依赖
```xml
<dependency>
    <groupId>com.github.eugeneheen</groupId>
    <artifactId>elastic-job-lite-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
- 配置`Zookeeper`:
```yml
elastic:
  job:
    zk:
      namespace: elastic-job
      serverList: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
```
- 定义作业类
  - 实现`SimpleJob`接口，覆盖`execute(ShardingContext context)`方法。使用`@ElasticJob`注解标注
    ```java
    @ElasticJob(
        jobName = "TestJob",
        cron = "0/5 * * * * ?",
        description = "自定义Task"
    )
    @Component
    public class TestTask implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
      System.out.println(String.format("------Thread ID: %s, 任务总片数: %s, " +
          "当前分片项: %s,当前参数: %s," +
          "当前任务名称: %s,当前任务参数: %s,"+
          "当前任务的id: %s",
          // 获取当前线程的ID
          Thread.currentThread().getId(),
          // 获取任务总片数
          shardingContext.getShardingTotalCount(),
          // 获取当前分片项
          shardingContext.getShardingItem(),
          // 获取当前的参数
          shardingContext.getShardingParameter(),
          // 获取当前的任务名称
          shardingContext.getJobName(),
          // 获取当前任务参数
          shardingContext.getJobParameter(),
          // 获取任务的ID
          shardingContext.getTaskId()
        ));
      }
    }
    ```
  - 实现`DataflowJob`接口，覆盖抓取操作`List<Foo> fetchData(ShardingContext context)`和处理操作`void processData(ShardingContext shardingContext, List<Foo> data)`方法。使用`@ElasticJob`注解标注
    ```java
    @ElasticJob(
        jobName = "FlowJob",
        cron = "0/5 * * * * ?",
        description = "处理数据流Task"
    )
    public class MyElasticJob implements DataflowJob<Foo> {
      @Override
      public List<Foo> fetchData(ShardingContext context) {
        // 获取数据
      }
    
      @Override
      public void processData(ShardingContext shardingContext, List<Foo> data) {
        // 处理数据
      }
    }
    ```
- `@ElasticJob`注解原始定义，使用时参考注解指定相关属性值
  ```java
  @Inherited
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ElasticJob {
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
  }
  ```