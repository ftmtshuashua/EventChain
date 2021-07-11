package com.acap.ec.internal;

/**
 * <pre>
 * Tip:
 *     事件的状态,任何事件都有它自身的状态.
 *     只有当事件的状态处于{@link #READY}时候,才允许执行事件,否则事件将不会被执行
 *
 *
 * Created by ACap on 2021/7/8 22:10
 * </pre>
 * @author A·Cap
 */
public enum EventState {
    /**
     * 事件准备就绪，这意味着事件随时可以被执行，并且只有当事件处于该状态时才能被执行
     */
    READY,

    /**
     * 事件已经开始执行，这意味着事件正在执行它的逻辑。
     * 这时除非事件状态变为{@link #COMPLETE}，否则该事件无法再次执行,直到事件的状态变为{@link #COMPLETE}
     */
    START,

    /**
     * 事件已经执行完成,这意味着事件再次启动时可以进入{@link #READY}状态,并且开始执行事件的逻辑
     */
    COMPLETE

}
