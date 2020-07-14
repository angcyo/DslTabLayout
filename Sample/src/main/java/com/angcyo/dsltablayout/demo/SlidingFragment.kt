package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.dp
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabIndicator
import com.angcyo.tablayout.DslTabLayout

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
            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 4 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        val tabLayout = this
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
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

            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -1
                        tabIndicator.indicatorHeight = 4 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP

                        configTabLayoutConfig {
                            tabTextMinSize = 9 * dp
                            tabTextMaxSize = 18 * dp
                        }
                    }
                }
            }

            //指示器在底部
            DslSlidingTabLayoutItem()() {
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

            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

//            DslSlidingTabLayoutItem()() {
//                itemBindOverride = { itemHolder, _, _, _ ->
//                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
//                        addTabLayout(this)
//                        tabIndicator.indicatorWidth = 10 * dpi
//                        tabIndicator.indicatorEnableFlow = true
//                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
//                    }
//                }
//            }

            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -1
                        tabIndicator.indicatorHeight = 4 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            //指示器在背部
            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.indicator_round_background_tran)
                        tabIndicator.indicatorHeight = -2
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorWidthOffset = 20 * dpi
                        tabIndicator.indicatorHeightOffset = -15 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BACKGROUND
                    }
                }
            }
            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.ic_love)
                        tabIndicator.indicatorHeight = -1
                        tabIndicator.indicatorWidth = 40 * dpi
                        tabIndicator.indicatorHeight = 40 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BACKGROUND
                    }
                }
            }

            //其他特性
            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 30 * dpi
                        tabIndicator.indicatorHeight = 6 * dpi
                        tabIndicator.indicatorYOffset = 20 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        setTabLayoutConfig {
                            tabEnableGradientScale = true
                            tabEnableGradientColor = true
                            tabSelectColor = resources.getColor(R.color.colorAccent)
                            tabDeselectColor = resources.getColor(R.color.colorPrimaryDark)
                        }
                    }
                }
            }
//            DslSlidingTabLayoutItem()() {
//                itemBindOverride = { itemHolder, _, _, _ ->
//                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
//                        addTabLayout(this)
//                        tabIndicator.indicatorWidth = 10 * dpi
//                        tabIndicator.indicatorEnableFlow = true
//                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
//
//                        setTabLayoutConfig {
//                            tabEnableGradientScale = true
//                            tabEnableGradientColor = true
//                            tabSelectColor = resources.getColor(R.color.colorAccent)
//                        }
//                    }
//                }
//            }
            DslSlidingTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 10 * dpi
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

            DslViewPagerItem(childFragmentManager)() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    setViewPager(itemHolder.v(R.id.view_pager)!!)
                }
            }
        }
    }
}