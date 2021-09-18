package com.acap.ec.action;

/**
 * <pre>
 * Tip:
 *      Call 接口函数
 *
 * Created by ACap on 2021/6/30 14:44
 * </pre>
 *
 * @author A·Cap
 */
public interface Action1<T> {
    /**
     * call函数的逻辑
     *
     * @param t 调用则对象
     */
    void call(T t);
}
