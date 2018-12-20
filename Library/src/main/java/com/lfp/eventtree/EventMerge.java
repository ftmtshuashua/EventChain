package com.lfp.eventtree;

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

    public EventMerge(EventChain... chains) {
        if (chains != null) {
            this.mMerge = chains;
            for (int i = 0; i < chains.length; i++) {
                chains[i].removeEventChianObserver(mEventChianObserver);
            }
        }
    }

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

    EventChianObserver mEventChianObserver = new EventChianObserver() {
        AtomicInteger count = new AtomicInteger(0);

        @Override
        public void onChainStart() {
            count.incrementAndGet();
        }

        @Override
        public void onStart(EventChain event) {

        }

        @Override
        public void onError(EventChain event, Throwable e) {

        }

        @Override
        public void onNext(EventChain event) {

        }

        @Override
        public void onChainComplete() {
            count.decrementAndGet();
            if (count.intValue() == 0) {
                next();
            }
        }
    };

}
