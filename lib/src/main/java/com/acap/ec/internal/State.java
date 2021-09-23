package com.acap.ec.internal;

/**
 * <pre>
 * Tip:
 *      事件状态
 *      一个事件通常包含(等待，激活，完成)状态
 *
 * Created by A·Cap on 2021/9/23 15:11
 * </pre>
 */
public enum State {
    /**
     * 事件处于等待执行状态，只有处于该状态下的事件才能接收激活信号
     */
    WAIT(true, false, false),
    /**
     * 事件处于激活状态，只有处于该状态下的事件才能完成事件
     */
    START(false, true, true),
    /**
     * 事件处于完成状态，已完成的事件不能被重新激活
     */
    COMPLETE(false, false, false);

    /**
     * 是否可以被激活
     */
    public boolean IS_START_ABLE;

    public boolean IS_START;

    /**
     * 是否可执行完成事件
     */
    public boolean IS_COMPLETE_ABLE;


    State(boolean IS_START_ABLE, boolean IS_COMPLETE_ABLE, boolean IS_START) {
        this.IS_START_ABLE = IS_START_ABLE;
        this.IS_COMPLETE_ABLE = IS_COMPLETE_ABLE;
        this.IS_START = IS_START;
    }
}
