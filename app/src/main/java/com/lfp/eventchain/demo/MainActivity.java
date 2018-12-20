package com.lfp.eventchain.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lfp.eventtree.EventChain;
import com.lfp.eventtree.OnEventListener;

public class MainActivity extends AppCompatActivity {

    TextView mTV_Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTV_Info = findViewById(R.id.view_Info);

    }


    private class TestEvent extends EventChain {
        String info;
        long delay = 300;
        boolean isAddTest;

        boolean isInterrupt = false; /*中断测试*/

        public TestEvent(String info) {
            this(info, false);
        }

        @Override
        public TestEvent setOnEventListener(OnEventListener l) {
            return (TestEvent) super.setOnEventListener(l);
        }

        public TestEvent(String info, boolean addtest) {
            this.info = info;
            isAddTest = addtest;

            delay += Math.random() * 1000;
        }

        @Override
        protected void call() {
//            Utils.log(info);
            mTV_Info.append("\n");
            mTV_Info.append(info + "   -   耗时：" + delay);
            ThreadHelper.io(
                    () -> {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                        }
                        ThreadHelper.main(() -> {
                            if (isAddTest) {
                                chain(new TestEvent("这是一个动态增加的事件"));
                                isAddTest = false;
                            }
                            if (isInterrupt) interrupt();
                            complete();
                        });
                    }
            );
        }

        @Override
        public String toString() {
            return info;
        }

        public EventChain setTestInterrupt(boolean is) {
            isInterrupt = is;
            return this;
        }
    }


    public void test1(View v) {
        mTV_Info.setText("简单链式调用");
        new TestEvent("链条 1")
                .chain(new TestEvent("链条 2"))
                .chain(new TestEvent("链条 3"))
                .chain(new TestEvent("链条 4"))
                .chain(new TestEvent("链条 5"))
                .start();
    }

    public void test2(View v) {
        mTV_Info.setText("Merge测试");
        new TestEvent("链条 1")
                .chain(new TestEvent("链条 2"))
                .chain(new TestEvent("链条 3"))
                .chain(new TestEvent("链条 4"))
                .chain(new TestEvent("链条 5"))
                .start();
    }

    public void test3(View v) {
        mTV_Info.setText("混合调用");


        EventChain.create(AllEvent,
                new TestEvent("链条事件开始执行")
                        .merge(new TestEvent("merge 1"), new TestEvent("merge 2", true))
                        .chain(
                                new TestEvent("chain 2").merge(new TestEvent("chain 2  -> merge 1 "), new TestEvent("chain 2  -> merge 2"))
                        )
                        .chain(true ? null : null)

                        .chain(new TestEvent("这是最后一个链条事件"))
        )
                .start();

    }

    OnEventListener AllEvent = new OnEventListener() {
        @Override
        public void onStart() {
            mTV_Info.append("\n");
            mTV_Info.append("监听器 -> 请求链开始执行...");
        }

        @Override
        public void onComplete() {
            mTV_Info.append("\n");
            mTV_Info.append("监听器 -> 请求链执行结束...");
        }
    };


    OnEventListener SingleEvent = new OnEventListener() {
        @Override
        public void onStart() {
            mTV_Info.append("\n");
            mTV_Info.append("监听器 -> 被监听事件 开始");
        }

        @Override
        public void onComplete() {
            mTV_Info.append("\n");
            mTV_Info.append("监听器 -> 被监听事件 结束");
        }
    };
}
