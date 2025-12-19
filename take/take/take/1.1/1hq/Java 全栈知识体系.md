

# SpringBoot集成MySQL - MyBatis-Plus基于字段隔离的多租户
![](assets/Pasted%20image%2020250806192801.png)

![](assets/Pasted%20image%2020250806192831.png)

原来那个longvalue（1）是用来拼接sql的




# # SpringBoot集成ShardingJDBC - Sharding-JDBC简介和基于MyBatis的单库分表



这个配置是 **ShardingSphere-JDBC** 的分库分表规则配置，用于实现水平分表（将单表数据分散到多个物理表中）。下面逐部分解析其作用：

---

### **1. 数据源配置**
```yaml
datasource:
  names: ds  # 数据源名称（只有一个数据源ds）
  ds:
    type: com.zaxxer.hikari.HikariDataSource  # 使用Hikari连接池
    driver-class-name: com.mysql.cj.jdbc.Driver
    jdbc-url: jdbc:mysql://localhost:3306/test_db_sharding  # 实际数据库地址
    username: test
    password: bfXa4Pt2lUUScy8jakXf
```
- **关键点**：这里只配置了一个数据源（`ds`），说明是 **单库多表分片**（所有分表在同一个数据库中）。

---

### **2. 分表规则（`tb_user`表）**
```yaml
sharding:
  tables:
    tb_user:  # 逻辑表名
      actual-data-nodes: ds.tb_user_$->{0..1}  # 物理表：ds库的tb_user_0、tb_user_1
      table-strategy:
        inline:  # 内联分片算法
          sharding-column: id  # 分片字段
          algorithm-expression: tb_user_$->{id % 2}  # 分片规则：id取模
      key-generator:  # 分布式ID生成器
        column: id    # 主键列
        type: SNOWFLAKE  # 雪花算法
        props:
          worker.id: 123  # 工作机器ID
```
#### **作用解析**
| 配置项 | 值 | 说明 |
|--------|----|------|
| `actual-data-nodes` | `ds.tb_user_$->{0..1}` | 物理表为 `tb_user_0` 和 `tb_user_1` |
| `sharding-column` | `id` | 根据 `id` 字段的值决定数据路由到哪个分表 |
| `algorithm-expression` | `tb_user_$->{id % 2}` | 分片算法：`id % 2` 结果为0时存 `tb_user_0`，为1时存 `tb_user_1` |
| `key-generator` | `SNOWFLAKE` | 主键 `id` 使用雪花算法生成（避免主键冲突） |

#### **示例数据路由**
| id值 | 计算 `id % 2` | 实际存储表 |
|------|---------------|------------|
| 1 | 1 | `tb_user_1` |
| 2 | 0 | `tb_user_0` |
| 3 | 1 | `tb_user_1` |

---

### **3. 绑定表与广播表**
```yaml
binding-tables: tb_user  # 绑定表（暂无其他关联表）
broadcast-tables: t_address  # 广播表（所有库表同步）
```
#### **概念解释**
| 类型 | 作用 | 示例 |
|------|------|------|
| **绑定表** | 多个分片表具有相同的分片规则，JOIN查询时优化路由 | 如 `tb_user` 和 `tb_order` 都按 `user_id` 分片 |
| **广播表** | 所有库/分片中完全相同的表（如字典表） | `t_address` 会在所有节点同步更新 |

---

### **4. 完整执行流程**
1. **插入数据**  
   ```java
   userMapper.insert(new User(3L, "Alice")); // 假设生成的id=3
   ```
   - 计算分片：`3 % 2 = 1` → 数据写入 `tb_user_1`
   - 主键 `id` 由雪花算法生成（如 123456789）

2. **查询数据**  
   ```sql
   SELECT * FROM tb_user WHERE id = 3;
   ```
   - ShardingSphere 自动改写SQL：  
     ```sql
     SELECT * FROM tb_user_1 WHERE id = 3; -- 只查tb_user_1
     ```

3. **全表扫描**  
   ```sql
   SELECT * FROM tb_user;
   ```
   - 改写为：  
     ```sql
     SELECT * FROM tb_user_0 
     UNION ALL 
     SELECT * FROM tb_user_1;
     ```

---

### **5. 性能与扩展建议**
#### **(1) 分片键选择**
- 优先选择 **高频查询条件字段**（如 `user_id`）
- 避免选择 **单调递增字段**（如自增ID），可能导致数据倾斜

#### **(2) 分片算法扩展**
```yaml
# 自定义复杂分片算法
algorithm-expression: tb_user_$->{Math.abs(id.hashCode()) % 16}  # 按哈希值分16张表
```

#### **(3) 多数据源分库**
若需分库+分表，配置多个数据源：
```yaml
datasource:
  names: ds0,ds1  # 两个数据源
  ds0: 
    jdbc-url: jdbc:mysql://node1:3306/db0
  ds1:
    jdbc-url: jdbc:mysql://node2:3306/db1
actual-data-nodes: ds$->{0..1}.tb_user_$->{0..1}  # 分2库2表
```

---

### **总结**
| 配置目标 | 实现方式 |
|----------|----------|
| **水平分表** | 按 `id % 2` 分散到 `tb_user_0`/`tb_user_1` |
| **分布式ID** | 雪花算法生成主键 |
| **绑定表** | 优化关联查询路由 |
| **广播表** | 同步更新全局表（如字典） |

该配置适合 **单库分表** 场景，如需分库需扩展数据源和分片算法。








# 1

hashmap

散列表（Hash table，也叫哈希表）
算哈希值
散列函数 - 》 计算出 桶 下标
桶内 
	链表
	红黑树
	他俩的长度是常数级别的，这个是哈希设计的好的原因（链表到红黑树的转化，动态扩容（扩大桶数组长度））

红黑树和 链表 这部分应该是 java 中 关于 hashmap的知识， 不算 hash table的知识吧



联合索引，覆盖索引，全文索引，
redolog,binlog,undolog
二阶段提交保证强一致性
xid

回表
联合索引，聚簇索引


范围分片，哈希分片，路由分片


借助主键索引分页进行优化



窗口函数，每个班的前十名
```mysql
SELECT student_name, class, score
FROM (
    SELECT 
        student_name, 
        class, 
        score,
        ROW_NUMBER() OVER (PARTITION BY class ORDER BY score DESC) AS rn
    FROM student_scores
) AS tmp
WHERE rn <= 10;
```



#### [联合索引 abc，a=1,c=1/b=1,c=1/a=1,c=1,b=1 走不走索引？](https://javabetter.cn/sidebar/sanfene/mysql.html#%E8%81%94%E5%90%88%E7%B4%A2%E5%BC%95-abc-a-1-c-1-b-1-c-1-a-1-c-1-b-1-%E8%B5%B0%E4%B8%8D%E8%B5%B0%E7%B4%A2%E5%BC%95)

> 2024 年 03 月 19 日增补

ac 能用上索引，条件 a=1 符合最左前缀原则，触发索引的第一列 a；由于跳过了中间列 b，c=1 无法直接利用索引的有序性优化，但可通过索引下推在存储引擎层过滤 c 的条件，减少回表次数。

bc 无法使用索引，只能全表扫描，因为不符合最左前缀原则；acb 虽然顺序是乱的，但 MySQL 优化器会自动重排为 abc，所以能命中索引。



# 查询每个班级年龄排名前两名的学生

这个SQL查询的目的是找出每个班级中年龄最大的两名学生。我来详细解释这个查询的工作原理：

## 查询语句

sql

```sql
SELECT a.class, a.name, a.age
FROM students a
WHERE (
    SELECT COUNT(DISTINCT b.age)
    FROM students b
    WHERE b.class = a.class AND b.age > a.age
) < 2
ORDER BY a.class, a.age DESC;
```

## 工作原理分析

1. **主查询**：从`students`表（别名为`a`）中选择班级、姓名和年龄字段。
    
2. **子查询条件**：
    
    sql
    
    ```sql
    (SELECT COUNT(DISTINCT b.age) FROM students b 
     WHERE b.class = a.class AND b.age > a.age) < 2
    ```
    
    - 对于主查询中的每一行（学生a），子查询计算：
        - 在同一个班级(`b.class = a.class`)中
        - 年龄比当前学生大的(`b.age > a.age`)
        - 不同年龄值的数量(`COUNT(DISTINCT b.age)`)
3. **筛选逻辑**：
    
    - 如果子查询结果`< 2`，表示当前学生的年龄：
        - 要么是班级中最大的（没有比他大的，COUNT=0）
        - 要么是第二大的（只有一个比他大的年龄值，COUNT=1）
    - 这样就筛选出了每个班级年龄最大的两个学生
4. **排序**：结果按班级分组，并按年龄降序排列。
    

## 示例说明

假设`students`表数据如下：

```
class | name | age
------+------+----
1     | 张三 | 18
1     | 李四 | 19
1     | 王五 | 19
1     | 赵六 | 17
2     | 钱七 | 20
2     | 孙八 | 18
2     | 周九 | 20
```

对于班级1：

- 张三(18)：有2个不同年龄值(19,19)比他大 → COUNT=1（因为DISTINCT）
- 李四(19)：没有比他大的年龄 → COUNT=0
- 王五(19)：同上 → COUNT=0
- 赵六(17)：有3个不同年龄值(18,19,19)比他大 → COUNT=2

所以班级1会返回李四和王五（年龄19）和张三（年龄18）

最终结果：

```
class | name | age
------+------+----
1     | 李四 | 19
1     | 王五 | 19
1     | 张三 | 18
2     | 钱七 | 20
2     | 周九 | 20
2     | 孙八 | 18
```

## 注意事项

1. 如果有多个学生年龄相同（如班级1的李四和王五都是19岁），他们都会被包含在结果中。
    
2. 这种方法使用了相关子查询，对于大表可能性能不佳。在MySQL 8.0+中，可以使用窗口函数更高效地实现：
    

sql

```sql
SELECT class, name, age
FROM (
    SELECT class, name, age,
           DENSE_RANK() OVER (PARTITION BY class ORDER BY age DESC) as age_rank
    FROM students
) ranked
WHERE age_rank <= 2
ORDER BY class, age DESC;
```

3. 如果只需要每个班级严格的两名学生（即使有并列），可以使用`ROW_NUMBER()`代替`DENSE_RANK()`。

# mysql

### 建索引的几大原则

1. 最左前缀匹配原则，非常重要的原则，mysql会一直向右匹配直到遇到范围查询(`>`、`<`、`between`、`like`)就停止匹配，比如`a = 1 and b = 2 and c > 3 and d = 4` 如果建立(a,b,c,d)顺序的索引，d是用不到索引的，如果建立(a,b,d,c)的索引则都可以用到，a,b,d的顺序可以任意调整。
    
2. =和in可以乱序，比如`a = 1 and b = 2 and c = 3` 建立(a,b,c)索引可以任意顺序，mysql的查询优化器会帮你优化成索引可以识别的形式。
    
3. 尽量选择区分度高的列作为索引，区分度的公式是`count(distinct col)/count(*)`，表示字段不重复的比例，比例越大我们扫描的记录数越少，唯一键的区分度是1，而一些状态、性别字段可能在大数据面前区分度就是0，那可能有人会问，这个比例有什么经验值吗？使用场景不同，这个值也很难确定，一般需要join的字段我们都要求是0.1以上，即平均1条扫描10条记录。
    
4. 索引列不能参与计算，保持列“干净”，比如`from_unixtime(create_time) = ’2014-05-29’`就不能使用到索引，原因很简单，b+树中存的都是数据表中的字段值，但进行检索时，需要把所有元素都应用函数才能比较，显然成本太大。所以语句应该写成create_time = unix_timestamp(’2014-05-29’)。
    
5. 尽量的扩展索引，不要新建索引。比如表中已经有a的索引，现在要加(a,b)的索引，那么只需要修改原来的索引即可。
    


# 跳表

![](assets/Pasted%20image%2020250901175145.png)


# 接收数据包是一个复杂的过程，
涉及很多底层的技术细节，但大致需要以下几个步骤：

- 网卡收到数据包。
- 将数据包从网卡硬件缓存转移到服务器内存中。
- 通知内核处理。
- 经过TCP/IP协议逐层处理。
- 应用程序通过read()从socket buffer读取数据。



# 繁

volatile 自带happens-before 规范



# end




# 二哥


这个问题涉及到 MySQL 的**二阶段提交（2PC）**机制，它是保证主从数据一致性的核心设计。下面详细解释崩溃恢复时的判断逻辑：

---

### 一、二阶段提交流程回顾

以事务`UPDATE t SET c=c+1 WHERE id=1`为例：

1. **Prepare 阶段**：
    - InnoDB 写入 redo log（标记为`prepare`状态）
    - 执行修改操作（内存中修改数据页）
2. **Commit 阶段**：
    - MySQL Server 写入 binlog
    - InnoDB 写入 redo log（标记为`commit`状态）

---

### 二、崩溃场景分析（binlog已写，redo未commit）

#### 关键判断依据：

MySQL 重启后通过**对比 redo log 和 binlog**决定事务去向：

1. **检查 redo log**  
    发现存在`prepare`状态但无`commit`标记的事务（记为事务X）
2. **检查 binlog**
    - 若事务X的`XID`（全局事务ID）存在于 binlog 中  
        → 说明 binlog 已成功写入
    - 若不存在 → 说明 binlog 未写入

#### 处理决策：

|redo log 状态|binlog 状态|处理方式|原因|
|---|---|---|---|
|prepare|存在XID|**提交事务**|binlog已持久化，从库必然会有该事务，必须保持主从一致|
|prepare|不存在XID|**回滚事务**|binlog未写入，从库不会同步，主库也不应提交|

> 📌 **XID的作用**：redo log 和 binlog 中会记录相同的全局事务ID，这是两者关联的关键。

---

### 三、技术实现细节

1. **崩溃恢复流程**：
    
    - 扫描 redo log 收集所有`prepare`状态事务
    - 取这些事务的`XID`，检查 binlog 中是否存在对应的`XID EVENT`
    - 根据检查结果提交或回滚
2. **代码层面的证据**：  
    InnoDB 的`trx_commit_in_memory()`函数在崩溃恢复时会调用`binlog_group_commit_ctx_consume()`验证binlog状态。
    
3. **用户可见现象**：  
    如果手动跳过崩溃恢复（`--tc-heuristic-recover=ROLLBACK`），可能导致主从数据不一致。
    

---

### 四、为什么这样设计？

1. **CAP理论权衡**：  
    优先保证数据一致性（C）而非可用性（A），这是金融级数据库的基本要求。
    
2. **避免脑裂问题**：  
    如果主库提交但从库未同步，会导致数据分裂。
    
3. **与分布式事务的统一**：  
    同样的机制也适用于XA事务（如跨库事务）。
    

---

### 五、实验验证方法

可以通过暴力kill MySQL进程模拟：

sql

```sql
-- 会话1
BEGIN;
UPDATE account SET balance=balance-100 WHERE user='Alice';
-- 此时在另一个终端执行 kill -9 mysql_pid
```

检查`innodb_status`和`show binary logs`确认恢复行为是否符合预期。

这种精妙的协调机制，正是数据库系统可靠性的基石之一。




您观察得很仔细！这里的数字变化需要更精确的解释。让我们拆解这个过程中的 **LSN增长机制**：

---

### 一、LSN增长的**原子性规则**

InnoDB的LSN增长遵循严格规则：

1. **每次日志写入**：LSN增加量 = 本次写入的redo log字节数（含日志头尾元数据）
2. **固定组成部分**：每条redo log都包含：
    - 12字节头信息（log block header）
    - 实际日志内容
    - 4字节校验码（trailer）
3. **对齐写入**：redo log总是按512字节块(disk sector大小)写入

---

### 二、示例场景的数字推演

假设初始状态：

- 当前LSN = 5000
- 要写入的事务日志内容 = 200字节

#### 步骤1：写入prepare日志

1. **实际写入内容**：
    - 12字节头 + 200字节内容 + 4字节尾 = **216字节**
2. **块对齐处理**：
    - 216字节需要填充到512字节块（下一个对齐边界是512的倍数）
    - 因此实际占用 **512字节**（剩余296字节用0填充）
3. **LSN增长**：
    
    math
    
    ```math
    新LSN = 5000 + 512 = 5512
    ```
    
    （不是示例中的5200，原示例数字为简化说明）

#### 步骤2：写入commit标记

1. **commit日志内容**：
    - 仅需写入特殊标记（约12字节）
2. **块对齐处理**：
    - 12字节头 + 12字节commit标记 + 4字节尾 = 28字节
    - 仍按512字节块写入
3. **LSN增长**：
    
    math
    
    ```math
    新LSN = 5512 + 512 = 6024
    ```
    
    （不是示例中的5400）

---

### 三、为什么看起来"浪费"空间？

1. **可靠性优先**：  
    磁盘 sector 原子写入最小单位是512字节，部分写入可能导致崩溃时数据损坏
2. **性能优化**：  
    对齐写入可避免read-modify-write操作（直接覆盖整个块）
3. **现代优化**：  
    MySQL 8.0引入`innodb_log_write_ahead_size`可配置块大小（通常4KB适配SSD）

---

### 四、真实场景的LSN计算

通过`SHOW ENGINE INNODB STATUS`可观察实际值：

sql

```sql
LOG
---
Log sequence number          1000002  # 当前LSN
Log buffer assigned up to    1000002  
Log buffer completed up to   1000002  
Log written up to            1000002  
Log flushed up to            1000000  # 持久化的LSN
Pages flushed up to           999000   # checkpoint LSN
```

---

### 五、修正后的完整流程

|步骤|操作内容|LSN变化|关键说明|
|---|---|---|---|
|初始状态|-|5000||
|写入prepare日志|216字节有效日志|5000 → 5512|按512字节对齐|
|写入binlog|-|5512（不变）|Server层操作不影响存储引擎LSN|
|写入commit标记|28字节commit标记|5512 → 6024|再次按512字节对齐|

这种设计虽然会"浪费"部分磁盘空间，但确保了：

1. 崩溃恢复时能准确找到所有日志
2. 避免部分写入导致的日志损坏
3. 与现代存储设备特性匹配



![](assets/Pasted%20image%2020251013174023.png)

![](assets/Pasted%20image%2020251013174444.png)
![](assets/Pasted%20image%2020251013174436.png)