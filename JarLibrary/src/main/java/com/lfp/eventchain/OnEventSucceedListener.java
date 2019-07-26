package com.lfp.eventchain;

/**
 * <pre>
 * Tip:
 *      当事件成功执行完成 回调onNext方法
 *
 * Function:
 *
 * Created by LiFuPing on 2019/1/12 12:49
 * </pre>
 */
@FunctionalInterface
public interface OnEventSucceedListener extends OnEventListener {
    @Override
    default void onError(Throwable e) {
    }

    @Override
    void onNext();

    @Override
    default void onComplete() {
    }

    @Override
    default void onStart() {
    }
}
