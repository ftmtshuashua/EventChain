package com.acap.demo.event;

import com.acap.demo.utils.ThreadHelper;
import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      模拟:请求广告
 *
 * Created by ACap on 2021/7/3 16:58
 * </pre>
 */
public class E5_RequestAd extends Event<Object, String> {

    @Override
    protected void onCall(Object params) {
        request(params);
    }

    //模拟：请求用户信息
    private void request(Object params) {
        ThreadHelper.io(() -> {
            ThreadHelper.sleep(500, 2000);
            ThreadHelper.main(() -> next("插播广告:https://github.com/ftmtshuashua/EventChain"));
        });
    }

}
