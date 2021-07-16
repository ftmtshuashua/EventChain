package com.acap.ec.schedulers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * Tip:
 *      线程工厂
 *
 * Created by ACap on 2021/7/12 18:49
 * </pre>
 */
public final class EventThreadFactory extends AtomicLong implements ThreadFactory {

    final String prefix;

    final int priority;

    public EventThreadFactory(String prefix) {
        this(prefix, Thread.NORM_PRIORITY);
    }

    public EventThreadFactory(String prefix, int priority) {
        this.prefix = prefix;
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, new StringBuilder(prefix).append('-').append(incrementAndGet()).toString());
        t.setPriority(priority);
        t.setDaemon(true);
        return t;
    }

    @Override
    public String toString() {
        return "RequestThreadFactory[" + prefix + "]";
    }


}
