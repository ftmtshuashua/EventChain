package com.acap.ec.listener;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      事件完成监听,在时间结束之后调用。一个事件或者事件链只会调用一次
 *      适配 Lambda 表达式
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 * @author A·Cap
 */
@FunctionalInterface
public interface OnEventCompleteListener<P,R> extends OnEventListener<P,R> {

    /**
     * 在时间结束之后调用。一个事件或者事件链只会调用一次
     */
    @Override
    void onComplete();


    @Override
    default void onStart(Event<P, R> event, P params){}


    @Override
    default void onError(Throwable e) {
    }

    @Override
    default void onNext(R result) {
    }
}
