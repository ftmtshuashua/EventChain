package com.acap.chain;

/**
 * <pre>
 * Tip:
 *      事件开始监听,在时间开始之前调用。一个事件或者事件链只会调用一次
 *      适配 Lambda 表达式
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
@FunctionalInterface
public interface OnEventStartListener extends OnEventListener {

    /**
     * 在时间开始之前调用。一个事件或者事件链只会调用一次
     */
    @Override
    void onStart();

    @Override
    default void onError(Throwable e) {
    }

    @Override
    default void onNext() {
    }

    @Override
    default void onComplete() {
    }
}
