package com.acap.ec.utils;

import com.acap.ec.Event;
import com.acap.ec.MergeEvent;

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
public class EUtils {
    public static final String TAG = "EventChain";

    /**
     * 获取对象的唯一ID
     *
     * @param object
     * @return
     */
    public static final String getObjectId(Object object) {
        if (object == null) return "Null_Obj";
        return object.getClass().getSimpleName() + "(" + Integer.toHexString(System.identityHashCode(object)) + ")";
    }

    /**
     * 控制台日志
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
//        System.out.println(TAG + "->" + tag + ":" + msg);
        System.out.println(String.format("%s-%s:%s", TAG, tag, msg));
    }

    /**
     * 控制台日志
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        System.err.println(TAG + "->" + tag + ":" + msg);
    }


    /***
     * 打印链结构
     *
     * @param event
     */
    public static void print(Event event) {
        printNext(event.getChain().getFirst());
    }

    /***
     * 打印链结构
     *
     * @param event
     */
    private static void printNext(Event event) {
        if (event == null) return;
        if (event instanceof MergeEvent) {
            List<Event> events = ((MergeEvent) event).getEvents();
            for (Event ev : events) {
                printNext(ev);
            }
        } else {
//            i("Print", event.getNext());
        }
        print(event.getNext());
    }


    /**
     * 异常信息内容
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
