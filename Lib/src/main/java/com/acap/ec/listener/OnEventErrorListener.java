package com.acap.ec.listener;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      当事件执行失败 回调onError方法
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 * @author A·Cap
 */
@FunctionalInterface
public interface OnEventErrorListener<P,R> extends OnEventListener<P,R> {
    @Override
    void onError(Throwable e);

    @Override
    default void onNext(R result) {
    }

    @Override
    default void onComplete() {
    }

    @Override
    default void onStart(Event<P, R> event, P params){}
}
