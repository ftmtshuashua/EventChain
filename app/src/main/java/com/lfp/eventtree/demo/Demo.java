package com.lfp.eventtree.demo;

import androidx.annotation.NonNull;

import com.lfp.eventtree.EventChain;
import com.lfp.eventtree.EventChainObserver;
import com.lfp.eventtree.OnEventListener;

/**
 * <pre>
 * Tip:
 *
 * Function:
 *
 * Created by LiFuPing on 2019/7/16 15:59
 * </pre>
 */
public class Demo {


    /*简单使用*/
    public static void main2(String[] args) {
        final DemoEvent e_1 = new DemoEvent("小明：小王,开车了,快上车!");
        final DemoEvent e_2 = new DemoEvent("小王：滴学生卡!");
        final DemoEvent e_3 = new DemoEvent("小明：坐稳,出发了!");

        e_1.chain(e_2).chain(e_3).start(); /*将事件串起来，并顺序执行*/

        /*或者*/
        new DemoEvent("小明：小王,开车了,快上车!")
                .chain(new DemoEvent("小王：滴学生卡!"))
                .chain(new DemoEvent("小明：坐稳,出发了!"))
                .start();
    }

    /*并行事件*/
    public static void main3(String[] args) {
        new DemoEvent("小明：开车了,快上车!")
                .merge( /*并行事件 , 由于都在同一线程执行，所以打印出来的效果看起来像的串行*/
                        new DemoEvent("小王：滴学生卡!"),
                        new DemoEvent("小红：滴学霸卡!")
                      )
                .chain(new DemoEvent("小明：坐稳,出发了!"))
                .start();
    }

    /*监听器*/
    public static void main4(String[] args) {
        new DemoEvent("小明：开车了,快上车!")
                .merge( /*并行事件 , 由于都在同一线程执行，所以打印出来的效果看起来像的串行*/
                        new DemoEvent("小王：滴学生卡!"),
                        new DemoEvent("小红：滴学霸卡!")
                      )
                .chain(new DemoEvent("小明：坐稳,出发了!"))
                .start();
    }

    /*监听器2*/
    public static void main5(String[] args) {
        EventChain.create(
                new DemoEvent("小明：开车了,快上车!")
                        .merge( /*并行事件 , 由于都在同一线程执行，所以打印出来的效果看起来像的串行*/
                                new DemoEvent("小王：滴学生卡!"),
                                new DemoEvent("小红：滴学霸卡!")
                              )
                        .chain(new DemoEvent("小明：坐稳,出发了!"))
                         )
                .addOnEventListener(new OnDemoEventLisenter())
                .start();
    }

    /*监听器3*/
    public static void main6(String[] args) {
        new DemoEvent("小明：开车了,快上车!")
                .merge( /*并行事件 , 由于都在同一线程执行，所以打印出来的效果看起来像的串行*/
                        new DemoEvent("小王：滴学生卡!"),
                        new DemoEvent("小红：滴学霸卡!")
                      )
                .chain(new DemoEvent("小明：坐稳,出发了!"))
                .addEventChainObserver(new DemoEventChainObserver())
                .start();
    }

    /*interrupt / complete*/
    public static void main(String[] args) {
        new DemoEvent("小明：开车了,快上车!")
                .merge( /*并行事件 , 由于都在同一线程执行，所以打印出来的效果看起来像的串行*/
                        new DemoEvent("小王：滴学生卡!"),
                        new DemoEvent("小红：滴学霸卡!")
                      )
                .chain(new DemoEvent("小明：坐稳,出发了!"))
                .addEventChainObserver(new DemoEventChainObserver())
                .start();
    }

    public static class DemoEvent extends EventChain {
        String msg;

        public DemoEvent(String msg) {
            this.msg = msg;
        }

        @Override
        protected void call() throws Throwable { /*执行具体的业务逻辑*/
//            error(new RuntimeException("抛出错误信息,抛出错误信息之后会中断后续事件"));
            System.out.println(msg);

            interrupt();//如果同时使用next()方法，interrupt()必须放在next()之前
//            complete();//如果同时使用next()方法，complete()必须放在next()之前
            //next(); /*事件执行成功之后，继续下一个事件*/
        }

        @NonNull
        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " - msg:[" + msg+"]";
        }
    }

    /*事件监听*/
    private static class OnDemoEventLisenter implements OnEventListener {

        @Override
        public void onStart() {
            System.err.println("onStart - 事件开始");
        }

        @Override
        public void onError(Throwable e) {
            System.err.println("onError - 出错了：" + e);
        }

        @Override
        public void onNext() {
            System.err.println("onNext - 事件 完成");
        }

        @Override
        public void onComplete() {
            System.err.println("onComplete - 事件链 完成");
        }
    }

    /*事件链监听*/
    private static class DemoEventChainObserver implements EventChainObserver {

        @Override
        public void onChainStart() {
            System.err.println("onChainStart - 事件链开始");
        }

        @Override
        public void onStart(EventChain event) {
            System.err.println("onStart - 子事件开始:" + event);
        }

        @Override
        public void onError(EventChain event, Throwable e) {
            System.err.println("onError - 子事件开始异常:" + event + "  Throwable:" + e);
        }

        @Override
        public void onNext(EventChain event) {
            System.err.println("onNext - 子事件完成:" + event);
        }

        @Override
        public void onChainComplete() {
            System.err.println("onChainComplete - 事件链 完成");
        }
    }

}


/*
*
            error(new RuntimeException("主动抛出异常，并中断事件链!"));
            complete();
            interrupt();
*/