package com.acap.demo.event;

import com.acap.ec.BaseEvent;
import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/9/23 17:01
 * </pre>
 */
public abstract class DemoEvent<P, R> extends BaseEvent<P, R> {
    private boolean IsError = false;

    public Event<P, R> setError(boolean error) {
        IsError = error;
        return this;
    }

    @Override
    protected void next(R result) {
        if (IsError) {
            error(new IllegalStateException("强制抛出异常"));
        } else {
            super.next(result);
        }
    }

    @Override
    protected void error(Throwable e) {
        super.error(e);
    }
}
