package com.acap.ec;

import com.acap.ec.listener.OnCallBeforeListener;
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
class Listeners_BL<P, R> implements OnCallBeforeListener<P, R> {

    private final List<OnCallBeforeListener<P, R>> array = new ArrayList<>();

    public void add(OnCallBeforeListener<P, R> l) {
        array.add(l);
    }

    public void remove(OnCallBeforeListener<P, R> l) {
        array.remove(l);
    }

    @Override
    public void onBefore(EventChain<P, R> node) {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onBefore(node);
        }
    }
}
