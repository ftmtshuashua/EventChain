package com.acap.ec;

/**
 * <pre>
 * Tip:
 *      一个简单的事件，该事件的入参和出参相同
 *      1.如果调用{@link #next()}方法，则使用入参去调用{@link #next(Object)}方法
 *
 * Created by ACap on 2021/7/9 23:26
 * </pre>
 * @author A·Cap
 */
public abstract class SimpleEvent<T> extends Event<T, T> {

    private T mParams;

    @Override
    public void onPrepare(T params) {
        super.onPrepare(params);
        mParams = params;
    }

    @Override
    protected void next() {
        super.next(mParams);
    }

}
