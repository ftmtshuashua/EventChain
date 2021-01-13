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
public class EventDelayCreate extends EventChain {

    private OnEventDelayCreate mOnEventDelay;

    public EventDelayCreate(OnEventDelayCreate delayevent) {
        mOnEventDelay = delayevent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mOnEventDelay != null) chain(mOnEventDelay.create());
    }

    @Override
    protected void call() {
        next();
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
