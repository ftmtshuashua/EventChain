package com.acap.demo.event;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/7 18:57
 * </pre>
 */
public abstract class BaseEvent<P, R> extends Event<P, R> {
    @Override
    protected final void onCall(P params) {
//        if (Utils.getSmallProbabilityEvent(2)) {
//            EUtils.i("onInterrupt", String.format("{%s}.call()", EUtils.id(this)));
//            interrupt();
//        }

        onChildCall(params);

    }

    protected void onChildCall(P params) {

    }



}
