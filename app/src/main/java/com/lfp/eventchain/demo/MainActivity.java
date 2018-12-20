package com.lfp.eventchain.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lfp.eventtree.EventChain;
import com.lfp.eventtree.EventChainObserver;
import com.lfp.eventtree.OnEventLisetener;

import java.text.MessageFormat;

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
        mTV_Info.setText(test);
        mTV_Info.append("\n");
    }

    public void test1(View v) {
        appendTop("事件链");
        EventChain event = new DemoEvent("Event 1")
                .chain(new DemoEvent("Event 2"))
                .chain(new DemoEvent("Event 3")).setOnEventLisetener(new EventLisenter("Event 3"))//OnEventLisetener只能监听一个事件
//                .chain(new DemoEvent("Event 3").setOnEventLisetener(SingleEvent)) //与上面一句等效
                .chain(new DemoEvent("Event 4"))
                .chain(new DemoEvent("Event 5"));
        event.addEventChainObserver(mEventObserver);//EventChainObserver监听整个链中所有事件
        event.start();
    }

    public void test2(View v) {
        appendTop("并发事件");
        EventChain event = EventChain.create(
                new DemoEvent("Event 1")
                , new DemoEvent("Event 2")
                , new DemoEvent("Event 3")
                , new DemoEvent("Event 4")
                , new DemoEvent("Event 5")
        );
        event.setOnEventLisetener(new EventLisenter("并发事件"));//OnEventLisetener只能监听一个事件
        event.start();
    }

    public void test3(View v) {
        StringBuffer sb = new StringBuffer("复杂业务场景模拟：")
                .append("\n").append("1.\'小明\'和\'666\'放学回家")
                .append("\n").append("2.\'小明\'骑车和\'666\'跑步到公园集合")
                .append("\n").append("3.偶遇\'小红\'，于是玩踢球游戏")
                .append("\n").append("4.概率事件");
        appendTop(sb.toString());


        EventChain chain = new DemoEvent("放学回家")
                .merge(new DemoEvent("小明骑车"), new DemoEvent("666跑步"))
                .chain(new DemoEvent("小明传球").chain(new DemoEvent("666接住并传给了小红")).chain(new DemoEvent("小红接住准备传给小明").setDynamic(true)))
                .merge(new DemoEvent("666见状赶紧溜了"), new DemoEvent("小红见状也溜了"))
                .chain(new DemoEvent("可怜的小明，终~"));


        chain.addEventChainObserver(mEventObserver);
        chain.start();
    }


    class EventLisenter implements OnEventLisetener {
        String info;

        public EventLisenter(String info) {
            this.info = info;
        }

        @Override
        public void onStart() {
            appendMsg(MessageFormat.format("{1} 监听器:{0}", "onStart()", info));
        }

        @Override
        public void onError(Throwable e) {
            appendMsg(MessageFormat.format("{1} 监听器:{0}", "onError()", info));
        }

        @Override
        public void onNext() {
            appendMsg(MessageFormat.format("{1} 监听器:{0}", "onNext()", info));
        }

        @Override
        public void onComplete() {
            appendMsg(MessageFormat.format("{1} 监听器:{0}", "onComplete()", info));
        }
    }



    EventChainObserver mEventObserver = new EventChainObserver() {
        @Override
        public void onChainStart() {
            appendMsg(MessageFormat.format("观察者:{0}", "onChainStart()"));
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
            appendMsg(MessageFormat.format("观察者:{0}", "onChainComplete()"));
            Utils.log("观察者 - onChainComplete");
        }
    };


    /*自定义的Demo事件*/
    private class DemoEvent extends EventChain {
        String info;
        long delay = 100; //默认事件执行时间
        boolean isDynamic; //是否增加动态事件添加功能
        boolean isInterrupt = false; //是否关闭事件链

        public DemoEvent(String info) {
            this.info = info;
            delay += Math.random() * 500;
        }

        @Override
        protected void call() {
            appendMsg(MessageFormat.format("{0}:开始", info));
            模拟线程事件处理();
        }

        private void 模拟线程事件处理() {
            ThreadHelper.io(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
                模拟线程时间处理结束();
            });
        }

        private void 模拟线程时间处理结束() {
            ThreadHelper.main(() -> {
                appendMsg(MessageFormat.format("{0}:结束  耗时：{1,number,0}ms", info, delay));
                模拟动态事件添加();
                if (isInterrupt) { //是否强制退出事件链
                    interrupt();
                } else {
                    next();
                }
            });
        }

        private void 模拟动态事件添加() {
            if (!isDynamic) return;
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


            isDynamic = false;
        }

        @Override
        public String toString() {
            return info;
        }

        /**
         * 将该节点设置为中断动作
         */
        public EventChain setTestInterrupt(boolean is) {
            isInterrupt = is;
            return this;
        }

        /*动态事件添加，模拟小明的概率事件*/
        public EventChain setDynamic(boolean isDynamic) {
            this.isDynamic = isDynamic;
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
