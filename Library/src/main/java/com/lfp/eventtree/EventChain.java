package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      链式事件，具有串联事件能能力，并依次执行被串起来的事件，当所有事件执行结束后停止该链条
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:39
 * </pre>
 */
public abstract class EventChain {

    /*标记已经开始运行了*/
    private static final int FLAG_STARTED = 0x1;
    /*中断事件，整个链条都会停止下发后续事件*/
    private static final int FLAG_INTERRUPT = 0x2;
    /*中断事件，跳过后面所有事件直接完成 */
    private static final int FLAG_END = 0x4;
    /*事件执行失败，不执行后续操作*/
    private static final int FLAG_ERROR = 0x10;
    /*事件执行成功，执行后续操作*/
    private static final int FLAG_NEXT = 0x20;
    /*事件执行完成*/
    private static final int FLAG_COMPLETE = 0xF0;


    private int mFlag;

    private EventChain pre;
    private EventChain next;
    private OnEventListener mOnEventListener;
    private OnEventListenerManager mOnEventListenerManager;
    private EventChainObserverManager mChainObserverManager;/*链条观察者 ，一个链条中只允许存在一个管理器 ,在链条的顶部*/

    /**
     * 设置事件监听器
     */
    public EventChain setOnEventListener(OnEventListener l) {
        this.mOnEventListener = l;
        return this;
    }

    /**
     * 获得链条观察者管理器,用于监控链条中事件状态
     */
    protected final EventChainObserverManager getChainObserverManager() {
        EventChain first = getFirst();
        if (this == first) {
            if (mChainObserverManager == null) {
                mChainObserverManager = new EventChainObserverManager();
            }
            return mChainObserverManager;
        } else {
            return first.getChainObserverManager();
        }
    }

    /**
     * 添加链条事件观察者,具有观察整个链条的能力
     */
    public EventChain addEventChainObserver(EventChainObserver l) {
        getChainObserverManager().addEventChainObserver(l);
        return this;
    }

    /**
     * 移除链条事件观察者
     */
    public EventChain removeEventChainObserver(EventChainObserver l) {
        getChainObserverManager().removeEventChainObserver(l);
        return this;
    }

    /**
     * 添加事件监听器
     */
    public EventChain addOnEventListener(OnEventListener l) {
        if (mOnEventListenerManager == null) mOnEventListenerManager = new OnEventListenerManager();
        mOnEventListenerManager.addOnEventListener(l);
        return this;
    }

    /**
     * 移除事件监听器
     */
    public EventChain removeOnEventListener(OnEventListener l) {
        if (mOnEventListenerManager != null) mOnEventListenerManager.removeOnEventListener(l);
        return this;
    }

    /**
     * 在当前链条的末尾插入一个事件
     *
     * @param chain 插入的事件
     */
    public EventChain chain(EventChain chain) {
        if (chain == null) return this;
        final EventChain first = chain.getFirst();
        final EventChain last = chain.getLast();
        first.pre = this;
        last.next = this.next;
        if (last.next != null) {
            last.next.pre = last;
        }
        this.next = first;
        return chain;
    }

    /**
     * 在当前链条的末尾插入一组并行执行的事件
     *
     * @param chain 插入的事件
     */
    public EventChain merge(EventChain... chain) {
        if (chain == null) return this;
        return chain(new EventMerge(chain));
    }

    /**
     * 获得上一个事件
     *
     * @return 上一个事件
     */
    public final EventChain getPre() {
        return pre;
    }

    /**
     * 获得下一个事件
     *
     * @return 下一个事件
     */
    public final EventChain getNext() {
        return next;
    }

    /**
     * 获得当前链条的头，注意EventMerge会生成独立的链条
     */
    public final EventChain getFirst() {
        EventChain first = this;
        while (first.pre != null) {
            first = first.pre;
        }
        return first;
    }

    /**
     * 获得当前链条的尾，注意EventMerge会生成独立的链条
     */
    public final EventChain getLast() {
        EventChain last = this;
        while (last.next != null) {
            last = last.next;
        }
        return last;
    }

    /**
     * 开始这个事件链
     */
    public final void start() {
        if ((getFirst().mFlag & FLAG_STARTED) != 0) {
            throw new IllegalStateException("The event is started,");
        }

        EventChain event = getFirst();
        if (this == event) {
            run();
        } else {
            event.start();
        }
    }

    /*真正的开始逻辑*/
    protected void run() {
        if (isComplete()) {
            throw new IllegalStateException("The event is complete!");
        }
        mFlag |= FLAG_STARTED;
        if (mOnEventListenerManager != null) mOnEventListenerManager.onStart();
        if (mOnEventListener != null) mOnEventListener.onStart();
        if (this == getFirst()) {
            getChainObserverManager().onChainStart();
        }
        getChainObserverManager().onStart(this);
        try {
            call();
        } catch (Throwable e) {
            e.printStackTrace();
            error(e);
        }
    }

    /**
     * 执行事件自己的业务逻辑,并且处理事件生命周期
     */
    protected abstract void call() throws Throwable;

    /**
     * 当前事件执行结束并且未发生错误，执行后续事件或者完成该事件链
     */
    protected void next() {
        if (isComplete()) {
            throw new IllegalStateException("The event can't execute command. Because it is complete!");
        }
        onNext();
    }

    /**
     * 当前事件执行结束，当是发生了错误结束该事件链
     */
    protected void error(Throwable e) {
        if (isComplete()) {
            throw new IllegalStateException("The event can't execute command. Because it is complete!");
        }
        onError(e);
    }

    /**
     * 跳过后续事件，直接完成事件链
     */
    public final void complete() {
        getFirst().onEnd();
    }

    /**
     * 中断整个事件链，调用该方法之后后续所有事件和回调将停止
     */
    public final void interrupt() {
        getFirst().onInterrup();
    }

    /**
     * 当该事件完成，检查时候有后续事件，并执行之后的事件
     */
    protected void onEnd() {
        mFlag |= FLAG_END;
        if (next != null) {
            next.onEnd();
        }
    }

    /**
     * 当该事件被终止，将立即停止当前时候的后续操作和回调
     */
    protected void onInterrup() {
        mFlag |= FLAG_INTERRUPT;
        if (next != null) {
            next.onInterrup();
        }
    }

    /**
     * 判断该事件是否已开始执行
     */
    public final boolean isStarted() {
        return (mFlag & FLAG_STARTED) > 0;
    }

    /**
     * 判断该事件是否被中断
     */
    public boolean isInterrupt() {
        return (mFlag & FLAG_INTERRUPT) > 0;
    }

    /**
     * 判断该事件是否被已完成
     */
    public boolean isComplete() {
        return (mFlag & FLAG_COMPLETE) != 0;
    }

    /*中断但是允许完成*/
    private boolean isEnd() {
        return (mFlag & FLAG_END) > 0;
    }

    /*执行后续事件*/
    private final void onNext() {
        if (isInterrupt()) return;

        mFlag |= FLAG_NEXT;
        if (!isComplete()) {
            throw new IllegalStateException("The event is not complete!");
        }
        if (mOnEventListenerManager != null) mOnEventListenerManager.onNext();
        if (mOnEventListener != null) mOnEventListener.onNext();
        if (mOnEventListenerManager != null) mOnEventListenerManager.onComplete();
        if (mOnEventListener != null) mOnEventListener.onComplete();
        getChainObserverManager().onNext(this);


        if (next != null && !isEnd()) {
            next.run();
        } else {
            getChainObserverManager().onChainComplete();
        }
    }

    /*回调错误*/
    private final void onError(Throwable e) {
        if (isInterrupt()) return;

        mFlag |= FLAG_ERROR;
        if (mOnEventListenerManager != null) mOnEventListenerManager.onError(e);
        if (mOnEventListener != null) mOnEventListener.onError(e);
        getChainObserverManager().onError(this, e);

        if (mOnEventListenerManager != null) mOnEventListenerManager.onComplete();
        if (mOnEventListener != null) mOnEventListener.onComplete();
        getChainObserverManager().onChainComplete();
    }


    /**
     * 用于创建一个并发事件或者包裹一个事件链
     *
     * @param chain 事件或者事件链
     * @return 并发事件
     */
    public static final EventChain create(EventChain... chain) {
        return new EventMerge(chain);
    }


}
