package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.excption.EventInterruptException;
import com.acap.ec.internal.ILinkableEvent;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.ListenerMap;

/**
 * <pre>
 * Tip:
 *      事件链是一种尝试
 *      将任何业务逻辑抽象成一个事件,该事件只关心自己的业务逻辑
 *
 * @param <P> params:上游事件完成之后的产物
 * @param <R> result:当前事件的产物
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public abstract class Event<P, R> implements ILinkableEvent<P, R, Event<P, R>> {

    /**
     * 这是事件所在的链，该实例储存的链的信息和状态
     */
    private Chain mChain;

    /**
     * 在同一条链上，当前事件的下一个事件的实例，如果该实例为空，则表示当前事件是链上的最后一个事件
     */
    Event<? super R, ?> mNext;

    /**
     * 事件回调监听器集合
     */
    private final ListenerMap<OnEventListener<P, R>> mOnEventListeners = new ListenerMap<>();

    /**
     * 当前事件是否完成的状态。如果它为{@code true}，那么{@link #next(Object)}和{@link #error(Throwable)}的调用将不会生效
     */
    private boolean mIsCompleted = false;
    /**
     * 当前事件是否正在执行
     */
    private boolean mIsStarted = false;

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


    @Override
    public <R1, T1 extends ILinkableEvent<P, R1, T1>, T2 extends ILinkableEvent<? super R, R1, T2>> T1 chain(T2 event) {
        return null;
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
    public <Result, T extends Event<? super R, Result>> Chain chain(T event) {
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
//    public <Result> MergeEvent<R, Result> merge(Event<? super R, ? extends Result>... events) {
//        return (MergeEvent<R, Result>) chain(new MergeEvent(events));
//    }
    public <Result, T extends Event<? super R, ? extends Result>> MergeEvent<R, Result> merge(T... events) {
        return (MergeEvent<R, Result>) chain(new MergeEvent(events));
    }

    /* *//**
     * 根据事件集中事件的处理速度，使用最先完成的事件作为返回值
     *
     * @param events 事件集
     * @return 事件集中最先返回结果的对象
     *//*
    public <Result> OneOfEvent<R, Result> oneOf(Event<? super R, ? extends Result>... events) {
        return (OneOfEvent<R, Result>) chain(new OneOfEvent(events));
    }*/

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
        ApplyEvent<R, Result> event = new ApplyEvent<>(apply);
        chain(event);
        return event;
    }


    /**
     * 快速创建
     *
     * @param event
     * @return
     */
    public static final <P, R, T extends Event<P, R>> T create(T event) {
        return event;
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
     * 当事件所在的链准备执行的时候
     */
    void performChainPrepareStart() {
        mIsCompleted = false;
        mIsStarted = false;

        onPrepare();
    }

    /**
     * 当链被启动时候，链上所有事件都会收到该回调，使事件有机会更新自己的状态
     * <p>避免在这里做耗时操作<p/>
     */
    protected void onPrepare() {
//        EUtils.i("onPrepare", String.format("{%s}.onPrepare()", EUtils.id(this)));

    }

    /**
     * 从链头开始执行链上的逻辑
     */
    public final void start() {
        start(null);
    }

    /**
     * 从链头开始执行链上的逻辑
     *
     * @param params 启动链所需要的前置参数
     */
    public final void start(Object params) {
        getChain().start(params);
    }

    /**
     * 当事件被执行时
     */
    void perStart(P params) {
        if (getChain().isFinish()) {
            performChainComplete();
            return;
        }

        mIsStarted = true;

        mOnEventListeners.map(listener -> listener.onStart(params));
        onCall(params);
    }

    /**
     * 终止这条链，链上所有事件的链接方法将不会再执行。 链上正在执行的事件结束之后，不管后续是否还有其他事件，该链将直接完成.
     */
    public void finish() {
        getChain().finish();
    }


    /**
     * 执行事件中断的逻辑
     */
    final void performEventInterrupt() {
        onInterrupt();

        EventInterruptException throwable = new EventInterruptException();
        mOnEventListeners.map(listener -> listener.onError(throwable));
        getChain().onError(this, throwable);

        performEventComplete();
        performChainComplete();
    }

    /**
     * 事件抛出错误,发生错误的事件将不会执行后续事件，并直接完成事件所在的链
     */
    final void performEventError(Throwable throwable) {
        performEventCompleteStateChange();
        if (getChain().isInterrupt()) {
            performEventInterrupt();
        } else {
            mOnEventListeners.map(listener -> listener.onError(throwable));
            getChain().onError(this, throwable);

            performEventComplete();
            performChainComplete();
        }
    }

    /**
     * 事件完成时候
     *
     * @param result
     */
    final void performEventNext(R result) {
        performEventCompleteStateChange();
        if (getChain().isInterrupt()) {
            performEventInterrupt();
        } else {
            mOnEventListeners.map(listener -> listener.onNext(result));
            getChain().onNext(this, result);

            performEventComplete();


            getChain().next(this, result);
        }
    }

    /**
     * 更新事件完成状态
     */
    final void performEventCompleteStateChange() {
        mIsCompleted = true;
        mIsStarted = false;
    }

    /**
     * 事件的完成逻辑
     */
    final void performEventComplete() {
        onComplete();
        mOnEventListeners.map(listener -> listener.onComplete());
    }

    /**
     * 链的完成逻辑
     */
    final void performChainComplete() {
        getChain().onChainComplete();
    }

    /**
     * 判断当前事件是否已完成，对于已完成的事件来说，{@link #next(Object)}和{@link #error(Throwable)}的调用将不会生效
     *
     * @return 事件的完成状态
     */
    public boolean isComplete() {
        return mIsCompleted;
    }

    /**
     * 在 {@link #error(Throwable)} 或 {@link #next(Object)} 被调用之后，会回调该方法
     * <p>
     * 通常当该方法被调用，表示当前事件的逻辑已经完成。
     * </p>
     */
    protected void onComplete() {
//        EUtils.i("onComplete", String.format("{%s}.onComplete()", EUtils.id(this)));
    }

    /**
     * 判断当前事件是否正在执行，并且还未调用 {@link #next(Object)} 或 {@link #error(Throwable)} 方法
     *
     * @return 当前事件正在执行
     */
    public boolean isStart() {
        return mIsStarted;
    }

    /**
     * 打断正在链上正在执行的事件
     */
    public void interrupt() {
        getChain().interrupt();
    }

    /**
     * 由于一些其他原因，当前事件可能会被外部强制打断。当发生这种情况时候，我们能在这里进行监听和处理
     */
    protected void onInterrupt() {
//        EUtils.i("onInterrupt", String.format("{%s}.onInterrupt()", EUtils.id(this)));

    }


    /**
     * 当前事件错误，当前事件所在的链将会直接完成。链上的后续事件将不会被执行。
     *
     * @param throwable 事件中发生的错误
     */
    protected synchronized void error(Throwable throwable) {
        if (isComplete()) return;
        if (getChain().isFinish()) {
            performEventComplete();
            performChainComplete();
        } else {
            performEventError(throwable);
        }
    }

    /**
     * 标记当前事件完成，并向之后的事件发送一个空对象作为参数
     */
    protected void next() {
        next(null);
    }

    /**
     * 标记当前事件完成，并向之后的事件发送一个对象作为参数
     *
     * @param result 想下一个事件发送的参数
     */
    protected synchronized void next(R result) {
        if (isComplete()) return;
        if (getChain().isFinish()) {
            performEventComplete();
            performChainComplete();
        } else {
            performEventNext(result);
        }
    }


    /**
     * 事件的业务逻辑执行函数，事件的业务逻辑应该放在该方法中执行，这样才能正常的与链上的其他事件相互配合
     * <p>
     * 当事件的业务逻辑执行结束之后,需要调用事件完成状态方法 {@link #error(Throwable)} 或 {@link #next(Object)} 方法。
     * </p>
     *
     * @param params 入参数据
     */
    protected abstract void onCall(P params);


    /**
     * 添加事件监听器
     *
     * @param listener
     */
    public Event<P, R> addOnEventListener(OnEventListener<P, R> listener) {
        mOnEventListeners.register(listener);
        return this;
    }

    /**
     * 移除添加事件监听器
     *
     * @param listener
     */
    public Event<P, R> removeOnEventListener(OnEventListener listener) {
        mOnEventListeners.unregister(listener);
        return this;
    }

    /**
     * 添加链监听器
     *
     * @param listener
     */
    public Event<P, R> addOnChainListener(OnChainListener listener) {
        getChain().register(listener);
        return this;
    }

    /**
     * 移除添加链监听器
     *
     * @param listener
     */
    public Event<P, R> removeOnChainListener(OnChainListener listener) {
        getChain().unregister(listener);
        return this;
    }


}

