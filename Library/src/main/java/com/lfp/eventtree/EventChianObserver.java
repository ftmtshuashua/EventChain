package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件监听
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
public interface EventChianObserver {
    /*当事件链开始*/
    void onChainStart();

    /*事件开始*/
    void onStart(EventChain event);

    /*错误信息*/
    void onError(EventChain event, Throwable e);

    /*事件结束*/
    void onNext(EventChain event);

    /*当事件链结束*/
    void onChainComplete();
}
