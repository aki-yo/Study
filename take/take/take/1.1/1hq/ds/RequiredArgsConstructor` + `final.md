在 Spring 框架中，`private final AbsencePeopleServiceImpl absencePeopleService;` 结合 `@RequiredArgsConstructor` 的效果 **类似于 `@Autowired` 自动注入**，但它们的机制有所不同。

---

### **1. `@RequiredArgsConstructor` + `final` 字段的作用**
- **Lombok 生成的构造方法**：  
  如果类中有 `final` 字段（如 `absencePeopleService`），`@RequiredArgsConstructor` 会自动生成一个包含该参数的构造方法：
  ```java
  @RequiredArgsConstructor
  public class SomeService {
      private final AbsencePeopleServiceImpl absencePeopleService;
  }
  ```
  等效代码：
  ```java
  public class SomeService {
      private final AbsencePeopleServiceImpl absencePeopleService;

      // Lombok 生成的构造方法
      public SomeService(AbsencePeopleServiceImpl absencePeopleService) {
          this.absencePeopleService = absencePeopleService;
      }
  }
  ```

- **Spring 的构造方法注入**：  
  Spring 会优先使用 **构造方法** 进行依赖注入。如果类只有一个构造方法，Spring 会自动将 `AbsencePeopleServiceImpl` 的 Bean 注入到参数中，无需额外注解。  
  （如果存在多个构造方法，需要用 `@Autowired` 明确指定。）

---

### **2. 对比 `@Autowired` 字段注入**
- **传统 `@Autowired` 字段注入**：
  ```java
  @Service
  public class SomeService {
      @Autowired
      private AbsencePeopleServiceImpl absencePeopleService;
  }
  ```
  - 直接通过反射注入字段，不需要构造方法或 setter。
  - 但不如构造方法注入推荐（因为不利于不可变性测试）。

- **`@RequiredArgsConstructor` + `final`**：
  - 通过构造方法注入，符合 **不可变对象** 设计（`final` 字段）。
  - 是 Spring 官方推荐的注入方式（显式依赖，易于测试）。

---

### **3. 关键区别**
| 特性                | `@RequiredArgsConstructor` + `final`       | `@Autowired` 字段注入          |
|---------------------|--------------------------------------------|-------------------------------|
| 注入方式            | 构造方法注入                               | 反射直接注入字段               |
| 是否支持 `final`    | ✔️（强制依赖，不可变）                     | ❌（字段不能是 `final`）       |
| 是否显式依赖        | ✔️（通过构造方法明确依赖）                 | ❌（隐藏依赖）                 |
| Spring 官方推荐     | ✔️                                        | ❌（不推荐，但常见于遗留代码） |
| 是否需要 `@Autowired` | ❌（Spring 自动处理单构造方法）            | ✔️                            |

---

### **4. 总结**
- **效果相同**：两者都能实现依赖注入。
- **本质不同**：
  - `@RequiredArgsConstructor` + `final` 是通过 **构造方法注入**，符合最佳实践。
  - `@Autowired` 字段注入是通过反射直接赋值字段。
- **推荐做法**：  
  在 Spring 中，优先使用 `@RequiredArgsConstructor` + `final`，因为：
  1. 强制依赖不可变（`final`）。
  2. 明确依赖关系，便于单元测试。
  3. 避免 `@Autowired` 的魔法性（Magic）。

---

### **补充：如果字段是 `@NonNull` 而非 `final`**
Lombok 的 `@NonNull` 也会触发构造方法生成，但 Spring 不会强制检查非空（除非配合 `@Autowired(required=true)`）。  
推荐直接用 `final` + `@RequiredArgsConstructor`。