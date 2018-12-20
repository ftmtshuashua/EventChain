package com.lfp.eventtree;

/**
 * <pre>
 * Tip:
 *      监听整个链条
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/19 20:53
 * </pre>
 */
class EventCompound extends EventChain {
    EventChain chain;

    OnEventListener listener;


    public EventCompound(OnEventListener listener, EventChain... chain) {
        this.listener = listener;
        if (chain != null) {
            if (chain.length == 1) {
                this.chain = chain[0];
            } else {
                this.chain = new EventMerge(chain);
            }
        }
    }


    @Override
    public EventChain chain(EventChain chain) {
        if (this.chain == null) {
            this.chain = chain;
        } else {
            this.chain.chain(chain);
        }
        return this.chain;
    }

    @Override
    protected void call() {

    }


    /**
     * 提供一个只在内部使用的观察者
     */
    private static final class ObservableEventChain extends EventChain {

        @Override
        protected void call() {

        }
    }

}
