package com.acap.ec;

import com.acap.ec.excption.MultiException;
import com.acap.ec.listener.OnChainListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Tip:
 *      并行事件
 *
 * Created by ACap on 2021/3/31 10:33
 * </pre>
 */
public class EventFork<P, R> extends EventChain<P, List<R>> {
    private final List<EventChain<P, R>> mMerge = new ArrayList<>();
    private final Map<EventChain<P, R>, R> mResults = new HashMap<>();

    /*事件计数器，用与识别所有并发事件全部执行完成*/
    private final AtomicInteger mEventCount = new AtomicInteger(0);

    public EventFork(EventChain<P, ? extends R>... chains) {
        if (chains != null) {
            for (int i = 0; i < chains.length; i++) {
                EventChain event = chains[i];
                if (event == null) continue;
                event.addOnChainListener(mChainListener);
                mMerge.add(event);
            }
        }
    }

    @Override
    protected void onCall(P params) {
        if (mMerge.isEmpty()) {
            next(null);
        } else {
            for (int i = 0; i < mMerge.size(); i++) {
                mMerge.get(i).start(params);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        for (int i = 0; i < mMerge.size(); i++) {
            mMerge.get(i).stop();
        }
    }

    //当某个节点完成时
    private void onChainComplete() {
        mEventCount.incrementAndGet();
        final int size = mMerge.size();
        int count = mEventCount.intValue();
        if (size != count) return;
        if (size < count) throw new RuntimeException("并发事件计数器异常,接收的完成信号数量超过的事件的数量,排查某个事件是否发出多个Complete信号");

        List<Throwable> throwable = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Throwable throwableAtChain = mMerge.get(i).getChain().getCL().getThrowableAtChain();
            if (throwableAtChain != null) throwable.add(throwableAtChain);
        }

        if (throwable.isEmpty()) {
            List<R> result = new ArrayList<>();
            for (EventChain<P, ? extends R> eventChain : mMerge) {
                result.add(mResults.get(eventChain));
            }
            next(result);
        } else {
            if (throwable.size() == 1) {
                error(throwable.get(0));
            } else {
                MultiException exception = new MultiException();
                for (int i = 0; i < throwable.size(); i++) {
                    exception.put(throwable.get(i));
                }
                error(exception);
            }
        }
    }

    private final OnChainListener mChainListener = new OnChainListener<R>() {
        @Override
        public void onChainStart() {

        }

        @Override
        public void onStart(EventChain node) {
            getChain().getCL().onStart(node);
        }

        @Override
        public void onError(EventChain node, Throwable throwable) {
            getChain().getCL().onError(node, throwable);
        }

        @Override
        public void onNext(EventChain node, R result) {
            mResults.put(node, result);
            getChain().getCL().onNext(node, result);
        }

        @Override
        public void onChainComplete() {
            EventFork.this.onChainComplete();
        }
    };

}
