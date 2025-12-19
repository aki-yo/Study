在 RESTful API 设计中，HTTP 方法（`POST`、`GET`、`PUT`、`DELETE`）和参数传递方式（`@PathVariable`、`@RequestParam`、`@RequestBody`）的使用有明确的规范。以下是它们的典型用法和示例：

路径变量方式传递参数适用于 必传字段

查询参数适合非必传字段

请求体传递
安全，post put patch
用于登录，批量操作

---

## **1. HTTP 方法的使用场景**

|方法|用途|幂等性|请求体|示例场景|
|---|---|---|---|---|
|`GET`|**查询数据**|是|无|获取用户信息、分页查询|
|`POST`|**新增数据**|否|有|创建订单、提交表单|
|`PUT`|**全量更新数据**|是|有|修改用户信息（全部字段）|
|`DELETE`|**删除数据**|是|无|删除用户|
|`PATCH`|**部分更新数据**|否|有|修改用户昵称（仅更新个别字段）|

---

## **2. 参数传递方式**

### **(1) `@PathVariable`（路径变量）**

- **用途**：从 URL 路径中获取参数（如 `/users/{id}`）。
- **示例**：
    
    java
    
    ```java
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }
    ```
    
    - 请求：`GET /users/1`
    - 结果：获取 ID=1 的用户。

### **(2) `@RequestParam`（查询参数）**

- **用途**：从 URL 的 `?` 后获取参数（如 `/users?name=John`）。
- **示例**：
    
    java
    
    ```java
    @GetMapping("/users")
    public List<User> getUsers(@RequestParam String name) {
        return userService.findByName(name);
    }
    ```
    
    - 请求：`GET /users?name=John`
    - 结果：返回所有名字为 `John` 的用户。

### **(3) `@RequestBody`（请求体）**

- **用途**：从请求体（JSON/XML）中获取复杂对象（通常用于 `POST`/`PUT`）。
- **示例**：
    
    java
    
    ```java
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }
    ```
    
    - 请求：`POST /users`
        
        json
        
        ```json
        { "name": "John", "age": 25 }
        ```
        
    - 结果：创建新用户。

---

## **3. 综合使用示例**

### **(1) 查询（GET + `@RequestParam`）**

java

```java
@GetMapping("/orders")
public List<Order> getOrders(
    @RequestParam(required = false) String orderNo,
    @RequestParam(required = false) String status
) {
    return orderService.findByConditions(orderNo, status);
}
```

- 请求：`GET /orders?orderNo=123&status=PAID`
- 结果：返回订单号包含 `123` 且状态为 `PAID` 的订单。

### **(2) 新增（POST + `@RequestBody`）**

java

```java
@PostMapping("/orders")
public Order createOrder(@RequestBody Order order) {
    return orderService.create(order);
}
```

- 请求：`POST /orders`
    
    json
    
    ```json
    { "orderNo": "123", "customerName": "John" }
    ```
    
- 结果：创建新订单。

### **(3) 更新（PUT + `@PathVariable` + `@RequestBody`）**

java

```java
@PutMapping("/orders/{id}")
public Order updateOrder(
    @PathVariable Long id,
    @RequestBody Order order
) {
    order.setId(id);
    return orderService.update(order);
}
```

- 请求：`PUT /orders/1`
    
    json
    
    ```json
    { "orderNo": "456", "customerName": "Alice" }
    ```
    
- 结果：更新 ID=1 的订单。

### **(4) 删除（DELETE + `@PathVariable`）**

java

```java
@DeleteMapping("/orders/{id}")
public void deleteOrder(@PathVariable Long id) {
    orderService.deleteById(id);
}
```

- 请求：`DELETE /orders/1`
- 结果：删除 ID=1 的订单。

---

## **4. 最佳实践**

1. **语义化路径**：
    - 资源用名词（如 `/users`），避免动词（如 `/getUsers`）。
2. **参数选择**：
    - 简单条件用 `@RequestParam`，ID 用 `@PathVariable`，复杂对象用 `@RequestBody`。
3. **幂等性**：
    - `GET`、`PUT`、`DELETE` 是幂等的（多次请求结果相同），`POST` 不是。
4. **状态码**：
    - `GET` → `200 OK`
    - `POST` → `201 Created`
    - `PUT` → `200 OK` 或 `204 No Content`
    - `DELETE` → `204 No Content`

---

通过这种方式，你的 API 会符合 RESTful 规范，易于理解和使用！ 🚀