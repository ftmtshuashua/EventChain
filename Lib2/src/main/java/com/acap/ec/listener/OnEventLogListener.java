package com.acap.ec.listener;

import com.acap.ec.utils.EUtils;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/3/31 11:32
 * </pre>
 */
public class OnEventLogListener<R> implements OnEventListener<R> {
    private String mTag;

    public OnEventLogListener(String mTag) {
        this.mTag = mTag;
    }


    @Override
    public void onStart() {
        EUtils.i(mTag, "onStart()");

    }

    @Override
    public void onError(Throwable e) {
        EUtils.e(mTag, "onError(" + e + ")");
    }

    @Override
    public void onNext(R result) {
        EUtils.i(mTag, "onNext(" + result + ")");
    }

    @Override
    public void onComplete() {
        EUtils.i(mTag, "onComplete()");
    }

}
