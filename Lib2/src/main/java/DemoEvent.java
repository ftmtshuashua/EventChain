import com.acap.ec.Event;
import com.acap.ec.utils.OnEventLogListener;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/9 23:28
 * </pre>
 */
public class DemoEvent<P, R> extends Event {
    private String tag;
    private R result;

    public DemoEvent(String tag, R result) {
        this.tag = tag;
        this.result = result;

        addOnEventListener(new OnEventLogListener(this.tag));
    }

    @Override
    protected void onCall(Object params) {
        next(result);
    }


}
