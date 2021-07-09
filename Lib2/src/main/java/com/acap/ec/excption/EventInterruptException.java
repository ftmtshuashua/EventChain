package com.acap.ec.excption;

/**
 * <pre>
 * Tip:
 *      事件打断异常,通常发生在事件被异常终止时
 *
 * Created by ACap on 2021/7/4 0:07
 * </pre>
 */
public class EventInterruptException extends RuntimeException {
    public EventInterruptException() {
    }

    public EventInterruptException(String message) {
        super(message);
    }

    public EventInterruptException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventInterruptException(Throwable cause) {
        super(cause);
    }

    public EventInterruptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
