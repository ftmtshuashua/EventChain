package com.acap.ec;

import com.acap.ec.excption.MergeException;
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
public class OneOfEvent<P, R> extends Event<P, R> {
    private final List<Event<P, R>> mFork = new ArrayList<>();
    private final Map<Event<P, R>, R> mForkResults = new HashMap<>();

    /*事件计数器，用与识别所有并发事件全部执行完成*/
    private final AtomicInteger mForkResultCount = new AtomicInteger(0);

    public OneOfEvent(Event<P, ? extends R>... chains) {
        if (chains != null) {
            for (int i = 0; i < chains.length; i++) {
                Event event = chains[i];
                if (event == null) continue;
                event.addOnChainListener(mChainListener);
                mFork.add(event);
            }
        }
    }

    @Override
    protected void onCall(P params) {
        mForkResultCount.set(0);
        if (mFork.isEmpty()) {
            next(null);
        } else {
            for (int i = 0; i < mFork.size(); i++) {
                mFork.get(i).start(params);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        for (int i = 0; i < mFork.size(); i++) {
            mFork.get(i).stop();
        }
    }


    private final OnChainListener mChainListener = new OnChainListener<R>() {
        @Override
        public void onChainStart() {
        }

        @Override
        public void onStart(Event node) {
            getChain().getCL().onStart(node);
        }

        @Override
        public void onError(Event node, Throwable throwable) {
            getChain().getCL().onError(node, throwable);
        }

        @Override
        public void onNext(Event node, R result) {
            mForkResults.put(node, result);
            getChain().getCL().onNext(node, result);
        }

        @Override
        public void onChainComplete() {
            mForkResultCount.incrementAndGet();
            final int size = mFork.size();
            int count = mForkResultCount.intValue();
            if (size != count) return;
            if (size < count) throw new RuntimeException("并发事件计数器异常,接收的完成信号数量超过的事件的数量,排查某个事件是否发出多个Complete信号");

            //检查是否有异常
            List<Throwable> throwable = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Listeners_CL chain = mFork.get(i).getChain().getCL();
                Throwable err = chain.getThrowable();
                Event node = chain.getThrowableNode();
                if (err != null) {
                    throwable.add(err);
                }
            }

            //如果有异常就抛出错误
            if (throwable.isEmpty()) {
                List<R> result = new ArrayList<>();
                for (Event<P, ? extends R> eventChain : mFork) {
                    result.add(mForkResults.get(eventChain));
                }
                next(result);
            } else {
                if (throwable.size() == 1) {
                    error(throwable.get(0));
                } else {
                    MergeException exception = new MergeException();
                    for (int i = 0; i < throwable.size(); i++) {
                        Throwable err = throwable.get(i);
                        exception.put(err);
                    }
                    error(exception);
                }
            }
        }
    };


    public List<Event<P, R>> getForkNode() {
        return mFork;
    }

}
