EventChain
=====
[![](https://jitpack.io/v/ftmtshuashua/EventChain.svg)](https://jitpack.io/#ftmtshuashua/EventChain)
[![](https://img.shields.io/badge/jdk-1.8%2B-blue)]()
[![License Apache2.0](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html)

EventChain是一个用于对复杂的业务场景进行解耦的库.将复杂的业务流程抽象和拆分为多个相互独立的业务流程,称之为Event.然后利用EventChain进行积木式搭建成完整的业务流程.

[![EventChain](https://github.com/ftmtshuashua/EventChain/blob/master/resouce/flow.png)]()



Download
-------
```
repositories {
    maven { url 'https://www.jitpack.io' }
}

dependencies {
    implementation 'com.github.ftmtshuashua:EventChain:2.0.2'
}
```

USE
-----

###### 创建业务事件并在之后执行
```
/**
* 创建一个事件,该事件的入参是一个整形数据,出参也是一个整形数据.
* 内部逻辑将入参平方后结束.
*/
class SimperEvent() : BaseEvent<Int, Int>() { 
    override fun onCall(params: Int) {
        next(params * params)
    }
}

//lesson 1 - 简单使用,并获取执行结果
SimperEvent()
    .listener(OnEventNextListener { println("result:$it") })	//result:4
    .start(2)
	
//lesson 2 - 将多个事件组合求值
SimperEvent()
    .listener(OnEventNextListener { println("result:$it") })	//result:4
    .chain(SimperEvent())
    .listener(OnEventNextListener { println("result:$it") })	//result:16
    .start(2)
```







