package com.acap.ec.listener;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      链上的事件监听器，能监听链上所有事件节点的回调
 *
 * Function:
 *
 * Created by ACap on 2021/3/30 10:53
 * </pre>
 */
public interface OnChainListener<R> {
    /**
     * 当事件链开始,一个链条只会回调一次该方法
     */
    void onChainStart();

    /**
     * 当事件链中某个事件开始执行
     *
     * @param event 执行的节点
     */
    void onStart(Event event);

    /**
     * 当事件链中某个事件执行错误
     *
     * @param event     执行的节点
     * @param throwable 节点抛出的异常信息
     */
    void onError(Event event, Throwable throwable);

    /**
     * 当事件链中某个事件执行结束
     *
     * @param event  执行的节点
     * @param result 节点的产物
     */
    void onNext(Event event, R result);

    /**
     * 当事件链结束,一个链条只会回调一次该方法
     */
    void onChainComplete();
}
