package com.acap.ec;

import com.acap.ec.excption.MergeException;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.utils.ListMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Tip:
 *      合并事件
 *      该事件包含多个子事件，当所有子事件完成时候，该事件完成
 *
 * Created by ACap on 2021/3/31 10:33
 * </pre>
 */
public class MergeEvent<P, R> extends Event<P, R[]> {
    private final List<Event<P, R>> mEvents = new ArrayList<>();
    private final Map<Event<P, R>, R> mForkResults = new HashMap<>();
    private final ListMap<Event<P, R>> mListMap = new ListMap(mEvents);

    /*事件计数器，用与识别所有并发事件全部执行完成*/
    private final AtomicInteger mForkResultCount = new AtomicInteger(0);

    public MergeEvent(Event<P, ? extends R>... chains) {
        if (chains != null) {
            for (int i = 0; i < chains.length; i++) {
                Event event = chains[i];
                if (event == null) continue;
                mEvents.add(event);
            }

            mListMap.map(prEvent -> prEvent.addOnChainListener(mChainListener));
        }
    }

    @Override
    protected void onCall(P params) {
        mForkResultCount.set(0);
        if (mEvents.isEmpty()) {
            next(null);
        } else {
            for (int i = 0; i < mEvents.size(); i++) {
                mEvents.get(i).start(params);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        mListMap.map(prEvent -> prEvent.finish());
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        mListMap.map(prEvent -> prEvent.performChainPrepareStart());
    }

    @Override
    protected void onInterrupt() {
        super.onInterrupt();
        mListMap.map(prEvent -> prEvent.interrupt());
    }

    private final OnChainListener mChainListener = new OnChainListener<R>() {
        @Override
        public void onChainStart() {
        }

        @Override
        public void onStart(Event node) {
            getChain().onStart(node);
        }

        @Override
        public void onError(Event node, Throwable throwable) {
            getChain().onError(node, throwable);
        }

        @Override
        public void onNext(Event node, R result) {
            mForkResults.put(node, result);
            getChain().onNext(node, result);
        }

        @Override
        public void onChainComplete() {
            mForkResultCount.incrementAndGet();
            final int size = mEvents.size();
            int count = mForkResultCount.intValue();
            if (size != count) return;
            if (size < count)
                throw new RuntimeException("并发事件计数器异常,接收的完成信号数量超过的事件的数量,排查某个事件是否发出多个Complete信号");

            //检查是否有异常
            List<Throwable> throwable = new ArrayList<>();
            for (int i = 0; i < size; i++) {

                Chain chain = mEvents.get(i).getChain();
                if (chain.isError()) {
                    throwable.add(chain.getError());
                }
            }

            //如果有异常就抛出错误
            if (throwable.isEmpty()) {
                List<R> result = new ArrayList<>();
                for (Event<P, ? extends R> eventChain : mEvents) {
                    result.add(mForkResults.get(eventChain));
                }
                MergeEvent.this.next((R[]) result.toArray());
            } else {
                if (throwable.size() == 1) {
                    MergeEvent.this.error(throwable.get(0));
                } else {
                    MergeException exception = new MergeException();
                    for (int i = 0; i < throwable.size(); i++) {
                        Throwable err = throwable.get(i);
                        exception.put(err);
                    }
                    MergeEvent.this.error(exception);
                }
            }
        }
    };


    public List<Event<P, R>> getEvents() {
        return mEvents;
    }

}
