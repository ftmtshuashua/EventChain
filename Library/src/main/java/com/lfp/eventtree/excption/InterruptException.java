package com.lfp.eventtree.excption;

/**
 * <pre>
 * Tip:
 *      收到该异常表示某条事件链被终止了，后续事件将不会进行
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 21:38
 * </pre>
 */
public class InterruptException extends RuntimeException {
    public InterruptException() {
        super("The chain is interrupt!");
    }
}
