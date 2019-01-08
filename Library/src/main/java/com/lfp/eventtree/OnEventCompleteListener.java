package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件完成监听,在时间结束之后调用。一个事件或者事件链只会调用一次
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
@FunctionalInterface
public interface OnEventCompleteListener {

    /** 在时间结束之后调用。一个事件或者事件链只会调用一次 */
    void onComplete();
}
