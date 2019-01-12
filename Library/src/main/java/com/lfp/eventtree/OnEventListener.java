package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件监听，包含一个完整的事件生命周期
 *
 *                ↗ onNext()  ↘
 *      onStart()                 onComplete()
 *                ↘ onError() ↗
 *
 *
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
public interface OnEventListener {

    /**
     * 在时间开始之前调用。一个事件或者事件链只会调用一次
     */
    void onStart();

    /**
     * 当事件或者事件链发生错误的时候调用，并且会立即中断该事件或者事件链,然后调用 onComplete()
     *
     * @param e 错误信息,包含一个或者多个错误信息
     * @see com.lfp.eventtree.excption.MultiException
     * @see com.lfp.eventtree.excption.EventException
     */
    void onError(Throwable e);

    /**
     * 当一个事件或者事件链结束之后调用
     */
    void onNext();

    /**
     * 在时间结束之后调用。一个事件或者事件链只会调用一次
     */
    void onComplete();
}
