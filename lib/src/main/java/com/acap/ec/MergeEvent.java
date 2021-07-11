package com.acap.ec;


import com.acap.ec.action.Action1;
import com.acap.ec.excption.MergeException;
import com.acap.ec.internal.EventLifecycle;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.AtomicCount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Tip:
 *      合并事件
 *      该事件包含多个子事件，当所有子事件完成时候，该事件完成
 *
 * Created by ACap on 2021/3/31 10:33
 * </pre>
 *
 * @author A·Cap
 */
public class MergeEvent<P, R> extends Event<P, R[]> {

    private final ILinkable<P, R>[] mEvents;
    private final Map<ILinkable<P, R>, EventResult<P, R>> mEventResult = new HashMap<>();
    private final AtomicCount mResultCount = new AtomicCount(0);

    private void mapEvents(Action1<ILinkable<P, R>> action) {
        if (mEvents != null) {
            for (ILinkable<P, R> mEvent : mEvents) {
                action.call(mEvent);
            }
        }
    }

    public <T extends ILinkable<P, ? extends R>> MergeEvent(T... chains) {
        mEvents = (ILinkable<P, R>[]) chains;

        mapEvents(it -> {
            if (it != null) {
                it.listener(new AtomicEventListener(it));
            }
        });
    }


    @Override
    public void onPrepare(P params) {
        super.onPrepare(params);
        mResultCount.reset();
    }


    @Override
    protected void onCall(P params) {
        EventLifecycle lifecycle = getLifecycle();
        mapEvents(it -> {
            if (it != null) {
                it.setLifecycle(lifecycle);
                it.start(params);
            } else {
                mResultCount.increase();
            }
        });
        verify();
    }

    /*验证事件是否完成*/
    private void verify() {
        int size = mEvents == null ? 0 : mEvents.length;
        if (size == 0) {
            next();
        } else if (size == mResultCount.getValue()) {
            Throwable throwable = getThrowable();
            if (throwable != null) {
                error(throwable);
            } else {
                next((R[]) getResult().toArray());
            }
        }
    }

    /**
     * 获得事件中发生的错误，请在所有事件都拿到结果之后调用
     *
     * @return
     */
    private Throwable getThrowable() {
        List<Throwable> errors = new ArrayList<>();

        mapEvents(it -> {
            if (it != null) {
                EventResult<P, R> result = mEventResult.get(it);
                if (result.isError()) {
                    Throwable throwable = result.getThrowable();
                    errors.add(throwable);
                }
            }
        });

        if (!errors.isEmpty()) {
            if (errors.size() == 1) {
                return errors.get(0);
            } else {
                MergeException exception = new MergeException();
                for (int i = 0; i < errors.size(); i++) {
                    exception.put(errors.get(i));
                }
                return exception;
            }
        }
        return null;
    }

    private List<R> getResult() {
        List<R> arrays = new ArrayList<>();

        mapEvents(it -> {
            if (it == null) {
                arrays.add(null);
            } else {
                arrays.add(mEventResult.get(it).getResult());
            }
        });

        return arrays;
    }

    /**
     * 同步的事件监听
     */
    private class AtomicEventListener implements OnEventListener<P, R> {
        private ILinkable mILinkable;

        public AtomicEventListener(ILinkable mILinkable) {
            this.mILinkable = mILinkable;
        }

        @Override
        public void onStart(ILinkable<P, R> event, P params) {

        }

        @Override
        public void onError(Throwable e) {
            mEventResult.put(mILinkable, new EventResult(mILinkable, e));
            mResultCount.increase();
        }

        @Override
        public void onNext(R result) {
            mEventResult.put(mILinkable, new EventResult(mILinkable, result));
            mResultCount.increase();
        }

        @Override
        public void onComplete() {
            verify();
        }
    }


    private static final class EventResult<P, R> {
        private ILinkable<P, R> mEvent;
        private Throwable mThrowable;
        private R mResult;

        private boolean mIsError;

        public EventResult(ILinkable<P, R> mEvent, R result) {
            this.mEvent = mEvent;
            this.mResult = result;
            this.mIsError = false;
        }

        public EventResult(ILinkable<P, R> mEvent, Throwable throwable) {
            this.mEvent = mEvent;
            this.mThrowable = throwable;
            this.mIsError = true;
        }

        public boolean isError() {
            return mIsError;
        }

        public Throwable getThrowable() {
            return mThrowable;
        }

        public R getResult() {
            return mResult;
        }
    }

}
