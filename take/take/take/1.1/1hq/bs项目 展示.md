
以下是 `BaseDAO.java` 文件中所有方法的详细解析，按功能分类说明其作用和实现逻辑：

---

### **一、核心方法**

#### **1. 构造方法**

java

```java
public BaseDAO() {
    this.entityClass = ReflectionUtil.getSuperClassGenricType(getClass());
}
```

- **作用**：初始化时通过反射获取泛型 `T` 的实际类型（实体类），用于后续所有数据库操作。
- **示例**：若子类定义为 `UserDAO extends BaseDAO<User, Long>`，则 `entityClass = User.class`。

---

### **二、CRUD 操作**

#### **2. 删除操作**

|方法|说明|
|---|---|
|`delete(PK id)`|根据主键删除实体，先通过 `get(id)` 加载实体再删除|
|`delete(PK[] ids)`|批量删除多个实体（循环调用单条删除）|

#### **3. 查询操作**

|方法|说明|
|---|---|
|`get(PK id)`|根据主键获取单个实体|
|`getAll()`|获取所有实体（无分页）|
|`getByIds(List<PK> ids)`|根据主键列表批量查询实体|

---

### **三、条件查询**

#### **4. 基础条件查询**

|方法|说明|
|---|---|
|`get(String propertyName, Object value)`|按属性值精确匹配查询列表|
|`get(Map<String, ?> params)`|多属性组合精确匹配查询|
|`get(Restriction filters)`|使用 `Restriction` 条件对象动态查询|

#### **5. 唯一性查询**

|方法|说明|
|---|---|
|`getUnique(String propertyName, Object value)`|按属性值查询唯一实体（结果不唯一时报错）|
|`getUnique(Map<String, ?> params)`|多属性组合查询唯一实体|
|`getUnique(Restriction filters)`|动态条件查询唯一实体|

---

### **四、分页与排序**

#### **6. 分页查询**

|方法|说明|
|---|---|
|`getPage(Page<?> page)`|分页查询所有实体|
|`getPage(Page<?> page, Restriction filters)`|带条件的分页查询|
|`getPage(SearchCondition sc)`|从 `SearchCondition` 对象解析分页和条件|

#### **7. 分页结果包装**

|方法|说明|
|---|---|
|`getPagingResult(Page<?> page, Restriction filters)`|返回分页数据 + 总记录数（`IPagingResult` 接口）|
|`getPagingResult(SearchCondition sc)`|同上，参数来自 `SearchCondition`|

---

### **五、辅助功能**

#### **8. 计数与校验**

|方法|说明|
|---|---|
|`count()`|统计实体总记录数|
|`isPropertyUnique(String property, Object newVal, Object oldVal)`|校验属性值是否唯一（用于更新时跳过原值）|

#### **9. 排序支持**

- 所有 `get*()` 方法均支持可变参数 `Sort... sorts`，例如：
    
    java
    
    ```java
    get(Sort.asc("createTime"), Sort.desc("id"));
    ```
    

---

### **六、权限控制（注释状态）**

java

```java
// protected void checkEntityPermission(IdEntity entity)
// protected DetachedCriteria getPermissiveCriteria(...)
// protected String getPermissiveHql(String hql)
```

- **作用**：预留的权限校验扩展点，默认未启用。
- **用途**：如需实现数据行级权限控制，可重写这些方法。

---

### **七、关键设计思想**

1. **泛型化实体操作**  
    通过 `Class<T> entityClass` 自动关联实体类型，避免子类重复定义。
    
2. **条件构造器模式**  
    `Restriction` 对象支持链式条件构建：
    
    java
    
    ```java
    Restriction.eq("name", "Alice").gt("age", 20)
    ```
    
3. **分页抽象**  
    `Page` 对象封装页码/页大小，`IPagingResult` 统一分页响应格式。
    
4. **与 Spring 集成**  
    使用 `Assert` 进行参数校验，确保健壮性。
    

---

### **八、典型使用场景**

#### **1. 子类继承示例**

java

```java
public class UserDAO extends BaseDAO<User, Long> {
    // 直接继承所有基础方法
    public User findByName(String name) {
        return getUnique("username", name);
    }
}
```

#### **2. 服务层调用**

java

```java
// 分页查询
Page<User> page = new Page<>(1, 10);
List<User> users = userDAO.getPage(page, Restriction.like("name", "张%"));

// 唯一性校验
if (!userDAO.isPropertyUnique("email", newEmail, oldEmail)) {
    throw new Exception("邮箱已存在");
}
```

---

### **九、注意事项**

1. **性能**：`delete(PK[] ids)` 是循环单条删除，如需批量优化需重写。
2. **事务**：所有方法需在 `@Transactional` 注解下调用。
3. **扩展性**：通过覆盖 `HibernateOperator` 方法可自定义底层实现。

如需进一步了解某个方法的实现细节，可提供具体使用场景。