package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.internal.ApplyEvent;
import com.acap.ec.internal.ListenerMap;
import com.acap.ec.listener.OnEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    protected void onAttachedToChain(Chain chain) {
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

    /**
     * 当用户在链上调用了 {@link BaseEvent#finish(boolean)} ,处于整条链上的所有事件的 {@link BaseEvent#onFinish(boolean)} 方法都将被依次调用
     *
     * @param isComplete 是否调用事件的 {@link OnEventListener#onComplete()} 回调
     */
    protected void onFinish(boolean isComplete) {
        dispatchEventComplete(isComplete);
    }

    /**
     * 判断当前事件是否已完成。已完成的事件将无法执行动作
     *
     * @return 是否完成
     */
    protected synchronized boolean isComplete() {
        return mIsComplete;
    }

    /**
     * 分发事件完成状态
     *
     * @param isComplete
     */
    protected synchronized final void dispatchEventComplete(boolean isComplete) {
        if (!isComplete()) {
            boolean isCallCompleteListener = false;
            synchronized (this) {
                if (!isComplete()) {
                    isCallCompleteListener = true;
                    mIsComplete = true;
                }
            }

            if (isCallCompleteListener) {
                onComplete();
                if (isComplete) {
                    mListener.map(OnEventListener::onComplete);

                    if (mChain != null) {
                        mChain.onChildComplete(this);
                    }
                }
            }


        }
    }

    /**
     * 当前事件完成时回调该方法,事件可以在这里做一个资源回收等操作
     */
    protected void onComplete() {

    }

    @Override
    public void start(P params) {
        if (!isComplete()) {
            mListener.map(listener -> listener.onStart(this, params));
            onCall(params);
        }
    }


    /**
     * 事件的业务逻辑入口,当事件开始时候,在这里执行事件的业务逻辑
     *
     * @param params 事件的入参
     */
    protected abstract void onCall(P params);


    /**
     * 将当前事件的执行结果向后传递
     *
     * @param result 当前事件的执行结果
     */
    protected void next(R result) {
        if (!isComplete()) {
            mListener.map(listener -> listener.onNext(result));

            if (mChain != null) {
                mChain.onChildNext(this, result);
            }
        }
    }

    /**
     * 当前事件执行错误,当事件链中出现错误将停止并完成所有正在执行中的事件
     *
     * @param e 发生的错误信息
     */
    protected void error(Throwable e) {
        if (!isComplete()) {
            mListener.map(listener -> listener.onError(e));

            finish(true);
        }
    }

    /**
     * 当前事件执行完成.注意已执行完成的事件将不再接收与发出任何参数
     */
    protected void complete() {
        dispatchEventComplete(true);
    }

    @Override
    public <R1> Event<P, R1> chain(Event<? super R, R1> event) {
        return Events.chain(this, event);
    }

    @Override
    public <R1> Event<P, List<R1>> merge(Event<? super R, ? extends R1>... events) {
        return Events.chain(this, Events.merge(events));
    }

    @Override
    public <R1> Event<P, R1> apply(Apply<R, R1> apply) {
        return Events.chain(this, Events.create(apply));
    }

    @Override
    public <R1> Event<P, R1> lazy(@NotNull Apply<R, Event<R, R1>> apply) {
        return Events.chain(this, Events.lazy(apply));
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
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
