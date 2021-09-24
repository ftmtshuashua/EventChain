package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.internal.ProxyEventListener;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      延迟事件，可以让事件在使用时被创建,并能获取上下文中的参数
 *
 * Created by A·Cap on 2021/9/22 10:34
 * </pre>
 */
public class LazyEvent<P, R> extends BaseEvent<P, R> {
    private Apply<P, Event<P, R>> mLazy;
    private Event<P, R> apply;

    public LazyEvent(Apply<P, Event<P, R>> mLazy) {
        this.mLazy = mLazy;
    }

    @Override
    protected void onCall(P params) {
        apply = mLazy.apply(params);
        apply.listener(new ProxyEventListener<>(mListener));
        apply.start(params);
    }

    @Override
    protected void onFinish(boolean isComplete) {
        if (apply != null) {
            apply.finish(isComplete);
        }
        super.onFinish(isComplete);
    }

    private OnEventListener<P, R> mListener = new OnEventListener<P, R>() {

        @Override
        public void onStart(Event<P, R> event, P params) {

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
