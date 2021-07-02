package com.acap.ec;

import com.acap.ec.action.Apply;

/**
 * <pre>
 * Tip:
 *      动作事件
 *
 * Created by ACap on 2021/3/31 16:17
 * </pre>
 */
public class ApplyEvent<P, R> extends Event<P, R> {
    private Apply<P, R> apply;

    public ApplyEvent(Apply<P, R> apply) {
        this.apply = apply;
    }

    @Override
    protected void onCall(P params) {
        next(apply.apply(params));
    }
}
