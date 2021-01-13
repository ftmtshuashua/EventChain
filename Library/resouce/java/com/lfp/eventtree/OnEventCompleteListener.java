package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件完成监听,在时间结束之后调用。一个事件或者事件链只会调用一次
 *      适配 Lambda 表达式
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
@FunctionalInterface
public interface OnEventCompleteListener extends OnEventListener {

    /**
     * 在时间结束之后调用。一个事件或者事件链只会调用一次
     */
    @Override
    void onComplete();


    @Override
    default void onStart() {
    }

    @Override
    default void onError(Throwable e) {
    }

    @Override
    default void onNext() {
    }
}
