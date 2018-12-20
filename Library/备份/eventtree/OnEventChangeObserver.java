package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件变化观察
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 09:50
 * </pre>
 */
public interface OnEventChangeObserver {
    void onStart(EventChain event);

    void onComplete(EventChain event);

    void onChain(EventChain event);
}
