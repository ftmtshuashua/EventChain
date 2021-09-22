package com.acap.demo.log;

import android.view.View;

import com.acap.demo.utils.ThreadHelper;
import com.acap.ec.Event;
import com.acap.ec.listener.OnEventListener;

/**
 * <pre>
 * Tip:
 *      事件运行监听
 *
 * Created by ACap on 2021/7/5 22:23
 * </pre>
 */
public class OnEventAnimation<P, R> implements OnEventListener<P, R> {

    private View mV_Show;
    private View mV_Hint;

    public OnEventAnimation(View mV_Show, View mV_Hint) {
        this.mV_Show = mV_Show;
        this.mV_Hint = mV_Hint;
    }

    private void show(boolean show) {
        ThreadHelper.main(() -> {
            if (mV_Show != null) mV_Show.setVisibility(show ? View.VISIBLE : View.GONE);
            if (mV_Hint != null) mV_Hint.setVisibility(show ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onStart(Event<P, R> event, P params) {
        show(true);
    }


    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(R result) {

    }

    @Override
    public void onComplete() {
        show(false);
    }
}
