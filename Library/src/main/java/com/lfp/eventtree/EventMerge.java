package com.lfp.eventtree;

import com.lfp.eventtree.excption.MultiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Tip:
 *      合并数据并发请求，同时执行数据集中的事件链，当所有事件链执行完成之后这个并发事件结束.
 *      当它被插入到一个事件链中会已类似多条分支的形式存在,当所有分支执行结束后才会回到它所插入的事件链上
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
            List<EventChain> array = new ArrayList<>();
            for (int i = 0; i < chains.length; i++) {
                EventChain item = chains[i];
                if (item != null) {
                    item.addEventChainObserver(mEventChianObserver);
                    array.add(item);
                }
            }
            if (!array.isEmpty()) {
                mMerge = new EventChain[array.size()];
                array.toArray(mMerge);
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

    @Override
    protected void onInterrupt() {
        if (mMerge != null) {
            for (int i = 0; i < mMerge.length; i++) {
                mMerge[i].interrupt();
            }
        }
        super.onInterrupt();
    }

    @Override
    protected void onComplete() {
        if (mMerge != null) {
            for (int i = 0; i < mMerge.length; i++) {
                mMerge[i].complete();
            }
        }
        super.onComplete();
    }

    private EventChainObserver mEventChianObserver = new EventChainObserver() {
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
                //可能是某一个请求要求直接结束请求
                boolean isComplete = false;
                for (int i = 0; i < mMerge.length; i++) {
                    isComplete = mMerge[i].isComplete();
                    if (isComplete) break;
                }

                if (isComplete) { //其中有事件要求中断请求
                    complete();
                } else {
                    if (exception == null) {
                        next();
                    } else {
                        error(exception);
                    }
                }
            }
        }
    };
}
