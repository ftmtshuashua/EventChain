package com.acap.ec;

import com.acap.ec.internal.EventState;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.ListenerMap;

/**
 * <pre>
 * Tip:
 *      这是一条链，在链上附加的事件将会按顺序依次执行，知道链上最后一个事件完成
 *
 * Created by ACap on 2021/3/29 18:44
 * </pre>
 */
public final class Chain<P, R> implements ILinkableEvent<P, R, Chain<P, R>> {

    /**
     * 链上第一个事件
     */
    private Event<P, ?> mFirst;

    private EventState mEventState = EventState.READY;

    private final ListenerMap<OnChainListener<P, R>> mOnChainListeners = new ListenerMap<>();
    private final ListenerMap<OnEventListener<P, R>> mOnEventListeners = new ListenerMap<>();


    Chain(Event event) {
        mFirst = event;
//        mOnChainListeners.register(mChainStateListener);
    }

    /**
     * 获得链上最后一个事件
     */
    private Event getLast() {
        Event mLast = mFirst;
        while (mLast != null) {
            if (mLast.mNext != null) {
                mLast = mLast.mNext;
            } else {
                break;
            }
        }
        return mLast;
    }

    @Override
    public <R1, TR extends ILinkableEvent<P, R1, TR>, TP extends ILinkableEvent<? super R, R1, TP>> TR chain(TP event) {
        getLast().chain(event);
        return (TR) this;
    }

    @Override
    public <R1, TR extends ILinkableEvent<P, R1, TR>, TP extends ILinkableEvent<? super R, ? extends R1, TP>> TR merge(TP... events) {
        getLast().chain(new MergeEvent(events));
        return (TR) this;
    }

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(P params) {
        performStart(params);

    }

    /**
     * 从链头开始执行链的逻辑
     *
     * @param params 链的入参，该参数将被传递到链上的第一个事件中
     */
    private void performStart(P params) {
        if (mEventState != EventState.READY || isFinish()) return;
        performPrepare();

        if (isFinish()) return;

        mOnChainListeners.map(it -> it.onChainStart(params));
        mOnEventListeners.map(it -> it.onStart(params));

        if (isFinish()) {
            performComplete();
        } else {
            onCall(params);
        }
    }

    /**
     * 执行事件完成回调
     */
    void performComplete() {
        mOnEventListeners.map(it -> it.onComplete());
        mOnChainListeners.map(it -> it.onChainComplete());
    }

    /**
     * 事件就绪
     */
    void performPrepare() {
        mEventState = EventState.READY;
        onPrepare();
    }


    @Override
    public void next() {
        next(null);
    }

    @Override
    public void next(R result) {
        mOnEventListeners.map(prOnEventListener -> prOnEventListener.onNext(result));
    }

    @Override
    public void error(Throwable throwable) {
        mOnEventListeners.map(prOnEventListener -> prOnEventListener.onError(throwable));
    }

    @Override
    public void onCall(P params) {
        mFirst.performStart(params);
    }

    void performOnEventStart(Event event, Object params) {
        mOnChainListeners.map(it -> it.onEventStart(event, params));
    }

    /**
     * 当事件执行完成
     *
     * @param event
     */
    void performOnEventNext(Event event, Object result) {
        mOnChainListeners.map(it -> it.onEventNext(event, result));
    }

    void performOnEventError(Event event, Throwable throwable) {
        mOnChainListeners.map(it -> it.onEventError(event, throwable));
    }

    /**
     * 开始下一个事件
     *
     * @param event
     * @param result
     */
    void startEventNext(Event event, Object result) {
        if (event.mNext != null) {
            event.mNext.performStart(result);
        } else {
            performComplete();
        }
    }

    void startEventError(Event event, Throwable throwable) {
        mOnChainListeners.map(it->it.onChainError(throwable));
        performComplete();
    }


    @Override
    public void onPrepare() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean isInterrupt() {
        return false;
    }

    @Override
    public void finish() {

    }

    @Override
    public void interrupt() {

    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isStart() {
        return false;
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public Chain<P, R> addOnEventListener(OnEventListener<P, R> listener) {
        return null;
    }

    @Override
    public Chain<P, R> removeOnEventListener(OnEventListener listener) {
        return null;
    }

    @Override
    public Chain<P, R> addOnChainListener(OnChainListener<P, R> listener) {
        return null;
    }

    @Override
    public Chain<P, R> removeOnChainListener(OnChainListener listener) {
        return null;
    }
}
