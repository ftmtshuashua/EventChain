package com.acap.ec;

import com.acap.ec.listener.OnChainListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      把多个监听器合并成一个监听器来处理
 *
 *
 * Created by ACap on 2021/3/30 9:52
 * </pre>
 */
class Listeners_CL implements OnChainListener {

    /**
     * 链上发生的错误
     */
    private Throwable mThrowableAtChain;

    private final List<OnChainListener> array = new ArrayList<>();

    public void add(OnChainListener l) {
        array.add(l);
    }

    public void remove(OnChainListener l) {
        array.remove(l);
    }

    @Override
    public void onChainStart() {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onChainStart();
        }
    }

    @Override
    public void onStart(EventChain node) {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onStart(node);
        }
    }

    @Override
    public void onError(EventChain node, Throwable throwable) {
        mThrowableAtChain = throwable;
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onError(node, throwable);
        }
    }

    @Override
    public void onNext(EventChain node, Object result) {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onNext(node, result);
        }
    }


    @Override
    public void onChainComplete() {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onChainComplete();
        }
    }

    /**
     * 获得链上发生的错误
     */
    public Throwable getThrowableAtChain() {
        return mThrowableAtChain;
    }

}
