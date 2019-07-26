package com.lfp.eventchain;

/**
 * <pre>
 * Tip:
 *      当事件执行失败 回调onError方法
 *
 * Function:
 *
 * Created by LiFuPing on 2019/1/12 12:49
 * </pre>
 */
@FunctionalInterface
public interface OnEventFailureListener extends OnEventListener {
    @Override
    void onError(Throwable e);

    @Override
    default void onNext() {
    }

    @Override
    default void onComplete() {
    }

    @Override
    default void onStart() {
    }
}
