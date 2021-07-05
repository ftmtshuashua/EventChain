package com.acap.demo.event;

import com.acap.demo.mode.ModelUserLogin;
import com.acap.demo.utils.ThreadHelper;
import com.acap.demo.utils.Utils;
import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      模拟：用户登录事件
 *
 * Created by ACap on 2021/7/3 16:49
 * </pre>
 */
public class E1_UserLogin extends Event<String, ModelUserLogin> {

    @Override
    protected void onCall(String params) {
        if (params == null || params.length() == 0) {
            error(new Exception("请输入用户名!"));
        } else {
            request(params);
        }
    }

    //模拟：通过用户名请求登录信息
    private void request(String username) {
        ThreadHelper.io(() -> {
            ThreadHelper.sleep(500, 1000);
            if (Utils.getSmallProbabilityEvent(15)) {
                ThreadHelper.main(() -> error(new Exception("用户信息服务异常!")));
            } else {
                String user_id = String.valueOf(Utils.random(100));
                ModelUserLogin result = new ModelUserLogin(username, user_id);
                ThreadHelper.main(() -> next(result));
            }
        });

    }
}