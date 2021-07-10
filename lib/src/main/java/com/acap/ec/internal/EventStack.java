package com.acap.ec.internal;

import java.util.LinkedList;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/11 0:06
 * </pre>
 */
public class EventStack<T> {
    private final LinkedList<T> array = new LinkedList<>();

    //入栈
    public void push(T t) {
        array.addFirst(t);
    }

    //出栈
    public T pop() {
        return array.removeFirst();
    }

    //栈顶元素
    public T peek() {
        T t = null;
        //直接取元素会报异常，需要先判断是否为空
        if (!array.isEmpty())
            t = array.getFirst();
        return t;
    }

    //栈空
    public boolean isEmpty() {
        return array.isEmpty();
    }
}
