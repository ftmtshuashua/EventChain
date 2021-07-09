package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      可以链接的事件的抽象
 *      所有子类实现都能以链的形式进行链节,以此来确定子类的顺序关系
 *
 * @param <P> 事件入参类型 - 表示该事件接收参数的类型
 * @param <R> 事件出参类型 - 表示事件执行结束后的结果类型
 * @param <T> 泛型递归 - 用于确定函数的返回值类型
 *
 * Created by ACap on 2021/7/8 16:29
 * </pre>
 */
public interface ILinkableEvent<P, R, T extends ILinkableEvent<P, R, T>> {


    /**
     * 将另一个可链接的事件放到该事件的后面,这样就能确定当前事件和链接事件的顺序关系
     *
     * @param event 被链接到当前事件之后的事件
     * @param <R1>  被连接事件的出参类型
     * @param <TR>  储存了‘链’的入参和出参对象，‘链’的入参应该是‘链’头事件的入参，而‘链’的出参应该为链尾事件的出参
     * @param <TP>  被连接事件的类型
     * @return 包含事件连接关系的‘链’对象
     */
    <R1> Chain<P, R1> chain(ILinkableEvent<? super R, R1, ?> event);


    <R1, T extends ILinkableEvent<? super R, ? extends R1, ?>> Chain<P, R1[]> merge(T... events);

    <R1> Chain<P, R1> apply(Apply<R, R1> apply);


    void start();

    void start(P params);
    /**
     * 事件的逻辑函数.事件的逻辑从该函数开始执行,以该调用完成函数之后结束
     *
     * @param params 事件入参
     */
    void onCall(P params);

    boolean isStart();

    void onPrepare();


    void interrupt();

    void onInterrupt();


    boolean isInterrupt();

    void onComplete();

    boolean isComplete();


    void finish();

    boolean isFinish();

    /**
     * 添加事件监听器
     *
     * @param listener
     */
    T addOnEventListener(OnEventListener<P, R> listener);

    /**
     * 移除添加事件监听器
     *
     * @param listener
     */
    T removeOnEventListener(OnEventListener listener);

    /**
     * 添加链监听器
     *
     * @param listener
     */
    T addOnChainListener(OnChainListener<P, R> listener);

    /**
     * 移除添加链监听器
     *
     * @param listener
     */
    T removeOnChainListener(OnChainListener listener);

}
