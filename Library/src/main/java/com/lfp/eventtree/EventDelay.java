package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *     延迟一个事件的创建过程,当前一个事件执行成功之后再创建当前事件
 *
 * Function:
 *
 * Created by LiFuPing on 2019/1/12 13:44
 * </pre>
 */
public class EventDelay extends EventChain {
    private OnEventDelayCreate mOnEventDelay;

    private EventChain mDelayEvent; // 延时加载的Event

    public EventDelay(OnEventDelayCreate delayevent) {
        mOnEventDelay = delayevent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mOnEventDelay != null) {
            mDelayEvent = mOnEventDelay.create();
            if (mDelayEvent != null) {
                mDelayEvent.addEventChainObserver(new EventChainObserver() {
                    Throwable e;

                    @Override
                    public void onChainStart() {

                    }

                    @Override
                    public void onStart(EventChain event) {
                        getChainObserverManager().onStart(event);
                    }

                    @Override
                    public void onError(EventChain event, Throwable e) {
                        this.e = e;
                        getChainObserverManager().onError(event, e);
                    }

                    @Override
                    public void onNext(EventChain event) {
                        getChainObserverManager().onNext(event);
                    }

                    @Override
                    public void onChainComplete() {
                        if (this.e == null) {
                            next();
                        } else {
                            error(e);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void call() throws Throwable {
        if (mDelayEvent != null) {
            mDelayEvent.start();
        } else {
            next();
        }
    }

    @Override
    protected void onInterrupt() {
        if (mDelayEvent != null) mDelayEvent.interrupt();
        super.onInterrupt();
    }

    @Override
    protected void onComplete() {
        if (mDelayEvent != null) mDelayEvent.complete();
        super.onComplete();
    }

    /**
     * 事件的创建过程
     */
    @FunctionalInterface
    public interface OnEventDelayCreate {
        /**
         * 创建延期事件
         */
        EventChain create();
    }

}
