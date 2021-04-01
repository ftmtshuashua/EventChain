package com.acap.ec.excption;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      正常情况下一条链上只会抛出一个错误信息，但是如果有两个并行的链同时抛出异常，则会产生多个异常信息
 *
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public class MultiException extends RuntimeException {
    private final ArrayList<Throwable> arrays = new ArrayList<>();

    public MultiException() {
    }

    /**
     * 获得导致这个错误的第一个原因
     */
    @Override
    public synchronized Throwable getCause() {
        return this;
    }

    /**
     * 插入一个异常信息
     *
     * @param throwable 错误信息
     */
    public void put(Throwable throwable) {
        if (throwable != null) {
            if (throwable instanceof MultiException) {
                arrays.addAll(((MultiException) throwable).getArray());
            } else {
                arrays.add(throwable);
            }
        }
    }

    /**
     * 获得异常列表
     */
    public List<Throwable> getArray() {
        return arrays;
    }

    /**
     * 判断错误列表是否为空
     */
    public boolean isEmpty() {
        return arrays.isEmpty();
    }

    /**
     * 获得错误列表的大小
     */
    public int size() {
        return arrays.size();
    }

    /**
     * 获得错误列表中第一个错误，如果错误列表为空则返回错误本身
     */
    public Throwable getFirst() {
        if (isEmpty()) return this;
        return arrays.get(0);
    }

    @Override
    public void printStackTrace() {
        if (size() <= 1) {
            getFirst().printStackTrace();
        } else {
            String message = getMessage();
            System.err.println("↓↓↓↓↓↓↓↓↓↓ " + message + " ↓↓↓↓↓↓↓↓↓↓");
            for (int i = 0; i < arrays.size(); i++) {
                if (i != 0) System.err.println("------------------------------------");
                arrays.get(i).printStackTrace();
            }
            System.err.println("↑↑↑↑↑↑↑↑↑↑ " + message + " ↑↑↑↑↑↑↑↑↑↑");
        }
    }

    @Override
    public String getMessage() {
        if (arrays.isEmpty()) {
            return super.getMessage();
        } else if (arrays.size() == 1) {
            return getFirst().getMessage();
        } else {
            return "链中包含 " + arrays.size() + " 个异常需要处理!";
        }
    }


    private static final String getObjectId(Object object) {
        if (object == null) return "Null_Obj";
        return object.getClass().getSimpleName() + "(" + Integer.toHexString(System.identityHashCode(object)) + ")";
    }

}
