package com.acap.ec.utils;

import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      事件日志监听器
 *
 * Created by ACap on 2021/3/31 11:32
 * </pre>
 */
public class OnEventLogListener<P, R> implements OnEventListener<P, R> {
    private String mTag;

    public OnEventLogListener(String mTag) {
        this.mTag = mTag;
    }


    @Override
    public void onStart(P params) {
        EUtils.i(mTag, String.format("onStart(%s)", params));

    }

    @Override
    public void onError(Throwable e) {
        EUtils.e(mTag, String.format("onError(%s)", e));
    }

    @Override
    public void onNext(R result) {
        EUtils.i(mTag, String.format("onNext(%s)", result));
    }

    @Override
    public void onComplete() {
        EUtils.i(mTag, "onComplete()");
    }

}
