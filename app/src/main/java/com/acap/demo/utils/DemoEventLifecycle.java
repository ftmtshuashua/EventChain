package com.acap.demo.utils;

import com.acap.demo.event.DemoEvent;
import com.acap.ec.ILinkable;
import com.acap.ec.internal.EventLifecycle;
import com.acap.ec.utils.EUtils;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/11 13:56
 * </pre>
 */
public class DemoEventLifecycle extends EventLifecycle {

    public synchronized <P, R> void onStart(ILinkable<P, R> event, P params) {
        int start = getEvents().size();
        super.onStart(event, params);
        int end = getEvents().size();
        EUtils.i(id(event), String.format("入栈:%s -> %s", start, end));
    }

    public synchronized <P, R> void onComplete(ILinkable<P, R> event) {
        int start = getEvents().size();
        super.onComplete(event);
        int end = getEvents().size();
        EUtils.i(id(event), String.format("出栈:%s -> %s", start, end));
    }

    public static String id(ILinkable event) {
        if (event instanceof DemoEvent) {
            DemoEvent ev = (DemoEvent) event;
            return event.getClass().getSimpleName() + "(" + ev.getResult() + ")";
        }
        return EUtils.id(event);
    }

}
