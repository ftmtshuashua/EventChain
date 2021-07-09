package com.acap.ec;


/**
 * <pre>
 * Tip:
 *      合并事件
 *      该事件包含多个子事件，当所有子事件完成时候，该事件完成
 *
 * Created by ACap on 2021/3/31 10:33
 * </pre>
 */
public class MergeEvent<P, R> extends Event<P, R[]> {

    public <T extends ILinkable<P, ? extends R>> MergeEvent(T... chains) {
    }

    @Override
    protected void onCall(P params) {

    }
}
