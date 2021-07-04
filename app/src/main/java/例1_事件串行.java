import com.acap.ec.Event;
import com.acap.ec.listener.OnEventNextListener;
import com.acap.ec.utils.EUtils;
import com.acap.ec.utils.OnChainLogListener;

import event.GetUserDetailEvent;
import event.UserLoginEvent;
import mode.ModelUserDetail;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/3 16:16
 * </pre>
 */
public class 例1_事件串行 {


    public static void main(String[] args) {

        /*
         * 模拟：
         * 1.用户登录获得Token
         * 2.通过Token获取用户详情
         */

        Event.create(new UserLoginEvent())

                .chain(new GetUserDetailEvent())
                .addOnEventListener((OnEventNextListener<ModelUserDetail>) result -> {
                    System.out.println("登录成功:" + result.toString());
                })

                .addOnChainListener(new OnChainLogListener<>("链日志"))
                .start("XXS");

    }


}
