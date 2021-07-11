package com.acap.demo.event;

import com.acap.demo.log.Logger;
import com.acap.demo.log.OnEventLogger;
import com.acap.demo.utils.DemoEventLifecycle;
import com.acap.demo.utils.ThreadHelper;
import com.acap.ec.Event;
import com.acap.ec.utils.EUtils;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/11 11:17
 * </pre>
 */
public class DemoEvent<P, R> extends Event<P, R> {
    public R r;

    public DemoEvent(Logger mLogger, R r) {
        this.r = r;
        listener(new OnEventLogger<>(mLogger));
    }

    @Override
    public void finish() {
        EUtils.i(DemoEventLifecycle.id(this) ,"finish()");
        super.finish();
    }

    @Override
    protected void onCall(P params) {
        ThreadHelper.io(() -> {
            ThreadHelper.sleep(500, 1000);
            next(r);
        });
        if (finish) {
            ThreadHelper.io(() -> {
                ThreadHelper.sleep(300, 700);
                finish();
            });
        }
    }

    private boolean finish;

    public DemoEvent<P, R> setFinish() {
        finish = true;
        return this;
    }

    public R getResult() {
        return r;
    }
}
