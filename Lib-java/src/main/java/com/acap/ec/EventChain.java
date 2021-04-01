package com.acap.ec;

import com.acap.ec.action.Apply;
import com.acap.ec.listener.OnCallBeforeListener;
import com.acap.ec.listener.OnCallLaterListener;
import com.acap.ec.listener.OnChainListener;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      事件链中的一个事件节点
 *
 * @param <P> params,上游节点完成之后的产物
 * @param <R> result,当前节点的产物
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public abstract class EventChain<P, R> {

    /**
     * 代表下一个节点
     */
    private EventChain<R, ?> mNext;
    private P mParams;

    public EventChain<R, ?> getNext() {
        return mNext;
    }

    /**
     * 停止链上的事件传递
     */
    public void stop() {
        getChain().stop();
    }

    /**
     * 如果当前节点发生错误,则通过error方法向后传递
     *
     * @param throwable 节点中发生的错误
     */
    protected void error(Throwable throwable) {
        if (getChain().isStop()) {
            System.out.println("链上发生了错误，但是链被终止了!");
            throwable.printStackTrace();
            return;
        }

        throwable = Utils.generateThrowable(throwable, this);

        getEL().onError(throwable);
        getChain().getCL().onError(this, throwable);

        getEL().onComplete();
        getChain().getCL().onChainComplete();
    }

    /**
     * 节点产物信号，该信号不会通知下一节点开始执行
     *
     * @param result 节点的产物
     */
    protected void result(R result) {
        if (getChain().isStop()) return;
        getEL().onNext(result);
        getChain().getCL().onNext(this, result);
    }

    /**
     * 当前节点的逻辑处理完成之后,将节点的产物向后传递，交给下一节点来处理
     *
     * @param result 当前节点的产物
     */
    protected void next(R result) {
        if (getChain().isStop()) return;
        getEL().onNext(result);
        getChain().getCL().onNext(this, result);

        getEL().onComplete();
        if (mNext != null) {
            mNext.perStart(result);
        } else {
            getChain().getCL().onChainComplete();
        }
    }

    /**
     * 当前节点被执行的时候回调，节点自己的逻辑在该方法中开始执行
     *
     * @param params 来自上游节点的产物
     */
    protected abstract void onCall(P params);

    /**
     * 从链头开始执行链上的逻辑
     *
     * @param params 启动链所需要的前置参数
     */
    public void start(Object params) {
        getChain().start(params);
    }

    /**
     * 从链头开始执行链上的逻辑
     */
    public void start() {
        start(null);
    }

    /**
     * 当事件被执行时
     */
    void perStart(P params) {
        mParams = params;

        getEL().onStart();
        getBL().onBefore(this);
        onCall(params);
        getLL().onLater(this);
    }

    public P getParams() {
        return mParams;
    }

    /**
     * 在链中插入一个节点，为了保证后续事件不会出错所以需要保证,事件的产物和当前事件相同
     */
    public EventChain<R, R> insert(EventChain<R, R> node) {
        node.setChain(getChain());
        if (mNext != null) node.chain(mNext);

        mNext = node;
        return node;
    }

    /**
     * 在节点后面连接另一个节点。
     * 注意：如果当前节点处于链中，则该方法会导致之前的节点丢失，如果希望在链中安全的插入节点请使用{@link EventChain#insert(EventChain)}
     *
     * @param node 节点对象
     * @return 返回的是下一个节点
     */
    public <Result, T extends EventChain<R, Result>> T chain(T node) {
        node.setChain(getChain());
        mNext = node;
        return node;
    }

    /**
     * 并行操作符，表示之后的节点为并行节点
     *
     * @param nodes 节点集合
     * @return
     */
    public <Result> EventFork<R, Result> fork(EventChain<? super R, ? extends Result>... nodes) {
        return (EventFork<R, Result>) chain(new EventFork(nodes));
    }

    /**
     * <pre>
     * 数据修剪
     * 1.数据类型转换
     * 2.数据过滤
     * 3.数据合并
     * ...
     * </pre>
     *
     * @param apply    修剪算法
     * @param <Result>
     * @return
     */
    public <Result> ApplyEvent<R, Result> clip(Apply<R, Result> apply) {
        return chain(new ApplyEvent<>(apply));
    }

    /**
     * 打印结果
     *
     * @param tag 日志的Tag
     * @return 返回数据本身
     */
    public ApplyEvent<R, R> print(String tag) {
        return chain(new ApplyEvent(params -> {
            if (params == null) {
                Utils.i(tag, "null");
            } else {
                Utils.i(tag, params.toString());
            }
            return params;
        }));
    }

    /**
     * 快速创建
     *
     * @param nodes
     * @param <P>
     * @return
     */
    public static final <P, R> EventFork<P, R> create(EventChain<? super P, ? extends R>... nodes) {
        return new EventFork(nodes);
    }

    /**
     * 快捷的发送信号
     *
     * @param r1
     * @param <Result>
     * @return
     */
    public static final <Result> ApplyEvent<Object, Result> just(Result r1) {
        return new ApplyEvent<>(params -> r1);
    }


    private InsideChain mChain;

    protected InsideChain getChain() {
        if (mChain == null) {
            mChain = new InsideChain(this);
        }
        return mChain;
    }

    /**
     * 设置链信息，链上储存有整条链的信息
     *
     * @param chain
     */
    void setChain(InsideChain chain) {
        mChain = chain;
        if (mNext != null) {
            mNext.setChain(chain);
        }
    }

    private Listeners_EL mEL;
    private Listeners_BL mBL;
    private Listeners_LL mLL;

    private Listeners_EL getEL() {
        if (mEL == null) mEL = new Listeners_EL();
        return mEL;
    }

    private Listeners_BL getBL() {
        if (mBL == null) mBL = new Listeners_BL();
        return mBL;
    }

    private Listeners_LL getLL() {
        if (mLL == null) mLL = new Listeners_LL();
        return mLL;
    }

    public <T extends EventChain<P, R>> T addOnEventListener(OnEventListener<R> listener) {
        getEL().add(listener);
        return (T) this;
    }

    public EventChain<P, R> removeOnEventListener(OnEventListener<R> listener) {
        getEL().remove(listener);
        return this;
    }

    public EventChain<P, R> addOnChainListener(OnChainListener<R> listener) {
        getChain().getCL().add(listener);
        return this;
    }

    public EventChain<P, R> removeOnChainListener(OnChainListener<R> listener) {
        getChain().getCL().remove(listener);
        return this;
    }

    public EventChain<P, R> addOnCallBeforeListener(OnCallBeforeListener<P, R> listener) {
        getBL().add(listener);
        return this;
    }

    public EventChain<P, R> removeOnCallBeforeListener(OnCallBeforeListener<P, R> listener) {
        getBL().remove(listener);
        return this;
    }

    public EventChain<P, R> addOnCallLaterListener(OnCallLaterListener<P, R> listener) {
        getLL().add(listener);
        return this;
    }

    public EventChain<P, R> removeOnCallLaterListener(OnCallLaterListener<P, R> listener) {
        getLL().remove(listener);
        return this;
    }


    public static final void print(EventChain node) {
        Utils.print(node);
    }

}

