package event;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      打印事件
 *
 * Created by ACap on 2021/3/30 10:23
 * </pre>
 */
public class PrintEvent<T> extends Event<T, T> {
    String msg;

    public PrintEvent(String msg) {
        this.msg = msg;
    }

    @Override
    public void onCall(T params) {
        System.out.println(msg);
        next(params);
    }
}
