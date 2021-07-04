package com.acap.ec.utils;

import com.acap.ec.action.Action1;

import java.util.List;

/**
 * <pre>
 * Tip:
 *      列表遍历工具
 *
 * Created by ACap on 2021/7/4 0:29
 * </pre>
 */
public class ListMap<T> {
    private List<T> mList;

    public ListMap(List<T> mList) {
        this.mList = mList;
    }


    /**
     * 列表中的数据
     */
    public void map(Action1<T> action1) {
        for (T t : mList) {
            action1.call(t);
        }
    }

}
