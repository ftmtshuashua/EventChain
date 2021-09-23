package com.acap.demo.event;

import com.acap.demo.utils.Utils;
import com.acap.ec.BaseEvent;

/**
 * <pre>
 * Tip:
 *     测试事件
 *
 * Created by A·Cap on 2021/9/22 9:48
 * </pre>
 */
public class TestEvent<P, R> extends DemoEvent<P, R> {

    private R mResult;

    public TestEvent(R mResult) {
        this.mResult = mResult;
    }

    @Override
    protected void onCall(P params) {
        try {
            Thread.sleep(Utils.random(100, 500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("%s : %s -> %s", Utils.id(this), params, mResult));
        next(mResult);
    }
}
