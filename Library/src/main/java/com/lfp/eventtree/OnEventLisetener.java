package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      事件监听
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:55
 * </pre>
 */
public interface OnEventLisetener {

    /*事件开始*/
    void onStart();

    /*错误信息*/
    void onError(Throwable e);

    /*事件结束*/
    void onNext();

    /*不管是发生错误还是事件结束都会调用*/
    void onComplete();
}
