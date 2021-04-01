package event;

import com.acap.ec.EventChain;

/**
 * <pre>
 * Tip:
 *      线程切换事件
 *
 * Created by ACap on 2021/4/1 10:31
 * </pre>
 */
public class ThreadEvent<T> extends EventChain<T, T> {

    @Override
    protected void onCall(T params) {
        new Thread(() -> next(params)).start();
    }
}
