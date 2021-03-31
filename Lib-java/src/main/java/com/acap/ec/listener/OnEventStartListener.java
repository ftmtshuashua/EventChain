package com.acap.ec.listener;

/**
 * <pre>
 * Tip:
 *      事件开始监听,在时间开始之前调用。一个事件或者事件链只会调用一次
 *      适配 Lambda 表达式
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
@FunctionalInterface
public interface OnEventStartListener extends OnEventListener<Object> {

    /**
     * 在时间开始之前调用。一个事件或者事件链只会调用一次
     */
    @Override
    void onStart();

    @Override
    default void onError(Throwable e) {
    }

    @Override
    default void onNext(Object result) {
    }

    @Override
    default void onComplete() {
    }
}
