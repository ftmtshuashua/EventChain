package com.acap.ec;

/**
 * <pre>
 * Tip:
 *      链
 *
 * Created by ACap on 2021/7/9 18:59
 * </pre>
 */
public final class Chain<P, R> extends Event<P, R> {

    private ILinkable mFirst;
    private ILinkable mLast;

    /**
     * @param first
     * @param last
     * @param <R1>
     */
    <R1> Chain(ILinkable<P, R1> first, ILinkable<? super R1, R> last) {
        mFirst = first;
        mLast = last;

        mFirst.onAttachedToChain(this);
        mLast.onAttachedToChain(this);
    }

    @Override
    protected void onCall(P params) {
        mFirst.start(params);
    }

    void performOnEventNext(ILinkable event, Object result) {
        if (mFirst == event) {
            mLast.start(result);
        } else {
            next((R) result);
        }
    }

    void performOnEventError(ILinkable event, Throwable throwable) {
        error(throwable);
    }

    /**
     * 生成链对象
     *
     * @param first
     * @param last
     * @param <P>
     * @param <R>
     * @param <R1>
     * @return
     */
    public static final <P, R, R1> Chain<P, R> generate(ILinkable<P, R1> first, ILinkable<? super R1, R> last) {
        return new Chain(first, last);
    }


}
