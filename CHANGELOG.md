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