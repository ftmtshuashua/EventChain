package com.lfp.eventtree;

import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Tip:
 *      合并事件
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/19 20:19
 * </pre>
 */
class EventMerge extends EventChain {

    EventChain[] mMerge;

    public EventMerge(final EventChain... chains) {
        if (chains != null) {
            this.mMerge = new EventChain[chains.length];
            for (int i = 0; i < chains.length; i++) {
                this.mMerge[i] = chains[i].getFirst();
                this.mMerge[i].addOnEventChangeObserver(mOnEventChangeObserver);
            }
        }
    }

    @Override
    protected void call() {
        final int size = mMerge == null ? 0 : mMerge.length;
        if (size == 0) {
            complete();
        } else {
            for (int i = 0; i < size; i++) {
                EventChain chain = mMerge[i];
                chain.start();
            }
        }
    }

    @Override
    protected void interrupt() {
        super.interrupt();
        if (mMerge != null) {
            final int size = mMerge.length;
            for (int i = 0; i < size; i++) {
                EventChain chain = mMerge[i];
                chain.interrupt();
            }
        }
    }

    @Override
    public boolean isInterrupt() {
        boolean isInterrupt = super.isInterrupt();
        if (!isInterrupt && mMerge != null) {
            final int size = mMerge.length;
            for (int i = 0; i < size; i++) {
                EventChain chain = mMerge[i];
                if (chain.isInterrupt()) return true;
            }
        }
        return isInterrupt;
    }

    @Override
    public boolean isComplete() {
        if (super.isComplete()) return true;
        if (mMerge == null || mMerge.length == 0) {
            return super.isComplete();
        }

        for (int i = 0; i < mMerge.length; i++) {
            if (!mMerge[i].isCompleteChain()) {
                return false;
            }
        }
        return true;
    }

    OnEventChangeObserver mOnEventChangeObserver = new OnEventChangeObserver() {
        AtomicInteger count = new AtomicInteger(0);

        @Override
        public void onStart(EventChain event) {
            count.incrementAndGet();
        }

        @Override
        public void onComplete(EventChain event) {
            count.decrementAndGet();
            Log.e("onComplete", "一个事件完成：" + event);
            if (count.intValue() == 0) {
                if (!event.hasNext() || event.isInterrupt()) {
                    complete();
                }
            }

        }

        @Override
        public void onChain(EventChain event) {
            event.addOnEventChangeObserver(this);
        }
    };

    @Override
    public String toString() {
        return "The merge array[" +
                mMerge
                + "]";
    }
}
