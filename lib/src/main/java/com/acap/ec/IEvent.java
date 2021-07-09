package com.acap.ec;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/9 18:35
 * </pre>
 */
interface IEvent<R> {

    /**
     * 当事件逻辑执行完成时候,请调用该函数来通知链上下一个事件
     */
    void next();

    /**
     * 当事件逻辑执行完成时候,请调用该函数来通知链上下一个事件
     *
     * @param result 当前事件的出参 - 表示事件执行结束后的结果
     */
    void next(R result);

    /**
     * <pre>
     * 如果当前事件的逻辑执行时遇到一些问题，并且不希望后续的事件继续执行时。
     * 请调用该函数,用于通知'链'
     * </pre>
     *
     * @param throwable 当前事件的异常信息
     */
    void error(Throwable throwable);

}
