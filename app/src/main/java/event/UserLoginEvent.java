package event;

import com.acap.ec.Event;

import mode.ModelUserToken;

/**
 * <pre>
 * Tip:
 *      模拟：用户登录事件
 *
 * Created by ACap on 2021/7/3 16:49
 * </pre>
 */
public class UserLoginEvent extends Event<String, ModelUserToken> {

    @Override
    protected void onCall(String params) {
        if (params == null || params.length() == 0) {
            error(new Exception("请输入用户名!"));
        } else {
            request(params);
        }
    }

    //模拟：通过用户名请求登录信息
    private void request(String userid) {
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ModelUserToken result = new ModelUserToken(userid, String.valueOf((int) (Math.random() * 10000)));
            next(result);
        }).start();
    }
}