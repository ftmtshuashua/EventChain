package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *
 * Function:
 *
 * Created by LiFuPing on 2019/5/9 15:53
 * </pre>
 */
public final class EventChainUtils {
    public static final String TAG = "EventChain";
    static boolean isDebug = false;
    private EventChainUtils(){}

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        EventChainUtils.isDebug = isDebug;
    }
}
