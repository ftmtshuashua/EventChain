package com.acap.ec;

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

}
