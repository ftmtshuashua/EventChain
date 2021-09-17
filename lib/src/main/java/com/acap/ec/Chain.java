package com.acap.ec;

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
    private BaseEvent<P, R> mHead;
    private BaseEvent<? super R, R1> mTail;

    private Map<BaseEvent, Boolean> mIsComplete = new ConcurrentHashMap<>();

    public Chain(BaseEvent<P, R> head, BaseEvent<? super R, R1> tail) {
        this.mHead = head;
        this.mTail = tail;
        mHead.onAttachedToChain(this);
        mTail.onAttachedToChain(this);
    }

    @Override
    protected void onFinish(boolean isComplete) {
        mHead.onFinish(isComplete);
        mTail.onFinish(isComplete);
    }

    @Override
    protected void onCall(P params) {
        mHead.onCall(params);
    }

    protected void complete(BaseEvent event) {
        mIsComplete.put(event, true);

        onComplete(true);
    }

}
