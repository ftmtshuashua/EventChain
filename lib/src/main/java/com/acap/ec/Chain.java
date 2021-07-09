package com.acap.ec;

import com.acap.ec.action.Apply;
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
public class Chain<P, R> implements ILinkableEvent<P, R, Chain<P, R>>, IEvent<R> {

    /**
     * 链上第一个事件
     */
    private Event<P, ?> mFirst;

    private EventState mEventState = EventState.READY;

    private final ListenerMap<OnChainListener<P, R>> mOnChainListeners = new ListenerMap<>();
    private final ListenerMap<OnEventListener<P, R>> mOnEventListeners = new ListenerMap<>();
    private boolean mIsFinish = true;
    private boolean mIsInterrupt = true;

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
    public <R1> Chain<P, R1> chain(ILinkableEvent<? super R, R1, ?> event) {
        return getLast().chain(event);
    }

    @Override
    public <R1, T extends ILinkableEvent<? super R, ? extends R1, ?>> Chain<P, R1[]> merge(T... events) {
        return getLast().chain(new MergeEvent(events));
    }

    @Override
    public <R1> Chain<P, R1> apply(Apply<R, R1> apply) {
        return getLast().apply(apply);
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

        if (isFinish()) return;
        performPrepare();

        mEventState = EventState.START;
        mOnChainListeners.map(it -> it.onChainStart(params));
        mOnEventListeners.map(it -> it.onStart(params));

        if (isFinish()) {
            performComplete();
        } else {
            onCall(params);
        }
    }


    private void performPrepare() {
        mEventState = EventState.READY;
        mIsFinish = false;
        mIsInterrupt = false;
        onPrepare();
    }

    /**
     * 执行事件完成回调
     */
    void performComplete() {
        mEventState = EventState.COMPLETE;
        mOnEventListeners.map(it -> it.onComplete());
        mOnChainListeners.map(it -> it.onChainComplete());
    }

    @Override
    public void next() {
        next(null);
    }

    @Override
    public void next(R result) {
        if (isComplete()) return;
        mOnEventListeners.map(it -> it.onNext(result));
        performComplete();
    }

    @Override
    public void error(Throwable throwable) {
        if (isComplete()) return;
        mOnEventListeners.map(it -> it.onError(throwable));
        performComplete();
    }

    @Override
    public void onCall(P params) {
        mFirst.performStart(params);
    }

    /**
     * 当链中事件开始时执行的逻辑,通知链上的{@link OnChainListener#onEventStart(Event, Object)}方法
     *
     * @param event  发生的事件实例
     * @param params 事件所接收的参数信息
     */
    void performOnEventStart(Event event, Object params) {
        if (isComplete()) return;
        mOnChainListeners.map(it -> it.onEventStart(event, params));
    }

    /**
     * 当链中事件结束时执行的逻辑,通知链上的{@link OnChainListener#onEventNext(Event, Object)}方法
     *
     * @param event  发生的事件实例
     * @param result 事件的出参
     */
    void performOnEventNext(Event event, Object result) {
        if (isComplete()) return;
        mOnChainListeners.map(it -> it.onEventNext(event, result));
    }

    /**
     * 当链中事件结束时执行的逻辑,通知链上的{@link OnChainListener#onEventStart(Event, Object)}方法
     *
     * @param event     发生的事件实例
     * @param throwable 事件中发生的异常
     */
    void performOnEventError(Event event, Throwable throwable) {
        if (isComplete()) return;
        mOnChainListeners.map(it -> it.onEventError(event, throwable));
    }

    /**
     * 开始下一个事件
     *
     * @param event
     * @param result
     */
    void performEventNextComplete(Event event, Object result) {
        if (event.mNext != null) {
            event.mNext.performStart(result);
        } else {
            R result1 = (R) result;
            mOnChainListeners.map(it -> it.onChainNext(result1));
            next(result1);
        }
    }

    void performEventErrorComplete(Event event, Throwable throwable) {
        mOnChainListeners.map(it -> it.onChainError(throwable));
        error(throwable);
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
        return mIsInterrupt;
    }

    @Override
    public void finish() {
        mIsFinish = true;
    }

    @Override
    public void interrupt() {
        mIsInterrupt = true;
    }

    @Override
    public boolean isComplete() {
        return mEventState == EventState.COMPLETE;
    }

    @Override
    public boolean isStart() {
        return mEventState == EventState.START;
    }

    @Override
    public boolean isFinish() {
        return mIsFinish;
    }

    @Override
    public Chain<P, R> addOnEventListener(OnEventListener<P, R> listener) {
        mOnEventListeners.register(listener);
        return this;
    }

    @Override
    public Chain<P, R> removeOnEventListener(OnEventListener listener) {
        mOnEventListeners.unregister(listener);
        return this;
    }

    @Override
    public Chain<P, R> addOnChainListener(OnChainListener<P, R> listener) {
        mOnChainListeners.register(listener);
        return this;
    }

    @Override
    public Chain<P, R> removeOnChainListener(OnChainListener listener) {
        mOnChainListeners.unregister(listener);
        return this;
    }
}
