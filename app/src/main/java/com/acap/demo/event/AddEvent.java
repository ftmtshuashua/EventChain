package com.acap.demo.event;

import com.acap.demo.event.DemoEvent;
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
public class AddEvent extends DemoEvent<Integer, Integer> {

    private Integer mResult;

    public AddEvent(Integer mResult) {
        this.mResult = mResult;
    }

    @Override
    protected void onCall(Integer params) {
        try {
            Thread.sleep(Utils.random(100, 200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int value = params + mResult;

        new Thread(() -> {
            System.out.println(String.format("%s : %s + %s = %s", Utils.id(this), params, mResult, value));
            next(value);
        }).start();
    }
}
