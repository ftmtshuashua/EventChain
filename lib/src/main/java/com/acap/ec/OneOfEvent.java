package com.acap.ec;

import com.acap.ec.listener.OnChainListener;
import com.acap.ec.utils.ListMap;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      选择事件
 *      该事件中包含多个子事件，当某个子事件完成时候，则会忽略其他未完成的子事件的回调，并直接完成该事件
 *
 * Created by ACap on 2021/3/31 10:33
 * </pre>
 */
public class OneOfEvent<P, R> extends Event<P, R> {
    private final List<Event<P, R>> mEvents = new ArrayList<>();
    private final ListMap<Event<P, R>> mListMap = new ListMap(mEvents);

    public OneOfEvent(Event<P, ? extends R>... chains) {
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
        interruptChildrenEvent();
    }

    /**
     * 打断所有正在执行的子事件
     */
    private void interruptChildrenEvent() {
        mListMap.map(prEvent -> prEvent.getChain().interrupt());
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
            interruptChildrenEvent();
            OneOfEvent.this.error(throwable);
            getChain().onError(node, throwable);
        }

        @Override
        public void onNext(Event node, R result) {
            interruptChildrenEvent();
            OneOfEvent.this.next(result);
            getChain().onNext(node, result);
        }

        @Override
        public void onChainComplete() {
        }
    };

}
