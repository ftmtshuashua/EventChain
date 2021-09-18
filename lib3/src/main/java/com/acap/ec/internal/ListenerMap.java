package com.acap.ec.internal;

import com.acap.ec.action.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      用于监听器集合的管理工具
 *
 * Created by ACap on 2021/6/30 14:24
 * </pre>
 * @author A·Cap
 */
public class ListenerMap<T> {
    private List<T> mArray = new ArrayList<>();

    /**
     * 注册监听器
     *
     * @param t 监听器对象
     */
    public void register(T t) {
        if (!mArray.contains(t)) {
            mArray.add(t);
        }
    }

    public void registerAtFirst(T t) {
        if (!mArray.contains(t)) {
            mArray.add(0, t);
        }
    }


    /**
     * 移除监听器
     *
     * @param t 监听器对象
     * @return 是否移除成功
     */
    public boolean unregister(T t) {
        return mArray.remove(t);
    }


    /**
     * 遍历监听器列表
     */
    public void map(Action1<T> action1) {
        for (T t : mArray) {
            action1.call(t);
        }
    }

}
