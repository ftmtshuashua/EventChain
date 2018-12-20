package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件监听
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/19 18:05
 * </pre>
 */
public interface OnEventListener {
    /**
     * 当事件开始的时候
     */
    void onStart();


    /**
     * 当事件结束的时候
     */
    void onComplete();
}
