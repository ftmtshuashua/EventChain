package com.lfp.eventchain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 * Tip:
 *     链条管理器
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 17:40
 * </pre>
 */
public class EventChainObserverGroup implements EventChainObserver {

    List<EventChainObserver> array;

    public EventChainObserverGroup() {
        array = new ArrayList<EventChainObserver>();
    }

    public void addEventChainObserver(EventChainObserver l) {
        array.add(l);
    }

    public void removeEventChainObserver(EventChainObserver l) {
        array.remove(l);
    }

    @Override
    public void onChainStart() {
        map(observer -> observer.onChainStart());
    }

    @Override
    public void onStart(EventChain event) {
        map(observer -> observer.onStart(event));
    }

    @Override
    public void onError(EventChain event, Throwable e) {
        map(observer -> observer.onError(event, e));
    }

    @Override
    public void onNext(EventChain event) {
        map(observer -> observer.onNext(event));
    }

    @Override
    public void onChainComplete() {
        map(observer -> observer.onChainComplete());
    }


    public void map(Action1<EventChainObserver> action1) {
        Iterator<EventChainObserver> array = this.array.iterator();
        while (array.hasNext()) {
            action1.call(array.next());
        }
    }
}
