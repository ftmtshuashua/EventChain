package com.acap.demo.lesson;

import com.acap.ec.BaseEvent;

/**
 *
 */
public class AddEvent extends BaseEvent<Integer, Integer> {
    private int mValue;

    public AddEvent(int mValue) {
        this.mValue = mValue;
    }

    @Override
    protected void onCall(Integer params) {
        next(params + mValue);
    }
}
