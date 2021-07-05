package com.acap.ec.listener;

/**
 * <pre>
 * Tip:
 *      当事件执行失败 回调onError方法
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
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
    default void onStart(P params) {
    }
}
