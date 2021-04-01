package com.acap.ec;


import com.acap.ec.listener.OnCallLaterListener;

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
class Listeners_LL<P, R> implements OnCallLaterListener<P, R> {

    private final List<OnCallLaterListener<P, R>> array = new ArrayList<>();

    public void add(OnCallLaterListener<P, R> l) {
        array.add(l);
    }

    public void remove(OnCallLaterListener<P, R> l) {
        array.remove(l);
    }

    @Override
    public void onLater(EventChain<P, R> node) {
        for (int i = 0; i < array.size(); i++) {
            array.get(i).onLater(node);
        }
    }
}
