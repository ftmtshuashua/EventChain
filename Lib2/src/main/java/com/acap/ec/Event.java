package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.internal.EventLifecycle;
import com.acap.ec.internal.EventState;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.ListenerMap;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/9 18:59
 * @author A·Cap
 * </pre>
 */
public abstract class Event<P, R> implements ILinkable<P, R> {

    private EventState mEventState = EventState.READY;

    private Chain<?, ?> mChain;
    private final ListenerMap<OnEventListener<P, R>> mListeners = new ListenerMap<>();

    @Override
    public <R1> ILinkable<P, R1> chain(ILinkable<? super R, R1> event) {
        return Chain.generate(this, event);
    }

    @Override
    public <R1> ILinkable<P, R1[]> merge(ILinkable<? super R, ? extends R1>... events) {
        return Chain.generate(this, new MergeEvent(events));
    }

    @Override
    public <R1> ILinkable<P, R1> apply(Apply<R, R1> apply) {
        return Chain.generate(this, new ApplyEvent<>(apply));
    }

    @Override
    public void onAttachedToChain(Chain<?, ?> chain) {
        mChain = chain;
    }

    /**
     * 当事件逻辑执行完成时候,请调用该函数来通知链上下一个事件
     */
    protected void next() {
        next(null);
    }

    /**
     * 当事件逻辑执行完成时候,请调用该函数来通知链上下一个事件
     *
     * @param result 当前事件的出参 - 表示事件执行结束后的结果
     */
    protected synchronized void next(R result) {
        if (!isStart() || isComplete() || isFinish()) {
            return;
        }

        getLifecycle().onNext(this, result);
        mListeners.map(it -> it.onNext(result));
        if (isFinish()) {
            return;
        }

        performEventComplete();
        if (mChain != null) {
            mChain.performOnEventNext(this, result);
        }
    }

    /**
     * <pre>
     * 如果当前事件的逻辑执行时遇到一些问题，并且不希望后续的事件继续执行时。
     * 请调用该函数,用于通知'链'
     * </pre>
     *
     * @param throwable 当前事件的异常信息
     */
    protected synchronized void error(Throwable throwable) {
        if (!isStart() || isComplete() || isFinish()) {
            return;
        }

        getLifecycle().onError(this, throwable);
        mListeners.map(it -> it.onError(throwable));

        if (isFinish()) {
            return;
        }

        performEventComplete();
        if (mChain != null) {
            mChain.performOnEventError(this, throwable);
        }
    }

    void performEventComplete() {
        getLifecycle().onComplete(this);
        onComplete();
        if (isFinish()) {
            return;
        }
        mListeners.map(it -> it.onComplete());
    }


    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(P params) {
        performStart(params);
    }

    void performStart(P params) {
        if (isStart()) {
            return;
        }

        if (isFinish()) {
            return;
        }

        mEventState = EventState.READY;
        onPrepare(params);

        if (isFinish()) {
            return;
        }

        mEventState = EventState.START;
        getLifecycle().onStart(this, params);
        mListeners.map(it -> it.onStart(Event.this, params));

        if (isFinish()) {
            return;
        }

        onCall(params);
    }

    /**
     * 事件的逻辑函数.事件的逻辑从该函数开始执行,以该调用完成函数之后结束
     *
     * @param params 事件入参
     */
    protected abstract void onCall(P params);

    @Override
    public ILinkable<P, R> listenerFirst(OnEventListener<P, R> listener) {
        mListeners.registerAtFirst(listener);
        return this;
    }

    @Override
    public ILinkable<P, R> listener(OnEventListener<P, R> listener) {
        mListeners.register(listener);
        return this;
    }

    @Override
    public ILinkable<P, R> listenerRemove(OnEventListener listener) {
        mListeners.unregister(listener);
        return this;
    }

    @Override
    public boolean isStart() {
        return mEventState == EventState.START;
    }

    @Override
    public boolean isComplete() {
        return mEventState == EventState.COMPLETE;
    }

    @Override
    public void onPrepare(P params) {
    }

    @Override
    public void onComplete() {
        mEventState = EventState.COMPLETE;

    }

    @Override
    public void finish() {
        getLifecycle().finish();
    }

    @Override
    public boolean isFinish() {
        return getLifecycle().isFinish();
    }

    @Override
    public void onFinish() {
        performEventComplete();
    }


    /**
     * 事件的生命周期，只存在于链头
     */
    private EventLifecycle mLifecycle;

    @Override
    public void setLifecycle(EventLifecycle lifecycle) {
        if (mChain == null) {
            mLifecycle = lifecycle;
        } else {
            mChain.setLifecycle(lifecycle);
        }
    }

    @Override
    public EventLifecycle getLifecycle() {
        if (mChain == null) {
            if (mLifecycle == null) {
                mLifecycle = new EventLifecycle();
            }
            return mLifecycle;
        }
        return mChain.getLifecycle();
    }

}
