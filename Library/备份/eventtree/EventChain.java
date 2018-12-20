package com.lfp.eventtree;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      事件链
 *
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/19 19:48
 * </pre>
 */
public abstract class EventChain {

    /*标记已经开始运行了*/
    private static final int FLAG_STARTED = 0x00000001;
    /*链条被中断，改状态下不应该继续执行后续事件*/
    private static final int FLAG_INTERRUPT = 0x00000002;
    /*事件执行完成*/
    private static final int FLAG_COMPLETE = 0x00000004;

    private EventChain pre;

    EventChain next;

    EventChangeObserver mEventChangeObserver;
    private OnEventListener listener;
    private int mFlag;

    public EventChain setOnEventListener(OnEventListener l) {
        listener = l;
        return this;
    }

    public EventChain chain(EventChain chain) {
        if (chain == null) return this;
        final EventChain first = chain.getFirst();
        final EventChain last = chain.getLast();
        first.pre = this;
        last.next = this.next;
        if (last.next != null) {
            last.next.pre = last;
        }
        this.next = first;
        if (mEventChangeObserver != null) {
            mEventChangeObserver.onChain(chain);
        }
        return chain;
    }

    public EventChain merge(EventChain... chain) {
        if (chain == null) return this;
        return chain(new EventMerge(chain));
    }


    public EventChain getFirst() {
        EventChain first = this;
        while (first.pre != null) {
            first = first.pre;
        }
        return first;
    }

    public EventChain getLast() {
        EventChain last = this;
        while (last.next != null) {
            last = last.next;
        }
        return last;
    }

    public final void start() {
        if ((mFlag & FLAG_STARTED) != 0) {
            throw new IllegalStateException("The event is started,");
        }
        if (isInterrupt()) {
            throw new IllegalStateException("The event is interrupt,");
        }

        mFlag |= FLAG_STARTED;
        if (pre != null) {
            pre.start();
        } else {
            onStart();
        }
    }

    /* 当开始执行业务的时候，允许使用自己的逻辑*/
    protected void onStart() {
        mFlag |= FLAG_STARTED;

        if (mEventChangeObserver != null) {
            mEventChangeObserver.onStart(this);
        }
        if (listener != null) {
            listener.onStart();
        }
        call();
    }

    protected abstract void call();

    /*表示该事件完成*/
    protected void complete() {
        if (mEventChangeObserver != null) {
            mEventChangeObserver.onComplete(this);
        }
        if (listener != null) {
            listener.onComplete();
        }

        mFlag |= FLAG_COMPLETE;
        if (!isInterrupt() && hasNext() && !next.isInterrupt()) {
            next.onStart();
        }
    }

    public boolean isComplete() {
        return (mFlag & FLAG_COMPLETE) != 0 || isInterrupt();
    }

    public boolean isCompleteChain() {
        EventChain chain = this;
        while (chain != null) {
            if (chain.isComplete()) return true;
            chain = chain.next;
        }
        return false;
    }


    /*打断该链条,之后的事件将不会继续执行*/
    protected void interrupt() {
        mFlag |= FLAG_INTERRUPT;
        if (next != null) {
            next.interrupt();
        }
    }

    protected boolean hasNext() {
        return next != null;
    }

    public boolean isInterrupt() {
        return (mFlag & FLAG_INTERRUPT) != 0;
    }


    public static final EventChain create(OnEventListener listener, EventChain... chain) {
        EventMerge event = new EventMerge(chain);
        event.setOnEventListener(listener);
        return event;
    }


    public void addOnEventChangeObserver(OnEventChangeObserver l) {
        if (mEventChangeObserver == null) {
            mEventChangeObserver = new EventChangeObserver();
        }
        mEventChangeObserver.addOnEventChangeObserver(l);
    }

    public void removeOnEventChangeObserver(OnEventChangeObserver l) {
        if (mEventChangeObserver != null) {
            mEventChangeObserver.removeOnEventChangeObserver(l);
        }
    }

    public EventChain log() { //打印链条日志
        EventChain begin = this;
        while (begin.pre != null) {
            begin = begin.pre;
        }
        Log.e("Tag", "输出链条信息");
        while (begin != null) {
            Log.e("Chain", begin.toString());
            begin = begin.next;
        }
        return this;
    }

    /**
     * 事件变化监听器
     */
    private static final class EventChangeObserver implements OnEventChangeObserver {

        List<OnEventChangeObserver> array;

        public EventChangeObserver() {
            array = new ArrayList<>();
        }

        public void addOnEventChangeObserver(OnEventChangeObserver l) {
            array.add(l);
        }

        public void removeOnEventChangeObserver(OnEventChangeObserver l) {
            array.remove(l);
        }

        @Override
        public void onStart(EventChain event) {
            map(observer -> observer.onStart(event));
        }

        @Override
        public void onComplete(EventChain event) {
            map(observer -> observer.onComplete(event));
        }

        @Override
        public void onChain(EventChain event) {
            map(observer -> observer.onChain(event));
        }


        public void map(Action1<OnEventChangeObserver> action1) {
            Iterator<OnEventChangeObserver> array = this.array.iterator();
            while (array.hasNext()) {
                action1.call(array.next());
            }
        }

    }

}
