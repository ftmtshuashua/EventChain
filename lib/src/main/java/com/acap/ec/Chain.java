package com.acap.ec;

import com.acap.ec.listener.OnChainListener;
import com.acap.ec.utils.ListenerMap;

/**
 * <pre>
 * Tip:
 *      这是一条链，在链上附加的事件将会按顺序依次执行，知道链上最后一个事件完成
 *
 * Created by ACap on 2021/3/29 18:44
 * </pre>
 */
public class Chain<P, R> implements OnChainListener {

    /**
     * 链头，一条链上只有一个链头，在链中任何事件调用开始方法都将从链头开始
     */
    private Event mFirst;

    /**
     * 链被终止的标志位.如果它为{@code true},那么链中所有的事件之间的消息传递将被阻止
     */
    private boolean mIsFinish = false; //链被终止，标志位

    /**
     * 当前链是否被打断的状态
     */
    private boolean mIsInterrupt = false;

    /**
     * 链的状态监听
     */
    private final ChainStateListener mChainStateListener = new ChainStateListener();


    /**
     * 链的监听器集合
     */
    private final ListenerMap<OnChainListener> mOnChainListeners = new ListenerMap<>();

    /**
     * 创建链对象，并传入头事件
     *
     * @param first 链上第一个事件实例
     */
    public Chain(Event first) {
        this.mFirst = first;
        mOnChainListeners.register(mChainStateListener);
    }


    /**
     * 获得链上第一个事件的实例
     */
    public Event getFirst() {
        return mFirst;
    }

    /**
     * 从链头开始启动
     *
     * @param params 链头启动所需要的前置条件
     */
    public void start(Object params) {
        if (isFinish() || isRunning()) return;

        mIsInterrupt = false;
        mFirst.performChainPrepareStart();

        performFirstEventStart(params);
    }

    /**
     * 从链头开始执行事件
     *
     * @param params 执行链头传入的参数
     */
    void performFirstEventStart(Object params) {
        mOnChainListeners.map(onChainListener -> onChainListener.onChainStart());
        mFirst.perStart(params);
    }


    //执行下一个事件
    void next(Event event, Object result) {
        if (event.mNext != null) {
            event.mNext.performChainPrepareStart();
            event.mNext.perStart(result);
        } else {
            event.performChainComplete();
        }
    }


    /**
     * 判断链是否被终止
     */
    public boolean isFinish() {
        return mIsFinish;
    }

    /**
     * 终止链上所有的后续行为
     */
    public void finish() {
        mIsFinish = true;
    }

    public boolean isRunning() {
        return mChainStateListener.mIsRunning;
    }

    /**
     * 打断正在链上正在执行的事件
     */
    public void interrupt() {
        mIsInterrupt = true;
    }

    /**
     * 判断当前链是否被打断,被打断的链上的事件将无法进行事件传递。它将完成当前链并抛出一个{@link com.acap.ec.excption.EventInterruptException}异常
     *
     * @return 是否已打断
     */
    public boolean isInterrupt() {
        return mIsInterrupt;
    }


    /**
     * 判断链是否出错了
     */
    public boolean isError() {
        return mChainStateListener.mError != null;
    }

    /**
     * 获得链上发生的错误，如果当前链没有发生错误，它将为Null
     *
     * @return 链上发生的错误
     */
    public Throwable getError() {
        return mChainStateListener.mError;
    }


    /**
     * 获得链上发生错误事件实例，如果当前链没有发生错误，它将为Null
     *
     * @return 链上发生的错误
     */
    public Event getErrorEvent() {
        return mChainStateListener.mErrorEvent;
    }


    /**
     * 注册监听器
     *
     * @param t 监听器对象
     */
    public void register(OnChainListener t) {
        mOnChainListeners.register(t);
    }

    /**
     * 移除监听器
     *
     * @param t 监听器对象
     * @return 是否移除成功
     */
    public boolean unregister(OnChainListener t) {
        return mOnChainListeners.unregister(t);
    }

    @Override
    public void onChainStart() {
        mOnChainListeners.map(onChainListener -> onChainListener.onChainStart());
    }

    /**
     * 当事件链中某个事件开始执行
     *
     * @param event 执行的事件
     */
    public void onStart(Event event) {
        mOnChainListeners.map(onChainListener -> onChainListener.onStart(event));
    }

    /**
     * 当事件链中某个事件执行错误
     *
     * @param event     执行的事件
     * @param throwable 事件抛出的异常信息
     */
    public void onError(Event event, Throwable throwable) {
        mOnChainListeners.map(onChainListener -> onChainListener.onError(event, throwable));
    }

    @Override
    public void onNext(Event event, Object result) {
        mOnChainListeners.map(onChainListener -> onChainListener.onNext(event, result));

    }

    @Override
    public void onChainComplete() {
        mOnChainListeners.map(onChainListener -> onChainListener.onChainComplete());
    }

    /**
     * 链的状态监听
     */
    private static final class ChainStateListener implements OnChainListener {

        /**
         * 链上发生错误的事件实例
         */
        private Event mErrorEvent;
        /**
         * 链上发生的错误信息
         */
        private Throwable mError;


        /**
         * 链是否正在运行
         */
        private boolean mIsRunning = false;


        @Override
        public void onChainStart() {
            mIsRunning = true;
        }

        @Override
        public void onStart(Event event) {

        }

        @Override
        public void onError(Event event, Throwable throwable) {
            mErrorEvent = event;
            mError = throwable;
        }

        @Override
        public void onNext(Event event, Object result) {

        }

        @Override
        public void onChainComplete() {
            mIsRunning = false;
        }


        /**
         * 获得 链上发生错误的事件实例
         */
        public Event getErrorEvent() {
            return mErrorEvent;
        }

        /**
         * 获得 链上发生的错误信息
         */
        public Throwable getError() {
            return mError;
        }
    }

}
