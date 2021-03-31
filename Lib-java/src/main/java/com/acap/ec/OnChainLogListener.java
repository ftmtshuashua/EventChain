package com.acap.ec;

import com.acap.ec.listener.OnChainListener;

/**
 * <pre>
 * Tip:
 *      监听链的日志
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
        Utils.i(mTag, "onChainStart()");
    }

    @Override
    public void onStart(EventChain node) {
        Utils.i(mTag, "onStart(" + Utils.getObjectId(node) + ")");
    }

    @Override
    public void onError(EventChain node, Throwable throwable) {
        Utils.e(mTag, "onError(" + Utils.getObjectId(node) + "," + throwable + ")");
    }

    @Override
    public void onNext(EventChain node, R result) {
        Utils.i(mTag, "onNext(" + Utils.getObjectId(node) + "," + result + ")");
    }

    @Override
    public void onChainComplete() {
        Utils.i(mTag, "onChainComplete()");
    }

}
