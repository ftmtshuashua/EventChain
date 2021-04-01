

<div align="center">


![EventChain](https://github.com/ftmtshuashua/EventChain/blob/master/resouce/flow.png)

</div>

<h1 align="center">EventChain</h1>
<div align="center">

[![](https://jitpack.io/v/ftmtshuashua/EventChain.svg)](https://jitpack.io/#ftmtshuashua/EventChain)

</div>

将业务逻辑拆分成多个原子事件节点，通过链的方式将这些事件节点拼接在一起来完成该业务逻辑。
也可以将多个业务逻辑通过链的方式排列组合来达到想要的结果。


#
强大的业务流程处理器基础组件，通过拓展它来实现复杂业务逻辑的流程化处理.
- 源生Java实现
- 链式事件处理
- 支持事件并发
- 支持事件动态插入
- 增加事件间的数据传递




## 使用
将我们的业务封装为一个<kbd>EventChain</kbd>,实现[onCall()]()方法.并在[onCall()]()中处理自己的业务.
>每一个事件都有自己的入参的出参，根据需求指定他们.

- [next()]():当前事件处理完成向下传递信号
- [error()]():当前事件处理失败，抛出异常信息
- [result()]():抛出事件的结果，但是不发送向下传递信号
- [stop()]():停止链上事件,事件信号将不会向下传递



## 配置依赖


```
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```
在Model的build.gradle中添加
```
dependencies {
    implementation 'com.github.ftmtshuashua:EventChain:version'
}
```
