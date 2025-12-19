
# boama

他喵的就是主键索引存储整个数据行，普通索引等只存储索引列值和主键值，
如果你要查的数据就是索引列值就不需要主键值回表查其他列数据，这样就是覆盖索引

索引下推就是 传统就是直接根据主键回表
这个是先根据索引过滤，剩下之后在主键回表

读未提交                           行锁共享锁
读已提交 解决脏读                     行锁排他锁
可重复读 解决不可重复读   第一次读的时候生成readview之后的查询复用这个从而避免不可重复读
串行化 解决幻读                       间隙锁 进行范围查询的时候加上间隙锁，中间不能插入数据

第一次读的时候生成readview之后的查询复用这个从而避免不可重复读
这个有可能会读不到最新的数据，但不是脏读，脏读是读到未提交的数据。

![](assets/Pasted%20image%2020251112195650.png)

写接口

简单的八股

面试问题提前想

英语自我介绍

反正大概率不去

试试呗

对还有笔试题


问 接口设计成哪几种形式 ，restful 还是 。/{id}这种
我记得还有一种，
@reponsebody
@requestparam
@pathvariable

工作中的难题
如密码校验
依赖问题，通过查看github官方说明

英语自我介绍

模板
代码生成，或者是文件模板




我被问到几个问题，你可以参考一下:实习中做了什么事？具体都做了那些事情？实习过程中有遇到哪些难点，怎么解决的？追问实习做事情的细节是什么处理的，比如发邮件，邮件模板怎么匹配？在实习过程中你最大的收获是什么？  
  
作者：Heartbeat_henu  
链接：[https://www.nowcoder.com/feed/main/detail/320f207337c94dba8b63ac2af78ea506?anchorPoint=comment](https://www.nowcoder.com/feed/main/detail/320f207337c94dba8b63ac2af78ea506?anchorPoint=comment)  
来源：牛客网
git@github.com:aki-yo/bmw.git

```
11.14的第三批线下面试，Java后端开发，面试内容包括上午的小组合作和下午的终面。  
【上午】  
1.上午小组合作写一个需求，每组大约有10人，包括前端、后端和测试人员，后端开发最多。淘汰率大概60%，时间两个多小时。  
2.开发环境都提前准备好了，使用Git进行团队协作，IDEA作为编辑器，SSM框架来开发接口。数据库连接工具是DBeaver，MyBatisPlus框架将用于数据库操作。  
3. 小组合作需要我们自己交流，类似无领导小组讨论，明确需要实现功能和每个人的职责划分，需求大概是实现与宝马相关的某个功能的CURD操作，例如开发一个购车页面并提供订单查询功能。要自己创建数据库表，后端目标是把需要接口跑通，查出数据。  
4. 全程会有两位面试官在旁指导，解释需要达成的效果和注意事项。如果有任何问题，可以随时提问，面试官会耐心解答。  
5.考察更多的是团队合作能力，要积极交流，时间若是不够，接口没有开发完成，可以讲一下实现思路。时间到后需要每个人总结个人工作，展示接口功能。最后面试官让我们做补充，或者给功能提出优化建议。  
6.建议：熟练使用SpringBoot框架进行接口开发，积极发言并参与团队协作。  
【下午】  
1.一点半开始分组进行终面，会有三位面试官，包括 hr、大领导和部门领导，偏向综合面试，开放性问题较多。  
2.上来先做个英文自我介绍，再问一些简历上内容，如项目经历、实习经历，以及遇到的困难如何解决的、对公司的了解、求职意向，最后是反问环节。面试官都很好，亲切和蔼，不会的没关系，不用紧张。  
  

```

我经历了众多项目，确实遇到了很多困难，但是也承认学习到了不少内容，就像我刚才自我介绍的时候说的动态菜单路由，国际化，自定义密码校验等等。


邮件模板就是templateengin.proccess（templatename,context)去资源目录下面找到这个templatename的模板文件，然后把context里面的setVariables键值对放到模板文件里面

代码生成模板是
```java
1. **引擎初始化**：
    
    VelocityInitializer.initVelocity(); // 加载classpath下的vm文件，设置UTF-8编码
    
2. 
    
    **准备模板上下文**：
    
    VelocityContext context = VelocityUtils.prepareContext(table);
    
    // 注入表名、类名、字段列表等变量到上下文
    
3. 
    
    **获取模板列表**：

    
    List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory
    
    ());
    
    // 根据模板类型(crud/tree)获取对应的模板文件列表
    
4. 
    
    **渲染模板生成代码**：
    
    Template tpl = Velocity.getTemplate(template, Constants.UTF8);
    
    tpl.merge(context, sw); // 将上下文数据合并到模板中
```







项目中的困难seckil
业务中的困难密码校验

公司的了解
：灵越给南京宝马提供技术支持，IT解决方案，

求职意向主要就是想主要在江苏这个范围，寻找关于java开发的职位


## seckill
问题
多node环境问题
nvm-windows工具

lombok注解解释器问题
需要进去设置 或者排除掉maven中的那个注解解释器插件

分布式锁和lua脚本的选择
本来因为有数据库和redis的双操作导致用的分布式锁大部分失败
所以改成lua脚本+预加载，这样就达到了大概100的qps

版本依赖问题
mybatisplus中用的是mybatisspring 2.几的版本
但是用的是springboot3
导致mybatis中赋值的string类型到springboot中类型不兼容抛出异常
然后看mybais官方给的更新日志，看到从一个版本开始，就开始用的mybatispring3了。所以升级这个版本即可
还有一种办法就是在maven中添加依赖的时候 用 exculsions标签来排除其中用的低版本mybaitspring2
再导入高版本mybaitspring3
就好了
哦，还不一定，需要rebuild才行

rabbit 消息监听器执行失败， 配置消息体用jackson序列化即可

超卖问题，因为创建订单减少库存的时候直接先查后减，没有原子性，导致另一边在恢复库存的时候这边直接再减少库存，导致恢复的库存直接被覆盖了，后来用了setsql解决了

还需要 支付订单 超时取消订单， 手动取消订单 三个地方建立订单的分布式锁
![](assets/Pasted%20image%2020251103111248.png)


架构流程

访问商品列表 商品详情 抢购 状态查询 
项目启动
库存预加载
然后抢购过程就是 
	发送请求，sentinel限流，
	到服务层
	lua脚本 查redis库存 减少redis库存
	生成一个时间戳+随机数的 订单号
	异步发送mq创建订单消息，监听队列到消费者实际执行数据库库存减少逻辑
	这个消费者创建完订单后再发送一个订单超时取消 延迟消息到延迟队列
	等到时间到了，如果还未支付，同时redis库存回滚，数据库库存回滚，订单状态修改
	如果已经支付，就过去呗
	注意点
		定时任务进行数据库和redis数据同步
		回滚的时候进行redis分布式锁➕事务管理，
		setsql(stork=stock+1)  transactionTemplate.execute(status -> {
		

异步的目的
也就是为了能承担更大量的并发请求
以及提高用户体验流程度
避免数据库崩溃


扩展：
分布式
微服务：商品服务，订单服务，秒杀服务

父工程

eureka_server
服务注册中心

各个服务方

总结各个服务的客户端服务，feign代理请求到各个服务


## 密码校验需求

项目背景

原有项目需要放到印度进行使用
因为技术设置差异等需要本次的项目密码校验规则改成与另外的一个项目的密码校验相同，并且修改密码的时候需要同时对两个项目的mysql oracle同时进行修改

本项目是bcaypt加密
另外的项目是md5加密

本来就兴冲冲的本项目中引入了个md5的密码加密依赖包试了试和另一个项目确实加密结果一样的
以为接下来就是简单的进行几行的修改即可
确实，重置密码的地方确实只需要把原有的springsecurity中的bcrypt加密调用改成md5的加密调用即可
可是登录检验的地方犯了难

原有的登录校验我可以理解
就是实现了springcecurity的几个接口，自定义了密码校验规则
有userservcie userdetail
通过实现了usersevice 的类的重写方法从数据库中 得到实现了userdietail接口的数据库中存储的用户名和密码，
然后再将前端传递过来的密码进行加密对比即可

是的看着很简单，然后我就很自信的把找到的bcrypt的bean相关配置bcryptpasswordencoder的地方加入了自己的md5的bena配置，
并且在authenticationmanagerbuilder.userdetailservice.passwordencoder中传入了自己的配置的mde5的passwordencoder bean
好了这个时候应该用原来的密码登录会失败，但是我进去了，我居然进去了，头一次会因为登录成功而失望
然后我打了断点，发现自己的md5根本就没有被执行，这也是我把原有的bcrypt配置删掉也不行的原因

然后找来找去发现另一个地方
CustomLoginAuthenticationProvider extends DaoAuthenticationProvider
这个里面手动的实现了
additionalAuthenticationChecks方法
里面手动的实现了用bcrypt加密前端传递来的密码 来 对比数据库中取到的密码
ok我知道，原来是这个地方，
我就把原来的地方改回去，在这个地方，改成手动的用md5进行加密对比，match
ok再次启动测试发现还是能登陆进去，我懵了，我打断点，诶，确实走到这个地方了，确实判断失败抛出异常了，结果还是进去了，这不对吧
总不能这代码写的不对吧，这系统用好长时间了
然后试了试完全不一样的密码登录确实不对，没登录成功
那我就猜想到了，应该是还是采用的bcrypt加密校验方式通过进去的
但是我这里不是确实执行到了确实抛出异常了啊
就很烦，这就是最难受的地方，然后就来回调试，实在不行，进源码看看
然后发现端倪
确实抛出异常之后又到了bcrypt的内置的密码校验步骤，
所以这里有两次密码校验，
这是怎么回事怎么会有两次不同的密码校验方式
然后就发现了有个
this.getProviders().iterator()
我去这不是迭代器吗
这不就像我想的多个密码校验方式循环进行密码校验吗，
然后我调试的时候进行表达式求值确实发现这个provider里面有两个密码校验方式一个是我的md5一个是bcrypt校验，
我知道了为什么会登录成功，但不知道为什么会有两个校验方式，总不能是默认就有的，然后再这个类里面找这个provider的定义位置初始化位置，打上断点，再调试，然后看方法调用栈向前找，来回打断点找方法，给我找到了，原来是我最开始注意的的就我之前配置的md5bean 的那个地方我该回去了原来的passwordencoder，用的是bcrypt校验，会去源码进行进行list.add（provider),添加新的校验方式
所以，这里代码我改之前是有重复不同方式的配置方式
我就改了一个地方，所以不对
把那个地方改了之后就对了，一切都完美闭环了



从这个地方其实我也看出来了，这个项目代码一开始写的时候 也很多时候就是从网上看看怎么完成这个功能的，然后比葫芦画瓢就完成了，测试没问题，就提交了
实际上，某些步骤到底实际上有什么作用，可能就不是很清楚，
确实开发的时候不少这样的情况，但是对于我能发现这个问题，有了这样的一次发现问题的过程，我感到很开心



## 过程

![](assets/Pasted%20image%2020251103210519.png)

一定要加上这个final

![](assets/Pasted%20image%2020251103210800.png)

你又忘记了
这是pom依赖问题，要用springboot3mybatisplus
![](assets/Pasted%20image%2020251103210839.png)

![](assets/Pasted%20image%2020251103215745.png)
为什么会有这个问题，为什么要加mybatisconfig，但是seckill就没有加我真服了
我真服了
我真服了

![](assets/Pasted%20image%2020251104093045.png)、
你妈的，又成了
没加又成了
有病吧
![](assets/Pasted%20image%2020251104093127.png)
好像是这个原因，
把这个去掉，把那个mybatisconfig去掉
但是一开始不就因为是么原因来加上的这俩
我真醉了


## ai话术

使用 springboot3 
mybatisplusspringboot3 
mybatis查询的时候尽量使用
```
List<SeckillItem> seckillItems = lambdaQuery()  
        .ge(SeckillItem::getEndTime, LocalDateTime.now())  
        .list();
```
        这种方式
        
		 redis  @Slf4j日志 
数据库使用mysql
还有lombok依赖
给出数据库sql语句，以及增加yml配置文件
记住有些地方需要使用分页查询别忘记了
需要有商品，订单，用户




requestparam
requestbody
pathvariable

```java
@GetMapping("/user")
public String getUser(@RequestParam("id") String userId) {// 当userId写成id的时候可以省略（“id”）
    return "User ID: " + userId;
}

@PostMapping("/user")
public String createUser(@RequestBody User user) {
    return "Created: " + user.getName();
}

@GetMapping("/user/{id}")
public String getUserById(@PathVariable("id") String userId) {// 当userId写成id的时候可以省略（“id”）
    return "User ID: " + userId;
}
@PutMapping("/{id}/status")  
public ResponseEntity<Map<String, Boolean>> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {// 两种方式并用
```

![](assets/Pasted%20image%2020251104113049.png)
还是看这个好使
![](assets/Pasted%20image%2020251104113103.png)
![](assets/Pasted%20image%2020251104113115.png)

官方说明真是好用




## trae


![](assets/Pasted%20image%2020251104140137.png)
是那个问题，
你需要去数据库软件中手动加载一下数据

或者url中添加
```
allowPublicKeyRetrieval=true&useSSL=false
```


![](assets/Pasted%20image%2020251104160538.png)
```
Non-static method cannot be referenced from a static context
```
![](assets/Pasted%20image%2020251104160607.png)
用实际的对象就好了

[RESTful API  参数传递方式](ds/RESTful%20API%20%20参数传递方式.md)

## mq

生产者重试
因为是阻塞式重试，所以考虑重试次数或者使用异步发送消息
```
CompletableFuture.runAsync
```
生产者确认机制
有确认机制和 返回机制
如果路由失败返回ack且返回异常信息
如果没到交换机返回nack

消费者确认机制
amqp环绕增加，业务错误返回nack,消费消息异常reject,比如，参数接收消息异常，格式转化有问题
如果业务失败返回nack重新回队列就需要重试发送消息然后可能会无限重试，所以要配置重试次数之类的玩意
还有最终重试还是失败后要配置一个RepublishMessageRecoverer用来专门处理这些消息，可以用于人工处理之类的





## 扩展

page进行拦截实现，感觉每个方法里面都要加上page参数好麻烦


## 练习
trae:
```
这是个简单的后端项目

简单实现与宝马相关的某个功能的CURD操作，例如开发一个购车页面并提供订单查询功能。要自己创建数据库表，

采用
springboot3
mybatis-plus-spring-boot3-starter
分页插件mybatis-plus-jsqlparser
日志采用Slf4j
数据库使用mysql
还有lombok依赖
工具依赖commons-lang3
其中
mybatis查询的时候尽量使用

List<SeckillItem> seckillItems = lambdaQuery()
.ge(SeckillItem::getEndTime, LocalDateTime.now())
.list();

这种简便方式

给出数据库sql语句，以及.yml结尾的配置文件，项目依赖pom

记住有些地方需要使用分页查询别忘记了
```
直接告诉需求，然后生成，进行修改（根据原有项目来决定来是否生成依赖等

依赖：

```
<?xml version="1.0" encoding="UTF-8"?>  
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">  
    <modelVersion>4.0.0</modelVersion>  
    <parent>        <groupId>org.springframework.boot</groupId>  
        <artifactId>spring-boot-starter-parent</artifactId>  
        <version>3.5.7</version>  
        <relativePath/> <!-- lookup parent from repository -->  
    </parent>  
    <groupId>com.oh</groupId>  
    <artifactId>bmw</artifactId>  
    <version>0.0.1-SNAPSHOT</version>  
    <name>bmw</name>  
    <description>bmw</description>  
    <url/>    <licenses>        <license/>    </licenses>    <developers>        <developer/>    </developers>    <scm>        <connection/>        <developerConnection/>        <tag/>        <url/>    </scm>    <properties>        <java.version>17</java.version>  
    </properties>  
    <dependencies>        <dependency>            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter-web</artifactId>  
        </dependency>        <!-- MyBatis-Plus -->  
        <dependency>  
            <groupId>com.baomidou</groupId>  
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>  
            <version>3.5.14</version>  
        </dependency>        <dependency>            <groupId>com.baomidou</groupId>  
            <artifactId>mybatis-plus-jsqlparser</artifactId>  
            <version>3.5.14</version>  
        </dependency>        <!-- MySQL驱动 -->  
        <dependency>  
            <groupId>mysql</groupId>  
            <artifactId>mysql-connector-java</artifactId>  
            <scope>runtime</scope>  
            <version>8.0.28</version>  
        </dependency>        <!-- Redis -->  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter-data-redis</artifactId>  
        </dependency>        <!-- Lombok -->  
        <dependency>  
            <groupId>org.projectlombok</groupId>  
            <artifactId>lombok</artifactId>  
            <optional>true</optional>  
            <version>1.18.16</version>  
        </dependency>        <!-- Spring Boot Test -->  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter-test</artifactId>  
            <scope>test</scope>  
        </dependency>        <dependency>            <groupId>org.apache.commons</groupId>  
            <artifactId>commons-lang3</artifactId>  
            <version>3.12.0</version> <!-- 可以使用最新版本 -->  
        </dependency>  
    </dependencies>  
    <build>        <plugins>            <plugin>                <groupId>org.springframework.boot</groupId>  
                <artifactId>spring-boot-maven-plugin</artifactId>  
            </plugin>        </plugins>    </build>  
</project>
```

自己写：

首先告诉ai需求生成对应的sql
根据需要生成配置yml内容，依赖内容
然后在此基础上生成用户订单商品的ssm三层结构代码

创建各层文件夹
根据表结构生成各个实体类
结果类
mybaits分页插件
分页实现
@RequestParam
注意是这个注解

```
LambdaQueryWrapper<Car> lambdaQueryWrapper = new LambdaQueryWrapper<>();
```
这个要提前写，要不然放到函数参数的位置进行new 会报静态方法问题错误

```
@Configuration  
public class MybatisConfig {
```
记得这个注解，不然分页插件不生效

添加的时候记得返回主键

```
路径变量方式传递参数适用于 必传字段

查询参数适合非必传字段

请求体传递
安全，post put patch
用于登录，批量操作
```

```
@RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer size,  
@RequestParam(value = "pageNum",defaultValue = "1",required = false) Integer num
```

全局异常返回
```
@RestControllerAdvice  
public class GlobalException {  
  
    @ExceptionHandler(Exception.class)  
    public Result<String> handleException(Exception e){  
        return Result.fail(e.getMessage());  
    }  
  
}
```
各种idea插件，如mybatis互相定位xml文件的地方
![](assets/Pasted%20image%2020251110201918.png)
这个地方一定要加final，这样才会自动注入

mybatisplus日志配置
```
logging:  
  level:  
    com.akiyo.bmw.mapper: debu
```

如果用limit num size 但是好像1不行，要用0
第二个方法
```
return carMapper.selectCar(new Page(pageNum,pageSize), modelName,series,minPrice,maxPrice).getRecords(); //return List<Car>

Page<Car> selectCar(Page page, @Param("modelName") String modelName, @Param("series") String series, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

```



记得lambdaupdate要接受boolen返回值确定是否修改成功

![](assets/Pasted%20image%2020251111174116.png)
这个set是多余的

更新有三种方法
```java
//updateById(user);  
return userMapper.changeUser(user);  
/*return lambdaUpdate()  
        .eq(User::getId,user.getId())        .set(StringUtils.isNotBlank(user.getUsername()),User::getUsername,user.getUsername())        .update();*/
```

![](assets/Pasted%20image%2020251113111911.png)
记得减少库存
对哦，取消订单还要回复库存
写之前看看哪些字段不能为空

```
// 降序
lambdaQuery()  
        .eq(Order::getUserId, userId)  
        .orderByDesc(Order::getCreateTime)  
        .list();
```

## mybatplus分页方法实现

```java
return lambdaQuery().eq(Car::getStatus,"1")  
        .list(new Page<>(num,size));



return lambdaQuery().eq(Car::getStatus,"1")  
        .page(new Page<>(num,size)).getRecords();

   
LambdaQueryWrapper<Car> lambdaQueryWrapper = new LambdaQueryWrapper<>();  
return baseMapper.selectPage(new Page<>(num,size),  
        lambdaQueryWrapper).getRecords();



LambdaQueryWrapper<BMWCar> queryWrapper = new LambdaQueryWrapper<>();  
  
if (modelName != null && !modelName.isEmpty()) {  
    queryWrapper.like(BMWCar::getModelName, modelName);  
}  
if (series != null && !series.isEmpty()) {  
    queryWrapper.eq(BMWCar::getSeries, series);  
}  
if (minPrice != null) {  
    queryWrapper.ge(BMWCar::getPrice, minPrice);  
}  
if (maxPrice != null) {  
    queryWrapper.le(BMWCar::getPrice, maxPrice);  
}  
  
queryWrapper.orderByDesc(BMWCar::getCreateTime);  
Page<BMWCar> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);  
return page.getRecords();



LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();  

return baseMapper.selectPage(new Page<>(pageNum, pageSize),  
                queryWrapper  
                        .like(StringUtils.isNotBlank(orderNo), Order::getOrderNo, orderNo)  
                        .like(StringUtils.isNotBlank(customerName), Order::getCustomerName, customerName)  
                        .eq(orderStatus != null, Order::getOrderStatus, orderStatus)  
                        .eq(startDate != null, Order::getCreateTime, startDate)  
                        .eq(endDate != null, Order::getCreateTime, endDate))  
        .getRecords();
```




## 自我介绍

```
我是小明，江苏大学本科计算机，无锡华勤Java后端开发实习生4个月多到现在，在校获得奖学金，竞赛有奖，学习成绩排名居于前20%

自己有着丰富的javaspringbootspringcloud微服务使用经验，开发过秒杀活动的demo，熟悉redis sentinel rabbitmq等中间件，对高并发锁等有着使用经验， 在无锡华勤实习期间主要负责人事区域的系统业务开发维护，有着国际化，项目兼容扩展，动态路由，动态面板，模板配置，数据权限切面控制，定时任务，源码调试debug等等业务经验

之所以选择贵公司，是因为希望能有更好的待遇，并不是说原有的公司不好，而且我希望能找到更好的，我的自我介绍完毕，谢谢大家

这里的国际化并不只是指抛出异常的国际化，配置文件的国际化，还有字典数据的菜单的国际化等等

```
```
**各位面试官好，我是小明，**  
江苏大学计算机科学与技术专业本科毕业，在校期间成绩排名前20%，曾多次获得校级奖学金，并参与过多个编程竞赛并获奖。

**技术能力方面，**  
我熟练掌握Java技术栈，包括Spring Boot、Spring Cloud等微服务框架，并有实际项目开发经验，例如独立开发过**秒杀系统Demo**，熟悉高并发场景下的解决方案，如分布式锁、Redis Sentinel集群、RabbitMQ消息队列等中间件的应用。

**在无锡华勤实习期间（4个月+），**  
我主要负责**人事管理系统**的业务开发与维护，积累了以下实战经验：

- **国际化（i18n）**：支持多语言动态切换，优化了前端资源加载逻辑。
- **动态路由与权限控制**：基于用户角色实现动态菜单渲染，结合AOP实现细粒度数据权限拦截。
- **可配置化开发**：参与模板化表单和动态面板的设计，通过数据库配置驱动页面渲染，提升系统灵活性。
- **定时任务与异步处理**：使用Quartz和线程池优化后台任务调度，减少主业务链路阻塞。
- **问题排查能力**：熟练通过源码调试（Debug）定位复杂问题，例如解决过分布式环境下的缓存一致性难题。

**关于求职动机，**  
华勤的经历让我快速成长，但现阶段我希望能在技术深度和业务挑战上更进一步。贵公司在（提及公司优势，如：技术影响力/业务场景/成长空间）方面非常吸引我，如果有机会加入，我会全力发挥技术价值，与团队共同成长。

**以上是我的介绍，谢谢！**
```
```java
Good morning, everyone.  
My name is houtinalu. I graduated with a Bachelor's degree in Computer Science and Technology from Jiangsu University, where I ranked in the top 20% of my major and received multiple scholarships. I also participated in programming competitions and won awards.

In terms of technical skills,  
I am proficient in Java and its ecosystem, including Spring Boot, Spring Cloud, and microservices architecture. I have hands-on experience in developing projects such as a flash sale (seckill) system demo, where I implemented solutions for high concurrency, including distributed locks, Redis Sentinel clusters, and RabbitMQ message queues.

During my 4-month internship at Huaqin (Wuxi) as a Java Backend Developer,  
I was primarily responsible for the HR management system, where I gained practical experience in:

- Troubleshooting & Debugging: Proficient in debugging complex issues, such as resolving password validation challenges in Spring Security.  
      
- Dynamic Routing & Permission Control: Designed role-based dynamic menus and used AOP for fine-grained data access control.

- Internationalization (i18n): Implemented dynamic language switching and optimized frontend resource loading.
  
- Configurable Development: Developed template-based forms and dynamic panels, allowing UI rendering to be driven by database configurations.
  
- Scheduled Tasks & Async Processing: Optimized background job scheduling using Quartz to reduce latency in core business processes.
  


Regarding my career motivation,  
My time at Huaqin helped me grow rapidly, but I now seek greater technical challenges and professional growth. Your company stands out to me because of its positive work environment, competitive compensation & benefits, and excellent career development opportunities, among other strengths. If given the opportunity, I am eager to contribute my skills and grow alongside the team.

That’s all for my introduction. Thank you!
```
