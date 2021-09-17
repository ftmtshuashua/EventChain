package com.acap.ec;

/**
 * <pre>
 * Tip:
 *      关系链，用来处理两个事件的链接关系
 *
 * Created by ACap on 2021/7/9 18:59
 * @author A·Cap
 * </pre>
 */
public final class Chain<P, R> extends Event<P, R> {

    ILinkable<P, ?> mFirst;
    ILinkable<Object, R> mLast;


    /**
     * 创建事件的关系链
     *
     * @param first 在链上位于链头的事件
     * @param last  在链上位于链尾的事件
     * @param <R1>  位于链头的事件的出参类型
     */
    <R1> Chain(ILinkable<P, R1> first, ILinkable<? super R1, R> last) {
        mFirst = first;
        mLast = (ILinkable<Object, R>) last;

        mFirst.onAttachedToChain(this);
        mLast.onAttachedToChain(this);
    }

    @Override
    protected void onCall(P params) {
        mFirst.start(params);
    }

    void performOnEventNext(ILinkable<?, ?> event, Object result) {
        if (mFirst == event) {
            mLast.start(result);
        } else {
            next((R) result);
        }
    }

    void performOnEventError(ILinkable<?, ?> event, Throwable throwable) {
        error(throwable);
    }

    /**
     * 生成两个事件的关系链
     *
     * @param first 在链上位于链头的事件
     * @param last  在链上位于链尾的事件
     * @param <P>   链头事件的入参类型
     * @param <R>   链尾事件的出参类型
     * @param <R1>  位于链头的事件的出参类型
     * @return 关系链
     */
    public static <P, R, R1> Chain<P, R> generate(ILinkable<P, R1> first, ILinkable<? super R1, R> last) {
        return new Chain<>(first, last);
    }


}
