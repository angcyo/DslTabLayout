# DslTabLayout
:hearts: Android界最万能的`TabLayout`(不仅仅是`TabLayout`). 高能自绘控件, 继承自`ViewGroup`, 非组合控件.

[![](https://img.shields.io/badge/License-MIT-3A77AC)](https://github.com/angcyo/DslTabLayout/blob/master/LICENSE) [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14) [![](https://img.shields.io/badge/kotlin-yes-F48729)](https://www.kotlincn.net/docs/reference/android-overview.html)  [![](https://img.shields.io/badge/androidx-yes-80B44D)](https://developer.android.google.cn/jetpack/androidx)

# 特性

1. 支持任意类型的`child`视图.
2. 支持任意类型`Drawable`的`指示器`.
3. 支持智能开启`横向滚动`.
4. 支持智能开启`平分item宽度`(`1.5.4`后).
5. 支持`高凸模式`, 允许某个`child`高出一节显示.
6. 支持`滑动选择模式`, 滑动的时候智能选择`上一个`或`下一个`.
7. 支持`角标`配置.
8. 支持`ViewPager`和`ViewPager2`

直接当做横向的`LinearLayout`使用方式即可, 无特殊要求.

[入门使用](https://github.com/angcyo/DslTabLayout/wiki/%E5%85%A5%E9%97%A8%E4%BD%BF%E7%94%A8)

[点击查看全部属性](https://github.com/angcyo/DslTabLayout/wiki/%E5%B1%9E%E6%80%A7%E5%A4%A7%E5%85%A8)

[点击查看事件回调](https://github.com/angcyo/DslTabLayout/wiki/Item%E9%80%89%E4%B8%AD%E4%BA%8B%E4%BB%B6)

[点击查看角标使用](https://github.com/angcyo/DslTabLayout/wiki/%E8%A7%92%E6%A0%87)

[关联ViewPager使用(兼容ViewPager2)](https://github.com/angcyo/DslTabLayout/wiki/ViewPager1%E5%92%8CViewPager2)

# 效果

**一个`DSLTabLayout`可实现以下3中效果**

高能自绘控件, 继承自`ViewGroup`, 非组合控件.

无需选择, `xml配置`即可, 还不赶紧收下?

|带滚动效果和角标|
|:--:|
|![](https://gitee.com/angcyo/DslTabLayout/raw/master/png/tab_sliding.gif)|

|普通和高凸效果|
|:--:|
|![](https://gitee.com/angcyo/DslTabLayout/raw/master/png/tab_common.gif)|

|带边框,分割线和滑动选择效果|
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
    implementation 'com.github.angcyo.DslTablayout:TabLayout:1.5.4'

    //可选
    implementation 'com.github.angcyo.DslTablayout:ViewPager1Delegate:1.5.4'
    //可选
    implementation 'com.github.angcyo.DslTablayout:ViewPager2Delegate:1.5.4'
}
```

[更新日志](https://github.com/angcyo/DslTabLayout/blob/master/CHANGELOG.md)

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
