package com.acap.ec.listener;

/**
 * <pre>
 * Tip:
 *      事件节点监听器
 *
 *                ↗ onNext()  ↘
 *      onStart()                 onComplete()
 *                ↘ onError() ↗
 *
 *
 *
 * Function:
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public interface OnEventListener<R> {

    /**
     * 当事件开始
     */
    void onStart();

    /**
     * 当事件处理失败，并返回了失败的错误
     *
     * @param e 错误信息,包含一个或者多个错误信息
     */
    void onError(Throwable e);

    /**
     * 当事件成功处理
     */
    void onNext(R result);

    /**
     * 当事件结束
     */
    void onComplete();
}
