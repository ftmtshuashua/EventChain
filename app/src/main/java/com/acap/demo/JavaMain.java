package com.acap.demo;

import com.acap.demo.event.AddEvent;
import com.acap.demo.event.TestEvent;
import com.acap.ec.Event;
import com.acap.ec.listener.OnEventLogListener;

/**
 * <pre>
 * Tip:
 *      用于测试链上调用逻辑正确性
 *
 * Created by ACap on 2021/7/9 21:29
 * </pre>
 */
class JavaMain {

    private static Event<Integer, ?> mEvent = null;

    private static void init() {
        if (mEvent != null) return;
        mEvent = new AddEvent(1).listener(new OnEventLogListener<>("1"))
                .lazy(params -> new AddEvent(2)).listener(new OnEventLogListener<>("2"))
                .merge(new AddEvent(2), new AddEvent(3)).listener(new OnEventLogListener<>("3"))
                .apply(params -> params.get(0) + params.get(1)).listener(new OnEventLogListener<>("4"))
                .chain(new TestEvent<Number, String>("2"))
        ;

        mEvent.listener(new OnEventLogListener<>("监听"));
    }


    public static void main(String[] args) {
        init();

        mEvent.start(1);
    }

}

