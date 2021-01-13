package com.acap.chain;


import com.acap.chain.excption.MultiException;

import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * 记录事件集合与事件状态
     */
    final List<EventChain> mMerge = new ArrayList<>();
    /*事件计数器，用与识别所有并发事件全部执行完成*/
    final AtomicInteger mEventCount = new AtomicInteger(0);


    public EventMerge(EventChain... chains) {
        if (chains != null) {
            for (int i = 0; i < chains.length; i++) {
                EventChain event = chains[i];
                if (event == null) continue;
                event.addEventChainObserver(mEventChianObserver);
                mMerge.add(event);
            }
        }
    }

    @Override
    protected void call() {
        if (mMerge.isEmpty()) {
            next();
        } else {
            final Iterator<EventChain> iterator = mMerge.iterator();
            while (iterator.hasNext()) {
                iterator.next().start();
            }
        }
    }

    @Override
    public void interrupt() {
        final Iterator<EventChain> iterator = mMerge.iterator();
        while (iterator.hasNext()) {
            final EventChain next = iterator.next();
            next.interrupt();
        }
        super.interrupt();
    }

    @Override
    public void complete() {
        final Iterator<EventChain> iterator = mMerge.iterator();
        while (iterator.hasNext()) {
            iterator.next().complete();
        }
        super.complete();
    }


    private final EventChainObserver mEventChianObserver = new EventChainObserver() {
        private MultiException exception;

        @Override
        public void onChainStart() {
        }

        @Override
        public void onStart(EventChain event) {
            getChainObserverGroup().onStart(event);
        }

        @Override
        public void onError(EventChain event, Throwable e) {
            if (exception == null) exception = new MultiException(e);
            exception.put(e);

            getChainObserverGroup().onError(event, e);
        }

        @Override
        public void onNext(EventChain event) {
            getChainObserverGroup().onNext(event);
        }

        @Override
        public void onChainComplete() {
            mEventCount.incrementAndGet();

            if (mMerge.size() < mEventCount.intValue())
                throw new RuntimeException("并发事件计数器异常！");
            if (mMerge.size() != mEventCount.intValue()) return;


            /*全部事件完成*/
            final Iterator<EventChain> events = mMerge.iterator();
            while (events.hasNext()) {
                final EventChain next = events.next();

                if (next.isComplete()) {
                    complete();
                    return;
                }
            }

            if (exception == null || exception.isEmpty()) {
                next();
            } else {
                if (exception.size() == 1) {
                    error(exception.getFirst());
                } else {
                    error(exception);
                }
            }

        }
    };
}
