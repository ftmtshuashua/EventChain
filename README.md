# EventChain
强大的业务流程处理器基础组件，通过拓展它来实现复杂业务逻辑的流程化处理.

>链式事件处理

>支持事件并发

>支持事件动态修改

>源生Java实现

## 配置依赖

在项目的build.gradle中添加
```
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```
在Model的build.gradle中添加 [![](https://jitpack.io/v/ftmtshuashua/EventChain.svg)](https://jitpack.io/#ftmtshuashua/EventChain)
```
dependencies {
    implementation 'com.github.ftmtshuashua:EventChain:version'
}
```


## 组件的拓展
1.覆写[call()]()方法实现业务逻辑

2.如果业务执行完成并且没有出错执行[next()]()方法进行后续流程

3.如果业务执行错误请调用[error(Throwable e)]()方法抛出异常，并且结束事件链

* 通常以上方法就能构建一个业务处理链
* [complete()]()和[interrupt()]()方法都能中断链条，[complete()]()会回调complete()方法，[interrupt()]()之后的左右事件都不会被调用



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




