package com.lfp.eventchain.excption;

/**
 * <pre>
 * Tip:
 *      事件链错误
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/21 16:23
 * </pre>
 */
public class EventException extends RuntimeException {
    public EventException(String msg) {
        super(msg);
    }
}
