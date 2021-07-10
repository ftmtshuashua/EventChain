package com.acap.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.acap.demo.dialog.OnEventRunningListener;
import com.acap.demo.event.E1_UserLogin;
import com.acap.demo.event.E2_GetUserDetail;
import com.acap.demo.event.E3_GetGroups;
import com.acap.demo.event.E4_GetFriends;
import com.acap.demo.mode.ModelGroups;
import com.acap.demo.mode.ModelUserDetail;
import com.acap.demo.mode.ModelUserLogin;
import com.acap.demo.utils.ThreadHelper;
import com.acap.demo.utils.Utils;
import com.acap.ec.ILinkable;
import com.acap.ec.listener.OnEventCompleteListener;
import com.acap.ec.listener.OnEventErrorListener;
import com.acap.ec.listener.OnEventLogListener;
import com.acap.ec.listener.OnEventNextListener;
import com.acap.ec.listener.OnEventStartListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.MessageFormat;

/**
 * <pre>
 * Tip:
 *
 *      事件链测试
 *
 *
 * Created by ACap on 2021/7/5 21:55
 * </pre>
 */
public class DemoActivity extends Activity {

    private LogView mLogView;
    private ImageView mV_Loading;


    private Button mV_Button;
    private ILinkable<String, ?> mDemoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demi);
        mV_Loading = findViewById(R.id.loading);
        mV_Button = findViewById(R.id.button);
        mLogView = new LogView(findViewById(R.id.info), findViewById(R.id.scroll));

        Glide.with(this).load(R.drawable.icon_loading).into(mV_Loading);


        //登录
        mDemoEvent = new E1_UserLogin()
//                .addOnEventListener(new OnEventLogListener<>("E1_UserLogin"))
                .addOnEventListener((OnEventStartListener<String, ModelUserLogin>) params -> mLogView.print_i(MessageFormat.format("----------用户<{0}>登录----------", params)))
                .addOnEventListener((OnEventNextListener<String, ModelUserLogin>) result -> mLogView.print_i(MessageFormat.format("用户<{0}>登录成功,用户ID：{1}", result.username, result.token)))
                .addOnEventListener((OnEventErrorListener<String, ModelUserLogin>) e -> mLogView.print_e(MessageFormat.format("登录失败:{0}", e.getMessage())))

                //获得用户信息
                .chain(
                        new E2_GetUserDetail()
//                                .addOnEventListener(new OnEventLogListener<>("E2_GetUserDetail"))
                                .addOnEventListener((OnEventStartListener<ModelUserLogin, ModelUserDetail>) params -> mLogView.print_i("----------获取用户信息----------"))

                )
                .addOnEventListener((OnEventNextListener<String, ModelUserDetail>) result -> mLogView.print_i(MessageFormat.format("获取用户信息成功:{0}", result.detail)))
                .addOnEventListener((OnEventErrorListener<String, ModelUserDetail>) e -> mLogView.print_e(MessageFormat.format("获取失败:{0}", e.getMessage())))

                //请求好友列表与群列表
                .merge(
                        new E3_GetGroups()
                                .addOnEventListener((OnEventCompleteListener<ModelUserDetail, ModelGroups>) () -> mLogView.print_i("----------获得好友和群列表----------"))
//                                .addOnEventListener(new OnEventLogListener<>("E3_GetGroups"))
                        ,
                        new E4_GetFriends()
//                                .addOnEventListener(new OnEventLogListener<>("E4_GetFriends"))
                )
                .addOnEventListener((OnEventStartListener<String, Object[]>) params -> mLogView.print_clear())
//                .addOnEventListener(new OnEventLogListener<>("Chain"))
                .addOnEventListener((OnEventErrorListener<String, Object[]>) e -> mLogView.print_e(MessageFormat.format("获取聊天数据:{0}", e.getMessage())))
                .addOnEventListener((OnEventNextListener<String, Object[]>) result -> {
                    mLogView.print_i(MessageFormat.format("群列表:{0}", new Gson().toJson(result[0])));
                    mLogView.print_i(MessageFormat.format("好友列表:{0}", new Gson().toJson(result[1])));
                })
                .addOnEventListener(new OnEventRunningListener<>(mV_Loading, mV_Button))


        ;

/*
        //TODO:Java泛型数组安全性问题 - https://blog.csdn.net/zxm317122667/article/details/78400398
//                //请求广告并设置超时时间1秒
//                .oneOf(new E5_RequestAd(), new E_TimeOut<>(1000))
//                .addOnEventListener(new OnEventLogListener<>("oneOf"))
//                .addOnEventListener((OnEventStartListener<Object[], String>) params -> print_i("----------获得广告----------"))
//                .addOnEventListener((OnEventNextListener<Object[], String>) result -> {
//                    if (result == null) {
//                        print_e("广告请求超时");
//                    } else {
//                        print_i(MessageFormat.format("广告 - {0}", result));
//                    }
//                })
//                .addOnChainListener(new OnEventRunningListener(mV_Loading, mV_Button))
//                .addOnChainListener(new OnChainLogListener("Demo"))
        ;*/
    }


    public void start(View v) {
        mDemoEvent.start(Utils.getUserName());
    }


    private static class LogView {
        private TextView mV_Info;
        private ScrollView mV_Scroll;

        public LogView(TextView mV_Info, ScrollView mV_Scroll) {
            this.mV_Info = mV_Info;
            this.mV_Scroll = mV_Scroll;
        }

        //清除
        private synchronized void print_clear() {
            ThreadHelper.main(() -> mV_Info.setText(""));
        }

        //追加消息
        private synchronized void print_i(String msg) {
//            EUtils.i("Event-----------------------", msg);
            ThreadHelper.main(() -> {
                mV_Info.append(msg + "\n");
                mV_Info.post(() -> mV_Scroll.fullScroll(ScrollView.FOCUS_DOWN));
            });
        }

        private synchronized void print_e(String msg) {
//            EUtils.i("Event-----------------------", msg);
            ThreadHelper.main(() -> {
                SpannableString spannableString = new SpannableString(msg + "\n");
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), 0);
                mV_Info.append(spannableString);
                mV_Info.post(() -> mV_Scroll.fullScroll(ScrollView.FOCUS_DOWN));
            });
        }

    }


}
