

<h1 align="center">EventChain</h1>
<div align="center">

[![](https://jitpack.io/v/ftmtshuashua/EventChain.svg)](https://jitpack.io/#ftmtshuashua/EventChain)

</div>

将业务逻辑拆分成多个原子事件节点，通过链的方式将这些事件节点拼接在一起来完成该业务逻辑。
也可以将多个业务逻辑通过链的方式排列组合来达到想要的结果。


#
强大的业务流程处理器基础组件，通过拓展它来实现复杂业务逻辑的流程化处理.

>链式事件处理

>支持事件并发

>支持事件动态修改

>源生Java实现


![EventChain](https://github.com/ftmtshuashua/EventChain/blob/master/resouce/flow.png)


## 使用
将我们的业务封装为一个<kbd>EventChain</kbd>,实现[call()]()方法.并在[call()]()中处理自己的业务.

1.当业务处理完成请显示调用[next()]()或者[error()]()方法告诉事件链该事件处理完成

2.[interrupt()]()方法被调用之后的所有回调都不会再发送

3.[complete()]()方法被调用之后将立即完成当前正在执行的事件,并完成整个事件链


## 配置依赖

在项目的build.gradle中添加
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


## LICENSE

```
Copyright (c) 2018-present, EventChain Contributors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




