package com.acap.ec;

import com.acap.ec.action.CallBack;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      懒加载的事件，允许事件在执行时动态加载
 *
 * Created by ACap on 2021/7/4 15:54
 * </pre>
 */
public class LazyEvent<P, R> extends Event<P, R> {
    private CallBack<Event<P, R>> mEventAction;

    private Event<P, R> mRunning;

    public LazyEvent(CallBack<Event<P, R>> action) {
        this.mEventAction = action;
    }

    @Override
    protected void onCall(P params) {
        mRunning = mEventAction.call();
        mRunning.addOnEventListener(mOnEventListener);
        mRunning.start(params);
    }

    @Override
    public void finish() {
        super.finish();
        if (mRunning != null) {
            mRunning.finish();
        }
    }

    @Override
    protected void onInterrupt() {
        super.onInterrupt();
        if (mRunning != null) {
            mRunning.interrupt();
        }
    }

    private OnEventListener mOnEventListener = new OnEventListener<P, R>() {


        @Override
        public void onStart(P params) {

        }

        @Override
        public void onError(Throwable e) {
            LazyEvent.this.error(e);
        }


        @Override
        public void onNext(R result) {
            LazyEvent.this.next(result);
        }

        @Override
        public void onComplete() {

        }
    };

}
