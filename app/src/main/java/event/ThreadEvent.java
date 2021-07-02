package event;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      线程切换事件
 *
 * Created by ACap on 2021/4/1 10:31
 * </pre>
 */
public class ThreadEvent<T> extends Event<T, T> {

    @Override
    protected void onCall(T params) {
        new Thread(() -> next(params)).start();
    }
}
