package com.acap.ec.listener;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      事件监听器,流程如下
 *
 *                ↗ onNext()  ↘
 *      onStart()                 onComplete()
 *                ↘ onError() ↗
 *
 *
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 * @author A·Cap
 */
public interface OnEventListener<P, R> {

    /**
     * 事件开始执行的信号
     *
     * @param event
     * @param params 入参
     */
    void onStart(Event<P, R> event, P params);

    /**
     * 当事件处理失败，并返回了失败的错误
     *
     * @param e 错误信息,包含一个或者多个错误信息
     */
    void onError(Throwable e);

    /**
     * 当事件成功处理
     */
    void onNext(R result);

    /**
     * 当事件结束
     */
    void onComplete();
}
