package event;

import com.acap.ec.Event;

import mode.ModelUserDetail;
import mode.ModelUserToken;

/**
 * <pre>
 * Tip:
 *      模拟:通过登录Token获取用户详情
 *
 * Created by ACap on 2021/7/3 16:58
 * </pre>
 */
public class GetUserDetailEvent extends Event<ModelUserToken, ModelUserDetail> {

    @Override
    protected void onCall(ModelUserToken params) {
        if (params == null) {
            error(new Exception("用户未登录"));
        } else {
            request(params);
        }
    }

    //模拟：请求用户信息
    private void request(ModelUserToken params) {
        ModelUserDetail modelUserDetail = new ModelUserDetail(String.format("这个用户(%s)很懒,什么都没填写!", params.username));
        next(modelUserDetail);
    }

}
