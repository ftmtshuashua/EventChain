package com.lfp.eventtree;

import com.lfp.eventtree.excption.MultiException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Tip:
 *      合并数据并发请求
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:45
 * </pre>
 */
public class EventMerge extends EventChain {
    EventChain[] mMerge;
/*

    HashMap<EventChainObserver, DisabilityEventChainObserver> mObserverMap;
*/

    public EventMerge(EventChain... chains) {
        if (chains != null) {
            this.mMerge = chains;
            for (int i = 0; i < chains.length; i++) {
                chains[i].addEventChainObserver(mEventChianObserver);
            }
        }
    }
/*

    protected void addDisabilityEventChainObserver(EventChainObserver observer) {
        if (mMerge != null) {
            if (mObserverMap == null) {
                mObserverMap = new HashMap<>();
            }
            DisabilityEventChainObserver disabilityEventChainObserver = new DisabilityEventChainObserver(observer);
            mObserverMap.put(observer, disabilityEventChainObserver);
            for (int i = 0; i < mMerge.length; i++) {
                mMerge[i].addEventChainObserver(disabilityEventChainObserver);
            }
        }
    }

    protected void removeDisabilityEventChainObserver(EventChainObserver observer) {
        if (mMerge != null && mObserverMap != null) {
            DisabilityEventChainObserver disabilityEventChainObserver = mObserverMap.get(observer);
            if (disabilityEventChainObserver != null) {
                for (int i = 0; i < mMerge.length; i++) {
                    mMerge[i].removeEventChainObserver(disabilityEventChainObserver);
                }
            }
        }
    }
*/

    @Override
    protected void call() throws Throwable {
        if (this.mMerge == null || this.mMerge.length == 0) {
            next();
        } else {
            for (int i = 0; i < this.mMerge.length; i++) {
                this.mMerge[i].start();
            }
        }
    }

    EventChainObserver mEventChianObserver = new EventChainObserver() {
        AtomicInteger count = new AtomicInteger(0);
        MultiException exception;

        @Override
        public void onChainStart() {
            count.incrementAndGet();
        }

        @Override
        public void onStart(EventChain event) {
            getChainObserverManager().onStart(event);
        }

        @Override
        public void onError(EventChain event, Throwable e) {
            if (exception == null) {
                exception = new MultiException();
            }
            exception.add(e);
            getChainObserverManager().onError(event, e);
        }

        @Override
        public void onNext(EventChain event) {
            getChainObserverManager().onNext(event);
        }

        @Override
        public void onChainComplete() {
            count.decrementAndGet();
            if (count.intValue() == 0) {
                if (exception == null) {
                    next();
                } else {
                    error(exception);
                }
            }
        }
    };
}
