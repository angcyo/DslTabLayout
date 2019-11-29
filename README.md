# DslTabLayout
:hearts: Android界最万能的`TabLayout`(不仅仅是`TabLayout`)

[![](https://img.shields.io/badge/License-MIT-3A77AC)](https://github.com/angcyo/DslTabLayout/blob/master/LICENSE) [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14) [![](https://img.shields.io/badge/kotlin-yes-F48729)](https://www.kotlincn.net/docs/reference/android-overview.html)  [![](https://img.shields.io/badge/androidx-yes-80B44D)](https://developer.android.google.cn/jetpack/androidx)

# 特性

1. 支持任意类型的`child`视图.
2. 支持任意类型`Drawable`的`指示器`.
3. 支持智能开启横向`滚动`.
4. 支持`高凸模式`, 允许某个`child`高出一节显示.

# 效果

**一个`DSLTabLayout`可实现以下3中效果**

无需选择, `xml配置`即可, 还不赶紧收下?

|带滚动效果|
|:--:|
|![](https://gitee.com/angcyo/DslTabLayout/raw/master/png/tab_sliding.gif)|

|普通效果|
|:--:|
|![](https://gitee.com/angcyo/DslTabLayout/raw/master/png/tab_common.gif)|

|带边框和分割线效果|
|:--:|
|![](https://gitee.com/angcyo/DslTabLayout/raw/master/png/tab_segment.gif)|


# 使用`JitPack`的方式, 引入库.

## 根目录中的 `build.gradle`

```kotlin
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## APP目录中的 `build.gradle`

```kotlin
dependencies {
    implementation 'com.github.angcyo:DslTabLayout:1.0.0'
}
```

# 下载体验

扫码安装

![](https://gitee.com/angcyo/DslTabLayout/raw/master/png/qrcode.png)

---
**群内有`各(pian)种(ni)各(jin)样(qun)`的大佬,等你来撩.**

# 联系作者

[点此QQ对话](http://wpa.qq.com/msgrd?v=3&uin=664738095&site=qq&menu=yes)  `该死的空格`    [点此快速加群](https://shang.qq.com/wpa/qunwpa?idkey=cbcf9a42faf2fe730b51004d33ac70863617e6999fce7daf43231f3cf2997460)

[开源地址](https://github.com/angcyo/DslTabLayout)

![](https://gitee.com/angcyo/res/raw/master/code/all_in1.jpg)

![](https://gitee.com/angcyo/res/raw/master/code/all_in2.jpg)