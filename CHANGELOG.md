# 2025-03-10

版本: `3.7.1`, 更新日志:

- 修复复用时的滚动位置问题

# 2025-02-19

版本: `3.7.0`, 更新日志:

- 修复已知问题

# 2025-1-18

版本: `3.6.5`, 更新日志:

- 新增加粗字体配置

# 2024-11-26

版本: `3.6.4`, 更新日志:

- 修复已知问题

# 2024-11-11 

版本: `3.6.3`, 更新日志:

- 新增部分`api`

# 2024-11-5

版本: `3.6.2`, 更新日志:

- 修复布局时, 自动滚动问题

# 2024-10-25

版本: `3.6.1`, 更新日志:

- 更新`AGP`为`8.7.1`
- 更新`compileSdk`为`34`
- 更新`minSdk`为`21`
- 新增部分`api`

# 2023-7-21

版本: `3.5.5`, 更新日志:

- 修复渐变Drawable指示器绘制问题

# 2023-7-7

版本: `3.5.4`, 更新日志:

- 新增`layout_tab_text_view_index`属性
- 新增`layout_tab_icon_view_index`属性
- 新增`layout_tab_text_view_id`属性
- 新增`layout_tab_icon_view_id`属性
- 上述属性可以在每个不同的item上指定不同`id`对应的`TextView`和`ImageView`

# 2023-1-7

版本: `3.5.3`, 更新日志:

- 新增`tab_use_typeface_bold`属性, 支持使用粗体字体实现变粗效果

# 2022-12-9

版本: `3.5.2`, 更新日志:

- 修复child`wrap_content`测量的min size支持

# 2022-12-6

版本: `3.5.0`, 更新日志:

- 新增横向支持`wrap_content`测量模式

# 2022-11-24

版本: `3.4.2`, 更新日志:

- 调整`tab_indicator_style`属性
- 新增`tab_item_equ_width_count_range`属性, 支持`[3~5]`条件控制等宽
- 修复`indicatorGravity`无效的问题

# 2022-10-31

版本: `3.3.1`, 更新日志:

- `Border`支持禁用色设置
- `DslTabLayout`支持属性`itemEquWidthCount`, 当子元素大于此值时, 开启等宽模式
- 调整变粗属性调用的api
- 修复已知问题

# 2022-9-16

版本: `3.2.5`, 更新日志:

- 修复无法`reselect`的问题

# 2022-5-6

版本: `3.2.4`, 更新日志:

- 新增`observeIndexChange`api, 方便快速监听index的改变

# 2022-5-1

版本: `3.2.3`, 更新日志:

- 修复`indicatorContentId` `indicatorContentIndex`对样式不生效的问题

# 2022-4-25

版本: `3.2.2`, 更新日志:

- `ItemView`支持`CompoundButton`
- compileSdkVersion 变更为31

# 2022-2-28

版本: `3.2.1`, 更新日志:

* 修改函数签名`com.angcyo.tablayout.ViewPagerDelegate.onSetCurrentItem`
* `ViewPagerDelegate` minSdkVersion修成成17

# 2022-2-22

版本: `3.1.0`, 更新日志:

+ 新增`闪现效果`, 可以通过`tab_indicator_enable_flash`激活. `tab_indicator_enable_flash_clip`
  开启`clip`模式.

# 2022-1-18

版本: `3.0.8`, 更新日志:

+ `STYLE_FOREGROUND` `STYLE_FOREGROUND_TOP` `STYLE_FOREGROUND_BOTTOM` `STYLE_BACKGROUND`
  `STYLE_BACKGROUND_TOP` `STYLE_BACKGROUND_BOTTOM`
  更细粒度的指示器样式

版本: `3.0.7`, 更新日志:

+ `tab_indicator_content_id` `layout_tab_indicator_content_id` 用来指定指示器`WRAP_CONTENT`参考的控件id

# 2022-01-02

版本: `3.0.6`, 更新日志:

+ `ViewPagerDelegate`支持`forceSmoothScroll`参数

# 2021-12-21

版本: `3.0.4`, 更新日志:

- 修复部分场景下`text_max_size`属性导致布局异常的bug

# 2021-11-26

版本: `3.0.3`, 更新日志:

- 修复首次布局时`text_max_size`等属性导致布局异常的bug

# 2021-11-8

版本: `3.0.2`, 更新日志:

- 修复高度变化时, 滚动状态异常的bug

# 2021-10-14

版本: `3.0.1`, 更新日志:

- 新增`指示器颜色渐变效果`,功能开关`tabEnableIndicatorGradientColor`

# 2021-7-8

版本: `3.0.0`, 更新日志:

- 支持`RTL`. `minSdk>=17`

# 2021-5-19

版本: `2.2.0`, 更新日志:

- 新增`tabHighlight`, 选中后突出显示效果

# 2021-5-17

版本: `2.1.0`, 更新日志:

- 新增指示器属性`indicatorGravity`
    - INDICATOR_GRAVITY_START 指示器靠左显示
    - INDICATOR_GRAVITY_END 指示器靠右显示
    - INDICATOR_GRAVITY_CENTER 指示器居中显示

# 2021-4-28

版本: `2.0.3`, 更新日志:

- 修复`setCurrentItem`后, 无限循环的问题
- 新增属性`layoutScrollAnim`, 动态添加item时, 是否开启滚动动画
- 新增属性`scrollAnimDuration`滚动动画的时长

# 2021-4-6

版本: `2.0.2`, 更新日志:

- 修复`setCurrentItem`后, `Item`没有滚动居中的问题

# 2020-12-14

版本: `2.0.1`, 更新日志:

- 移除 未使用的类`DslDrawable`
- 调整 一些注释

# 2020-12-1

版本: `2.0.0`, 更新日志:

- 新增 支持`竖向`布局

# 2020-12-1

版本: `1.6.3`, 更新日志:

- 修复 切换至`等宽`模式下滚动位置未恢复的BUG

# 2020-10-19

版本: `1.6.2`, 更新日志:

- 新增 角标支持`badgeMinWidth`和`badgeMinHeight`属性

# 2020-9-28

版本: `1.6.1`, 更新日志:

- 修复`indicatorContentIndex`属性的支持

# 2020-9-25

版本: `1.6.0`, 更新日志:

- 支持`layout_tab_weight`, 剩余空间所占比例

# 2020-7-14

版本: `1.5.9`, 更新日志:

- `TabBadgeConfig`新增角标边框颜色和宽度设置
- 修复`removeAll`之后, 再次`addView`时的回调异常
- 支持状态恢复
- 圆点角标也支持边框

# 2020-06-26

版本: `1.5.5`, 更新日志:

- 新增xml属性`tab_text_view_id`和`tab_icon_view_id`, 可以快速指定效果生效控件

# 2020-6-4

版本: `1.5.4`, 更新日志:

- 新增`tab_item_auto_equ_width`属性.

智能判断Item是否等宽, 如果所有子项, 未撑满tab时, 开启等宽模式, 否则默认处理.
此属性会覆盖`tab_item_is_equ_width`属性

# 2020-04-24

版本: `1.5.3`, 更新日志:

- 修复快速切换`item`时,渐变状态异常的问题

# 2020-04-03

版本: `1.5.2`, 更新日志:

- 修复移除所有`item`之后,动态添加新`item`, 选中判断的样式问题.

# 2020-03-28

版本: `1.5.1`, 更新日志:

- 修复 `DslGravity` offset计算问题

# 2020-03-28

版本: `1.5.0`, 更新日志:

- `onSelectIndexChange` `onSelectViewChange` `onSelectItemView` 支持 `fromUser`
- `badge` 支持单独为`圆形状态`设置`offset_x` `offset_y`属性
- `badge` 定位`Gravity`支持定位锚点属性`tab_badge_anchor_child_index`
- `badge` 定位支持忽略锚点`padding`属性`tab_badge_ignore_child_padding`
- 调整`DslGravity`定位计算默认输出目标的中心坐标. 可以通过属性`gravityRelativeCenter`关闭.

# 2020-3-12

版本: `1.4.4`, 更新日志:

- `DslTabLayoutConfig`支持文本大小渐变属性配置.

可以通过:

```
tabLayout.configTabLayoutConfig {
    tabTextMinSize = 9 * dp
    tabTextMaxSize = 18 * dp
}
```

# 2020-02-29

> 特殊版本 用于不使用`AndroidX`的开发者.

> 未特殊说明 所有版本都将基于`AndroidX`开发 如果需要非`AndroidX`版本 请关注`-support`结尾的版本更新.

版本: `1.4.3-support` ,更新日志:

- 去除`AndroidX`依赖, 兼容`support`版本.

```groovy
    implementation 'com.github.angcyo.DslTabLayout:TabLayout:1.4.3-support'
```

# 2020-02-27

版本: `1.4.3` ,更新日志:

- 修复`child` `CENTER_VERTICAL` 垂直居中`Bottom`坐标计算问题

# 2020-02-22

版本: `1.4.2` ,更新日志:

新增库`Delegate`库:

```groovy
    implementation 'com.github.angcyo.DslTabLayout:ViewPager1Delegate:1.4.2'
implementation 'com.github.angcyo.DslTabLayout:ViewPager2Delegate:1.4.2'
```

原库的使用方式变成了:

```groovy
    //implementation 'com.github.angcyo:DslTabLayout:1.4.2' 之前
implementation 'com.github.angcyo.DslTabLayout:TabLayout:1.4.2'
```

# 2020-01-06

版本: `1.4.1` ,更新日志:

+ 新增指示器动画控制属性`tab_indicator_anim`

# 2019-12-16

版本: `1.4.0` ,更新日志:

1. 更友好的`Badge` 角标更新方法
2. 开发全属性角标更新

[WIKI详细说明](https://github.com/angcyo/DslTabLayout/wiki/%E8%A7%92%E6%A0%87)

版本: `1.3.1` ,更新日志:

1. 修复 `Badge` 角标在顶层绘制
2. 新增`Badge` 角标xml属性`tab_badge_text_size`, 角标字体大小配置

# 2019-12-14

版本: `1.3.0` ,更新日志:

1. 支持`ViewPager2`

> 库不依赖`ViewPager`和`ViewPager2`,通过`ViewPagerDelegate`转发事件.

[请查看WIKI使用说明](https://github.com/angcyo/DslTabLayout/wiki/ViewPager1%E5%92%8CViewPager2)

# 2019-12-13

版本: `1.2.0` ,更新日志:

1. 修复`child`设置`GONE`之后, `item`平分计算的不正确的问题
2. 修复开启`高凸模式`后, 指示器高度没有过渡的问题
3. 允许指定`文本渐变控件`, 和`图标渐变控件`
4. 修复`tab`切换的时候, 不再强制控制`ViewPager.setCurrentItem`
5. 新增`角标`功能
