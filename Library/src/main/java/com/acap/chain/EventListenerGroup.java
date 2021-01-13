package com.acap.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 * Tip:
 *     事件监听器管理
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 17:40
 * </pre>
 */
public class EventListenerGroup implements OnEventListener {

    List<OnEventListener> array;

    public EventListenerGroup() {
        array = new ArrayList<OnEventListener>();
    }

    public void addOnEventListener(OnEventListener l) {
        array.add(l);
    }

    public void removeOnEventListener(OnEventListener l) {
        array.remove(l);
    }


    public void map(Action1<OnEventListener> action1) {
        Iterator<OnEventListener> array = this.array.iterator();
        while (array.hasNext()) {
            action1.call(array.next());
        }
    }

    @Override
    public void onError(Throwable e) {
        map(listener -> listener.onError(e));
    }

    @Override
    public void onNext() {
        map(listener -> listener.onNext());
    }

    @Override
    public void onComplete() {
        map(listener -> listener.onComplete());
    }

    @Override
    public void onStart() {
        map(listener -> listener.onStart());
    }
}
