package com.acap.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.acap.demo.event.DemoEvent;
import com.acap.demo.log.OnEventAnimation;
import com.acap.demo.log.ViewLogger;
import com.acap.demo.utils.DemoEventLifecycle;
import com.acap.ec.ILinkable;
import com.acap.ec.listener.OnEventStartListener;
import com.bumptech.glide.Glide;

/**
 * <pre>
 * Tip:
 *
 *      事件链测试
 *
 *
 * Created by ACap on 2021/7/5 21:55
 * </pre>
 */
public class DemoActivity extends Activity {

    private ViewLogger mViewLog;

    private ILinkable mDemoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demi);
        mViewLog = new ViewLogger(findViewById(R.id.info), findViewById(R.id.scroll));

        Glide.with(this).load(R.drawable.icon_loading).into((ImageView) findViewById(R.id.loading));

        create();

        //添加事件执行动画
        mDemoEvent.listener(new OnEventAnimation<>(findViewById(R.id.loading), findViewById(R.id.button)));
        mDemoEvent.listenerFirst((OnEventStartListener) (event, params) -> mViewLog.clean())
                .listenerRemove(null)
                .listenerRemove(null);

    }


    public void create() {
        mDemoEvent =
                new DemoEvent<String, Integer>(mViewLog, 11)

                        .chain(new DemoEvent<Integer, Object>(mViewLog, "c_1"))
                        .merge(new DemoEvent<Object, String>(mViewLog, "m-1"), new DemoEvent<Object, String>(mViewLog, "m-2"))
        ;

    }


    public void start(View v) {
        mDemoEvent.setLifecycle(new DemoEventLifecycle());
        mDemoEvent.start();
    }


}
