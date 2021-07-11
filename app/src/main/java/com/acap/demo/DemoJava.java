package com.acap.demo;

import com.acap.ec.Event;
import com.acap.ec.listener.OnEventLogListener;

/**
 * <pre>
 * Tip:
 *      用于测试链上调用逻辑正确性
 *
 * Created by ACap on 2021/7/9 21:29
 * </pre>
 */
class DemoJava {
    public static void main(String[] args) {

        s_i().chain(i_o().chain(o_s()))
                .apply(params -> params)
                .merge(s_i(), s_o())
                .listener(new OnEventLogListener<>("Chain"))
                .start("123");

    }


    private static Event<String, String> s_s() {
        return new DemoEvent<>("s_s", "s_s");
    }

    private static Event<String, Integer> s_i() {
        return new DemoEvent<>("s_i", 1);
    }

    private static Event<String, Object> s_o() {
        return new DemoEvent<>("s_o", "s_o");
    }

    private static Event<Integer, String> i_s() {
        return new DemoEvent<>("i_s", "i_s");
    }

    private static Event<Integer, Integer> i_i() {
        return new DemoEvent<>("i_i", 2);
    }

    private static Event<Integer, Object> i_o() {
        return new DemoEvent<>("i_o", "i_o");
    }

    private static Event<Object, String> o_s() {
        return new DemoEvent<>("o_s", "o_s");
    }

    private static Event<Object, Integer> o_i() {
        return new DemoEvent<>("o_i", 3);
    }

    private static Event<Object, Object> o_o() {
        return new DemoEvent<>("o_o", "o_o");
    }

    public static final class DemoEvent<P, R> extends Event<P, R> {
        private String tag;
        private R result;

        public DemoEvent(String tag, R result) {
            this.tag = tag;
            this.result = result;

            listener(new OnEventLogListener(this.tag));
        }

        @Override
        protected void onCall(Object params) {
            next(result);
        }
    }

}

