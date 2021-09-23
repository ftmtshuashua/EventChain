package com.acap.ec.internal;

import com.acap.ec.Event;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      子事件回调监听代理，主要用于保证事件回调逻辑顺序的正确性
 *
 * Created by A·Cap on 2021/9/23 16:34
 * </pre>
 */
public class ProxyEventListener<P, R> implements OnEventListener<P, R> {

    private OnEventListener<P, R> mListener;
    private Result<P, R> mResult;
    private Event<P, R> mEvent;

    public ProxyEventListener(OnEventListener<P, R> mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onStart(Event<P, R> event, P params) {
        mEvent = event;
        if (mListener != null) {
            mListener.onStart(event, params);
        }
    }

    @Override
    public void onError(Throwable e) {
        mResult = new Result(mEvent, e);
    }

    @Override
    public void onNext(R result) {
        mResult = new Result(mEvent, result);
    }

    @Override
    public void onComplete() {
        if (mListener != null && mResult != null) {
            if (mResult.isError()) {
                mListener.onError(mResult.getThrowable());
            } else {
                mListener.onNext(mResult.getResult());
            }
            mListener.onComplete();
        }
    }
}
