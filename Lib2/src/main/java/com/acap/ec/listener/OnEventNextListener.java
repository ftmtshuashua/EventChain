package com.acap.ec.listener;

/**
 * <pre>
 * Tip:
 *      当事件成功执行完成 回调onNext方法
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
@FunctionalInterface
public interface OnEventNextListener<P,R> extends OnEventListener<P,R> {
    @Override
    default void onError(Throwable e) {
    }

    @Override
    void onNext(R result);

    @Override
    default void onComplete() {
    }

    @Override
    default void onStart(P params) {
    }
}
