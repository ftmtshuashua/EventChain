package com.acap.demo.utils;

import android.os.Handler;
import android.os.Looper;

import io.reactivex.internal.schedulers.RxThreadFactory;

/**
 * <pre>
 * Tip:
 *      线程工具
 *
 * Created by ACap on 2021/7/5 23:01
 * </pre>
 */
public class ThreadHelper {
    private static Handler MAIN_THREAD = new Handler(Looper.myLooper());
    private static final RxThreadFactory IO_THREAD = new RxThreadFactory("IOT");


    //线程暂停
    public static final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //线程暂停随机时间
    public static final void sleep(long minmillis, long maxmillis) {
        sleep(Utils.random(minmillis, maxmillis));
    }

    //在子线程运行
    public static final void io(Runnable runnable) {
        IO_THREAD.newThread(runnable).start();
    }

    //在主线运行
    public static final void main(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            MAIN_THREAD.post(runnable);
        }
    }

}
