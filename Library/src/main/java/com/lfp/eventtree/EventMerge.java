package com.lfp.eventtree;

import android.util.Log;

import com.lfp.eventtree.excption.MultiException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    final Map<EventChain, Boolean> mMerge = new HashMap<>();

    public EventMerge(EventChain... chains) {
        if (chains != null) {
            for (int i = 0; i < chains.length; i++) {
                EventChain event = chains[i];
                if (event == null) continue;
                event.addEventChainObserver(mEventChianObserver);
                mMerge.put(event, false);
            }
        }
    }

    @Override
    protected void call() {
        if (mMerge.isEmpty()) {
            next();
        } else {
            final Iterator<EventChain> iterator = mMerge.keySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().start();
            }
        }
    }

    @Override
    public void interrupt() {
        Log.e("EventMerge", "interrupt:"+this);
        final Iterator<EventChain> iterator = mMerge.keySet().iterator();
        while (iterator.hasNext()) {
            final EventChain next = iterator.next();
            Log.e("EventChain", "interrupt:" + next);
            next.interrupt();
        }
        super.interrupt();
    }

    @Override
    public void complete() {
        final Iterator<EventChain> iterator = mMerge.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().complete();
        }
        super.complete();
    }


    private final EventChainObserver mEventChianObserver = new EventChainObserver() {
        private final MultiException exception = new MultiException();

        @Override
        public void onChainStart() {
        }

        @Override
        public void onStart(EventChain event) {
            getChainObserverManager().onStart(event);
        }

        @Override
        public void onError(EventChain event, Throwable e) {
            exception.add(e);

            getChainObserverManager().onError(event, e);
            mMerge.put(event, true);
        }

        @Override
        public void onNext(EventChain event) {
            getChainObserverManager().onNext(event);
            mMerge.put(event, true);
        }

        @Override
        public void onChainComplete() {
            final Iterator<Boolean> iterator = mMerge.values().iterator();
            while (iterator.hasNext()) {
                if (!iterator.next()) return;
            }

            /*全部事件完成*/
            final Iterator<EventChain> events = mMerge.keySet().iterator();
            while (events.hasNext()) {
                final EventChain next = events.next();

                if (next.isComplete()) {
                    complete();
                    return;
                }
            }

            if (exception.getArray().isEmpty()) {
                next();
            } else {
                error(exception);
            }

        }
    };
}
