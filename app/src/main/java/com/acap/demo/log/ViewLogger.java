package com.acap.demo.log;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;
import android.widget.TextView;

import com.acap.demo.utils.ThreadHelper;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/11 11:20
 * </pre>
 */
public class ViewLogger implements Logger {
    private TextView mV_Info;
    private ScrollView mV_Scroll;

    public ViewLogger(TextView mV_Info, ScrollView mV_Scroll) {
        this.mV_Info = mV_Info;
        this.mV_Scroll = mV_Scroll;
    }

    //清除
    public synchronized void clean() {
        ThreadHelper.main(() -> mV_Info.setText(""));
    }

    //追加消息
    public synchronized void i(String msg) {
//            EUtils.i("Event-----------------------", msg);
        ThreadHelper.main(() -> {
            mV_Info.append(msg + "\n");
            mV_Info.post(() -> mV_Scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    public synchronized void e(String msg) {
//            EUtils.i("Event-----------------------", msg);
        ThreadHelper.main(() -> {
            SpannableString spannableString = new SpannableString(msg + "\n");
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), 0);
            mV_Info.append(spannableString);
            mV_Info.post(() -> mV_Scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }
}
