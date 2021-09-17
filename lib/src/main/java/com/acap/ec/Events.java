package com.acap.ec;

import com.acap.ec.action.Apply;

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

    public static <P, R> Event<P, R> create(Apply<P, R> apply) {
//        return new ApplyCreateEvent(apply);
        return null;
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
    public static <P, R, R1> Event<P, R1> chain(Event<P, R> head, Event<? super R, R1> tail) {
        return new Chain<>((BaseEvent<P, R>) head, (BaseEvent<? super R, R1>) tail);
    }

}
