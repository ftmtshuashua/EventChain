package event;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *      类型事件
 *
 * Created by ACap on 2021/3/31 15:24
 * </pre>
 */
public class TypeEvent<P, R> extends Event<P, R> {
    @Override
    protected void onCall(P params) {
        next(null);
    }
}
