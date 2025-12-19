null不能直接打印

\t 是8个的倍数

long 定义需要 L(不建议小写)

+是从左到右依次执行

关于| & 的各种便捷操作


### switch

```java
  
//        以下两种方法不能混着用  
        switch(i){  
            case 1:{  
                break;  
            }  
        }  
        switch (i){  
            case 1->{  
  
            }  
        }  
//        保存结果  
        String day = "Monday";  
        int numLetters = switch (day) {  
            case "Monday", "Friday", "Sunday" -> 6;  
            case "Tuesday" -> 7;  
            case "Thursday", "Saturday" -> 8;  
            case "Wednesday" -> 9;  
            default -> throw new IllegalArgumentException("Unknown day: " + day);  
        };  
        System.out.println(numLetters); // 输出6
```


```

int[] arr = new int[]{1,2};

int[] arr1 ={1,2};

```


字符类型默认空格

引用类型默认；null
布尔类型默认false


## string
string (null,string,char数组，byte数组) 最后一个就是字节数组转化成string类型，网络传输中很好用

双引号直接创建会看串池里面有没有


![[Pasted image 20250629205320.png]]


![[Pasted image 20250629205353.png]]

```
String str =sc.next();//这个str是New出来的
```

![[Pasted image 20250629210334.png]]

stringbuild
	append\reverse\length\tostring

stringjoiner
创建的时候可以选择间隔，开头，结尾，

## 集合


```
ArrayList<String> arrayList = new ArrayList<>();
```
```
ArrayList<String> arrayList = new ArrayList<>();  
arrayList.add();  
arrayList.remove();  
arrayList.set();  
arrayList.get();  
arrayList.size();
```

![[Pasted image 20250629211620.png]]


## 继承

构造方法不能继承
成员变量能继承
成员方法 非私有能继承，私有不能继承
有的能继承但是不能使用



如果c方法在虚方法表中则会直接可以调用，但是如果不在，java还是会之后还是会一层一层向上从父类中找c方法


![[Pasted image 20250629212145.png]]


```
this.eat()
super.eat()

```

子类可以重写父类方法
返回值要小于等于父类

![[Pasted image 20250629212822.png]]

![[Pasted image 20250629212859.png]]

![[Pasted image 20250629213017.png]]

子类中的构造函数直接调用父类的构造方法
这样虽然子类不可以继承父类构造方法但是可以调用

![[Pasted image 20250629213441.png]]
![[Pasted image 20250629213500.png]]

## 多态

父类调用方法，实际运行看子类

![[Pasted image 20250629213714.png]]

![[Pasted image 20250629213900.png]]

因为子方法会覆写虚方法表，所以先调用子类方法

![[Pasted image 20250629215648.png]]


父类不能调用子类特定方法
![[Pasted image 20250629215827.png]]

jdk14之后新特性

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps1.jpg)



## final
最终方法
最终类
常量

基本类型，值不能变
引用类型中，地址不能变

![[Pasted image 20250629220159.png]]

![[Pasted image 20250629220313.png]]

## 代码块


![[Pasted image 20250629220421.png]]

静态代码块则是类的随着类的加载而执行的
可以用于数据初始化
这样就可以实现只用执行一次



![[Pasted image 20250629220409.png]]



## 抽象类
抽象方法
继承抽象类，自动重写所有方法
强制子类实现某种方法，有种统一的意义在
public abstract 返回值 name （参数）
public abstract class name{}

## 接口
public interface name{}
public class name extend name implements name，name2{}

如果实现的接口里有多个，且里面有重名的接口，只需要重写一个即可，是对两个同时重写

多态应该是指 父类调用子类方法
另外是 抽象，继承，接口

接口新特性

某方法只为接口调用，所以只提供私有方法，还有静态私有方法（在接口里面

private void show()
private static void show()
（比如各个方法中调用日志方法

### 适配器 

如果只想要实现一个接口里面的一个方法怎么半

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps2.jpg) 

先写一个适配器类来对所有方法进行空实现

由于这个不能被其他随意创建

所以要用abstract变成抽象类

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps3.jpg) 

之后只需要再写一个继承类来继承并重写指定方法就解决问题了

## 匿名内部类

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps4.jpg)

用大括号来实现Swim接口

之后用大括号里面的东西来实现重写

之后再用new创建对象

然后这一整个表示个对象


抽象类，同理，只不过是实现继承关系罢了

![[Pasted image 20250629222938.png]]


![[Pasted image 20250629223000.png]]




## api

![[Pasted image 20250629223636.png]]

![[Pasted image 20250629223741.png]]

![[Pasted image 20250629223839.png]]

![[Pasted image 20250629224015.png]]

这个是直接get得到对象的，多个对象也没意义

runtime.getruntime.exit()

内存大小单位是字节

![[Pasted image 20250629224548.png]]

![[Pasted image 20250629224559.png]]

![[Pasted image 20250629224630.png]]

object
![[Pasted image 20250629224741.png]]

![[Pasted image 20250629225205.png]]

![[Pasted image 20250629225220.png]]



object的克隆是浅克隆


一劳永逸的方法
用来深度拷贝

![[Pasted image 20250629231122.png]]

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps5.jpg)

新建包

如果要用到别人写的代码就把代码放到这个里面

![[Pasted image 20250629231301.png]]

![[Pasted image 20250629231437.png]]



![[Pasted image 20250629231352.png]]

### 日期

![[Pasted image 20250629231728.png]]

datetimeformatter.ofpattern(" yyyy-MM-dd HH:mm:ss EE a)

![[Pasted image 20250629232305.png]]


![[Pasted image 20250629232446.png]]

![[Pasted image 20250629232520.png]]





![[Pasted image 20250629232852.png]]





![[Pasted image 20250629232706.png]]

## 匿名函数lambda

![[Pasted image 20250629232925.png]]

![[Pasted image 20250629233209.png]]
这个很有意思

![[Pasted image 20250629233254.png]]


![[Pasted image 20250629233535.png]]


![[Pasted image 20250629233621.png]]

![[Pasted image 20250629233743.png]]


## 集合

![[Pasted image 20250629234029.png]]

![[Pasted image 20250629234032.png]]

### 集合遍历方法
![[Pasted image 20250629234209.png]]

增强for循环 for直接回车生成
循环内部的部分是临时变量
不能改变集合本身

lambda表达式，foreach遍历

### list遍历
![[Pasted image 20250629234702.png]]

listiterator

![[Pasted image 20250629234755.png]]




## 泛型
它只是在门口检查一下是否符合string

实际运行还是object

最后再强制转化成string


![[Pasted image 20250629235251.png]]

```
public class name <E>{}
```

addall可以传递不定量参数



![[Pasted image 20250630001517.png]]

![[Pasted image 20250630001634.png]]
改成这样就不报错了

![[Pasted image 20250630001707.png]]

这样更好


## treeset排序

![[Pasted image 20250630002342.png]]
这种情况下如果要自定义排序的时候

### 第一种

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps6.jpg)


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml4440\wps7.jpg) 

这些东西底层是红黑树

![[Pasted image 20250630002046.png]]


### 第二种

![[Pasted image 20250630002438.png]]

## map

![[Pasted image 20250630002532.png]]

注意remove返回删除的值


遍历方式
### 第一种
![[Pasted image 20250630103721.png]]

### 第二种
![[Pasted image 20250630104303.png]]

### 第三钟
![[Pasted image 20250630104324.png]]

## 可变参数

![[Pasted image 20250630104551.png]]


## collections
![[Pasted image 20250630104737.png]]


## stream

![[Pasted image 20250630112727.png]]


![[Pasted image 20250630105151.png]]

list.stream.foreach(e->{sout)

stream.of(arr).foreach()


创建
![[Pasted image 20250630105301.png]]

中间方法
![[Pasted image 20250630105318.png]]

去重需要重写方法

终结方法
![[Pasted image 20250630105501.png]]

![[Pasted image 20250630105513.png]]

eg:
![[Pasted image 20250630105547.png]]

## 方法引用

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps1.jpg)

这样在静态函数里面创建本类的对象就可以用了

（它这个上部分代码是静态函数里面的）

在静态方法里面创建本类对象来调用本类的非静态方法

![[Pasted image 20250630110150.png]]

![[Pasted image 20250630110341.png]]

![[Pasted image 20250630110608.png]]

throw new nullpointexception


### 自定义异常
![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps2.jpg)

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps3.jpg)


## file
![[Pasted image 20250630111305.png]]

方法
![[Pasted image 20250630111332.png]]

![[Pasted image 20250630111347.png]]

![[Pasted image 20250630111711.png]]

![[Pasted image 20250630112150.png]]
但是实际开发就是直接抛出了



![[Pasted image 20250630112918.png]]


![[Pasted image 20250630112852.png]]

![[Pasted image 20250630112944.png]]

![[Pasted image 20250630113003.png]]


### 转换流
![[Pasted image 20250630113135.png]]

换编码

![[Pasted image 20250630113843.png]]

![[Pasted image 20250630114034.png]]
![[Pasted image 20250630114022.png]]


序列化流
对象到文件
反序列化流
文件到对象


这个标准流关闭后就无法再次打开了，除非再次运行
printstream ps = system.out

## Commons-io

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps4.jpg) 

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps5.jpg)


## **Hutool**

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps6.jpg)

![[Pasted image 20250630114647.png]]


## properties
![[Pasted image 20250630114728.png]]


## 线程池

![[Pasted image 20250630114824.png]]

![[Pasted image 20250630114853.png]]

## UDP
![[Pasted image 20250630115040.png]]
![[Pasted image 20250630115045.png]]

![[Pasted image 20250630115136.png]]

## TCP
![[Pasted image 20250630115242.png]]

释放资源记得两个都要释放一个bw 一个socket


![[Pasted image 20250630115249.png]]

### 线程池优化
![[Pasted image 20250630115520.png]]

## 反射

获取各种信息，根据获取到的构造方法创建对象，

![[Pasted image 20250630121017.png]]


![[Pasted image 20250630120805.png]]


## 动态代理

![[Pasted image 20250630120335.png]]
![[Pasted image 20250630120342.png]]



## 编码
Idea默认是utf-8

lunx系统默认是gbk
window默认是ansi,就是gbk













## 运算符优先级
![[Pasted image 20250630121910.png]]


## 巧知
![[Pasted image 20250630122214.png]]

长度不固定一般用集合

长度固定一般用数组

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps11.jpg)

不能说看不懂，要说不符合阅读习惯

格局大一点
和Java标准答案对了一下对了
不是我对了
是java对了

bug可以从下向上找
点击蓝色字体
![[Pasted image 20250630122422.png]]

要尽量把固定数据放到前面，再和后面的进行比较，因为后面的可能会是Null,可能是空指针，不适合放到前面和后面的’男’进行比较、


通过这种方式可以将int 类型数字转化成字符输出（通过这种+空字符的形式

注意公共数据用static修饰

## 繁知
只要是new出来的就是引用类型

二维中右边是第一维


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps12.jpg)

只能有一个public，要和文件名一样的才能public


![[Pasted image 20250630122708.png]]

![[Pasted image 20250630122746.png]]

integer.of(value)
小于127的值已经底层 创建好了，而大于的需要重新New
（准确来说是-128到127

![[Pasted image 20250630123201.png]]

![[Pasted image 20250630123930.png]]

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps13.jpg)

回收站

无权


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps14.jpg)

不要在类刚开shi的时候就写那个id

这个id是根据成员函数什么玩意计算出来的

写完之后再写这个


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps16.jpg)

勾选这个意思是用系统默认方式打开这个文件


## 思路

先顺序生成再随机交换数字和其他字母



## 占位符

![[Pasted image 20250630124057.png]]



## 快捷键

psvm


alt insert 构造函数等初始化
你还有ptg插件


自动生成左边


.fori

ctrl alt l
ctrl alt v/t/m



Shift F6
批量修改

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps7.jpg)



![[Pasted image 20250630121218.png]]
应该是文档的左右前进后退


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps8.jpg)

这个地方按着ctrl之后再鼠标左键就可以进入文档

Ctrl F12就可以找到相关方法

ctrl p查看参数

ctrl shift u全部大写



ctrl b

从调用的函数到定义处

![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps9.jpg)

鼠标滚轮集体修改


![](file:///C:\Users\houtl\AppData\Local\Temp\ksohtml27448\wps10.jpg)alt 7

生成大纲


ctrl shift 加上和下可以将代码上下换行



## end