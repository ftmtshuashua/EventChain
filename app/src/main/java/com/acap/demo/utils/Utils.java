package com.acap.demo.utils;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/5 23:07
 * </pre>
 */
public class Utils {

    /**
     * 获得随机数 [0 ~ end)
     *
     * @param end
     * @return
     */
    public static long random(long end) {
        return random(0, end);
    }

    /**
     * 获得随机数 [start ~ end)
     *
     * @param start
     * @param end
     * @return
     */
    public static long random(long start, long end) {
        return (long) (Math.random() * (end - start)) + start;
    }


    /**
     * 小概率事件判断
     *
     * @param probability 值越大，改率越小
     * @return 概率是否命中
     */
    public static boolean getSmallProbabilityEvent(long probability) {
        if (probability < 1) probability = 1;
        return Utils.random(probability) == (probability - 1) / 2;
    }


    public static String getUserName() {
        if (Utils.getSmallProbabilityEvent(2)) {
            return "Vip888";
        }
        if (Utils.getSmallProbabilityEvent(2)) {
            return "Gm666";
        }
        if (Utils.getSmallProbabilityEvent(2)) {
            return "PM333";
        }
        if (Utils.getSmallProbabilityEvent(2)) {
            return "小明";
        }
        return "";
    }


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
}
