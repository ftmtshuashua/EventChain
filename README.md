EventChain
=====
[![](https://jitpack.io/v/ftmtshuashua/EventChain.svg)](https://jitpack.io/#ftmtshuashua/EventChain)
[![](https://img.shields.io/badge/android-1.0%2B-blue)]
[![License Apache2.0](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html)

EventChain是一个用于对复杂的业务场景进行解耦的库.将复杂的业务流程抽象和拆分为多个相互独立的业务流程,称之为Event.然后利用EventChain进行积木式搭建成完整的业务流程.

[![EventChain](https://github.com/ftmtshuashua/EventChain/blob/master/resouce/flow.png)]

将业务逻辑拆分成多个原子事件事件，通过链的方式将这些事件事件拼接在一起来完成该业务逻辑。
也可以将多个业务逻辑通过链的方式排列组合来达到想要的结果。

Download
--------


强大的业务流程处理器基础组件，通过拓展它来实现复杂业务逻辑的流程化处理.
- 源生Java实现
- 链式事件处理
- 支持事件并发
- 支持事件动态插入
- 增加事件间的数据传递




## 使用
将我们的业务封装为一个<kbd>EventChain</kbd>,实现<kbd>onCall()</kbd>方法.并在<kbd>onCall()</kbd>中处理自己的业务.
> 每一个事件都有自己的入参和出参，根据需求指定他们.

- <kbd>next()</kbd>:当前事件处理完成向下传递信号
- <kbd>error()</kbd>:当前事件处理失败，抛出异常信息
- <kbd>finish()</kbd>:停止链上的事件传递,事件信号将不会向下传递,但是会调用事件和链的完成回调函数



## 配置依赖


```
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}

android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation 'com.github.ftmtshuashua:EventChain:version'
}
```
