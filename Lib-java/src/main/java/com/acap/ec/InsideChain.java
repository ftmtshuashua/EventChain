package com.acap.ec;

import com.acap.ec.listener.OnChainStateChangeListener;

import java.util.LinkedList;

/**
 * <pre>
 * Tip:
 *      链的信息储存器，保存了链的完整信息
 *
 * Created by ACap on 2021/3/29 18:44
 * </pre>
 */
class InsideChain implements OnChainStateChangeListener {

    private LinkedList<EventChain> mArray = new LinkedList<>();

    /**
     * 链头，一条链上只有一个链头，在链中任何节点调用开始方法都将从链头开始
     */
    private EventChain mFirst;

    /**
     * 链被终止的标志位，如果为true表示链被终止，链中未执行的节点将不会接收到开始信号    *
     */
    private boolean mIsStop = false; //链被终止，标志位

    /**
     * 链上所有事件的执行监听器
     */
    private Listeners_CL mCL;

    public Listeners_CL getCL() {
        if (mCL == null) mCL = new Listeners_CL();
        return mCL;
    }

    public InsideChain(EventChain first) {
        this.mFirst = first;
        init();
    }

    private void init() {
        getCL().add(this);
    }

    /**
     * 从链头开始启动
     *
     * @param params 链头启动所需要的前置条件
     */
    public void start(Object params) {
        if (mChainIsStarted) throw new IllegalStateException("准备执行的链正在运行...");
        mIsStop = false;
        getCL().onChainStart();
        mFirst.perStart(params);
    }

    /**
     * 判断链是否被终止
     */
    public boolean isStop() {
        return mIsStop;
    }

    /**
     * 终止链上所有的后续行为
     */
    public void stop() {
        mIsStop = true;
    }

    //链的状态标志
    private boolean mChainIsStarted = false;

    @Override
    public void onChange(boolean isStarted) {
        mChainIsStarted = isStarted;
    }
}
