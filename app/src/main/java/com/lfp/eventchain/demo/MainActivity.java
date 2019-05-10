package com.lfp.eventchain.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lfp.eventtree.EventChain;
import com.lfp.eventtree.EventChainObserver;
import com.lfp.eventtree.OnEventListener;

import java.text.MessageFormat;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView mTV_Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTV_Info = findViewById(R.id.view_Info);
    }

    /*显示日志*/
    private void appendMsg(String test) {
        mTV_Info.append("\n");
        mTV_Info.append(test);
    }

    private void appendTop(String test) {
        cancel(null);
        mTV_Info.setText(test);
        mTV_Info.append("\n");
    }

    EventChain mChain;

    public void cancel(View v) {
        Utils.log("操作 - 中断链");
        appendMsg("操作 - 中断链");
        if (mChain != null) mChain.interrupt();
    }

    public void complete(View v) {
        Utils.log("操作 - 完成链");
        appendMsg("操作 - 完成链");
        if (mChain != null) mChain.complete();
    }


    public void test1(View v) {
        appendTop("事件链");
        mChain = new DemoEvent("E 1")
                .chain(new DemoEvent("E 2"))
                .chain(new DemoEvent("E 3"))
                .chain(new DemoEvent("E 4"))
                .chainDelay(() -> {
                    appendMsg("------- 正在创建事件 E6 -------");
                    return new DemoEvent("E 6");
                })
                .chain(new DemoEvent("E 5"));
        mChain.addEventChainObserver(mEventObserver);//用于监听整个链条的事件
        mChain.start();
    }

    public void test2(View v) {
        appendTop("并发事件");
        mChain = EventChain.create(
                new DemoEvent("E 1")
                , new DemoEvent("E 2")
                , new DemoEvent("E 3")
                , new DemoEvent("E 4")
                , new DemoEvent("E 5")
        );
        mChain.addOnEventListener(new EventLisenter("并发事件")).start();
    }

    public void test3(View v) {
        StringBuffer sb = new StringBuffer("复杂业务场景模拟：")
                .append("\n").append("1.\'小明\'和\'666\'放学回家")
                .append("\n").append("2.\'小明\'骑车和\'666\'跑步到公园集合")
                .append("\n").append("3.偶遇\'小红\'，于是玩踢球游戏")
                .append("\n").append("4.概率事件");
        appendTop(sb.toString());


        mChain = DemoEvent.create(
                new DemoEvent("放学回家")
                        .merge(
                                new DemoEvent("小明骑车")
                                , new DemoEvent("666跑步")
                        ).chain(
                        new DemoEvent("小明传球")
                                .chain(new DemoEvent("666接住并传给了小红"))
                                .chain(new DemoEvent("小红接住准备传给小明").setTestDynamic(true))
                ).merge(
                        new DemoEvent("666见状赶紧溜了")
                        , new DemoEvent("小红见状也溜了")
                ).chain(
                        new DemoEvent("可怜的小明，终~")
                )
        );


        mChain.addEventChainObserver(mEventObserver);
        mChain.start();
    }


    class EventLisenter implements OnEventListener {
        String info;

        public EventLisenter(String info) {
            this.info = info;
        }

        @Override
        public void onStart() {
            log("onStart()");
        }

        @Override
        public void onError(Throwable e) {
            log("onError()");
        }

        @Override
        public void onNext() {
            log("onNext()");
        }

        @Override
        public void onComplete() {
            log("onComplete()");
        }

        void log(String str) {
            appendMsg(MessageFormat.format("{0} 监听器:{1}", info, str));
        }
    }

    EventChainObserver mEventObserver = new EventChainObserver() {
        @Override
        public void onChainStart() {
            appendMsg("事件链 - 开始");
            Utils.log("观察者 - onChainStart");
        }

        @Override
        public void onStart(EventChain event) {
            Utils.log("观察者 - onStart:" + event);
        }

        @Override
        public void onError(EventChain event, Throwable e) {
            Utils.log("观察者 - onError:" + event);
        }

        @Override
        public void onNext(EventChain event) {
            Utils.log("观察者 - onNext:" + event);
        }

        @Override
        public void onChainComplete() {
            appendMsg("事件链 - 结束");
            Utils.log("观察者 - onChainComplete");
        }
    };


    /*自定义的Demo事件*/
    private class DemoEvent extends EventChain {
        String info;
        long delay; //默认事件执行时间

        boolean testDynamic; //是否增加动态事件添加功能
        boolean testInterrupt = false; //是否关闭事件链

        public DemoEvent(String info) {
            this.info = info;
            delay += Math.random() * 500;
            addOnEventListener(new EventLisenter(info));
        }

        @Override
        protected void call() {
            appendMsg(MessageFormat.format("{0}:logic 开始", info));
            ThreadHelper.io(() -> {
                try {
                    Thread.sleep(delay);

                    ThreadHelper.main(() -> {
                        模拟动态事件添加();
                        //是否强制退出事件链
                        appendMsg(MessageFormat.format("{0}:logic 结束  耗时：{1,number,0}ms", info, delay));
                        if (testInterrupt) interrupt();
                        next();
                    });

                } catch (InterruptedException e) {
                }
            });

        }

        private void 模拟动态事件添加() {
            if (!testDynamic) return;
            switch ((int) (Math.random() * 3)) {
                case 0:
                    模拟动态事件_小明妈妈来了();
                    break;
                case 1:
                    模拟动态事件_小明掉粪坑();
                    break;
                default:
                    模拟动态事件_小明踢裤裆上了();
                    break;
            }


            testDynamic = false;
        }

        @Override
        public String toString() {
            return info;
        }

        /**
         * 将该节点设置为中断动作
         */
        public EventChain setTestInterrupt(boolean is) {
            testInterrupt = is;
            return this;
        }

        /*动态事件添加，模拟小明的概率事件*/
        public EventChain setTestDynamic(boolean isDynamic) {
            this.testDynamic = isDynamic;
            return this;
        }


        private void 模拟动态事件_小明妈妈来了() {
            merge(new DemoEvent("这时小明妈妈来了"), new DemoEvent("小明姥姥也来了"))
                    .chain(new DemoEvent("她们上前一人扇一巴掌"));
        }

        private void 模拟动态事件_小明掉粪坑() {
            chain(new DemoEvent("这时小明脚滑掉粪坑了"))
            ;
        }

        private void 模拟动态事件_小明踢裤裆上了() {
            chain(new DemoEvent("传球"))
                    .chain(new DemoEvent("啊~~ 小明蛋碎了"))
            ;
        }
    }
}
