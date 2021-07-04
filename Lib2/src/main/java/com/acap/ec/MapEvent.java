package com.acap.ec;

import com.acap.ec.action.Apply;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/2 18:09
 * </pre>
 */
public class MapEvent<P, R> extends Event<P, R> {
    private Apply<P, R> mApply;

    public MapEvent(Apply<P, R> mApply) {
        this.mApply = mApply;
    }

    @Override
    protected void onCall(P params) {
//
//        if(params)
//
//        R apply = mApply.apply(params);
    }
}
