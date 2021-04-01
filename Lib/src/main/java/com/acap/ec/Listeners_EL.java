package com.acap.ec;

import com.acap.ec.listener.OnEventListener;

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
class Listeners_EL<R> implements OnEventListener<R> {

    private final List<OnEventListener<R>> array = new ArrayList<>();

    public void add(OnEventListener<R> l) {
        array.add(l);
    }

    public void remove(OnEventListener<R> l) {
        array.remove(l);
    }

    @Override
    public void onStart() {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onStart();
        }
    }

    @Override
    public void onError(Throwable e) {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onError(e);
        }
    }

    @Override
    public void onNext(R result) {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onNext(result);
        }
    }

    @Override
    public void onComplete() {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onComplete();
        }
    }
}
