package com.acap.ec.listener;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      事件日志监听器
 *
 * Created by ACap on 2021/3/31 11:32
 * </pre>
 *
 * @author A·Cap
 */
public class OnEventLogListener<P, R> implements OnEventListener<P, R> {
    private String mTag;

    public OnEventLogListener(String mTag) {
        this.mTag = mTag;
    }


    @Override
    public void onStart(Event<P, R> event, P params) {
        System.out.println(String.format("%s:onStart(%s)", mTag, params));
    }

    @Override
    public void onError(Throwable e) {
        System.out.println(String.format("%s:onError(%s)", mTag, e));
    }

    @Override
    public void onNext(R result) {
        System.out.println(String.format("%s:onNext(%s)", mTag, result));
    }

    @Override
    public void onComplete() {
        System.out.println(String.format("%s:onComplete()", mTag));
    }

}
