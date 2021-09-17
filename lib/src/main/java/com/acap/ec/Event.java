package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.listener.OnEventListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <pre>
 * Tip:
 *      EventChain 的核心思想，业务逻辑的抽象.<br/>
 *      使用请继承{@link BaseEvent}
 *
 *
 * @param <P> 输入 - 业务接收的参数
 * @param <R> 输出 - 业务执行的结果
 *
 * Created by A·Cap on 2021/9/17 16:51
 * </pre>
 */
public interface Event<P, R> {

    /**
     * 指定一个事件放到当前事件之后执行,并在当前事件执行结束之后将执行结果当作它的输入参数发送给它.<br/>
     * {@link #chain(Event)} 指定当前事件与下一个事件的逻辑关系事件, 并保证了它们能按照预想的顺序正确的执行
     *
     * @param event 下一个事件的对象
     * @param <R1>  下一个事件的执行结果的类型
     * @return 链式逻辑事件
     */
    <R1> Event<P, R1> chain(@NotNull Event<? super R, R1> event);


    /**
     * 指定一组事件放到当前事件之后执行,并在当前事件执行结束之后将执行结果当作它们的输入参数发送给它们.<br/>
     * {@link #merge(Event[])}用于创建并发的逻辑结构,当前事件开始时同时执行事件组中的所有事件(如果它们是异步的),并保证事件组中所有事件全部完成之后才进行后续逻辑
     *
     * @param events 希望并行执行的事件集
     * @param <R1>   从事件集中统计出来的结果。并且输出的结果的顺序，与事件集的顺序相同
     * @return 并发逻辑事件
     */
    <R1> Event<P, R1[]> merge(@NotNull Event<? super R, ? extends R1>... events);


    /**
     * 对当前事件的输出结果进行处理,并生成新的结果<br/>
     * 通常用于对{@link #merge(Event[])}的结果进行合并,对于复用来说这是很有必要的
     *
     * @param apply 处理结果的回调函数
     * @param <R1>  处理之后的数据类型
     * @return 链式逻辑事件
     */
    <R1> Event<P, R1> apply(@NotNull Apply<R, R1> apply);

    /**
     * 开始执行已构建完成的事件链<br/>
     * 对于不需要启动参数的事件链来说这是一个聪明的做法
     */
    default void start() {
        start(null);
    }

    /**
     * 开始执行已构建完成的事件链
     *
     * @param params 事件链中链头事件的启动参数
     */
    void start(@Nullable P params);

    /**
     * 添加当前事件的生命周期的监听器，并保证它不会被重复添加<br/>
     * {@link OnEventListener}中包含了以下可用的监听事件<br/>
     * {@link OnEventListener#onStart(Event, Object)} 事件开始执行<br/>
     * {@link OnEventListener#onNext(Object)} 事件执行之后的结果<br/>
     * {@link OnEventListener#onError(Throwable)} 事件发生错误<br/>
     * {@link OnEventListener#onComplete()} 事件完成<br/>
     *
     * @param listener 事件生命周期监听器
     * @return 当前事件本身
     */
    Event<P, R> listener(@Nullable OnEventListener<P, R> listener);


    /**
     * 添加当前事件的生命周期的监听器，并保证它不会被重复添加<br/>
     * {@link OnEventListener}中包含了以下可用的监听事件<br/>
     * {@link OnEventListener#onStart(Event, Object)} 事件开始执行<br/>
     * {@link OnEventListener#onNext(Object)} 事件执行之后的结果<br/>
     * {@link OnEventListener#onError(Throwable)} 事件发生错误<br/>
     * {@link OnEventListener#onComplete()} 事件完成<br/>
     *
     * @param listener 事件生命周期监听器
     * @return 当前事件本身
     */
    Event<P, R> listenerFirst(@Nullable OnEventListener<P, R> listener);

    /**
     * 移除当前事件的生命周期的监听器
     *
     * @param listener 事件生命周期监听器
     * @return 当前事件本身
     */
    Event<P, R> listenerRemove(@Nullable OnEventListener<P, R> listener);


    /**
     * 停止链上正在执行的逻辑.它会立即事件的{@link BaseEvent#onFinish()}方法,给与事件一个进行资源回收的时机<br/>
     * 该操作对处于相同链上的所有事件生效
     */
    default void finish() {
        finish(false);
    }

    /**
     * 停止链上正在执行的逻辑.它会立即事件的{@link BaseEvent#onFinish()}方法,给与事件一个进行资源回收的时机<br/>
     * 该操作对处于相同链上的所有事件生效
     *
     * @param isComplete 用于控制是否执行{@link BaseEvent#onComplete()}的逻辑
     */
    void finish(boolean isComplete);

}
