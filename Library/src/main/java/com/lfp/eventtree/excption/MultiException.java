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
public class MultiException extends RuntimeException {
    List<Throwable> other;

    public MultiException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiException(Throwable cause) {
        super(cause);
    }

    public void add(Throwable t) {
        if (other == null) other = new ArrayList<>();
        other.add(t);
    }

    public List<Throwable> getArray() {
        return other;
    }

    @Override
    public void printStackTrace() {
        if (other == null || other.isEmpty()) {
            super.printStackTrace();
        } else {
            for (Throwable t : other) {
                t.printStackTrace();
            }
        }
    }

}
