package com.acap.ec;


import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/9/17 21:03
 * </pre>
 *
 * @author A·Cap
 */
public final class Chain<P, R, R1> extends BaseEvent<P, R1> {
    @NotNull
    private BaseEvent<P, R> mHead;
    @NotNull
    private BaseEvent<? super R, R1> mTail;

    private Map<BaseEvent, Boolean> mIsComplete = new ConcurrentHashMap<>();

    public Chain(@NotNull BaseEvent<P, R> head, @NotNull BaseEvent<? super R, R1> tail) {
        this.mHead = head;
        this.mTail = tail;
        mHead.onAttachedToChain(this);
        mTail.onAttachedToChain(this);

        mIsComplete.put(mHead, false);
        mIsComplete.put(mTail, false);
    }

    @Override
    protected void onFinish(boolean isComplete) {
        mHead.onFinish(isComplete);
        mTail.onFinish(isComplete);
    }

    @Override
    protected void onCall(P params) {
        mHead.start(params);
    }

    /**
     * 当子事件完成时候的处理逻辑<br/>
     * 如果处于链关系上的两个事件都完成，则认为当前链关系完成
     *
     * @param event
     */
    protected void onChildComplete(BaseEvent event) {
        mIsComplete.put(event, true);

        if (mIsComplete.get(mHead) && mIsComplete.get(mTail)) {
            dispatchEventComplete(true);
        }
    }

    /**
     * 处于链关系的两个事件，发起 {@link BaseEvent#next(Object)} 时，在这里处理<br/>
     * 1.当 Head 事件发起 {@link BaseEvent#next(Object)} ，则将详细转发给 Tail 事件<br/>
     * 2.当 Tail 事件发起 {@link BaseEvent#next(Object)} ，则调用自己的 {@link BaseEvent#next(Object)}
     *
     * @param event  发起 {@link BaseEvent#next(Object)} 的事件
     * @param result 该事件的处理结果
     */
    protected void onChildNext(BaseEvent event, Object result) {
        if (!isComplete()) {
            if (mHead == event) {
                mTail.start((R) result);
            } else if (mTail == event) {
                next((R1) result);
            } else {
                throw new IllegalStateException("Event链路逻辑异常");
            }
        }
    }


}
