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

    private EventChain pre; /*前一个*/
    private EventChain next; /*后一个*/


    /*设置事件链中的事件状态*/
    private void setLaterChainState(ChainState state) {
        EventChain event = this;
        while (event != null) {
            event.mState = state;
            event = event.next;
        }
    }

    /**
     * 在当前链条的末尾插入一个事件
     *
     * @param chain 插入的事件
     */
    public EventChain chain(EventChain chain) {
        if (chain == null) return this;
        checkLoop(chain);

        final EventChain first = chain.getFirst();
        final EventChain last = chain.getLast();
        first.pre = this;
        last.next = this.next;
        if (last.next != null) {
            last.next.pre = last;
        }
        this.next = first;


        chain.setLaterChainState(mState); /*配置链状态*/
        return chain;
    }

    /*检查循环,死循环导致假死*/
    private void checkLoop(EventChain chain) {
        final EventChain last = chain.getLast();
        EventChain tag = getFirst();
        do {
            if (tag == last) { /* Loop */
                throw new RuntimeException("插入链的时候出现死循环!");
            }
            tag = tag.next;
        } while (tag != null);
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
     * 延迟事件的创建，当上一个事件执行结束之后再创建当前请求
     *
     * @param eventdelay 事件的创建过程
     */
    public EventChain chainDelay(EventDelayCreate.OnEventDelayCreate eventdelay) {
        if (eventdelay == null) return this;
        return chain(new EventDelayCreate(eventdelay));
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

    // </editor-fold>

    boolean mEventend = false;

    /**
     * 有实现自己重写业务逻辑，并处理事件的生命周期
     */
    protected abstract void call() throws Throwable;

    /**
     * 从链头开始执行事件链
     */
    public void start() {
        final EventChain first = getFirst();
        if (this == first) { /*如果当前为链头，开始执行*/
            if (isStarted()) throw new IllegalStateException("The event is started,");
            mState.isStarted = true;
            mState.mCurrentRunEvent = this;
            if (isProcess()) exeStartChain();
            run();
        } else {
            first.start();
        }
    }

    /*执行事件链内部流程*/
    private void run() {
        mState.mCurrentRunEvent = this;
        if (isProcess()) onStart();
        if (isProcess()) exeStartEvent();
        if (isProcess()) exeStartChainEvent();

        if (isProcess()) {
            try {
                call();
            } catch (Throwable e) {
                error(e);
            }
        }
    }


    /**
     * 当前事件执行结束并且未发生错误，执行后续事件或者完成该事件链
     */
    protected void next() {
        if (mEventend) return;
        mEventend = true;

        if (isProcess()) exeNextEvent();
        if (isProcess()) exeNextChain();
        if (isProcess()) exeCompleteEvent();

        if (isProcess()) {
            /*如果还有后续事件，则执行后续事件 ， 否者完成这条事件链*/
            if (next != null) {
                next.run();
            } else {
                getChainObserverGroup().onChainComplete();
            }
        }
    }

    /**
     * 当前事件执行结束，当是发生了错误结束该事件链
     */
    protected void error(Throwable e) {
        if (mEventend) return;
        mEventend = true;

        if (isProcess()) exeErrorEvent(e);
        if (isProcess()) exeErrorChain(e);
        if (isProcess()) exeCompleteEvent();
        if (isProcess()) exeCompleteChain();
    }

    /**
     * 当事件开始执的时候调用
     */
    protected void onStart() {

    }

    // </editor-fold>


    /**
     * 强制跳过后续事件，直接完成事件链
     */
    public void complete() {
        /*已中断和已完成的事件*/
        if (isStarted() && isProcess()) {
            mState.isComplete = true;
            if (!isInterrupt()) exeCompleteEvent(mState.mCurrentRunEvent);
            if (!isInterrupt()) exeCompleteChain(mState.mCurrentRunEvent);
        }
        if (next != null) next.complete();
    }

    /**
     * 中断整个事件链，调用该方法之后后续所有事件和回调将停止
     */
    public void interrupt() {
        mState.isInterrupt = true;
        if (next != null) next.interrupt();
    }

    /**
     * 判断该事件是否被中断
     */
    public boolean isInterrupt() {
        return mState.isInterrupt();
    }

    /**
     * 判断是否强制完成整个事件链
     */
    public boolean isComplete() {
        return mState.isComplete();
    }

    /**
     * 判断该事件是否已开始执行
     */
    public final boolean isStarted() {
        return mState.isStarted();
    }


    /*判断时候正常进行后续流程 , 如果事件链为Interrupt或者Complete的情况中断后续业务*/
    public final boolean isProcess() {
        return !isInterrupt() && !isComplete();
    }

    /**
     * 每一个链条都有且之后一个独立的链条状态,当后续事件接入某个链的后面的时候，他的状态会被替换为链头的状态对象
     */
    private ChainState mState = new ChainState();

    /**
     * 事件链的状态
     */
    private static final class ChainState {
        /**
         * 当事件开始之后该值变为true
         */
        boolean isStarted = false;
        boolean isInterrupt = false;
        boolean isComplete = false;
        EventChain mCurrentRunEvent; /*当前正在运行的事件 */

        /**
         * 判断该事件是否被中断
         */
        public boolean isStarted() {
            return isStarted;
        }

        /**
         * 判断该事件是否被中断
         */
        public boolean isInterrupt() {
            return isInterrupt;
        }

        /**
         * 中断但是允许完成
         */
        public boolean isComplete() {
            return isComplete;
        }
    }
    // </editor-fold>

    private EventListenerGroup mOnEventListenerGroup;
    /*链条观察者 ，一个链条中只允许存在一个管理器 ,在链条的顶部*/
    private EventChainObserverGroup mChainObserverGroup;


    /**
     * 获得链条观察者管理器,用于监控链条中事件状态
     */
    protected final EventChainObserverGroup getChainObserverGroup() {
        EventChain first = getFirst();
        if (this == first) {
            if (mChainObserverGroup == null) {
                mChainObserverGroup = new EventChainObserverGroup();
            }
            return mChainObserverGroup;
        } else {
            return first.getChainObserverGroup();
        }
    }

    /**
     * 添加链条事件观察者,具有观察整个链条的能力
     */
    public EventChain addEventChainObserver(EventChainObserver l) {
        getChainObserverGroup().addEventChainObserver(l);
        return this;
    }

    /**
     * 移除链条事件观察者
     */
    public EventChain removeEventChainObserver(EventChainObserver l) {
        getChainObserverGroup().removeEventChainObserver(l);
        return this;
    }

    /**
     * 设置事件监听，监听事件进度
     * <p>
     * 请使用 {@link EventChain#addOnEventListener(OnEventListener)} ，该方法可能会在未来的某个版本中被删除
     */
    @Deprecated
    public EventChain setOnEventListener(OnEventListener l) {
        addOnEventListener(l);
        return this;
    }

    /**
     * 添加事件监听器
     */
    public EventChain addOnEventListener(OnEventListener l) {
        if (mOnEventListenerGroup == null) mOnEventListenerGroup = new EventListenerGroup();
        mOnEventListenerGroup.addOnEventListener(l);
        return this;
    }

    /**
     * 移除事件监听器
     */
    public EventChain removeOnEventListener(OnEventListener l) {
        if (mOnEventListenerGroup != null) mOnEventListenerGroup.removeOnEventListener(l);
        return this;
    }


    /*当整个链条开始执行的时候*/
    private void exeStartChain() {
        getChainObserverGroup().onChainStart();
    }

    /*当链条中的事件开始执行*/
    private void exeStartChainEvent() {
        getChainObserverGroup().onStart(this);
    }

    private void exeStartEvent() {
        if (mOnEventListenerGroup != null) mOnEventListenerGroup.onStart();
    }

    protected void exeCompleteChain() {
        exeCompleteChain(this);
    }

    private static void exeCompleteChain(EventChain event) {
        event.getChainObserverGroup().onChainComplete();
    }

    /*事件完成*/
    boolean isCompleteEvent = false;

    protected void exeCompleteEvent() {
        exeCompleteEvent(this);
    }

    private static void exeCompleteEvent(EventChain eventChain) {
        if (eventChain.isCompleteEvent) return;
        eventChain.isCompleteEvent = true;
        if (eventChain.mOnEventListenerGroup != null)
            eventChain.mOnEventListenerGroup.onComplete();
    }

    protected void exeNextChain() {
        getChainObserverGroup().onNext(this);
    }

    protected void exeNextEvent() {
        if (mOnEventListenerGroup != null) mOnEventListenerGroup.onNext();
    }

    protected void exeErrorChain(Throwable e) {
        getChainObserverGroup().onError(this, e);
    }

    protected void exeErrorEvent(Throwable e) {
        if (mOnEventListenerGroup != null) mOnEventListenerGroup.onError(e);
    }


    // </editor-fold>
}
