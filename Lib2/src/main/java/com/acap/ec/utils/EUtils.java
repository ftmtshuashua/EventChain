package com.acap.ec.utils;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/3/31 11:34
 * </pre>
 */
public class EUtils {
    public static final String TAG = "Event";


    /**
     * 获取对象的唯一ID
     *
     * @param object
     * @return
     */
    public static final String id(Object object) {
        if (object == null) return "Null_Obj";
        return object.getClass().getSimpleName() + "(" + Integer.toHexString(System.identityHashCode(object)) + ")";
    }

    /**
     * 字符串判空
     *
     * @param str
     * @return
     */
    public static final boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 控制台日志输出
     *
     * @param msg
     */
    public static void i(String msg) {
        i(null, msg);
    }

    /**
     * 控制台日志
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (isEmpty(tag)) {
            System.out.println(String.format("%s:%s", TAG, msg));
        } else {
            System.out.println(String.format("%s-%s:%s", TAG, tag, msg));
        }
    }

    public static void e(String msg) {
        e(null, msg);
    }

    /**
     * 控制台日志
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (isEmpty(tag)) {
            System.err.println(String.format("%s:%s", TAG, msg));
        } else {
            System.err.println(String.format("%s-%s:%s", TAG, tag, msg));
        }
    }

}
