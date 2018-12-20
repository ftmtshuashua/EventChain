package com.lfp.eventtree.excption;

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
public class MultiException extends InterruptException {
    List<Throwable> array;


    public void add(Throwable t) {
        if (array == null) array = new ArrayList<>();
        array.add(t);
    }

    public List<Throwable> getArray() {
        return array;
    }
}
