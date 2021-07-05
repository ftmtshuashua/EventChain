package com.acap.demo.event;

import com.acap.demo.utils.ThreadHelper;
import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      模拟：请求超时
 *
 * Created by ACap on 2021/7/3 16:49
 * </pre>
 */
public class E_TimeOut<R> extends Event<Object, R> {
    private long time;

    public E_TimeOut(long time) {
        this.time = time;
    }

    @Override
    protected void onCall(Object params) {
        ThreadHelper.io(() -> {
            ThreadHelper.sleep(time);
            next(null);
        });
    }
}