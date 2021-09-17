package com.acap.ec.internal;

import com.acap.ec.action.Apply;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by A·Cap on 2021/9/17 17:10
 * </pre>
 */
public class ApplyCreateEvent<P, R> {
    private Apply<P, R> mApply;

    public ApplyCreateEvent(Apply<P, R> apply) {
        mApply = apply;
    }

}
