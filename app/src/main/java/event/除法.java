package event;

import com.acap.ec.Event;

/**
 * <pre>
 * Tip:
 *
 * Created by ACap on 2021/3/30 14:05
 * </pre>
 */
public class 除法 extends Event<Integer, Integer> {

    int value;

    public 除法(int value) {
        this.value = value;
    }

    @Override
    public void onCall(Integer params) {
        next(params / value);
    }
}
