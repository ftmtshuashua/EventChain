package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      合并数据并发请求
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/20 16:45
 * </pre>
 */
public class EventMerge extends EventChain {
    EventChain[] mMerge;

    public EventMerge(EventChain... chains) {
        if (chains != null) {
            this.mMerge = new EventChain[chains.length];
            for (int i = 0; i < chains.length; i++) {
                this.mMerge[i] = chains[i].getLast();

            }
        }
    }

    @Override
    protected void call() throws Throwable {

    }

    private static final class ChianObserver implements EventChianObserver {

        @Override
        public void onChainStart() {

        }

        @Override
        public void onStart(EventChain event) {

        }

        @Override
        public void onError(EventChain event, Throwable e) {

        }

        @Override
        public void onNext(EventChain event) {

        }

        @Override
        public void onChainComplete() {

        }
    }

}
