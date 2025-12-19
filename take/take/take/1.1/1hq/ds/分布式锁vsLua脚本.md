在秒杀等高并发场景下，**分布式锁** 和 **Lua脚本** 的处理方式确实有显著差异，它们的性能表现和适用场景也不同。下面详细分析两者的行为，并给出优化建议。

---

## **1. 分布式锁 vs. Lua脚本的对比**

|**对比项**|**分布式锁**|**Lua脚本**|
|---|---|---|
|**并发控制方式**|互斥锁（串行化）|Redis 单线程执行（原子性）|
|**等待机制**|竞争锁失败直接返回|排队等待执行|
|**性能瓶颈**|锁竞争激烈时大量请求失败|Redis 单线程执行可能成为瓶颈|
|**适用场景**|需要严格互斥的业务逻辑|简单原子操作（如库存扣减）|
|**业务失败率**|高（锁竞争失败直接拒绝）|低（排队执行，但可能超时）|

---

## **2. 分布式锁的问题**

### **(1) 锁竞争导致高失败率**

- **现象**：大量请求同时抢锁，只有一个成功，其余直接失败（返回“秒杀失败”）。
- **原因**：锁的互斥性导致并发请求无法并行处理。
- **示例代码**：
    
    java
    
    ```java
    public boolean seckill(Long itemId) {
        String lockKey = "seckill:lock:" + itemId;
        try {
            // 抢锁（非阻塞式）
            boolean locked = redisLock.tryLock(lockKey, 100); // 100ms超时
            if (!locked) {
                return false; // 直接失败
            }
            // 执行业务逻辑（扣库存、下单等）
            return doSeckill(itemId);
        } finally {
            redisLock.unlock(lockKey);
        }
    }
    ```
    
    **问题**：99% 的请求可能因抢锁失败直接被拒绝，用户体验差。

### **(2) 锁超时问题**

- 如果业务执行时间过长，可能导致锁自动释放，引发 **超卖**。
- 解决方案：
    - 使用 **Redisson 的 WatchDog 机制**（自动续期）。
    - 或采用 **Lua 脚本** 替代锁。

---

## **3. Lua脚本的优势**

### **(1) Redis 单线程执行，天然串行化**

- **Lua 脚本在 Redis 中是原子执行的**，不会出现并发问题。
- **请求会排队等待**，但不会像分布式锁那样直接失败。
- **示例脚本（库存扣减）**：
    
    lua
    
    ```lua
    local stock = tonumber(redis.call('GET', KEYS[1]))
    if stock > 0 then
        redis.call('DECR', KEYS[1])
        return 1 -- 成功
    else
        return 0 -- 失败
    end
    ```
    
    **Java 调用**：
    
    java
    
    ```java
    String script = "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                   "if stock > 0 then redis.call('DECR', KEYS[1]) return 1 else return 0 end";
    Long result = redisTemplate.execute(
        new DefaultRedisScript<>(script, Long.class),
        Collections.singletonList("seckill:stock:" + itemId)
    );
    return result == 1;
    ```
    

### **(2) 业务失败率低**

- **不会直接拒绝请求**，而是让 Redis 串行执行，避免竞争。
- **适合简单操作**（如库存扣减），但不适合复杂业务（如创建订单）。

---

## **4. Lua脚本的局限性**

### **(1) Redis 单线程可能成为瓶颈**

- 如果 Lua 脚本执行时间过长（如包含复杂逻辑），会导致后续请求堆积，影响吞吐量。
- **优化方案**：
    - **仅用 Lua 处理核心竞争逻辑**（如库存扣减）。
    - **后续业务（如订单创建）异步处理**（MQ + 数据库）。

### **(2) 无法处理外部依赖**

- Lua 脚本 **不能访问数据库、调用外部接口**，仅适用于 Redis 内部操作。
- **解决方案**：
    - **Lua 扣减库存 → MQ 异步创建订单**。
    - **数据库用乐观锁防超卖**：
        
        sql
        
        ```sql
        UPDATE item SET stock = stock - 1 WHERE id = ? AND stock > 0
        ```
        

---

## **5. 最佳实践：分布式锁 + Lua + 异步**

### **(1) 方案设计**

|**步骤**|**技术方案**|**说明**|
|---|---|---|
|**1. 请求拦截**|限流（Nginx / Sentinel）|过滤掉超量请求|
|**2. 库存扣减**|Lua 脚本（原子操作）|避免锁竞争|
|**3. 订单创建**|MQ 异步处理|降低数据库压力|
|**4. 最终一致性**|定时任务补偿|处理失败订单|

### **(2) 代码示例**

java

```java
public boolean seckill(Long userId, Long itemId) {
    // 1. Lua 扣减库存
    String script = "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                   "if stock > 0 then redis.call('DECR', KEYS[1]) return 1 else return 0 end";
    Long result = redisTemplate.execute(
        new DefaultRedisScript<>(script, Long.class),
        Collections.singletonList("seckill:stock:" + itemId)
    );
    if (result != 1) {
        return false; // 库存不足
    }

    // 2. 发送MQ异步创建订单
    mqProducer.send("seckill_order", userId + ":" + itemId);
    return true; // 前端显示“秒杀成功”，订单稍后生成
}
```

---

## **6. 结论**

|**方案**|**适用场景**|**优点**|**缺点**|
|---|---|---|---|
|**分布式锁**|需要严格互斥的业务|强一致性|高失败率|
|**Lua脚本**|简单原子操作（库存扣减）|低失败率|单线程瓶颈|
|**Lua + 异步**|高并发秒杀|高性能 + 最终一致性|架构复杂|

**推荐方案**：

- **核心竞争逻辑（库存扣减）用 Lua 脚本**（避免锁竞争）。
- **复杂业务（订单创建）异步处理**（MQ + 数据库）。
- **结合限流、缓存、数据库乐观锁** 综合优化。

这样既能 **降低业务失败率**，又能 **保证高并发性能**。