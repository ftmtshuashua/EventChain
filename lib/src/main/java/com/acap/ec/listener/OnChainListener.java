package com.acap.ec.listener;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      链上的事件监听器，能监听链上所有事件事件的回调
 *
 * Function:
 *
 * Created by ACap on 2021/3/30 10:53
 * </pre>
 */
public interface OnChainListener<P, R> {
    /**
     * 当事件链开始,一个链条只会回调一次该方法
     */
    void onChainStart(P params);

    /**
     * 当事件链中某个事件开始执行
     *
     * @param event  执行的事件
     * @param params 事件入参
     */
    void onEventStart(Event event, Object params);

    /**
     * 当事件链中某个事件执行错误
     *
     * @param event     执行的事件
     * @param throwable 事件抛出的异常信息
     */
    void onEventError(Event event, Throwable throwable);

    /**
     * 当事件链中某个事件执行结束
     *
     * @param event  执行的事件
     * @param result 事件的产物
     */
    void onEventNext(Event event, Object result);

    /**
     * 当事件链中某个事件执行错误
     *
     * @param throwable 事件抛出的异常信息
     */
    void onChainError(Throwable throwable);

    /**
     * 当事件链中某个事件执行结束
     *
     * @param result 事件的产物
     */
    void onChainNext(R result);

    /**
     * 当事件链结束,一个链条只会回调一次该方法
     */
    void onChainComplete();
}
