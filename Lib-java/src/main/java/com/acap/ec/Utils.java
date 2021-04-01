package com.acap.ec;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/3/31 11:34
 * </pre>
 */
class Utils {
    public static final String TAG = "EC";

    public static final String getObjectId(Object object) {
        if (object == null) return "Null_Obj";
        return object.getClass().getSimpleName() + "(" + Integer.toHexString(System.identityHashCode(object)) + ")";
    }

    public static void i(String tag, String msg) {
        System.out.println(TAG + "->" + tag + ":" + msg);
    }

    public static void e(String tag, String msg) {
        System.err.println(TAG + "->" + tag + ":" + msg);
    }

    public static void print(EventChain node) {
        printNext(node.getChain().getFirst());
    }

    private static void printNext(EventChain node) {
        if (node == null) return;
        if (node instanceof EventFork) {
            List<EventChain> forkNode = ((EventFork) node).getForkNode();
            for (EventChain event : forkNode) {
                printNext(event);
            }
        } else {
//            i("Print", node.getNext());
        }
        print(node.getNext());
    }


    /**
     * 异常信息调整
     *
     * @param throwable 错误信息
     * @param generator 生产者
     */
    public static final Throwable generateThrowable(Throwable throwable, Object generator) {
        try {

            Constructor<? extends Throwable> constructor = throwable.getClass().getConstructor(String.class, Throwable.class);
            Throwable error = constructor.newInstance(generateThrowableMsg(throwable.getMessage(), generator), throwable);

            return error;
        } catch (Exception e) {
            return throwable;
        }
    }

    public static final String generateThrowableMsg(String msg, Object generator) {
        String splice = " _at_ ";
        if (msg.contains(splice)) return msg;
        return msg + splice + getObjectId(generator);
    }

}
