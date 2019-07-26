package com.lfp.eventchain;

/**
 * <pre>
 * Tip:
 *      事件链观察者，它能观察链条中所有事件的状态变化
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
public interface EventChainObserver {
    /**
     * 当事件链开始,一个链条只会回调一次该方法
     */
    void onChainStart();

    /**
     * 当事件链中某个事件开始执行
     *
     * @param event 执行的事件
     */
    void onStart(EventChain event);

    /**
     * 当事件链中某个事件执行错误
     *
     * @param event 执行的事件
     */
    void onError(EventChain event, Throwable e);

    /**
     * 当事件链中某个事件执行结束
     *
     * @param event 执行的事件
     */
    void onNext(EventChain event);

    /**
     * 当事件链结束,一个链条只会回调一次该方法
     */
    void onChainComplete();
}
