package com.acap.demo;

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
public class AddEvent extends BaseEvent<Integer, Integer> {

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

        System.out.println(String.format("%s : %s + %s = %s", Utils.id(this), params, mResult, value));
        next(value);
        complete();
    }
}
