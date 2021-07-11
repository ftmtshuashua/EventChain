package com.acap.demo.log;

import com.acap.demo.event.DemoEvent;
import com.acap.ec.ILinkable;
import com.acap.ec.listener.OnEventListener;
import com.acap.ec.utils.EUtils;

import java.text.MessageFormat;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/11 11:21
 * </pre>
 */
public class OnEventLogger<P, R> implements OnEventListener<P, R> {
    private Logger mLogger;

    private ILinkable mEvent;

    public OnEventLogger(Logger mLogger) {
        this.mLogger = mLogger;
    }

    @Override
    public void onStart(ILinkable<P, R> event, P params) {
        mEvent = event;
        mLogger.i(MessageFormat.format("{0}.onStart({1})", getId(mEvent), params));
    }

    @Override
    public void onError(Throwable e) {
        mLogger.i(MessageFormat.format("{0}.onError({1})", getId(mEvent), e));
    }

    @Override
    public void onNext(R result) {
        mLogger.i(MessageFormat.format("{0}.onNext({1})", getId(mEvent), result));
    }

    @Override
    public void onComplete() {
        mLogger.i(MessageFormat.format("{0}.onComplete()", getId(mEvent)));
    }

    private String getId(ILinkable iLinkable) {
        if (iLinkable instanceof DemoEvent) {

        }
        return EUtils.id(iLinkable);
    }
}
