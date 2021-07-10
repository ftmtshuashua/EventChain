package com.acap.demo.event;

import com.acap.demo.utils.Utils;
import com.acap.ec.Event;
import com.acap.ec.utils.EUtils;

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
        if (Utils.getSmallProbabilityEvent(2)) {
            EUtils.i("finish", String.format("{%s}.call()", EUtils.id(this)));
            finish();
        }

        onChildCall(params);

    }

    protected void onChildCall(P params) {

    }



}
