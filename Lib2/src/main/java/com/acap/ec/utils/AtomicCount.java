package com.acap.ec.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Tip:
 *      计数器，用于正确的异步计数
 *
 * Created by ACap on 2021/7/10 12:43
 * </pre>
 *
 * @author A·Cap
 */
public class AtomicCount {
    /**
     * 用于初始化的默认值
     */
    private final int mDefaultValue;
    private final AtomicInteger mCount;

    public AtomicCount(int defaultValue) {
        this.mDefaultValue = defaultValue;
        mCount = new AtomicInteger(defaultValue);
    }

    /**
     * 重置计数器
     */
    public void reset() {
        mCount.set(mDefaultValue);
    }

    public void increase() {
        mCount.incrementAndGet();
    }

    public int getValue() {
        return mCount.intValue();
    }
}

