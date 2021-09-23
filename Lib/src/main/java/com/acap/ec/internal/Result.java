package com.acap.ec.internal;

import com.acap.ec.Event;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      子事件结果收集器
 *
 * Created by A·Cap on 2021/9/23 16:34
 * </pre>
 */
public class Result<P, R> {
    private Event<P, R> mEvent;
    private Throwable mThrowable;
    private R mResult;

    private boolean mIsError;

    public Result(Event<P, R> mEvent, R result) {
        this.mEvent = mEvent;
        this.mResult = result;
        this.mIsError = false;
    }

    public Result(Event<P, R> mEvent, Throwable throwable) {
        this.mEvent = mEvent;
        this.mThrowable = throwable;
        this.mIsError = true;
    }

    public boolean isError() {
        return mIsError;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public R getResult() {
        return mResult;
    }


}
