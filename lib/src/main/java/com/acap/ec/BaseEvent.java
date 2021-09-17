package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.internal.ListenerMap;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/9/17 18:45
 * </pre>
 */
public abstract class BaseEvent<P, R> implements Event<P, R> {

    private boolean mIsComplete = false;

    private final ListenerMap<OnEventListener<P, R>> mListener = new ListenerMap<>();

    /**
     * 保存着事件在链上的位置信息的对象。如果它为空则表示当前事件是一个独立的事件
     */
    private Chain mChain;

    /**
     * 当事件附着到某个链上
     */
    public void onAttachedToChain(Chain chain) {
        mChain = chain;
    }


    @Override
    public final void finish(boolean isComplete) {
        if (mChain != null) {
            mChain.finish(isComplete);
        } else {
            onFinish(isComplete);
        }
    }

    protected void onFinish(boolean isComplete) {
        onComplete(isComplete);
    }

    /**
     * 判断当前事件是否已完成。已完成的事件将无法执行动作
     *
     * @return 是否完成
     */
    protected synchronized boolean isComplete() {
        return mIsComplete;
    }

    protected synchronized void onComplete(boolean isComplete) {
        if (!isComplete()) {
            boolean isCall = false;
            synchronized (this) {
                if (!isComplete()) {
                    isCall = true;
                    mIsComplete = true;
                }
            }

            if (isCall && isComplete) {
                mListener.map(OnEventListener::onComplete);

                if (mChain != null) {
                    mChain.complete(this);
                }
            }
        }
    }

    @Override
    public void start(P params) {
        if (!isComplete()) {
            onCall(params);
        }
    }


    /**
     * 事件的业务逻辑入口,当事件开始时候,在这里执行事件的业务逻辑
     *
     * @param params 事件的入参
     */
    protected abstract void onCall(P params);


    protected void next(R result) {
        if (!isComplete()) {
            mListener.map(listener -> listener.onNext(result));
        }
    }

    protected void error(Throwable e) {
        if (!isComplete()) {
            mListener.map(listener -> listener.onError(e));
            finish();
        }
    }

    protected void complete() {
        if (!isComplete()) {

        }
    }

    @Override
    public <R1> Event<P, R1> chain(Event<? super R, R1> event) {
        return Events.chain(this, event);
    }


    @Override
    public <R1> Event<P, R1[]> merge(Event<? super R, ? extends R1>... events) {
        return null;
    }

    @Override
    public <R1> Event<P, R1> apply(Apply<R, R1> apply) {
        return null;
    }


    @Override
    public Event<P, R> listener(OnEventListener<P, R> listener) {
        if (listener != null) {
            mListener.register(listener);
        }
        return this;
    }

    @Override
    public Event<P, R> listenerFirst(OnEventListener<P, R> listener) {
        if (listener != null) {
            mListener.registerAtFirst(listener);
        }
        return this;
    }

    @Override
    public Event<P, R> listenerRemove(OnEventListener<P, R> listener) {
        if (listener != null) {
            mListener.unregister(listener);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
