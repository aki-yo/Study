`@Resource` 和 `@Autowired` 是 Java 中两种常用的依赖注入（Dependency Injection, DI）注解，但它们在工作机制、来源标准和适用场景上有显著区别。以下是详细对比：

---

### **1. 来源与标准**
| **注解**       | **所属规范**               | **包路径**                     |
|----------------|--------------------------|-------------------------------|
| `@Autowired`   | Spring 框架独有           | `org.springframework.beans.factory.annotation` |
| `@Resource`    | Java 标准（JSR-250）      | `javax.annotation`（JDK 内置） |

- **关键区别**：  
  `@Resource` 是 Java 通用标准，不依赖 Spring，也可用于其他支持 JSR-250 的框架（如 Jakarta EE）；  
  `@Autowired` 是 Spring 专属，功能更强大但与 Spring 强绑定。

---

### **2. 注入机制**
#### **(1) `@Autowired`**
- **默认按类型（byType）注入**：  
  根据字段/方法的参数类型匹配 Spring 容器中的 Bean。  
  ```java
  @Autowired
  private UserService userService; // 注入 UserService 类型的 Bean
  ```
- **存在多个同类型 Bean 时**：  
  需配合 `@Qualifier("beanName")` 指定名称，否则抛出 `NoUniqueBeanDefinitionException`。  
  ```java
  @Autowired
  @Qualifier("userServiceImplA")
  private UserService userService;
  ```

#### **(2) `@Resource`**
- **默认按名称（byName）注入**：  
  先根据字段/方法名匹配 Bean 名称，找不到再按类型匹配。  
  ```java
  @Resource
  private UserService userService; // 先找名为 "userService" 的 Bean
  ```
- **显式指定名称**：  
  通过 `name` 属性直接指定 Bean 名称。  
  ```java
  @Resource(name = "userServiceImplA")
  private UserService myService;
  ```

---

### **3. 适用场景对比**
| **场景**                     | **推荐注解**       | **理由**                                                                 |
|------------------------------|------------------|-------------------------------------------------------------------------|
| **需要强类型安全**            | `@Autowired`     | 编译期可检查类型匹配，减少运行时错误。                                   |
| **需要精确指定 Bean 名称**    | `@Resource`      | 直接通过 `name` 属性指定，代码更直观。                                   |
| **非 Spring 环境（如 Jakarta EE）** | `@Resource`      | 作为 Java 标准注解，兼容性更好。                                         |
| **需要处理多实现类**          | `@Autowired` + `@Qualifier` | Spring 提供更灵活的组合控制。                                           |
| **简单注入（无冲突）**        | 两者均可          | 根据团队规范选择。                                                       |

---

### **4. 其他差异**
| **特性**               | `@Autowired`                  | `@Resource`                   |
|------------------------|-------------------------------|-------------------------------|
| **是否支持构造器注入**  | ✅ 是（推荐方式）              | ❌ 否（仅支持字段/方法注入）     |
| **是否支持 `required` 属性** | ✅ 可设为 `false`（允许 null） | ❌ 无此功能                     |
| **是否支持集合注入**    | ✅ 可注入 `List<BeanType>`     | ❌ 不支持                       |

---

### **5. 代码示例**
#### **(1) `@Autowired` 的典型用法**
```java
@Service
public class OrderService {
    // 按类型注入
    @Autowired
    private PaymentService paymentService;

    // 处理多个同类型 Bean
    @Autowired
    @Qualifier("smsNotifier")
    private Notifier notifier;
}
```

#### **(2) `@Resource` 的典型用法**
```java
@Service
public class OrderService {
    // 按名称注入（字段名需与 Bean 名一致）
    @Resource
    private PaymentService paymentService;

    // 显式指定 Bean 名称
    @Resource(name = "emailNotifier")
    private Notifier notifier;
}
```

---

### **6. 如何选择？**
- **Spring 项目**：  
  - 优先用 `@Autowired`（尤其需要构造器注入或集合注入时）。  
  - 需要按名称注入时，可用 `@Resource` 简化代码。  
- **多框架兼容项目**：  
  选择 `@Resource` 避免对 Spring 的强依赖。  
- **团队规范**：  
  统一代码风格（例如强制使用 `@Autowired` 以保持一致性）。

---

### **总结**
- **`@Autowired`**：Spring 生态首选，功能强大，适合复杂场景。  
- **`@Resource`**：Java 标准，简单直观，适合需要兼容性或按名称注入的场景。  
- **避免混用**：同一项目中尽量统一注解，减少维护成本。