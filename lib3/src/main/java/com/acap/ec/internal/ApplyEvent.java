package com.acap.ec.internal;

import com.acap.ec.BaseEvent;
import com.acap.ec.action.Apply;

/**
 * <pre>
 * Tip:
 *      一个简单的事件
 *
 * Created by A·Cap on 2021/9/17 17:10
 * </pre>
 */
public class ApplyEvent<P, R> extends BaseEvent<P, R> {
    private Apply<P, R> mApply;

    public ApplyEvent(Apply<P, R> apply) {
        mApply = apply;
    }

    @Override
    protected void onCall(P params) {
        next(mApply.apply(params));
    }
}
