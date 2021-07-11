package com.acap.ec.listener;

import com.acap.ec.ILinkable;

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
 * @author A·Cap
 */
@FunctionalInterface
public interface OnEventStartListener<P, R> extends OnEventListener<P, R> {


    @Override
    void onStart(ILinkable<P, R> event, P params);

    @Override
    default void onError(Throwable e) {
    }

    @Override
    default void onNext(R result) {
    }

    @Override
    default void onComplete() {
    }
}
