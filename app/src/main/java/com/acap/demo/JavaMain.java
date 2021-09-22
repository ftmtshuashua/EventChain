package com.acap.demo;

import com.acap.ec.Event;
import com.acap.ec.listener.OnEventLogListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      用于测试链上调用逻辑正确性
 *
 * Created by ACap on 2021/7/9 21:29
 * </pre>
 */
class JavaMain {

    private static Event<Integer, ?> mEvent = null;

    private static void init() {
        if (mEvent != null) return;
//        Events.create( )

//        Event<Integer, Integer[]> merge = Events.merge(new AddEvent(2), new AddEvent(3));
//        merge .apply((Apply<Integer[], Integer>) params -> params[0] + params[1]);
//        merge.start(2);


        //TODO:Chain的完成逻辑 - 总是在所有事件执行完成之后才回调

        mEvent = new AddEvent(1).listener(new OnEventLogListener<>("1"))
                .chain(new AddEvent(2)).listener(new OnEventLogListener<>("2"))
                .merge(new AddEvent(2), new AddEvent(3)).listener(new OnEventLogListener<>("3"))
//                .apply((Apply<List<Integer>, Integer>) params -> params.get(0) + params.get(1)) //TODO:ApplyEvent的完成逻辑 - 无完成逻辑实现
//                .chain(new TestEvent<Number, String>("2"))
        ;

        mEvent.listener(new OnEventLogListener<>("监听"));
    }


    public static void main(String[] args) {
        init();

        mEvent.start(1);
        mEvent.start(2);

//        Integer[] integers = get(1);
//        System.out.println(integers);

    }

    public static <R> R[] get(R ad) {
        List<R> array = new ArrayList<>();
        array.add(ad);
        return (R[]) array.toArray();
    }

}

