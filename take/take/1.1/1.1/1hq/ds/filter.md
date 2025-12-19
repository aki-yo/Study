```
public class XssFilter implements Filter  
{  
    /**  
     * 排除链接  
     */  
    public List<String> excludes = new ArrayList<>();  
  
    /**  
     * xss过滤开关  
     */  
    public boolean enabled = false;  
  
    @Override  
    public void init(FilterConfig filterConfig) throws ServletException  
    {  
        String tempExcludes = filterConfig.getInitParameter("excludes");  
        String tempEnabled = filterConfig.getInitParameter("enabled");
```


在 `filterConfig.getInitParameter("excludes")` 中，`"excludes"` 这个字符串是 **你自己定义的参数名**，并不是 Filter 类中固定写死的属性。它的值来源于你在 **Filter 配置**（如 `web.xml` 或 `@WebFilter` 注解）中指定的 `<init-param>` 或 `@WebInitParam` 的名称。

![](assets/Pasted%20image%2020250725151957.png)



```
package com.o.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.o.common.utils.StringUtils;

/**
 * 防止XSS攻击的过滤器
 * 
 * @author HQ
 */
public class XssFilter implements Filter
{
    /**
     * 排除链接
     */
    public List<String> excludes = new ArrayList<>();

    /**
     * xss过滤开关
     */
    public boolean enabled = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        String tempEnabled = filterConfig.getInitParameter("enabled");
        if (StringUtils.isNotEmpty(tempExcludes))
        {
            String[] url = tempExcludes.split(",");
            for (int i = 0; url != null && i < url.length; i++)
            {
                excludes.add(url[i]);
            }
        }
        if (StringUtils.isNotEmpty(tempEnabled))
        {
            enabled = Boolean.valueOf(tempEnabled);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (handleExcludeURL(req, resp))
        {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response)
    {
        if (!enabled)
        {
            return true;
        }
        if (excludes == null || excludes.isEmpty())
        {
            return false;
        }
        String url = request.getServletPath();
        for (String pattern : excludes)
        {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy()
    {

    }
}

xss攻击是什么，这个是怎么防护的
```



# XSS攻击及防护解析

## 什么是XSS攻击？

XSS（Cross-Site Scripting，跨站脚本攻击）是一种常见的Web安全漏洞，攻击者通过在网页中注入恶意脚本代码，当其他用户浏览该页面时，这些脚本会在用户的浏览器中执行，从而达到窃取用户数据、会话劫持等恶意目的。

### XSS攻击主要分为三类：

1. **反射型XSS**：恶意脚本作为请求的一部分发送到服务器，然后服务器将脚本"反射"回响应中
2. **存储型XSS**：恶意脚本被永久存储在目标服务器上（如数据库）
3. **DOM型XSS**：完全在客户端发生的XSS攻击，不涉及服务器响应

## 这段代码如何防护XSS？

这个`XssFilter`是一个Servlet过滤器，主要通过以下方式防护XSS攻击：

### 1. 请求参数过滤

核心防护在`XssHttpServletRequestWrapper`类中（虽然代码中没有展示完整实现，但通常它会）：

- 对所有传入的请求参数进行HTML标签和JavaScript代码的过滤或转义
- 将危险字符如`<`, `>`, `"`, `'`, `&`等转换为HTML实体（如`<`变为`&lt;`）
- 移除或禁用可能执行脚本的属性（如`onclick`, `onload`等）

### 2. 排除路径机制

```java
public List<String> excludes = new ArrayList<>();
```

- 通过`excludes`列表配置不需要过滤的URL路径
- 在`handleExcludeURL`方法中使用正则表达式匹配当前请求路径
- 如果匹配到排除路径，则跳过XSS过滤

### 3. 全局开关控制

```java
public boolean enabled = false;
```

- 提供`enabled`开关，可动态启用/禁用整个过滤器
- 在`handleExcludeURL`中首先检查是否启用

### 4. 初始化配置

```java
public void init(FilterConfig filterConfig) {
    String tempExcludes = filterConfig.getInitParameter("excludes");
    String tempEnabled = filterConfig.getInitParameter("enabled");
    //...
}
```

- 从web.xml或注解配置中读取排除路径和启用状态
- 提供灵活的配置方式，无需修改代码即可调整防护策略

## 防护原理

1. **输入过滤**：在请求到达业务逻辑前，对所有用户输入进行净化
2. **上下文感知**：根据数据将要放置的上下文（HTML、JS、CSS等）进行适当的转义
3. **白名单机制**：允许已知安全的HTML标签和属性，拒绝其他所有内容
4. **黑名单机制**：阻止已知危险的脚本和表达式

## 使用建议

1. 在web.xml中配置需要过滤的URL模式（通常为`/*`）
2. 为静态资源、API接口等配置适当的排除路径
3. 在生产环境中保持enabled为true
4. 结合其他安全措施如CSP（内容安全策略）使用

## 局限性

1. 仅防护服务器端反射型和存储型XSS
2. 无法防护纯客户端DOM型XSS
3. 需要定期更新过滤规则以应对新的攻击手法

这个过滤器是XSS防护的第一道防线，但完整的安全方案还应包括输出编码、HTTP安全头设置等多层防护。



```
package com.o.common.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.o.common.utils.StringUtils;
import com.o.common.utils.html.EscapeUtil;

/**
 * XSS过滤处理
 * 
 * @author HQ
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    /**
     * @param request
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request)
    {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name)
    {
        String[] values = super.getParameterValues(name);
        if (values != null)
        {
            int length = values.length;
            String[] escapseValues = new String[length];
            for (int i = 0; i < length; i++)
            {
                // 防xss攻击和过滤前后空格
                escapseValues[i] = EscapeUtil.clean(values[i]).trim();
            }
            return escapseValues;
        }
        return super.getParameterValues(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        // 非json类型，直接返回
        if (!isJsonRequest())
        {
            return super.getInputStream();
        }

        // 为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), "utf-8");
        if (StringUtils.isEmpty(json))
        {
            return super.getInputStream();
        }

        // xss过滤
        json = EscapeUtil.clean(json).trim();
        final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("utf-8"));
        return new ServletInputStream()
        {
            @Override
            public boolean isFinished()
            {
                return true;
            }

            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {
            }

            @Override
            public int read() throws IOException
            {
                return bis.read();
            }
        };
    }

    /**
     * 是否是Json请求
     * 
     * @param request
     */
    public boolean isJsonRequest()
    {
        String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(header)
                || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(header);
    }
}

这是补充的切实实现，告诉我怎么具体实现的
```


# XssHttpServletRequestWrapper 具体实现解析

这个类是实现XSS防护的核心部分，它通过包装HttpServletRequest来拦截和净化所有传入的请求数据。下面我将详细解析它的具体实现方式：

## 1. 基本结构和工作原理

`XssHttpServletRequestWrapper`继承自`HttpServletRequestWrapper`，这是Servlet API提供的请求包装器，允许我们修改请求参数。它主要通过以下两种方式处理XSS防护：

- **表单参数过滤**：覆盖`getParameterValues()`方法
- **JSON请求体过滤**：覆盖`getInputStream()`方法

## 2. 表单参数过滤实现

```java
@Override
public String[] getParameterValues(String name) {
    String[] values = super.getParameterValues(name);
    if (values != null) {
        int length = values.length;
        String[] escapseValues = new String[length];
        for (int i = 0; i < length; i++) {
            // 防xss攻击和过滤前后空格
            escapseValues[i] = EscapeUtil.clean(values[i]).trim();
        }
        return escapseValues;
    }
    return super.getParameterValues(name);
}
```

### 实现细节：
1. 获取原始请求参数值数组
2. 创建新数组用于存储净化后的值
3. 对每个参数值：
   - 使用`EscapeUtil.clean()`进行XSS过滤
   - 调用`trim()`去除前后空格
4. 返回净化后的参数数组

## 3. JSON请求体过滤实现

```java
@Override
public ServletInputStream getInputStream() throws IOException {
    // 非json类型，直接返回
    if (!isJsonRequest()) {
        return super.getInputStream();
    }

    // 为空，直接返回
    String json = IOUtils.toString(super.getInputStream(), "utf-8");
    if (StringUtils.isEmpty(json)) {
        return super.getInputStream();
    }

    // xss过滤
    json = EscapeUtil.clean(json).trim();
    final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("utf-8"));
    return new ServletInputStream() {
        @Override public boolean isFinished() { return true; }
        @Override public boolean isReady() { return true; }
        @Override public void setReadListener(ReadListener readListener) {}
        @Override public int read() throws IOException { return bis.read(); }
    };
}
```

### 实现细节：
1. 首先检查是否是JSON请求（通过Content-Type头判断）
2. 如果不是JSON请求，直接返回原始输入流
3. 读取原始请求体内容到字符串
4. 如果内容为空，直接返回原始输入流
5. 对JSON字符串进行XSS过滤和trim处理
6. 将净化后的JSON字符串转换为新的输入流返回

## 4. JSON请求检测方法

```java
public boolean isJsonRequest() {
    String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
    return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(header)
            || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(header);
}
```

检查Content-Type头是否为：
- `application/json`
- `application/json;charset=UTF-8`

## 5. 关键安全处理点

1. **EscapeUtil.clean()**：
   - 这是实际执行XSS过滤的核心方法（代码中未展示实现）
   - 通常包括HTML标签转义、JavaScript代码移除等操作

2. **输入流处理**：
   - 对于JSON请求，需要完整读取、过滤后重新生成输入流
   - 确保过滤后的数据可以被后续处理器正确解析

3. **空值处理**：
   - 对null或空字符串不做处理，避免NPE

## 6. 防护范围

这个实现主要防护以下类型的XSS攻击：

1. **反射型XSS**：通过URL参数或表单提交的恶意脚本
2. **存储型XSS**：通过JSON API提交的恶意内容
3. **部分DOM型XSS**：如果恶意代码是通过服务器返回的

## 7. 使用注意事项

1. **性能考虑**：
   - JSON请求需要完整读取和重写输入流，有一定性能开销
   - 对于大文件上传等场景，应考虑排除过滤

2. **与其他过滤器的顺序**：
   - 应该在其他参数处理过滤器之前执行

3. **Content-Type依赖**：
   - 依赖客户端正确设置Content-Type头

4. **字符编码**：
   - 硬编码使用UTF-8，可能与某些场景不兼容

这个实现提供了基础的XSS防护，但实际项目中可能需要根据具体业务需求进行增强，比如添加更复杂的白名单规则、处理更多内容类型等。



```
package com.o.common.utils.html;

import com.o.common.utils.StringUtils;

/**
 * 转义和反转义工具类
 * 
 * @author HQ
 */
public class EscapeUtil
{
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

    private static final char[][] TEXT = new char[64][];

    static
    {
        for (int i = 0; i < 64; i++)
        {
            TEXT[i] = new char[] { (char) i };
        }

        // special HTML characters
        TEXT['\''] = "&#039;".toCharArray(); // 单引号
        TEXT['"'] = "&#34;".toCharArray(); // 单引号
        TEXT['&'] = "&#38;".toCharArray(); // &符
        TEXT['<'] = "&#60;".toCharArray(); // 小于号
        TEXT['>'] = "&#62;".toCharArray(); // 大于号
    }

    /**
     * 转义文本中的HTML字符为安全的字符
     * 
     * @param text 被转义的文本
     * @return 转义后的文本
     */
    public static String escape(String text)
    {
        return encode(text);
    }

    /**
     * 还原被转义的HTML特殊字符
     * 
     * @param content 包含转义符的HTML内容
     * @return 转换后的字符串
     */
    public static String unescape(String content)
    {
        return decode(content);
    }

    /**
     * 清除所有HTML标签，但是不删除标签内的内容
     * 
     * @param content 文本
     * @return 清除标签后的文本
     */
    public static String clean(String content)
    {
        return new HTMLFilter().filter(content);
    }

    /**
     * Escape编码
     * 
     * @param text 被编码的文本
     * @return 编码后的字符
     */
    private static String encode(String text)
    {
        int len;
        if ((text == null) || ((len = text.length()) == 0))
        {
            return StringUtils.EMPTY;
        }
        StringBuilder buffer = new StringBuilder(len + (len >> 2));
        char c;
        for (int i = 0; i < len; i++)
        {
            c = text.charAt(i);
            if (c < 64)
            {
                buffer.append(TEXT[c]);
            }
            else
            {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * Escape解码
     * 
     * @param content 被转义的内容
     * @return 解码后的字符串
     */
    public static String decode(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return content;
        }

        StringBuilder tmp = new StringBuilder(content.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < content.length())
        {
            pos = content.indexOf("%", lastPos);
            if (pos == lastPos)
            {
                if (content.charAt(pos + 1) == 'u')
                {
                    ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                }
                else
                {
                    ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            }
            else
            {
                if (pos == -1)
                {
                    tmp.append(content.substring(lastPos));
                    lastPos = content.length();
                }
                else
                {
                    tmp.append(content.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static void main(String[] args)
    {
        String html = "alert('11111');";
        System.out.println(EscapeUtil.clean(html));
        System.out.println(EscapeUtil.escape(html));
        System.out.println(EscapeUtil.unescape(html));
    }
}


这是补充的实际过滤逻辑，给出解释
```


# EscapeUtil XSS过滤实现深度解析

这个工具类提供了HTML/XSS防护的核心转义和过滤功能，下面我将详细解释其实现原理和各个方法的作用：

## 1. 类结构与常量定义

```java
public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

private static final char[][] TEXT = new char[64][];
```

- `RE_HTML_MARK`：用于匹配所有HTML标签的正则表达式模式
- `TEXT`数组：预定义的字符转义映射表，初始化时会对ASCII码0-63的字符进行处理

## 2. 静态初始化块

```java
static {
    for (int i = 0; i < 64; i++) {
        TEXT[i] = new char[] { (char) i };
    }
    // 特殊HTML字符转义
    TEXT['\''] = "&#039;".toCharArray(); // 单引号
    TEXT['"'] = "&#34;".toCharArray();  // 双引号
    TEXT['&'] = "&#38;".toCharArray();  // &符
    TEXT['<'] = "&#60;".toCharArray();  // 小于号
    TEXT['>'] = "&#62;".toCharArray();  // 大于号
}
```

这里预定义了5种需要转义的特殊HTML字符，将它们转换为对应的HTML实体编码。

## 3. 核心方法解析

### 3.1 `clean(String content)` - HTML标签清除

```java
public static String clean(String content) {
    return new HTMLFilter().filter(content);
}
```

这是最严格的过滤方法：
- 使用`HTMLFilter`(虽然代码中未展示)移除所有HTML标签
- 但会保留标签内的文本内容
- 示例：`<script>alert(1)</script>` → `alert(1)`

### 3.2 `escape(String text)` - HTML字符转义

```java
public static String escape(String text) {
    return encode(text);
}

private static String encode(String text) {
    // 实现细节...
}
```

这个方法：
1. 将5种特殊字符(`'`, `"`, `&`, `<`, `>`)转换为HTML实体
2. 其他字符原样保留
3. 示例：`<a href='test'>` → `&#60;a href=&#039;test&#039;&#62;`

### 3.3 `unescape(String content)` - 反转义

```java
public static String unescape(String content) {
    return decode(content);
}

public static String decode(String content) {
    // 实现细节...
}
```

这个方法处理URL编码的字符串：
- 将`%xx`形式的编码转换为对应字符
- 将`%uxxxx`形式的Unicode编码转换为对应字符
- 示例：`%3Cscript%3E` → `<script>`

## 4. 编码/解码实现细节

### 4.1 `encode`方法实现

```java
StringBuilder buffer = new StringBuilder(len + (len >> 2));
char c;
for (int i = 0; i < len; i++) {
    c = text.charAt(i);
    if (c < 64) {
        buffer.append(TEXT[c]); // 使用预定义的转义字符
    } else {
        buffer.append(c); // 其他字符原样输出
    }
}
```

- 预分配StringBuilder大小(原始长度+25%缓冲)
- 只处理ASCII码小于64的字符
- 利用预定义的TEXT数组进行快速转义

### 4.2 `decode`方法实现

```java
while (lastPos < content.length()) {
    pos = content.indexOf("%", lastPos);
    if (pos == lastPos) {
        if (content.charAt(pos + 1) == 'u') {
            // 处理Unicode转义(%uxxxx)
            ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
            lastPos = pos + 6;
        } else {
            // 处理普通转义(%xx)
            ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
            lastPos = pos + 3;
        }
        tmp.append(ch);
    } else {
        // 处理非转义部分
        if (pos == -1) {
            tmp.append(content.substring(lastPos));
            lastPos = content.length();
        } else {
            tmp.append(content.substring(lastPos, pos));
            lastPos = pos;
        }
    }
}
```

- 逐个查找`%`符号
- 区分普通URL编码(`%xx`)和Unicode编码(`%uxxxx`)
- 将编码转换为实际字符

## 5. 安全防护特点

1. **多层防护**：
   - `clean()`完全移除HTML标签
   - `escape()`转义特殊字符但保留标签结构

2. **性能优化**：
   - 使用预定义的转义表(TEXT数组)
   - 预分配StringBuilder大小

3. **上下文感知**：
   - 区分内容清理(`clean`)和字符转义(`escape`)

4. **可逆操作**：
   - 提供`unescape`方法用于需要原始数据的场景

## 6. 使用场景建议

- **用户输入处理**：优先使用`clean()`彻底移除HTML标签
- **HTML内容展示**：使用`escape()`转义特殊字符
- **数据还原**：谨慎使用`unescape()`，确保安全性

## 7. 潜在限制

1. 不处理CSS和JavaScript上下文中的XSS
2. 对现代Web攻击如SVG XSS防护有限
3. HTMLFilter实现未展示，可能影响最终过滤效果

这个工具类提供了基础的XSS防护功能，在实际使用中可能需要根据具体业务需求进行增强或配合其他安全措施使用。


# 初始化就一次读取配置就一次


在你的代码示例中，`init()` 方法是 Servlet Filter 生命周期的一部分，它**只会在 Filter 初始化时被调用一次**，而不是每次请求过来时都调用。

### 具体流程说明：

1. **初始化阶段（一次性的）**：
   - 当 Spring Boot 应用启动时，Servlet 容器（如 Tomcat）会初始化这个 Filter。
   - `init()` 方法会被调用，从 `FilterConfig` 中读取配置参数（如 `excludes` 和 `enabled`），并保存到类的成员变量中。
   - 这些配置值会一直保留在内存中，供后续的请求处理使用。

2. **请求处理阶段（每次请求）**：
   - 对于每个 HTTP 请求，Servlet 容器会调用 Filter 的 `doFilter()` 方法（你的代码中未展示，但必须实现）。
   - 在 `doFilter()` 中，你可以根据初始化时加载的配置（如 `enabled` 和 `excludes`）决定是否对当前请求进行 XSS 过滤。

### 为什么不是每次请求都读取配置？
- 性能考虑：从配置源（如 `web.xml` 或 Spring 配置）重复读取配置会带来不必要的 I/O 开销。
- 设计原则：Filter 的配置通常是静态的，适合在初始化时加载一次。

### 补充说明（Spring Boot 场景）：
- 如果你的 Filter 是通过 `@Component` 注册的，配置可能来自 `application.properties`，此时建议通过 `@Value` 直接注入配置，而非手动解析 `FilterConfig`。
- 如果通过 `web.xml` 或 `FilterRegistrationBean` 注册，`FilterConfig` 的参数会来自注册时的初始化参数。

### 示例修正（完整逻辑）：
```java
@Override
public void init(FilterConfig filterConfig) {
    // 仅初始化时运行一次
    this.enabled = Boolean.parseBoolean(filterConfig.getInitParameter("enabled"));
    String excludesParam = filterConfig.getInitParameter("excludes");
    if (excludesParam != null) {
        this.excludes = Arrays.asList(excludesParam.split(","));
    }
}

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    // 每次请求都会调用这里
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    if (!enabled || excludes.contains(httpRequest.getRequestURI())) {
        chain.doFilter(request, response); // 跳过过滤
        return;
    }
    // 执行 XSS 过滤逻辑
    XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(httpRequest);
    chain.doFilter(wrappedRequest, response);
}
```

### 总结：
- `init()`：**一次性**初始化配置。
- `doFilter()`：**每次请求**处理时根据配置决定行为。
- 