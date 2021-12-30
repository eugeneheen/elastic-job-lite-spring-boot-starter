# 使用说明

***此工程实现的依赖，必须结合SpringBoot使用***

## 1 集成本工程实现的SpringBoot Starter

### 1.1 集成说明
- 源码安装
  ```bash
  $ git clone https://github.com/eugeneheen/elastic-job-lite-spring-boot-starter.git
  $ mvn clean install
  ```

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
    regCenter:
      namespace: elastic-job
      serverList: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
  ```
### 1.2 定义作业类
- 实现`SimpleJob`接口，覆盖`execute(ShardingContext context)`方法。使用`@ElasticJob`注解标注
  ```java
  @ElasticJob(
      jobName = "TestJob",
      cron = "0/5 * * * * ?",
      description = "自定义Task"
  )
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
## 2 直接使用SpringBoot集成

### 2.1 集成说明
- 注意，直接使用SpringBoot集成，时不能引入`1节`由本工程实现的`SpringBoot Starter`，因为我们是使用的官方实现
- 引入依赖
  ```xml
  <dependency>
    <groupId>org.apache.shardingsphere.elasticjob</groupId>
    <artifactId>elasticjob-lite-spring-boot-starter</artifactId>
    <version>3.0.0-RC1</version>
  </dependency>
  ```
### 2.2 定义作业类
- 实现`SimpleJob`接口，覆盖`execute(ShardingContext context)`方法。使用`@ElasticJob`注解标注
  ```java
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
  @Component
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
### 2.3 配置Job

- 在`application.yml`配置文件中，增加以下配置
  ```yaml
  elasticjob:
  regCenter:
    #zookeeper 的ip:port
    serverLists: 10.11.27.99:2181
    #名命空间，自己定义就好了
    namespace: es-hs-job
  jobs:
    #你的这个定时任务名称，自定义名称
    myElasticJob:
      #定时任务的全路径名
      elasticJobClass: com.github.eugeneheen.job.task.TestTask
      #定时任务执行的cron表达式
      cron: 0/5 * * * * ?
      #分片数量
      shardingTotalCount: 10
      overwrite: true
  ```
## 3 定义作业监听器

### 3.1 监听器说明
- 作业监听器，用于在任务执行前和执行后执行监听的方法。
- 监听器分为每台作业节点均执行的常规监听器和分布式场景中仅单一节点执行的分布式监听器。
- 应尽量使用常规监听器

### 3.2 开发监听器

- Step 1: 创建监听器，可通过实现`ElasticJobListener`实现`常规监听器`。也可通过继承`AbstractDistributeOnceElasticJobListener`实现`分布式监听器`
  - 常规监听器
    ```java
    public class MyJobListener implements ElasticJobListener {
    
      @Override
      public void beforeJobExecuted(ShardingContexts shardingContexts) {
        // do something ...
      }
    
      @Override
      public void afterJobExecuted(ShardingContexts shardingContexts) {
        // do something ...
      }
    
      @Override
      public String getType() {
        return "simpleJobListener";
      }
    }
    ```
  - 分布式监听器
    ```java
    public class MyDistributeOnceJobListener extends AbstractDistributeOnceElasticJobListener {
    
      private static final long startTimeoutMills = 3000;
      private static final long completeTimeoutMills = 3000;

      public MyDistributeOnceJobListener() {
        super(startTimeoutMills, completeTimeoutMills);
      }
    
    
      @Override
      public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        // do something ...
      }
    
        @Override
        public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        // do something ...
        }
    
      @Override
      public String getType() {
        return "distributeOnceJobListener";
      }
    }
    ```
- Step 2: 添加`SPI`实现，在工程下`resources/META-INF/services/`目录下，创建文件名为`org.apache.shardingsphere.elasticjob.infra.listener.ElasticJobListener`的文件，并将`监听器实现类`的`全包类名`添加到该文件，多个时换行
  ```text
  com.yongyou.es.hs.job.listener.MyJobListener
  com.yongyou.es.hs.job.listener.MyDistributeOnceJobListener
  ```
- Step 3: 定义作业类时，通过`@ElasticJob`注解的`listener`属性定义需要指定的`监听器`。该值匹配`监听器实现类`中的`getType`方法返回的值。
  ```java
  @ElasticJob(
      jobName = "TestJob",
      cron = "0/5 * * * * ?",
      description = "自定义Task",
      listener = "simpleJobListener"
  )
  ```
# 关于启动异常

- 启动时，可能发生异常，如果看到下列异常，是由于`Zookeeper`连接超时造成。通过在`.yml`或`properties`配置文件，增大`elasticjob.regCenter.baseSleepTimeMilliseconds`(默认值1000ms)和`elasticjob.regCenter.maxSleepTimeMilliseconds`(默认值3000ms)的值设置
  - Invocation of init method failed;
  - nested exception is org.apache.shardingsphere.elasticjob.reg.exception.RegException:
  - org.apache.zookeeper.KeeperException$OperationTimeoutException:
  - KeeperErrorCode = OperationTimeout
- 配置示例
  ```yaml
  elasticjob:
    regCenter:
        namespace: elastic-job
        serverList: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
        baseSleepTimeMilliseconds: 3000
        maxSleepTimeMilliseconds: 5000
  ```  