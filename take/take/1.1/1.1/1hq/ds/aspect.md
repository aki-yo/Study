



# 动态数据权限切面

你提出了一个很好的问题。让我来解释一下这个过程是如何工作的。

在Java中，当我们将一个对象作为参数传递给方法时，实际上传递的是对象的引用，而不是对象的副本。这意味着在AOP切面中对对象属性的修改会直接影响到原对象。

让我详细解释这个过程：

## 对象引用传递机制

1. **Controller层调用Service方法**：
   ```java
   // Controller中
   public List<SysUser> list(SysUser user) {
       List<SysUser> list = userService.selectUserList(user); // user是方法参数
       return list;
   }
   ```


2. **Service方法被AOP拦截**：
   ```java
   // Service中
   @DataScope(deptAlias = "u.dept_id", dataPowerType = "1")
   public List<SysUser> selectUserList(SysUser user) {
       return userMapper.selectUserList(user); // user作为参数传递给Mapper
   }
   ```


3. **AOP切面处理**：
   ```java
   // DataScopeAspect中
   protected void handleDataScope(final JoinPoint joinPoint) {
       // ...
       BaseEntity orgHidBean = (BaseEntity)joinPoint.getArgs()[0]; // 这里获取的是user对象的引用
       String orgHid = orgHidBean.getOrgHidString();
       dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
               orgHid ,controllerDataScope.dataPowerType());
   }

   public void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias,String orgHid, String dataPowerType) {
       // ...
       if (StringUtils.isNotBlank(sqlString.toString())) {
           BaseEntity baseEntity = (BaseEntity) joinPoint.getArgs()[0]; // 仍然是user对象的引用
           baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")"); // 修改了user对象的params属性
       }
   }
   ```


4. **Mapper中使用修改后的对象**：
   ```java
   // Service中继续执行
   return userMapper.selectUserList(user); // 此时user对象的params属性已经被AOP切面修改
   ```


5. **Mapper XML中使用参数**：
   ```xml
   <select id="selectUserList" parameterType="SysUser" resultMap="SysUserResult">
       <!-- ... -->
       <!-- 数据范围过滤 -->
       ${params.dataScope}
   </select>
   ```


## 关键点说明

1. **引用传递**：Java中对象参数是按引用传递的，因此AOP切面对参数对象的修改会直接影响原对象。

2. **BaseEntity中的params属性**：
   ```java
   public class BaseEntity {
       private Map<String, Object> params = new HashMap<>(); // params属性用于存放动态参数
       
       public Map<String, Object> getParams() {
           if (params == null) {
               params = new HashMap<>();
           }
           return params;
       }
   }
   ```


3. **执行顺序**：
   - AOP切面在Service方法执行前修改了参数对象
   - Service方法继续执行并将修改后的参数传递给Mapper
   - Mapper使用已经被修改的参数对象生成最终的SQL

## 流程图解

```
Controller: user对象 -----------------------> Service: user对象 -----------------------> Mapper: user对象
              |                                 |                                        |
              |                                 |                                        |
              v                                 v                                        v
           调用Service                    AOP切面修改user.params                   使用user.params生成SQL
           (user作为参数)                 (添加dataScope属性)                     (通过${params.dataScope})
```


因此，虽然AOP切面方法没有返回值，但它通过对参数对象的引用修改了对象的属性，这些修改在后续的方法调用中仍然有效。这就是为什么在Mapper中能够访问到`params.dataScope`的原因。












```
@Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("方法调用前: " + joinPoint.getSignature().getName());
    }
```

# 这个logbefore是在什么时候开始调用的，谁让他调用的，怎么自动调用的

# `@Before` 通知的调用机制

`@Before` 是 Spring AOP 中的一种通知（Advice）类型，它会在匹配的方法执行**之前**自动被调用。下面详细解释它的调用机制：

## 调用时机

`logBefore` 方法会在所有 `com.example.service` 包下的任何类的任何方法**执行前**被自动调用。

## 谁让它调用的？

1. **Spring AOP 框架**：当 Spring 容器启动时，它会检测带有 `@Aspect` 注解的类
2. **AOP 代理机制**：Spring 会为匹配的 bean 创建动态代理对象
3. **代理对象**：当调用目标方法时，实际上是先调用代理对象，代理对象再按顺序执行相关的切面逻辑

## 如何实现自动调用？

整个自动调用过程是通过以下步骤实现的：

1. **组件扫描**：
   - `@Component` 让 Spring 发现这个切面类
   - `@Aspect` 标识这是一个切面

2. **代理创建**：
   - Spring 为匹配 `@Pointcut` 的 bean 创建代理对象
   - 当应用调用 `userService.saveUser()` 时，实际上调用的是代理对象的方法

3. **拦截和执行**：
   ```java
   // 伪代码展示代理机制
   public class UserServiceProxy extends UserService {
       private UserService target;
       private LoggingAspect loggingAspect;
       
       @Override
       public void saveUser(User user) {
           // 1. 执行前置通知
           loggingAspect.logBefore(this, "saveUser");
           
           // 2. 调用实际方法
           target.saveUser(user);
           
           // 3. 如果有后置通知也会在这里执行
       }
   }
   ```

4. **织入时机**：
   - **编译时织入**：AspectJ 可以在编译时修改字节码
   - **运行时织入**：Spring AOP 默认使用动态代理（JDK 或 CGLIB）

## 实际调用栈示例

```
1. 你的代码调用: userService.saveUser()
2. → Spring 代理对象拦截调用
3. → 执行所有符合条件的 @Before 通知
4. → 调用实际的 userService.saveUser() 实现
5. → 如果有 @After 或 @AfterReturning 通知，继续执行
```

## 验证方式

你可以在切面方法中添加调试信息来观察：

```java
@Before("execution(* com.example.service.*.*(..))")
public void logBefore(JoinPoint joinPoint) {
    System.out.println("【AOP调试】调用前拦截: " + joinPoint.getSignature());
    System.out.println("【AOP调试】目标类: " + joinPoint.getTarget().getClass());
    System.out.println("【AOP调试】代理类: " + joinPoint.getThis().getClass());
}
```

这会显示实际的代理类和目标类，帮助你理解 AOP 的工作原理。



# @Before("@annotation(dataScope)")

那么这种，是检测到什么开始调用这个Befor方法的

# `@Before("@annotation(dataScope)")` 的调用机制

这种形式的 `@Before` 通知是基于自定义注解触发的，它的调用机制如下：

## 触发条件

当方法上标注了特定的注解时，这个 `@Before` 通知会被触发。具体来说：

1. **检测目标**：检查即将执行的方法是否带有 `@DataScope` 注解
2. **注解匹配**：`dataScope` 是切点表达式中的变量名，需要与方法参数名一致
3. **执行时机**：在带有 `@DataScope` 注解的方法**执行前**调用

## 典型使用场景

这种模式常用于需要基于注解进行AOP拦截的场景，例如：

- 数据权限控制(`@DataScope`)
- 特殊日志记录(`@AuditLog`)
- 性能监控(`@Monitor`)
- 分布式锁(`@DistributedLock`)

## 完整示例代码

```java
// 1. 首先定义自定义注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {
    String deptAlias() default "";
    String userAlias() default "";
}

// 2. 定义切面
@Aspect
@Component
public class DataScopeAspect {
    
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint joinPoint, DataScope dataScope) {
        // 可以获取注解的属性值
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();
        
        System.out.println("拦截到数据权限注解操作");
        System.out.println("部门别名: " + deptAlias);
        System.out.println("用户别名: " + userAlias);
        
        // 实际业务中通常在这里处理数据权限过滤
        // 比如根据用户权限动态修改SQL
    }
}

// 3. 在Service方法上使用注解
@Service
public class UserService {
    
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<User> selectUserList(User user) {
        // 方法执行前会先触发doBefore通知
        // 实际业务逻辑...
    }
}
```

## 调用流程详解

1. **方法调用**：当调用 `userService.selectUserList()` 时
2. **代理拦截**：Spring AOP代理检测到方法上有 `@DataScope` 注解
3. **切面执行**：
   - 先执行 `doBefore` 方法
   - 将方法上的 `@DataScope` 注解对象作为参数传入
4. **原方法执行**：切面执行完成后，继续执行原方法

## 与其他切点表达式的区别

| 表达式类型 | 示例 | 触发条件 |
|------------|------|----------|
| 方法执行 | `execution(* com..service.*.*(..))` | 匹配方法签名 |
| 注解匹配 | `@annotation(com.example.DataScope)` | 匹配方法上的注解 |
| bean名称 | `bean(userService)` | 匹配bean名称 |

这种基于注解的AOP方式使拦截逻辑更加清晰和精确，特别适合需要针对特定方法进行特殊处理的场景。



# String deptAlias() default "";
    String userAlias() default "";

@DataScope(deptAlias = "d", userAlias = "u")

这部分是干什么的


# `@DataScope` 注解及其属性的作用

这部分代码定义了一个自定义注解 `@DataScope` 并设置了它的属性，主要用于实现**数据权限控制**的功能。下面详细解释每个部分的作用：

## 注解定义部分

```java
String deptAlias() default "";
String userAlias() default "";
```

这部分定义了注解的两个属性：

1. `deptAlias` - 部门表别名
   - 类型：String
   - 默认值：空字符串
   - 用途：指定SQL中部门表的别名

2. `userAlias` - 用户表别名
   - 类型：String
   - 默认值：空字符串
   - 用途：指定SQL中用户表的别名

## 注解使用部分

```java
@DataScope(deptAlias = "d", userAlias = "u")
```

这是在具体方法上使用注解的示例，表示：

- 该方法需要数据权限过滤
- 部门表在SQL中使用别名 `d`
- 用户表在SQL中使用别名 `u`

## 完整工作流程

1. **定义注解**：创建 `@DataScope` 注解并定义可配置属性
2. **标记方法**：在需要数据权限控制的方法上添加该注解
3. **AOP拦截**：切面检测到带有 `@DataScope` 注解的方法调用
4. **权限处理**：切面根据当前用户权限动态修改SQL
   - 添加部门数据过滤条件：`AND d.dept_id IN (用户可访问的部门ID列表)`
   - 添加用户数据过滤条件：`AND u.user_id = 当前用户ID`

## 实际应用场景示例

```java
@Mapper
public interface UserMapper {
    @DataScope(deptAlias = "u", userAlias = "u")
    @Select("SELECT * FROM sys_user u WHERE u.status = '0'")
    List<User> selectAllUsers();
}

// 切面中会根据注解配置自动追加SQL条件：
// 原始SQL: SELECT * FROM sys_user u WHERE u.status = '0'
// 切面处理后可能变成:
// SELECT * FROM sys_user u 
// WHERE u.status = '0' 
// AND u.dept_id IN (100, 101, 102)  // 当前用户有权限的部门
// AND u.user_id = 12345              // 或者根据用户权限追加其他条件
```

## 为什么需要别名？

1. **SQL可读性**：在多表关联查询时使用短别名
2. **灵活性**：同一个表在不同SQL中可以使用不同别名
3. **防止冲突**：当多个表有相同字段名时避免歧义

这种设计常见于需要细粒度数据权限控制的系统，如：
- 多租户SaaS系统
- 大型企业ERP系统
- 政府/金融行业系统


![](assets/Pasted%20image%2020250704133424.png)




# @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult)")
@AfterThrowing(pointcut = "@annotation(controllerLog)", throwing = "e)")
这两个注解方法详解


# Spring AOP `@AfterReturning` 和 `@AfterThrowing` 注解详解

这两个注解是Spring AOP中用于方法执行后处理的通知(Advice)类型，下面我将详细解析它们的用法和区别：

## 一、`@AfterReturning` - 返回后通知

### 1. 基本定义
```java
@AfterReturning(
    pointcut = "@annotation(controllerLog)", 
    returning = "jsonResult"
)
public void doAfterReturning(Log controllerLog, Object jsonResult) {
    // 方法成功返回后执行的逻辑
}
```

### 2. 核心特性
- **触发时机**：目标方法**成功执行完成**（未抛出异常）后执行
- **参数获取**：
  - `pointcut`：指定切入点表达式
  - `returning`：指定接收返回值的参数名（需与方法参数名一致）
- **返回值访问**：通过`returning`指定的参数获取方法返回值
- **无法修改返回值**：虽然能获取返回值，但不能改变返回结果

### 3. 使用场景
```java
// 记录成功日志
@AfterReturning(pointcut="@annotation(log)", returning="result")
public void logSuccess(Log log, Object result) {
    auditService.recordSuccess(
        log.operation(), 
        System.currentTimeMillis(), 
        result
    );
}

// 统计方法执行时长
@Around("@annotation(monitored)")
public Object measurePerformance(ProceedingJoinPoint pjp, Monitored monitored) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = pjp.proceed();
    long duration = System.currentTimeMillis() - start;
    metricsService.record(monitored.value(), duration);
    return result;
}
```

## 二、`@AfterThrowing` - 异常抛出后通知

### 1. 基本定义
```java
@AfterThrowing(
    pointcut = "@annotation(controllerLog)", 
    throwing = "e"
)
public void doAfterThrowing(Log controllerLog, Exception e) {
    // 方法抛出异常后执行的逻辑
}
```

### 2. 核心特性
- **触发时机**：目标方法**抛出异常**后执行
- **参数获取**：
  - `pointcut`：指定切入点表达式
  - `throwing`：指定接收异常的参数名（需与方法参数名一致）
- **异常处理**：
  - 能获取抛出的异常对象
  - **不能捕获或处理异常**（异常仍会传播到调用方）
- **异常类型过滤**：可通过参数类型限定特定异常（如`NullPointerException e`）

### 3. 使用场景
```java
// 记录失败日志
@AfterThrowing(pointcut="@annotation(log)", throwing="ex")
public void logFailure(Log log, Exception ex) {
    auditService.recordFailure(
        log.operation(),
        ex.getClass().getSimpleName(),
        ex.getMessage()
    );
}

// 异常监控报警
@AfterThrowing(pointcut="execution(* com..service.*.*(..))", throwing="e")
public void monitorException(Exception e) {
    alertService.sendCriticalAlert(
        "Service Exception", 
        e.getMessage(), 
        LocalDateTime.now()
    );
}
```

## 三、关键对比

| 特性                | @AfterReturning                     | @AfterThrowing                     |
|---------------------|-------------------------------------|------------------------------------|
| **触发条件**         | 方法正常返回时                      | 方法抛出异常时                     |
| **获取返回值**       | 通过`returning`参数                 | 无                                |
| **获取异常**         | 无                                  | 通过`throwing`参数                |
| **能否修改结果**     | 否                                  | 否                                |
| **能否阻止异常传播** | 不适用                              | 否                                |
| **执行顺序**         | 在@After之后，@Around的最终返回之前 | 在@After之后，异常抛出到调用方之前 |

## 四、组合使用示例

```java
@Aspect
@Component
public class OperationMonitorAspect {

    // 成功时记录
    @AfterReturning(pointcut="@annotation(monitored)", returning="result")
    public void logSuccess(Monitored monitored, Object result) {
        log.info("[操作成功] {} 返回: {}", monitored.value(), result);
    }

    // 失败时记录
    @AfterThrowing(pointcut="@annotation(monitored)", throwing="ex")
    public void logFailure(Monitored monitored, Exception ex) {
        log.error("[操作失败] {} 异常: {}", monitored.value(), ex.getMessage());
    }

    // 无论成功失败都执行
    @After("@annotation(monitored)")
    public void doAfter(Monitored monitored) {
        log.debug("[操作完成] {}", monitored.value());
    }
}
```

## 五、技术细节

1. **连接点信息获取**：
   ```java
   @AfterReturning(pointcut="@annotation(log)", returning="result")
   public void afterReturning(JoinPoint jp, Log log, Object result) {
       String methodName = jp.getSignature().getName();
       Object[] args = jp.getArgs();
       // ...
   }
   ```

2. **异常类型限定**：
   ```java
   // 只捕获IOException
   @AfterThrowing(pointcut="execution(* com..FileService.*(..))", 
                 throwing="ioEx")
   public void handleIO(IOException ioEx) {
       // 特定异常处理
   }
   ```

3. **注解继承问题**：
   - 默认不支持继承父类/接口上的注解
   - 需配合`@Inherited`元注解使用

4. **性能考虑**：
   - 简单的日志记录建议使用`@AfterReturning/@AfterThrowing`
   - 复杂的逻辑（如事务管理）应使用`@Around`

这两个通知类型与`@Before`、`@After`和`@Around`共同构成了Spring AOP的完整通知体系，使开发者能够针对方法执行的不同阶段插入横切逻辑。正确使用这些注解可以大大提高系统的可维护性和可观测性。


# 繁
![](assets/Pasted%20image%2020250707081215.png)



# @within
@Pointcut("@annotation(com.o.framework.aspectj.lang.annotation.DataSource)"
            + "|| @within(com.o.framework.aspectj.lang.annotation.DataSource)")
什么意思
这段代码定义了一个 **Spring AOP 切点(Pointcut)**，用于匹配带有 `@DataSource` 注解的类或方法。以下是详细解析：

---

### **代码结构解析**
```java
@Pointcut("@annotation(com.o.framework.aspectj.lang.annotation.DataSource)" 
         + "|| @within(com.o.framework.aspectj.lang.annotation.DataSource)")
```
这是两个条件的 **逻辑或(OR)** 组合：
1. `@annotation`：匹配 **方法级别** 的 `@DataSource` 注解
2. `@within`：匹配 **类级别** 的 `@DataSource` 注解

---

### **1. `@annotation` 部分**
```java
@annotation(com.o.framework.aspectj.lang.annotation.DataSource)
```
- **作用**：匹配 **被`@DataSource`注解修饰的方法**  
- **示例**：
  ```java
  public class UserService {
      @DataSource(name = "slaveDb") // 会被切点匹配
      public void queryUser() { ... }
  }
  ```

---

### **2. `@within` 部分**
```java
@within(com.o.framework.aspectj.lang.annotation.DataSource)
```
- **作用**：匹配 **类/接口上存在`@DataSource`注解的所有方法**  
- **示例**：
  ```java
  @DataSource(name = "masterDb") // 类中所有方法都会被匹配
  public class OrderService {
      public void createOrder() { ... } // 会被切点匹配
      public void cancelOrder() { ... } // 会被切点匹配
  }
  ```

---

### **`||` 操作符**
- 表示 **逻辑或**，满足任意一个条件即可触发切面逻辑
- 最终效果：  
  匹配 **类或方法上有`@DataSource`注解的所有方法**

---

### **典型应用场景**
结合代码中的包路径 `com.o.framework.aspectj.lang.annotation.DataSource`，可以推断这是用于 **动态数据源切换** 的切面，例如：
1. 方法/类注解指定数据源名称（如 `@DataSource("slave")`）
2. 切面在方法执行前切换数据源
3. 方法执行后恢复默认数据源

---

### **对比其他切点表达式**
| 表达式类型          | 示例                                | 作用                          |
|---------------------|-----------------------------------|-----------------------------|
| `@annotation`       | `@annotation(com.example.A)`      | 匹配方法级别的注解               |
| `@within`           | `@within(com.example.A)`          | 匹配类级别的注解及其所有方法       |
| `@target`           | `@target(com.example.A)`          | 匹配目标对象类型上的注解（运行时） |
| `@args`             | `@args(com.example.A)`            | 匹配方法参数上的注解             |

---

### **注意事项**
1. **注解路径必须全匹配**：  
   如果注解路径错误（如包名修改），切点会失效
2. **性能影响**：  
   过于宽泛的切点（如 `@within`）可能匹配到不需要的方法
3. **与其他切点的组合**：  
   可通过 `&&`、`!` 等操作符组合更复杂的条件

这种切点定义方式在 **多数据源切换**、**审计日志**、**权限控制** 等场景中非常常见。
















# end
