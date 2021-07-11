package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.internal.EventLifecycle;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      可以链接的对象的抽象
 *      所有子类实现都能以链的形式进行链节,以此来确定子类的顺序关系
 *      使用方式请参考{@link Event}
 *
 * @author A·Cap
 * @param <P> 对象入参类型 - 表示该对象接收参数的类型
 * @param <R> 对象出参类型 - 表示对象执行结束后的结果类型
 *
 * Created by ACap on 2021/7/8 16:29
 * </pre>
 */
public interface ILinkable<P, R> {

    /**
     * 将另一个可链接的对象放到该对象的后面,这样就能确定当前对象和链接对象的顺序关系
     *
     * @param event 被链接到当前对象之后的对象
     * @param <R1>  被连接对象的出参类型
     * @return 链对象:包含当前对象与被链接对象之间的关系
     */
    <R1> ILinkable<P, R1> chain(ILinkable<? super R, R1> event);

    /**
     * 将多个对象以{@link MergeEvent}的方式链接到当前对象的后面
     *
     * @param events 被链接对象集合
     * @param <R1>   被连接对象的出参类型
     * @return 链对象:包含当前对象与被链接对象之间的关系
     */
    <R1> ILinkable<P, R1[]> merge(ILinkable<? super R, ? extends R1>... events);

    /**
     * 对链的结果进行处理或转换
     *
     * @param apply 处理结果的回调函数
     * @param <R1>  处理之后的数据类型
     * @return 链对象:包含当前对象与被链接对象之间的关系
     */
    <R1> ILinkable<P, R1> apply(Apply<R, R1> apply);

    /**
     * 当前对象与其他对象确定关系时，将会绑定一个包含它们之前关系的链对象。
     * 当前对象通常通过这个链对象来确定完成后的逻辑
     *
     * @param chain 链对象
     */
    void onAttachedToChain(Chain<?, ?> chain);

    /**
     * 更具链上的顺序执行这个链
     */
    void start();

    /**
     * 更具链上的顺序执行这个链
     *
     * @param params 当前链的入参
     */
    void start(P params);

    /**
     * 添加对象监听器,并把它放入所有已添加的监听器之前的位置,让这个监听器被第一个使用
     *
     * @param listener 听众
     * @return 当前对象本身
     */
    ILinkable<P, R> listenerFirst(OnEventListener<P, R> listener);

    /**
     * 添加当前对象的听众,已添加的听众不会被重复添加
     *
     * @param listener 听众
     * @return 当前对象本身
     */
    ILinkable<P, R> listener(OnEventListener<P, R> listener);

    /**
     * 移除当前对象的听众
     *
     * @param listener 听众
     * @return 当前对象本身
     */
    ILinkable<P, R> listenerRemove(OnEventListener<P, R> listener);


    /**
     * 如果当前对象的逻辑正在被执行则为{@code true},否者它为{@code false}
     *
     * @return 当前对象是否正在被执行
     */
    boolean isStart();

    /**
     * 乳沟当前对象的逻辑被执行，并且已执行完成则为{@code true},否则为{@code false}
     *
     * @return 当前对象是否以执行完成
     */
    boolean isComplete();


    /**
     * 当前事件开始执行之前，会先回调该方法。通常在这里执行初始化逻辑
     *
     * @param params 对象接受的入参
     */
    void onPrepare(P params);


    /**
     * 当事件执行完成,在调用外部监听器之前回调该方法
     */
    void onComplete();


    /**
     * <pre>
     * 强制关闭正在执行对象，并停止对后续对象进行调用.
     * 该操作会直接调用已经开始的对象的{@link OnEventListener#onComplete()}方法.
     * 调用该方法之后，当前链将被关闭，这意味着你无法使用{@link #start()}方法来再次执行它
     * </pre>
     */
    void finish();

    /**
     * 如果当前事件的{@link #finish()}方法被调用，则为{@code true},
     * 否则为{@code false}
     *
     * @return 当前对象是否已结束
     */
    boolean isFinish();

    /**
     * 当用户调用了{@link #finish()}时，正在执行的事件的当前函数将被调用，事件函数内处理自己的强制完成的逻辑
     */
    void onFinish();

    /**
     * 设置链的生命周期对象,用于管理链的完成状态.
     *
     * @param lifecycle 生命周期对象
     */
    void setLifecycle(EventLifecycle lifecycle);

    /**
     * 获得链上的生命周期对象
     *
     * @return 生命周期对象
     */
    EventLifecycle getLifecycle();


}
