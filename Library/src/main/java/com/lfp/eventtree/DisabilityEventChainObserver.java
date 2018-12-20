package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      提供发部分功能，移除了onChainStart和onChainComplete信息
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 23:23
 * </pre>
 */
public class DisabilityEventChainObserver implements EventChainObserver {
    EventChainObserver mObserver;

    public DisabilityEventChainObserver(EventChainObserver l) {
        mObserver = l;
    }

    @Override
    public void onChainStart() {

    }

    @Override
    public void onStart(EventChain event) {
        mObserver.onStart(event);
    }

    @Override
    public void onError(EventChain event, Throwable e) {
        mObserver.onError(event, e);
    }

    @Override
    public void onNext(EventChain event) {
        mObserver.onNext(event);
    }

    @Override
    public void onChainComplete() {

    }
}
