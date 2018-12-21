package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
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
    private OnEventListener listener;

    private ChainObserverManager mChainObserverManager;/*链条观察者 ，一个链条中只允许存在一个管理器 ,在链条的顶部*/

    public EventChain setOnEventListener(OnEventListener l) {
        this.listener = l;
        return this;
    }

    protected final ChainObserverManager getChainObserverManager() {
        EventChain first = getFirst();
        if (this == first) {
            if (mChainObserverManager == null) {
                mChainObserverManager = new ChainObserverManager();
            }
            return mChainObserverManager;
        } else {
            return first.getChainObserverManager();
        }
    }

    public void addEventChainObserver(EventChainObserver l) {
        getChainObserverManager().addEventChainObserver(l);
    }

    public void removeEventChainObserver(EventChainObserver l) {
        getChainObserverManager().removeEventChainObserver(l);
    }

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

    public EventChain merge(EventChain... chain) {
        if (chain == null) return this;
        return chain(new EventMerge(chain));
    }

    /*获得当前链条的头，注意EventMerge会生成独立的链条*/
    public final EventChain getFirst() {
        EventChain first = this;
        while (first.pre != null) {
            first = first.pre;
        }
        return first;
    }

    /*获得当前链条的尾，注意EventMerge会生成独立的链条*/
    public final EventChain getLast() {
        EventChain last = this;
        while (last.next != null) {
            last = last.next;
        }
        return last;
    }

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

    protected void run() {
        if (isComplete()) {
            throw new IllegalStateException("The event is complete!");
        }
        mFlag |= FLAG_STARTED;
        if (listener != null) {
            listener.onStart();
        }
        if (this == getFirst()) {
            getChainObserverManager().onChainStart();
        }
        getChainObserverManager().onStart(this);
        try {
            call();
        } catch (Throwable e) {
            error(e);
        }
    }

    protected abstract void call() throws Throwable;

    protected void next() {
        if (isComplete()) {
            throw new IllegalStateException("The event can't execute command. Because it is complete!");
        }
        onNext();
    }

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

    protected void onEnd() {
        mFlag |= FLAG_END;
        if (next != null) {
            next.onEnd();
        }
    }

    /**
     * 中断整个事件链，调用该方法之后后续事件将停止
     */
    public final void interrupt() {
        getFirst().onInterrup();
    }


    protected void onInterrup() {
        mFlag |= FLAG_INTERRUPT;
        if (next != null) {
            next.onInterrup();
        }
    }

    public boolean isInterrupt() {
        return (mFlag & FLAG_INTERRUPT) > 0;
    }

    public boolean isComplete() {
        return (mFlag & FLAG_COMPLETE) != 0;
    }

    /*中断但是允许完成*/
    private boolean isInterruptAllowComplete() {
        return (mFlag & FLAG_END) > 0;
    }

    private final void onNext() {
        if (isInterrupt()) return;

        mFlag |= FLAG_NEXT;
        if (!isComplete()) {
            throw new IllegalStateException("The event is not complete!");
        }
        if (listener != null) {
            listener.onNext();
            listener.onComplete();
        }
        getChainObserverManager().onNext(this);


        if (next != null && !isInterruptAllowComplete()) {
            next.run();
        } else {
            getChainObserverManager().onChainComplete();
        }
    }

    private final void onError(Throwable e) {
        if (isInterrupt()) return;

        mFlag |= FLAG_ERROR;
        if (listener != null) {
            listener.onError(e);
            listener.onComplete();
        }
        getChainObserverManager().onError(this, e);
        getChainObserverManager().onChainComplete();
    }


    /**
     * 创建一个链条事件
     *
     * @param chain
     * @return
     */
    public static final EventChain create(EventChain... chain) {
        return new EventMerge(chain);
    }
}
