package com.acap.demo.event;

import com.acap.demo.mode.ModelUserDetail;
import com.acap.demo.mode.ModelUserLogin;
import com.acap.demo.utils.ThreadHelper;
import com.acap.demo.utils.Utils;
import com.acap.ec.Event;

import java.text.MessageFormat;

/**
 * <pre>
 * Tip:
 *      模拟:通过登录Token获取用户详情
 *
 * Created by ACap on 2021/7/3 16:58
 * </pre>
 */
public class E2_GetUserDetail extends BaseEvent<ModelUserLogin, ModelUserDetail> {

    @Override
    protected void onChildCall(ModelUserLogin params) {
        if (params == null) {
            error(new Exception("用户未登录"));
        } else {
            request(params);
        }
    }

    //模拟：请求用户信息
    private void request(ModelUserLogin params) {
        ThreadHelper.io(() -> {
            ThreadHelper.sleep(500, 1000);
            if (Utils.getSmallProbabilityEvent(25)) {
                ThreadHelper.main(() -> error(new Exception("登录服务异常!")));
            } else {
                String detail = MessageFormat.format("用户<{0}>很懒,什么都没填写!", params.username);
                ModelUserDetail modelUserDetail = new ModelUserDetail(detail);
                ThreadHelper.main(() -> next(modelUserDetail));
            }
        });
    }

}
