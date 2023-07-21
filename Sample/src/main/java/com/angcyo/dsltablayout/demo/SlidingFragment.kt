package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.dp
import com.angcyo.dsladapter.dpi
import com.angcyo.dsltablayout.demo.dslitem.DslSlidingTabLayoutItem
import com.angcyo.tablayout.DslTabIndicator
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.isHorizontal

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
class SlidingFragment : BaseTabLayoutFragment() {

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)

        renderAdapter {

            //指示器在顶部
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 4 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            //动态改变文本的颜色
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        val tabLayout = this
                        addTabLayout(this)
                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = -2
                        } else {
                            tabIndicator.indicatorWidth = 3 * dpi
                            tabIndicator.indicatorHeight = -2
                        }
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP

                        //动态切换[tabSelectColor] [tabDeselectColor] 属性
                        configTabLayoutConfig {
                            onSelectIndexChange =
                                { fromIndex, selectIndexList, reselect, fromUser ->
                                    val toIndex = selectIndexList.first()
                                    if (toIndex < 3) {
                                        tabSelectColor = Color.WHITE
                                        tabDeselectColor = Color.parseColor("#999999")
                                    } else {
                                        tabSelectColor = Color.RED
                                        tabDeselectColor = Color.YELLOW
                                    }
                                    tabLayout.dslSelector.updateStyle()
                                }
                        }
                    }
                }
            }

            //指示器在顶部, 动态调整指示器的颜色
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)

                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = -1
                            tabIndicator.indicatorHeight = 4 * dpi
                        } else {
                            tabIndicator.indicatorWidth = 4 * dpi
                            tabIndicator.indicatorHeight = -1
                        }

                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP

                        configTabLayoutConfig {
                            tabTextMinSize = 9 * dp
                            tabTextMaxSize = 18 * dp
                        }

                        configTabLayoutConfig {
                            onSelectIndexChange =
                                { fromIndex, selectIndexList, reselect, fromUser ->
                                    tabIndicator.indicatorColor = randomColor()
                                    _viewPagerDelegate?.onSetCurrentItem(
                                        fromIndex,
                                        selectIndexList.last(),
                                        reselect, fromUser
                                    )
                                }
                        }
                    }
                }
            }

            //指示器在底部
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 10 * dpi
                        tabIndicator.indicatorHeight = 10 * dpi
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.ic_triangle)
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = -2
                        } else {
                            tabIndicator.indicatorWidth = 3 * dpi
                            tabIndicator.indicatorHeight = -2
                        }
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)

                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = -1
                            tabIndicator.indicatorHeight = 4 * dpi
                        } else {
                            tabIndicator.indicatorWidth = 4 * dpi
                            tabIndicator.indicatorHeight = -1
                        }

                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            //闪现效果
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable = getDrawable(R.drawable.indicator_flash)
                        tabIndicator.indicatorEnableFlash = true
                        tabIndicator.indicatorEnableFlashClip = true
                        tabIndicator.indicatorWidth = 28 * dpi
                        tabIndicator.indicatorHeight = 5 * dpi
                    }
                }
            }

            //指示器在背部
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.indicator_round_background_tran)
                        tabIndicator.indicatorHeight = -2
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorWidthOffset = 20 * dpi
                        tabIndicator.indicatorHeightOffset = -15 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_CENTER
                    }
                }
            }
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.ic_love)
                        tabIndicator.indicatorWidth = 40 * dpi
                        tabIndicator.indicatorHeight = 40 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_CENTER
                    }
                }
            }

            //其他特性
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)

                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = 30 * dpi
                            tabIndicator.indicatorHeight = 6 * dpi
                        } else {
                            tabIndicator.indicatorWidth = 6 * dpi
                            tabIndicator.indicatorHeight = 30 * dpi
                        }

                        tabIndicator.indicatorYOffset = 20 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        configTabLayoutConfig {
                            tabEnableGradientScale = true
                            tabEnableGradientColor = true
                            tabEnableIndicatorGradientColor = true
                            tabSelectColor = resources.getColor(R.color.colorAccent)
                            tabDeselectColor = resources.getColor(R.color.colorPrimaryDark)

                            //动态设置指示器的颜色和文本的颜色
                            onSelectIndexChange =
                                { fromIndex, selectIndexList, reselect, fromUser ->
                                    tabSelectColor = getRandomColor(selectIndexList.last())
                                    tabIndicator.indicatorColor = tabSelectColor
                                }

                            onGetGradientIndicatorColor = { fromIndex, toIndex, positionOffset ->
                                getRandomColor(toIndex)
                            }
                        }
                    }
                }
            }

            //渐变指示器
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                        tabIndicator.indicatorDrawable = getDrawable(R.drawable.indicator_gradient)
                    }
                }
            }

            //角标配置
            slidingItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)

                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = 10 * dpi
                        } else {
                            tabIndicator.indicatorWidth = 3 * dpi
                            tabIndicator.indicatorHeight = 10 * dpi
                        }

                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                        tabIndicator.indicatorColor = randomColor()

                        setTabLayoutConfig {
                            tabEnableGradientScale = true
                            tabEnableGradientColor = true
                            tabEnableTextBold = true
                        }

                        //角标
                        onTabBadgeConfig = { child, tabBadge, index ->
                            tabBadge.dslGravity.gravityRelativeCenter = false
                            tabBadge.badgeOffsetX = 10 * dpi
                            tabBadge.badgeOffsetY = 4 * dpi
                            tabBadge.gradientStrokeColor = 0
                            tabBadge.gradientStrokeWidth = 0
                            when (index) {
                                1 -> {
                                    tabBadge.badgeGravity = Gravity.LEFT or Gravity.TOP
                                    tabBadge.badgeText = "9"
                                    tabBadge.badgeOffsetX = 10 * dpi
                                    tabBadge.badgeOffsetY = 4 * dpi
                                }

                                2 -> {
                                    tabBadge.badgeGravity = Gravity.RIGHT or Gravity.TOP
                                    tabBadge.badgeText = "99"
                                }

                                3 -> {
                                    tabBadge.badgeGravity = Gravity.RIGHT or Gravity.BOTTOM
                                    tabBadge.badgeText = "99+"
                                    tabBadge.gradientStrokeColor = Color.BLACK
                                    tabBadge.gradientStrokeWidth = 2 * dpi
                                }

                                4 -> {
                                    tabBadge.badgeGravity = Gravity.LEFT or Gravity.BOTTOM
                                    tabBadge.badgeText = "999+"
                                }

                                5 -> {
                                    tabBadge.badgeCircleOffsetX = 14 * dpi
                                    tabBadge.badgeCircleOffsetY = 6 * dpi
                                    tabBadge.badgeText = ""
                                    tabBadge.badgeGravity = Gravity.LEFT or Gravity.TOP
                                    tabBadge.gradientStrokeColor = Color.BLUE
                                    tabBadge.gradientStrokeWidth = 2 * dpi
                                }

                                else -> {
                                    tabBadge.badgeGravity = Gravity.CENTER
                                    tabBadge.badgeText = ""
                                    tabBadge.badgeOffsetX = child.measuredWidth / 2 - 20 * dpi
                                    tabBadge.badgeOffsetY = -child.measuredHeight / 2 + 12 * dpi
                                }
                            }

                            tabBadge.gradientSolidColor = when (index) {
                                1 -> Color.BLUE
                                2 -> Color.GREEN
                                3 -> Color.RED
                                4 -> Color.RED
                                else -> if (tabBadge.gradientSolidColor == -1) randomColor() else tabBadge.gradientSolidColor
                            }
                            tabBadge.updateOriginDrawable()
                            null
                        }
                    }
                }
            }

            //
            pageItem()
        }
    }

    fun DslAdapter.slidingItem(init: DslSlidingTabLayoutItem.() -> Unit) {
        DslSlidingTabLayoutItem()() {
            if (orientation == LinearLayout.VERTICAL) {
                itemLayoutId = R.layout.item_sliding_vertical_tab_layout
            }
            init()
        }
    }
}