package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      可以链接的对象的抽象
 *      所有子类实现都能以链的形式进行链节,以此来确定子类的顺序关系
 *
 * @param <P> 对象入参类型 - 表示该对象接收参数的类型
 * @param <R> 对象出参类型 - 表示对象执行结束后的结果类型
 * @param <T> 泛型递归 - 用于确定函数的返回值类型
 *
 * Created by ACap on 2021/7/8 16:29
 * </pre>
 */
public interface ILinkable<P, R> {

    /**
     *
     *
     * @param event 被链接到当前对象之后的对象
     * @param <R1>  被连接对象的出参类型
     * @param <TR>  储存了‘链’的入参和出参对象，‘链’的入参应该是‘链’头对象的入参，而‘链’的出参应该为链尾对象的出参
     * @param <TP>  被连接对象的类型
     * @return 包含对象连接关系的‘链’对象
     */
    /**
     * 将另一个可链接的对象放到该对象的后面,这样就能确定当前对象和链接对象的顺序关系
     *
     * @param event
     * @param <R1>
     * @return
     */
    <R1> ILinkable<P, R1> chain(ILinkable<? super R, R1> event);

    <R1> ILinkable<P, R1[]> merge(ILinkable<? super R, ? extends R1>... events);

    <R1> ILinkable<P, R1> apply(Apply<R, R1> apply);

    void onAttachedToChain(Chain chain);

    void start();

    void start(P params);


    /**
     * 添加对象监听器
     *
     * @param listener
     */
    ILinkable<P, R> addOnEventListener(OnEventListener<P, R> listener);

    /**
     * 移除添加对象监听器
     *
     * @param listener
     */
    ILinkable<P, R> removeOnEventListener(OnEventListener listener);


    boolean isStart();

    boolean isComplete();

    //
    void onPrepare();
//
//
//    void interrupt();
//
//    void onInterrupt();
//
//
//    boolean isInterrupt();
//
//    void onComplete();
//
//
//
//    void finish();
//
//    boolean isFinish();

}
