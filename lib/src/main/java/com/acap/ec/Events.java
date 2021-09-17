package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.internal.ApplyCreateEvent;

/**
 * <pre>
 * Tip:
 *      Event 构造器
 *
 * Created by A·Cap on 2021/9/17 17:07
 * </pre>
 */
public final class Events {

    private Events() {
    }

    public static <P, R> Event<P, R> create(Apply<P, R> apply) {
//        return new ApplyCreateEvent(apply);
        return null;
    }

}
