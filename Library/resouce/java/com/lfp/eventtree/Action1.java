package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      CallBack
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 10:22
 * </pre>
 */
@FunctionalInterface
interface Action1<T> {
    void call(T t);
}
