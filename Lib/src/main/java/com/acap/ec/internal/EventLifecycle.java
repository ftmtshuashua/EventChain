package com.acap.ec.internal;

import com.acap.ec.ILinkable;

import java.util.LinkedList;

/**
 * <pre>
 * Tip:
 *      管理链的生命周期，提供强制结束链的功能
 *
 * Created by ACap on 2021/7/10 23:50
 * </pre>
 * @author A·Cap
 */
public class EventLifecycle {

    /**
     * 事件完成,如果
     */
    private boolean mIsFinish = false;

    private LinkedList<ILinkable> mEventStack = new LinkedList<>();

    public void finish() {
        if (isFinish()) {
            return;
        }
        mIsFinish = true;

        while (!mEventStack.isEmpty()) {
            mEventStack.getLast().onFinish();
        }
    }

    public boolean isFinish() {
        return mIsFinish;
    }


    /**
     * 事件开始执行的信号
     *
     * @param params 入参
     */
    public synchronized <P, R> void onStart(ILinkable<P, R> event, P params) {
        mEventStack.add(event);
    }

    /**
     * 当事件处理失败，并返回了失败的错误
     *
     * @param e 错误信息,包含一个或者多个错误信息
     */
    public <P, R> void onError(ILinkable<P, R> event, Throwable e) {
    }

    /**
     * 当事件成功处理
     */
    public <P, R> void onNext(ILinkable<P, R> event, R result) {
    }

    /**
     * 当事件结束
     */
    public synchronized <P, R> void onComplete(ILinkable<P, R> event) {
        mEventStack.removeLastOccurrence(event);
    }

    public LinkedList<ILinkable> getEvents() {
        return mEventStack;
    }

}
