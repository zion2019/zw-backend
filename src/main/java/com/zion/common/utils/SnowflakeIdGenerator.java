package com.zion.common.utils;

import java.time.Instant;

public class SnowflakeIdGenerator {
    // 根据需要自行调整位数
    private static final long TIMESTAMP_BITS = 41L;
    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;
    private static final long WORKER_ID_BITS = 10L;
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + MAX_WORKER_ID);
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 当前毫秒的序列号已经用完，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 根据需要自行调整位移和组合顺序
        return ((timestamp & MAX_TIMESTAMP) << (WORKER_ID_BITS + SEQUENCE_BITS))
                | ((workerId & MAX_WORKER_ID) << SEQUENCE_BITS)
                | (sequence & MAX_SEQUENCE);
    }

    private long currentTimeMillis() {
        return Instant.now().toEpochMilli();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        // 示例使用，假设workerId为1
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1);
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.nextId();
            System.out.println("Generated ID: " + id);
        }
    }
}
