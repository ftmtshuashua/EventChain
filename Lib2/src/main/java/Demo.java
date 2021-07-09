import com.acap.ec.Event;
import com.acap.ec.utils.OnEventLogListener;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/9 21:29
 * </pre>
 */
class Demo {
    public static void main(String[] args) {


        Event<String, String> s_s = new DemoEvent<>("s_s", "S_S");
        Event<String, Integer> s_i = new DemoEvent<>("s_i", 1);
        Event<String, Object> s_o = new DemoEvent<>("s_o", "s_o");

        Event<Integer, Integer> i_i = new DemoEvent<>("i_i", 2);
        Event<Integer, String> i_s = new DemoEvent<>("i_s", "i_s");
        Event<Integer, Object> i_o = new DemoEvent<>("i_o", "i_o");

        Event<Object, Object> o_o = new DemoEvent<>("o_o", "o_o");
        Event<Object, String> o_s = new DemoEvent<>("o_s", "o_s");
        Event<Object, Integer> o_i = new DemoEvent<>("o_i", 3);


        s_i
                .chain(
                        i_o.chain(o_s)
                                .addOnEventListener(new OnEventLogListener<>("Child"))
                )

                .addOnEventListener(new OnEventLogListener<>("Chain"))
                .start("123");


    }
}
