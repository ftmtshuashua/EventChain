package com.acap.demo.event;

import com.acap.demo.mode.ModelFriends;
import com.acap.demo.mode.ModelGroups;
import com.acap.demo.mode.ModelUserDetail;
import com.acap.demo.utils.ThreadHelper;
import com.acap.demo.utils.Utils;
import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      模拟:获得群列表
 *
 * Created by ACap on 2021/7/3 16:58
 * </pre>
 */
public class E3_GetGroups extends BaseEvent<ModelUserDetail, ModelGroups> {

    @Override
    protected void onChildCall(ModelUserDetail params) {
        if (params == null) {
            error(new Exception("用户未登录"));
        } else {
            request(params);
        }
    }

    //模拟：请求用户信息
    private void request(ModelUserDetail params) {
        ThreadHelper.io(() -> {
            ThreadHelper.sleep(500, 1000);
            if (Utils.getSmallProbabilityEvent(20)) {
                ThreadHelper.main(() -> error(new Exception("好友服务异常!")));
            } else {
                ModelGroups model = new ModelGroups("吹水群", "301五傻", "X技术交流");
                ThreadHelper.main(() -> next(model));
            }
        });
    }

}
