package com.acap.ec;

import com.acap.ec.action.Apply;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <pre>
 * Tip:
 *      Event 构造器
 *
 * Created by A·Cap on 2021/9/17 17:07
 * </pre>
 */
public final class Events {

    private Events() {
    }

    /**
     * 创建一个简单的事件
     *
     * @param apply 事件的业务逻辑
     * @param <P>   事件的入参
     * @param <R>   事件的执行结果
     * @return 普通事件
     */
    public static final <P, R> Event<P, R> create(@NotNull Apply<P, R> apply) {
        return new ApplyEvent(apply);
    }

    /**
     * 创建一个懒加载的事件,该事件将在将要被执行时创建,并能获取上下文中的参数
     *
     * @param apply 事件获取器
     * @param <P>   事件的入参
     * @param <R>   事件的执行结果
     * @return 懒加载事件
     */
    public static final <P, R> Event<P, R> lazy(@NotNull Apply<P, Event<P, R>> apply) {
        return new LazyEvent<>(apply);
    }

    /**
     * 创建一个又多个事件组成的并发事件,并发事件能保证组成它的事件全部响应之后才发出自己的响应
     *
     * @param events 组成并发事件的事件集
     * @param <P>    事件的入参
     * @param <R>    事件的执行结果
     * @return 并发事件
     */
    public static final <P, R> Event<P, List<R>> merge(@NotNull Event<? super P, ? extends R>... events) {
        return new MergeEvent(events);
    }

    /**
     * 将两个事件链接起来,组成带有顺序的链式结构
     *
     * @param head 链头的事件
     * @param tail 链尾的事件
     * @param <P>  链头事件的入参
     * @param <R>  链头事件的执行结果和链尾事件的入参
     * @param <R1> 链尾事件的执行结果
     * @return 存放链头与链尾的关系的事件
     */
    public static final <P, R, R1> Event<P, R1> chain(Event<P, R> head, Event<? super R, R1> tail) {
        return new Chain<>((BaseEvent<P, R>) head, (BaseEvent<? super R, R1>) tail);
    }

}
