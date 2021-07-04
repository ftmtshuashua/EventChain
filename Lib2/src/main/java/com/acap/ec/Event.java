package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.EUtils;
import com.acap.ec.utils.ListenerMap;

/**
 * <pre>
 * Tip:
 *      事件链是一种尝试
 *      将任何业务逻辑抽象成一个事件,该事件只关心自己的业务逻辑
 *
 * @param <P> params,上游事件完成之后的产物
 * @param <R> result,当前事件的产物
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public abstract class Event<P, R> {

    /**
     * 这是事件所在的链，该实例储存的链的信息和状态
     */
    private Chain mChain;

    /**
     * 在同一条链上，当前事件的下一个事件的实例，如果该实例为空，则表示当前事件是链上的最后一个事件
     */
    private Event<? super R, ?> mNext;


    /**
     * 该事件的参数信息,通常表示通过 {@link #start(Object)} 方法传入的数据
     *
     * <p>
     * 注意：就算是通过当前事件的 {@link #start(Object)}方法传入的参数,也不一定会传递给该当前事件.
     * 因为{@link #start(Object)}方法传入的参数，通常是被链头的事件接收
     * </p>
     */
    private P mParams;


    /**
     * 在已经创建完成的链中插入一个事件，为了保证不会影响到链，应注意以下情况
     * <ul>
     *     <li>
     *         由于插入事件位于当前事件之后，所以插入事件的接收参数类型因为当前事件的返回值
     *     </li>
     *     <li>
     *         为了不影响后续事件的执行，插入事件的返回值应该与当前事件的返回值相同
     *     </li>
     * </ul>
     */
    public <T extends Event<? super R, R>> T insert(T event) {
        event.setChain(getChain());
        if (mNext != null) {
            event.chain(mNext);
        }
        mNext = event;
        return event;
    }

    /**
     * 在当前事件执行之后执行另外一个事件
     * <p>
     * 注意：如果当前事件的后面已存在事件，则该方法会导致后面的事件丢失，如果希望在链中安全的插入事件请使用{@link #insert(Event)}方式
     * </p>
     *
     * @param event 被放在当前事件之后的事件实例
     * @return 返回 被放入的事件实例本身
     */
    public <Result, T extends Event<? super R, Result>> T chain(T event) {
        event.setChain(getChain());
        mNext = event;
        return event;
    }

    /**
     * 合并操作符,用于打包传入事件集的结果, 当事件集中所有事件都返回结果之后才继续执行之后的事件。
     * <p>
     * 值得注意的是,合并操作符的返回值通常是一个对象数组
     * </p>
     *
     * @param events 事件集
     * @return 事件集的返回值对象数组, 该数组包含了事件集中所有事件的返回值。对象数组中返回值的顺序与事件集的顺序相同
     */
    public <Result> MergeEvent<R, Result> merge(Event<? super R, ? extends Result>... events) {
        return (MergeEvent<R, Result>) chain(new MergeEvent(events));
    }

    /**
     * 根据事件集中事件的处理速度，使用最先完成的事件作为返回值
     *
     * @param events 事件集
     * @return 事件集中最先返回结果的对象
     */
    public <Result> OneOfEvent<R, Result> oneOf(Event<? super R, ? extends Result>... events) {
        return (OneOfEvent<R, Result>) chain(new OneOfEvent(events));
    }

    /**
     * 对事件返回的数据进行处理
     * <ul>
     *     <li>
     *      1.数据类型转换
     *     </li>
     *     <li>
     *      2.数据过滤
     *     </li>
     *     <li>
     *      3.数据合并
     *     </li>
     *     <li>
     *      ...
     *     </li>
     * </ul>
     *
     * @param apply    修剪算法
     * @param <Result>
     * @return
     */
    public <Result> ApplyEvent<R, Result> apply(Apply<R, Result> apply) {
        return chain(new ApplyEvent<>(apply));
    }


    /**
     * 打印前一个事件的返回值
     *
     * @param tag 日志的Tag
     * @return 数据打印事件
     */
    public ApplyEvent<R, R> print(String tag) {
        return chain(new ApplyEvent(params -> {
            if (params == null) {
                EUtils.i(tag, "null");
            } else {
                EUtils.i(tag, params.toString());
            }
            return params;
        }));
    }


    /**
     * 快速创建
     *
     * @param events
     * @param <P>
     * @return
     */
    public static final <P, R> MergeEvent<P, R> create(Event<? super P, ? extends R>... events) {
        return new MergeEvent(events);
    }


    /**
     * 获得事件绑定的链信息
     *
     * @return
     */
    public Chain getChain() {
        if (mChain == null) {
            mChain = new Chain(this);
        }
        return mChain;
    }

    /**
     * 设置链信息，链上储存有整条链的信息
     *
     * @param chain
     */
    protected void setChain(Chain chain) {
        mChain = chain;
        if (mNext != null) {
            mNext.setChain(chain);
        }
    }

    /**
     * 获得链上当前事件的下一个事件的实例
     *
     * @return 如果为Null，表示当前事件为链上最后一个事件
     */
    public Event<? super R, ?> getNext() {
        return mNext;
    }

    /**
     * 从链头开始执行链上的逻辑
     */
    public void start() {
        start(null);
    }

    /**
     * 从链头开始执行链上的逻辑
     *
     * @param params 启动链所需要的前置参数
     */
    public void start(Object params) {
        getChain().start(params);
    }


    /**
     * 当事件被执行时
     */
    void perStart(P params) {
        mParams = params;

        mOnEventListeners.map(listener -> listener.onStart());
        onCall(params);
    }

    public P getParams() {
        return mParams;
    }

    /**
     * 停止链上的事件传递
     */
    public void stop() {
        getChain().stop();
    }

    /**
     * 如果当前事件发生错误,则通过error方法向后传递
     *
     * @param throwable 事件中发生的错误
     */
    protected void error(final Throwable throwable) {
        if (getChain().isStop()) {
            System.out.println("链上发生了错误，但是链被终止了!");
            throwable.printStackTrace();
            return;
        }

//        throwable = EUtils.generateThrowable(throwable, this);

        mOnEventListeners.map(listener -> listener.onError(throwable));
        getChain().getCL().onError(this, throwable);

        mOnEventListeners.map(listener -> listener.onComplete());
        getChain().getCL().onChainComplete();
    }

    /**
     * 事件产物信号，该信号不会通知下一事件开始执行
     *
     * @param result 事件的产物
     */
    protected void result(R result) {
        if (getChain().isStop()) return;
        mOnEventListeners.map(listener -> listener.onNext(result));
        getChain().getCL().onNext(this, result);
    }

    /**
     * 当前事件的逻辑处理完成之后,将事件的产物向后传递，交给下一事件来处理
     *
     * @param result 当前事件的产物
     */
    protected void next(R result) {
        if (getChain().isStop()) return;
        mOnEventListeners.map(listener -> listener.onNext(result));
        getChain().getCL().onNext(this, result);

        mOnEventListeners.map(listener -> listener.onComplete());
        if (mNext != null) {
            mNext.perStart(result);
        } else {
            getChain().getCL().onChainComplete();
        }
    }

    /**
     * 当前事件被执行的时候回调，事件自己的逻辑在该方法中开始执行
     *
     * @param params 来自上游事件的产物
     */
    protected abstract void onCall(P params);


    private ListenerMap<OnEventListener<R>> mOnEventListeners = new ListenerMap<>();

    public <T extends Event<P, R>> T addOnEventListener(OnEventListener<R> listener) {
        mOnEventListeners.register(listener);
        return (T) this;
    }

    public Event<P, R> removeOnEventListener(OnEventListener<R> listener) {
        mOnEventListeners.unregister(listener);
        return this;
    }

    public Event<P, R> addOnChainListener(OnChainListener<R> listener) {
        getChain().getCL().add(listener);
        return this;
    }

    public Event<P, R> removeOnChainListener(OnChainListener<R> listener) {
        getChain().getCL().remove(listener);
        return this;
    }


    /**
     * @param event
     */
    public static final void print(Event event) {
        EUtils.print(event);
    }

}

