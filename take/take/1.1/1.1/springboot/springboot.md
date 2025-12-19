![](assets/Pasted%20image%2020250630194606.png)

![](assets/Pasted%20image%2020250630194729.png)

如果有个第三方的包
怎么注入bean


## bean注册
创建配置类，加上配置注解，里面返回对象的方法加上bean注解（可以自定义对象名字），

### 导入第三方bean
import单个或者含有多个的数组，
或者另起一个类，重写方法里面返回类的包名（一般这里直接读取配置文件

![](assets/Pasted%20image%2020250630202806.png)


或者使用注解，可以自定义组合注解

总结：
![](assets/Pasted%20image%2020250630203012.png)


## 改变注册bean的属性

### 1
![](assets/Pasted%20image%2020250630203216.png)

### 2
![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml30196\wps1.jpg)

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml30196\wps2.jpg) 

然后通过这种方法就可以解决上面那个写死的情况

根据上面这个又可以延伸出来这个bean的注册生效条件
![](assets/Pasted%20image%2020250630203734.png)


eg:
![](assets/Pasted%20image%2020250630203806.png)
![](assets/Pasted%20image%2020250630203813.png)

![](assets/Pasted%20image%2020250630203835.png)


## 自动配置原理
![](assets/Pasted%20image%2020250630204511.png)




## lombok依赖
@data注解
生成setter getter方法


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml30196\wps3.jpg)

来解决result中的没有定义的构造函数，这样就不会报错了


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml30196\wps4.jpg)

来解决result中的没有定义的构造函数，这样就不会报错了

分别是

自动生成一个**无参构造方法**（默认构造方法）。

自动生成一个**全参构造方法**（包含所有字段的构造方法）。


## **jwt**

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml30196\wps5.jpg) 

通过解析数字签名得到header和palyload然后和传递来的header和playload进行比对，

## 参数校验

![](assets/Pasted%20image%2020250630205829.png)

sql语句中可以直接获取now()时间

### 分组校验

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml30196\wps6.jpg)

为了解决在同一个实体类中添加了很多注解，来控制参数校验，但是某个函数明明不需要这个参数校验，却因为别的函数需要也被强制检验了，的这个问题

### 自定义校验

![](assets/Pasted%20image%2020250630211102.png)

## 属性配置优先级
![](assets/Pasted%20image%2020250630211353.png)

## 多环境开发
![](assets/Pasted%20image%2020250630211453.png)



## 前端

axios
发送请求的玩意

![](assets/Pasted%20image%2020250630212159.png)






## 繁
![](assets/Pasted%20image%2020250701001649.png)





![](assets/Pasted%20image%2020250701005215.png)


autowired和resource的区别（注入



### 用于java对象到xml之间的序列化和反序列化

```
<dependency>  
  <groupId>javax.xml.bind</groupId>  
  <artifactId>jaxb-api</artifactId>  
  <version>2.3.1</version>  
</dependency>  
<dependency>  
  <groupId>javax.activation</groupId>  
  <artifactId>activation</artifactId>  
  <version>1.1.1</version>  
</dependency>  
<!-- no more than 2.3.3-->  
<dependency>  
  <groupId>org.glassfish.jaxb</groupId>  
  <artifactId>jaxb-runtime</artifactId>  
  <version>2.3.3</version>  
</dependency>
```

现在解决方案：
![](assets/Pasted%20image%2020250701014158.png)

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<user>
    <name>John</name>
    <age>30</age>
</user>
```

```
User("John", 30);
```
之间的转化



## 样例代码

### 分页查询

```java
@Override  
public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {  
    //1.创建PageBean对象  
    PageBean<Article> pb = new PageBean<>();  
  
    //2.开启分页查询 PageHelper    PageHelper.startPage(pageNum,pageSize);  
  
    //3.调用mapper  
    Map<String,Object> map = ThreadLocalUtil.get();  
    Integer userId = (Integer) map.get("id");  
    List<Article> as = articleMapper.list(userId,categoryId,state);  
    //Page中提供了方法,可以获取PageHelper分页查询后 得到的总记录条数和当前页数据  
    Page<Article> p = (Page<Article>) as;//这是个强转，很奇妙的。page是list的实现类·  
  
    //把数据填充到PageBean对象中  
    pb.setTotal(p.getTotal());//这个实体类里面没有写那个set方法，因为加那个注解所以可以用这个方法也许  
    pb.setItems(p.getResult());  
    return pb;  
}
```

从list到page到pagebean


## end