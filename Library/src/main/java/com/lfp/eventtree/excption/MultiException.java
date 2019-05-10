package com.lfp.eventtree.excption;

import com.lfp.eventtree.EventChainUtils;

import java.util.ArrayList;
import java.util.List;

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
    final List<Throwable> arrays = new ArrayList<>();

    public MultiException(String message) {
        super(message);
    }

    public MultiException() {
        super();
    }

    /**
     * 插入异常信息
     */
    public void add(Throwable t) {
        if (t != null) arrays.add(t);
        else {
            if (EventChainUtils.isDebug()) System.err.println("不能放入一个空异常信息!");
        }
    }

    /**
     * 获得异常列表
     */
    public List<Throwable> getArray() {
        return arrays;
    }

    @Override
    public void printStackTrace() {
        if (arrays.isEmpty()) {
            super.printStackTrace();
        } else if (arrays.size() == 1) {
            arrays.get(0).printStackTrace();
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
            return arrays.get(0).getMessage();
        } else {
            return "事件链中有 " + arrays.size() + " 个异常需要处理";
        }
    }
}
