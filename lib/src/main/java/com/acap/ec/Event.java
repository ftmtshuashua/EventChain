package com.acap.ec;

import com.acap.ec.excption.EventInterruptException;
import com.acap.ec.internal.EventState;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.ListenerMap;

/**
 * <pre>
 * Tip:
 *      事件链是一种尝试
 *      将任何业务逻辑抽象成一个事件,该事件只关心自己的业务逻辑
 *
 * @param <P> params:上游事件完成之后的产物
 * @param <R> result:当前事件的产物
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public abstract class Event<P, R> implements ILinkableEvent<P, R, Event<P, R>> {

    /**
     * 事件所在的‘链’，
     */
    Chain<?, ?> mChain;
    Event<R, ?> mNext;

    private EventState mEventState = EventState.READY;

    private final ListenerMap<OnEventListener<P, R>> mOnEventListeners = new ListenerMap<>();


    public Chain getChain() {
        if (mChain == null) {
            mChain = new Chain<>(this);
        }
        return mChain;
    }

    @Override
    public <R1, TR extends ILinkableEvent<P, R1, TR>, TP extends ILinkableEvent<? super R, R1, TP>> TR chain(TP event) {
        mNext = (Event<R, ?>) event;
        return (TR) getChain();
    }

    @Override
    public <R1, TR extends ILinkableEvent<P, R1, TR>, TP extends ILinkableEvent<? super R, ? extends R1, TP>> TR merge(TP... events) {
        return chain(new MergeEvent(events));
    }

    @Override
    public void start() {
        getChain().start();
    }

    @Override
    public void start(P params) {
        getChain().start(params);
    }


    /**
     * <pre>
     * 检查事件的状态，并执行事件的逻辑
     *  1.如果事件未就绪，则不执行逻辑
     *  2.如果事件已结束，则直接完成事件所在的链
     *  3.如果事件已就绪并且未结束，则执行事件的逻辑
     * </pre>
     *
     * @param params 事件入参，该参数将被当作{@link #onCall(Object)}方法的参数传入
     */
    void performStart(P params) {
        mEventState = EventState.READY;

        if (isFinish()) {
            performChainComplete();
        } else {
            onPrepare();

            mEventState = EventState.START;
            mOnEventListeners.map(listener -> listener.onStart(params));
            getChain().performOnEventStart(this, params);
            onCall(params);
        }
    }

    @Override
    public void next() {
        next(null);
    }


    /**
     * 检查活动状态
     */
    private boolean checkAlive() {
        if (isComplete() || !isStart()) {
            return false;
        }
        if (isFinish()) {
            performEventComplete();
            performChainComplete();
            return false;
        }
        if (isInterrupt()) {
            performEventInterrupt();
            return false;
        }
        return true;
    }

    @Override
    public void next(R result) {
        if (checkAlive()) {
            mEventState = EventState.COMPLETE;
            mOnEventListeners.map(listener -> listener.onNext(result));
            getChain().performOnEventNext(this, result);
            performEventComplete();
            getChain().startEventNext(this, result);
        }
    }

    @Override
    public void error(Throwable throwable) {
        if (checkAlive()) {
            performEventError(throwable);
        }
    }

    private final void performEventInterrupt() {
        onInterrupt();
        performEventError(new EventInterruptException());
    }

    private final void performEventError(Throwable throwable) {
        mEventState = EventState.COMPLETE;

        mOnEventListeners.map(listener -> listener.onError(throwable));
        getChain().performOnEventError(this, throwable);

        performEventComplete();
        getChain().startEventError(this, throwable);
    }


    /**
     * 执行事件的完成逻辑
     */
    private final void performEventComplete() {
        onComplete();
        mOnEventListeners.map(listener -> listener.onComplete());
    }

    /**
     * 执行链的完成逻辑
     */
    private final void performChainComplete() {
        getChain().performComplete();
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
    public void finish() {
        getChain().finish();
    }

    @Override
    public void interrupt() {
        getChain().interrupt();
    }

    @Override
    public boolean isInterrupt() {
        return getChain().isInterrupt();
    }

    @Override
    public boolean isFinish() {
        return getChain().isFinish();
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
    public Event<P, R> addOnEventListener(OnEventListener<P, R> listener) {
        mOnEventListeners.register(listener);
        return this;
    }

    @Override
    public Event<P, R> removeOnEventListener(OnEventListener listener) {
        mOnEventListeners.unregister(listener);
        return this;
    }

    @Override
    public Event<P, R> addOnChainListener(OnChainListener<P, R> listener) {
        getChain().addOnChainListener(listener);
        return this;
    }

    @Override
    public Event<P, R> removeOnChainListener(OnChainListener listener) {
        getChain().addOnChainListener(listener);
        return this;
    }
}

