package com.acap.ec;

import com.acap.ec.listener.OnChainStateChangeListener;
import com.acap.ec.utils.EUtils;

import java.util.LinkedList;

/**
 * <pre>
 * Tip:
 *      这是一条链，在链上附加的事件将会按顺序依次执行，知道链上最后一个事件完成
 *
 * Created by ACap on 2021/3/29 18:44
 * </pre>
 */
public class Chain implements OnChainStateChangeListener {

    private LinkedList<Event> mArray = new LinkedList<>();

    /**
     * 链头，一条链上只有一个链头，在链中任何事件调用开始方法都将从链头开始
     */
    private Event mFirst;

    /**
     * 链被终止的标志位，如果为true表示链被终止，链中未执行的事件将不会接收到开始信号    *
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

    public Chain(Event first) {
        this.mFirst = first;
        init();
    }

    private void init() {
        getCL().add(this);
    }

    public Event getFirst() {
        return mFirst;
    }

    /**
     * 从链头开始启动
     *
     * @param params 链头启动所需要的前置条件
     */
    public void start(Object params) {
        Event start = this.mFirst;

        if (mChainIsStarted) {
            throw new IllegalStateException(EUtils.generateThrowableMsg("不能开始一个正在运行的链!", mFirst));
        }
        mIsStop = false;
        getCL().onChainStart();
        start.perStart(params);
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
