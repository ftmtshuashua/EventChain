package com.acap.ec.action;

/**
 * <pre>
 * Tip:
 *      事件的数据处理接口,用于对数据进行处理和转化
 *
 * Created by ACap on 2021/3/31 16:14
 * </pre>
 *
 * @param <P> 事件的出参类型
 * @param <R> 处理和转化之后的数据类型
 * @author A·Cap
 */
public interface Apply<P, R> {
    /**
     * 数据处理
     *
     * @param params 被处理的对象
     * @return 处理结果
     */
    R apply(P params);
}
