package com.acap.ec.utils;

import com.acap.ec.Event;
import com.acap.ec.listener.OnChainListener;

/**
 * <pre>
 * Tip:
 *      事件链的日志监听器
 *
 *
 * Created by ACap on 2021/3/31 11:29
 * </pre>
 */
public class OnChainLogListener<R> implements OnChainListener<R> {
    private String mTag;

    public OnChainLogListener(String mTag) {
        this.mTag = mTag;
    }


    @Override
    public void onChainStart() {
        EUtils.i(mTag, "onChainStart()");
    }

    @Override
    public void onStart(Event node) {
        EUtils.i(mTag, "onStart(" + EUtils.id(node) + ")");
    }

    @Override
    public void onError(Event node, Throwable throwable) {
        EUtils.e(mTag, "onError(" + EUtils.id(node) + "," + throwable + ")");
    }

    @Override
    public void onNext(Event node, R result) {
        EUtils.i(mTag, "onNext(" + EUtils.id(node) + "," + result + ")");
    }

    @Override
    public void onChainComplete() {
        EUtils.i(mTag, "onChainComplete()");
    }

}
