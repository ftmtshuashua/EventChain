package com.acap.ec.listener;

import com.acap.ec.EventChain;

/**
 * <pre>
 * Tip:
 * 在Call之前调用
 *
 * Created by ACap on 2021/3/30 14:08
 * </pre>
 */
public interface OnCallBeforeListener<P, R> {
    void onBefore(EventChain<P, R> node);
}
