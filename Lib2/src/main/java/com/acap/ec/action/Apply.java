package com.acap.ec.action;

/**
 * <pre>
 * Tip:
 *      动作执行
 *
 * Created by ACap on 2021/3/31 16:14
 * </pre>
 */
public interface Apply<P, R> {
    R apply(P params);
}
