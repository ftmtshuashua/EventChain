package com.acap.ec.interior;

/**
 * <pre>
 * Tip:
 *
 *
 * @param <P> params,上游节点完成之后的产物
 * @param <R> result,当前节点的产物
 *
 * Created by ACap on 2021/3/29 18:12
 * </pre>
 */
public interface IChainNode<P, R> {

    /**
     * 停止链上的事件传递
     */
    void stop();

    /**
     * 如果当前节点发生错误,则通过error方法向后传递
     *
     * @param throwable 节点中发生的错误
     */
    void error(Throwable throwable);


    /**
     * 当前节点的逻辑处理完成之后,将节点的产物向后传递，交给下一节点来处理
     *
     * @param result 当前节点的产物
     */
    void next(R result);

    /**
     * 当前节点被执行的时候回调，节点自己的逻辑在该方法中开始执行
     *
     * @param params 来自上游节点的产物
     */
    void onCall(P params);

    /**
     * 从链头开始执行链上的逻辑
     */
    void start();


    /**
     * 在节点后面连接另一个节点
     *
     * @param node 节点对象
     */
    <T extends IChainNode<R, ?>> T chain(T node);

}
