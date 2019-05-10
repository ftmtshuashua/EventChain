package com.lfp.eventtree.excption;

import com.lfp.eventtree.EventChainUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * <pre>
 * Tip:
 *      多个异常信息
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 23:20
 * </pre>
 */
public class MultiException extends RuntimeException {
    final Set<Throwable> arrays = new HashSet<>();

    public MultiException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiException(Throwable cause) {
        super(cause);
    }

    public MultiException(String message) {
        super(message);
    }

    public MultiException() {
        super();
    }

    /**
     * 插入异常信息
     */
    public void put(Throwable t) {
        if (t != null) arrays.add(t);
        else {
            if (EventChainUtils.isDebug()) System.err.println("不能放入一个空异常信息!");
        }
    }

    /**
     * 获得异常列表
     */
    public Set<Throwable> getArray() {
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
        if (arrays.isEmpty()) return this;
        return arrays.iterator().next();
    }

    @Override
    public void printStackTrace() {
        if (arrays.isEmpty()) {
            super.printStackTrace();
        } else if (arrays.size() == 1) {
            getFirst().printStackTrace();
        } else {
            System.err.println("↓↓↓↓↓↓↓↓↓↓ " + getMessage() + " ↓↓↓↓↓↓↓↓↓↓");
            for (Throwable t : arrays) {
                t.printStackTrace();
                System.err.println("------------------------------------");
            }
            System.err.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        }
    }

    @Override
    public String getMessage() {
        if (arrays.isEmpty()) {
            return super.getMessage();
        } else if (arrays.size() == 1) {
            return getFirst().getMessage();
        } else {
            return "事件链中有 " + arrays.size() + " 个异常需要处理";
        }
    }
}
