# UUID 类详解

这个 `UUID` 类是一个完整的通用唯一识别码实现，提供了多种生成和处理 UUID 的方法。下面我将详细解释每个部分的功能和实现原理。

## 1. 类定义和基础属性

```java
public final class UUID implements java.io.Serializable, Comparable<UUID>
{
    private static final long serialVersionUID = -1185015143654744140L;
    
    private static class Holder {
        static final SecureRandom numberGenerator = getSecureRandom();
    }
    
    private final long mostSigBits;  // 最高64位
    private final long leastSigBits; // 最低64位
}
```

- **final类**：防止被继承，确保UUID的不可变性
- **Serializable**：支持序列化
- **Comparable**：提供比较功能
- **Holder模式**：延迟初始化SecureRandom，确保线程安全
- **128位存储**：分为两个64位long值存储

## 2. 构造方法

```java
private UUID(byte[] data) {
    // 从16字节数组构造UUID
}

public UUID(long mostSigBits, long leastSigBits) {
    // 直接从两个long值构造
}
```

- 提供了两种构造方式：字节数组和直接long值
- 私有构造方法确保只能通过工厂方法创建实例

## 3. UUID生成方法

### 3.1 随机UUID生成

```java
public static UUID fastUUID() // 快速但不安全的随机
public static UUID randomUUID() // 安全的随机
public static UUID randomUUID(boolean isSecure) // 可选择安全性的随机
```

- 生成版本4的UUID（随机生成）
- `fastUUID()`使用性能更好的`ThreadLocalRandom`
- `randomUUID()`使用加密安全的`SecureRandom`

### 3.2 基于名称的UUID生成

```java
public static UUID nameUUIDFromBytes(byte[] name)
```

- 生成版本3的UUID（基于名称）
- 使用MD5哈希算法处理输入字节

### 3.3 从字符串解析

```java
public static UUID fromString(String name)
```

- 从标准UUID字符串格式解析
- 格式要求：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

## 4. 信息获取方法

```java
public long getLeastSignificantBits() // 获取低64位
public long getMostSignificantBits() // 获取高64位
public int version() // 获取版本号(1-4)
public int variant() // 获取变体号
public long timestamp() // 获取时间戳(仅版本1)
public int clockSequence() // 获取时钟序列(仅版本1)
public long node() // 获取节点值(仅版本1)
```

- 提供对UUID内部信息的访问
- 时间相关方法仅适用于版本1的UUID

## 5. 工具方法

### 5.1 字符串表示

```java
public String toString() // 标准格式
public String toString(boolean isSimple) // 可选是否包含连字符
```

- 支持标准格式和简化格式输出

### 5.2 对象基本方法

```java
public int hashCode()
public boolean equals(Object obj)
public int compareTo(UUID val)
```

- 实现了对象比较和哈希的基本契约

## 6. 私有辅助方法

```java
private static String digits(long val, int digits) // 数字转hex
private void checkTimeBase() // 检查是否为时间型UUID
public static SecureRandom getSecureRandom() // 获取安全随机数生成器
public static ThreadLocalRandom getRandom() // 获取高性能随机数生成器
```

- 内部使用的工具方法
- 提供两种随机数生成器选择

## 7. 设计特点

1. **不可变对象**：所有属性都是final，线程安全
2. **多种生成方式**：随机、基于名称、从字符串解析
3. **性能考虑**：提供快速和安全两种随机生成选择
4. **完整信息获取**：可以获取UUID的所有组成部分
5. **标准兼容**：遵循RFC 4122规范

这个实现比Java标准库的UUID类提供了更多灵活性，特别是在随机数生成器的选择和字符串格式化方面。

# 代码

```
package com.o.common.core.lang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import com.o.common.exception.UtilException;

/**
 * 提供通用唯一识别码（universally unique identifier）（UUID）实现
 *
 * @author HQ
 */
public final class UUID implements java.io.Serializable, Comparable<UUID>
{
    private static final long serialVersionUID = -1185015143654744140L;

    /**
     * SecureRandom 的单例
     *
     */
    private static class Holder
    {
        static final SecureRandom numberGenerator = getSecureRandom();
    }

    /** 此UUID的最高64有效位 */
    private final long mostSigBits;

    /** 此UUID的最低64有效位 */
    private final long leastSigBits;

    /**
     * 私有构造
     * 
     * @param data 数据
     */
    private UUID(byte[] data)
    {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";
        for (int i = 0; i < 8; i++)
        {
            msb = (msb << 8) | (data[i] & 0xff);
        }
        for (int i = 8; i < 16; i++)
        {
            lsb = (lsb << 8) | (data[i] & 0xff);
        }
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * 使用指定的数据构造新的 UUID。
     *
     * @param mostSigBits 用于 {@code UUID} 的最高有效 64 位
     * @param leastSigBits 用于 {@code UUID} 的最低有效 64 位
     */
    public UUID(long mostSigBits, long leastSigBits)
    {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的本地线程伪随机数生成器生成该 UUID。
     * 
     * @return 随机生成的 {@code UUID}
     */
    public static UUID fastUUID()
    {
        return randomUUID(false);
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
     * 
     * @return 随机生成的 {@code UUID}
     */
    public static UUID randomUUID()
    {
        return randomUUID(true);
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
     * 
     * @param isSecure 是否使用{@link SecureRandom}如果是可以获得更安全的随机码，否则可以得到更好的性能
     * @return 随机生成的 {@code UUID}
     */
    public static UUID randomUUID(boolean isSecure)
    {
        final Random ng = isSecure ? Holder.numberGenerator : getRandom();

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f; /* clear version */
        randomBytes[6] |= 0x40; /* set to version 4 */
        randomBytes[8] &= 0x3f; /* clear variant */
        randomBytes[8] |= 0x80; /* set to IETF variant */
        return new UUID(randomBytes);
    }

    /**
     * 根据指定的字节数组获取类型 3（基于名称的）UUID 的静态工厂。
     *
     * @param name 用于构造 UUID 的字节数组。
     *
     * @return 根据指定数组生成的 {@code UUID}
     */
    public static UUID nameUUIDFromBytes(byte[] name)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new InternalError("MD5 not supported");
        }
        byte[] md5Bytes = md.digest(name);
        md5Bytes[6] &= 0x0f; /* clear version */
        md5Bytes[6] |= 0x30; /* set to version 3 */
        md5Bytes[8] &= 0x3f; /* clear variant */
        md5Bytes[8] |= 0x80; /* set to IETF variant */
        return new UUID(md5Bytes);
    }

    /**
     * 根据 {@link #toString()} 方法中描述的字符串标准表示形式创建{@code UUID}。
     *
     * @param name 指定 {@code UUID} 字符串
     * @return 具有指定值的 {@code UUID}
     * @throws IllegalArgumentException 如果 name 与 {@link #toString} 中描述的字符串表示形式不符抛出此异常
     *
     */
    public static UUID fromString(String name)
    {
        String[] components = name.split("-");
        if (components.length != 5)
        {
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        }
        for (int i = 0; i < 5; i++)
        {
            components[i] = "0x" + components[i];
        }

        long mostSigBits = Long.decode(components[0]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]).longValue();

        long leastSigBits = Long.decode(components[3]).longValue();
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]).longValue();

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * 返回此 UUID 的 128 位值中的最低有效 64 位。
     *
     * @return 此 UUID 的 128 位值中的最低有效 64 位。
     */
    public long getLeastSignificantBits()
    {
        return leastSigBits;
    }

    /**
     * 返回此 UUID 的 128 位值中的最高有效 64 位。
     *
     * @return 此 UUID 的 128 位值中最高有效 64 位。
     */
    public long getMostSignificantBits()
    {
        return mostSigBits;
    }

    /**
     * 与此 {@code UUID} 相关联的版本号. 版本号描述此 {@code UUID} 是如何生成的。
     * <p>
     * 版本号具有以下含意:
     * <ul>
     * <li>1 基于时间的 UUID
     * <li>2 DCE 安全 UUID
     * <li>3 基于名称的 UUID
     * <li>4 随机生成的 UUID
     * </ul>
     *
     * @return 此 {@code UUID} 的版本号
     */
    public int version()
    {
        // Version is bits masked by 0x000000000000F000 in MS long
        return (int) ((mostSigBits >> 12) & 0x0f);
    }

    /**
     * 与此 {@code UUID} 相关联的变体号。变体号描述 {@code UUID} 的布局。
     * <p>
     * 变体号具有以下含意：
     * <ul>
     * <li>0 为 NCS 向后兼容保留
     * <li>2 <a href="http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122</a>(Leach-Salz), 用于此类
     * <li>6 保留，微软向后兼容
     * <li>7 保留供以后定义使用
     * </ul>
     *
     * @return 此 {@code UUID} 相关联的变体号
     */
    public int variant()
    {
        // This field is composed of a varying number of bits.
        // 0 - - Reserved for NCS backward compatibility
        // 1 0 - The IETF aka Leach-Salz variant (used by this class)
        // 1 1 0 Reserved, Microsoft backward compatibility
        // 1 1 1 Reserved for future definition.
        return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62))) & (leastSigBits >> 63));
    }

    /**
     * 与此 UUID 相关联的时间戳值。
     *
     * <p>
     * 60 位的时间戳值根据此 {@code UUID} 的 time_low、time_mid 和 time_hi 字段构造。<br>
     * 所得到的时间戳以 100 毫微秒为单位，从 UTC（通用协调时间） 1582 年 10 月 15 日零时开始。
     *
     * <p>
     * 时间戳值仅在在基于时间的 UUID（其 version 类型为 1）中才有意义。<br>
     * 如果此 {@code UUID} 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException 如果此 {@code UUID} 不是 version 为 1 的 UUID。
     */
    public long timestamp() throws UnsupportedOperationException
    {
        checkTimeBase();
        return (mostSigBits & 0x0FFFL) << 48//
                | ((mostSigBits >> 16) & 0x0FFFFL) << 32//
                | mostSigBits >>> 32;
    }

    /**
     * 与此 UUID 相关联的时钟序列值。
     *
     * <p>
     * 14 位的时钟序列值根据此 UUID 的 clock_seq 字段构造。clock_seq 字段用于保证在基于时间的 UUID 中的时间唯一性。
     * <p>
     * {@code clockSequence} 值仅在基于时间的 UUID（其 version 类型为 1）中才有意义。 如果此 UUID 不是基于时间的 UUID，则此方法抛出
     * UnsupportedOperationException。
     *
     * @return 此 {@code UUID} 的时钟序列
     *
     * @throws UnsupportedOperationException 如果此 UUID 的 version 不为 1
     */
    public int clockSequence() throws UnsupportedOperationException
    {
        checkTimeBase();
        return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48);
    }

    /**
     * 与此 UUID 相关的节点值。
     *
     * <p>
     * 48 位的节点值根据此 UUID 的 node 字段构造。此字段旨在用于保存机器的 IEEE 802 地址，该地址用于生成此 UUID 以保证空间唯一性。
     * <p>
     * 节点值仅在基于时间的 UUID（其 version 类型为 1）中才有意义。<br>
     * 如果此 UUID 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException。
     *
     * @return 此 {@code UUID} 的节点值
     *
     * @throws UnsupportedOperationException 如果此 UUID 的 version 不为 1
     */
    public long node() throws UnsupportedOperationException
    {
        checkTimeBase();
        return leastSigBits & 0x0000FFFFFFFFFFFFL;
    }

    /**
     * 返回此{@code UUID} 的字符串表现形式。
     *
     * <p>
     * UUID 的字符串表示形式由此 BNF 描述：
     * 
     * <pre>
     * {@code
     * UUID                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @return 此{@code UUID} 的字符串表现形式
     * @see #toString(boolean)
     */
    @Override
    public String toString()
    {
        return toString(false);
    }

    /**
     * 返回此{@code UUID} 的字符串表现形式。
     *
     * <p>
     * UUID 的字符串表示形式由此 BNF 描述：
     * 
     * <pre>
     * {@code
     * UUID                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @param isSimple 是否简单模式，简单模式为不带'-'的UUID字符串
     * @return 此{@code UUID} 的字符串表现形式
     */
    public String toString(boolean isSimple)
    {
        final StringBuilder builder = new StringBuilder(isSimple ? 32 : 36);
        // time_low
        builder.append(digits(mostSigBits >> 32, 8));
        if (false == isSimple)
        {
            builder.append('-');
        }
        // time_mid
        builder.append(digits(mostSigBits >> 16, 4));
        if (false == isSimple)
        {
            builder.append('-');
        }
        // time_high_and_version
        builder.append(digits(mostSigBits, 4));
        if (false == isSimple)
        {
            builder.append('-');
        }
        // variant_and_sequence
        builder.append(digits(leastSigBits >> 48, 4));
        if (false == isSimple)
        {
            builder.append('-');
        }
        // node
        builder.append(digits(leastSigBits, 12));

        return builder.toString();
    }

    /**
     * 返回此 UUID 的哈希码。
     *
     * @return UUID 的哈希码值。
     */
    @Override
    public int hashCode()
    {
        long hilo = mostSigBits ^ leastSigBits;
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

    /**
     * 将此对象与指定对象比较。
     * <p>
     * 当且仅当参数不为 {@code null}、而是一个 UUID 对象、具有与此 UUID 相同的 varriant、包含相同的值（每一位均相同）时，结果才为 {@code true}。
     *
     * @param obj 要与之比较的对象
     *
     * @return 如果对象相同，则返回 {@code true}；否则返回 {@code false}
     */
    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != UUID.class))
        {
            return false;
        }
        UUID id = (UUID) obj;
        return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits);
    }

    // Comparison Operations

    /**
     * 将此 UUID 与指定的 UUID 比较。
     *
     * <p>
     * 如果两个 UUID 不同，且第一个 UUID 的最高有效字段大于第二个 UUID 的对应字段，则第一个 UUID 大于第二个 UUID。
     *
     * @param val 与此 UUID 比较的 UUID
     *
     * @return 在此 UUID 小于、等于或大于 val 时，分别返回 -1、0 或 1。
     *
     */
    @Override
    public int compareTo(UUID val)
    {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits ? -1 : //
                (this.mostSigBits > val.mostSigBits ? 1 : //
                        (this.leastSigBits < val.leastSigBits ? -1 : //
                                (this.leastSigBits > val.leastSigBits ? 1 : //
                                        0))));
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Private method start
    /**
     * 返回指定数字对应的hex值
     * 
     * @param val 值
     * @param digits 位
     * @return 值
     */
    private static String digits(long val, int digits)
    {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    /**
     * 检查是否为time-based版本UUID
     */
    private void checkTimeBase()
    {
        if (version() != 1)
        {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
    }

    /**
     * 获取{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)
     * 
     * @return {@link SecureRandom}
     */
    public static SecureRandom getSecureRandom()
    {
        try
        {
            return SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new UtilException(e);
        }
    }

    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     * 
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom()
    {
        return ThreadLocalRandom.current();
    }
}

解释所有的部分
```