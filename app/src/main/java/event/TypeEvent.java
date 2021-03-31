package event;

import com.acap.ec.EventChain;

/**
 * <pre>
 * Tip:
 *      类型事件
 *
 * Created by ACap on 2021/3/31 15:24
 * </pre>
 */
public class TypeEvent<P, R> extends EventChain<P, R> {
    @Override
    protected void onCall(P params) {
        next(null);
    }
}
