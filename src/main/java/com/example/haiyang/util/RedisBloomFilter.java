package com.example.haiyang.util;

import com.google.common.hash.Hashing;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.nio.charset.StandardCharsets;

public class RedisBloomFilter {
    private final StringRedisTemplate redisTemplate;
    private final String filterKey;
    private final int bitSize;
    private final int hashFunctions;

    /**
     * 构造函数
     * @param redisTemplate StringRedisTemplate实例
     * @param filterKey Redis中存储的key
     * @param expectedInsertions 预计元素数量
     * @param falsePositiveRate 可接受的误判率(0-1)
     */
    public RedisBloomFilter(StringRedisTemplate redisTemplate, String filterKey, 
                          long expectedInsertions, double falsePositiveRate) {
        this.redisTemplate = redisTemplate;
        this.filterKey = filterKey;
        this.bitSize = calculateBitSetSize(expectedInsertions, falsePositiveRate);
        this.hashFunctions = calculateHashFunctions(expectedInsertions, bitSize);
    }

    /**
     * 添加元素到布隆过滤器
     */
    public void add(String element) {
        long[] offsets = getHashOffsets(element);
        for (long offset : offsets) {
            redisTemplate.opsForValue().setBit(filterKey, offset, true);
        }
    }

    /**
     * 检查元素是否可能存在
     */
    public boolean mightContain(String element) {
        long[] offsets = getHashOffsets(element);
        for (long offset : offsets) {
            if (!redisTemplate.opsForValue().getBit(filterKey, offset)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算元素的哈希偏移量(使用Murmur3)
     */
    private long[] getHashOffsets(String element) {
        long[] offsets = new long[hashFunctions];
        // 使用Murmur3_128生成基础哈希值
        byte[] bytes = Hashing.murmur3_128()
                .hashString(element, StandardCharsets.UTF_8)
                .asBytes();
        
        // 转换为两个long值作为基础哈希
        long hash1 = bytesToLong(bytes, 0);
        long hash2 = bytesToLong(bytes, 8);
        
        // 生成k个哈希位置
        for (int i = 0; i < hashFunctions; i++) {
            offsets[i] = Math.abs((hash1 + i * hash2) % bitSize);
        }
        return offsets;
    }

    /**
     * 计算位数组大小
     */
    private int calculateBitSetSize(long n, double p) {
        if (p == 0) p = Double.MIN_VALUE;
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算哈希函数数量
     */
    private int calculateHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /**
     * 字节数组转long
     */
    private static long bytesToLong(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) bytes[offset + i] & 0xff) << (8 * i);
        }
        return value;
    }
}