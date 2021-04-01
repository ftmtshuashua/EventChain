package com.acap.ec;

import com.acap.ec.listener.OnEventListener;

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
        Utils.i(mTag, "onStart()");

    }

    @Override
    public void onError(Throwable e) {
        Utils.e(mTag, "onError(" + e + ")");
    }

    @Override
    public void onNext(R result) {
        Utils.i(mTag, "onNext(" + result + ")");
    }

    @Override
    public void onComplete() {
        Utils.i(mTag, "onComplete()");
    }

}
