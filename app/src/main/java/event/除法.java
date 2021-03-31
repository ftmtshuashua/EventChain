package event;

import com.acap.ec.EventChain;

/**
 * <pre>
 * Tip:
 *
 * Created by ACap on 2021/3/30 14:05
 * </pre>
 */
public class 除法 extends EventChain<Integer, Integer> {

    int value;

    public 除法(int value) {
        this.value = value;
    }

    @Override
    public void onCall(Integer params) {
        next(params / value);
    }
}
