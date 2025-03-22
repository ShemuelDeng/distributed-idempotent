### 组件特点

1. **基于AOP实现**：通过`@Idempotent`注解实现方法级幂等控制
2. **多种策略支持**：
- 内置`DEFAULT`（Redis存储）和`CUSTOM`（自定义持久化）两种幂等策略
- 提供`NULL`（返回空）、`PREVIOUS`（返回前次结果）两种响应策略
3. **分布式锁机制**：采用Redisson实现分布式锁，支持快速失败（failFast）和阻塞等待两种模式
4. **可扩展性**：支持自定义`IdemHandler`和`RespHandler`
5. **自动装配**：通过`IdempotentAutoConfiguration`实现Spring Boot Starter的自动配置

### 核心类作用说明

#### 1. 配置类（configuration包）

| 类名  | 作用  |
| --- | --- |
| `IdempotentAutoConfiguration` | 自动配置入口 |
| `IdempotentRedisConfiguration` | Redisson配置 |
| `IdempotentProperty` | 配置属性映射 |

#### 2. AOP核心（aop包）

| 类名  | 作用  |
| --- | --- |
| `IdempotentAspect` | 切面逻辑实现 |
| `Idempotent` | 幂等注解定义 |

#### 3. 核心执行逻辑（executor包）

| 类名  | 作用  |
| --- | --- |
| `IdempotentExecutor` | 幂等执行引擎 |

### 使用方式

#### 1. 添加依赖（pom.xml）

```xml
<dependency>
    <groupId>com.shemuel</groupId>
    <artifactId>idempotent-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. 配置属性（application.yml）

```yaml
idempotent:
  enable: true
  env: ${spring.profiles.active}
  redis:
    host: 127.0.0.1
    port: 6379
    password: 
    database: 0
```

#### 3. 注解使用示例

```java
@Idempotent(
    columns = {"#user.id", "#orderNo"}, // 组合幂等键
    duration = 5, // 有效期5秒
    failFast = true, // 快速失败模式
    custom = @Strategy(
        idemStrategy = IdemStrategy.DEFAULT, // 使用Redis策略
        respStrategy = RespStrategy.PREVIOUS // 返回前次结果
    )
)
public Order createOrder(User user, String orderNo) {
    // 业务逻辑
}
```

### 执行流程说明

1. **切面拦截**：`IdempotentAspect`拦截被注解的方法
2. **构建上下文**：生成`IdempotentContext`包含方法签名、参数等信息
3. **获取分布式锁**：通过Redisson获取基于`方法名+参数哈希`的分布式锁
4. **策略执行**：
- `DEFAULT`策略：检查Redis中是否存在缓存结果
- `CUSTOM`策略：调用自定义处理器
5. **结果处理**：根据策略返回缓存结果或执行实际业务逻辑