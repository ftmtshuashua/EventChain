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
public class MultiException extends EventException {
    List<Throwable> array;

    public MultiException() {
        super("发生多处错误!");
    }

    public void add(Throwable t) {
        if (array == null) array = new ArrayList<>();
        array.add(t);
    }

    public List<Throwable> getArray() {
        return array;
    }

    @Override
    public void printStackTrace() {
        if (array == null || array.isEmpty()) {
            super.printStackTrace();
        } else {
            for (Throwable t : array) {
                t.printStackTrace();
            }
        }
    }

}
