package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.view.Gravity
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.dp
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.isHorizontal
import com.angcyo.tablayout.isVertical

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
class SegmentFragment : BaseTabLayoutFragment() {
    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)

        renderAdapter {

            DslSegmentTabLayoutItem()() {
                if (orientation.isVertical()) {
                    itemLayoutId = R.layout.item_segment_vertical_tab_layout
                }
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout2 else R.layout.item_segment_vertical_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout3 else R.layout.item_segment_vertical_tab_layout3

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout4 else R.layout.item_segment_vertical_tab_layout4

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout5 else R.layout.item_segment_vertical_tab_layout5

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)

                        //角标
                        onTabBadgeConfig = { child, tabBadge, index ->
                            when (index) {
                                1 -> {
                                    tabBadge.badgeGravity = Gravity.CENTER
                                    tabBadge.badgeText = "999+"
                                    tabBadge.badgeOffsetX = 20 * dpi
                                    tabBadge.badgeOffsetY = -6 * dpi
                                }
                                2 -> {
                                    tabBadge.badgeGravity = Gravity.LEFT or Gravity.TOP
                                    tabBadge.badgeText = "99"
                                    tabBadge.badgeOffsetX = 10 * dpi
                                    tabBadge.badgeOffsetY = 4 * dpi
                                }
                                else -> {
                                    tabBadge.badgeGravity = Gravity.CENTER
                                    tabBadge.badgeText = ""
                                    tabBadge.badgeOffsetX = child.measuredWidth / 2 - 20 * dpi
                                    tabBadge.badgeOffsetY = -child.measuredHeight / 2 + 12 * dpi
                                }
                            }

                            tabBadge.gradientSolidColor = randomColor()

                            tabBadge.updateOriginDrawable()
                            null
                        }
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout6 else R.layout.item_segment_vertical_tab_layout6
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)

                        configTabLayoutConfig {
                            onSelectIndexChange = { _, selectIndexList, reselect, fromUser ->
                                if (reselect) {
                                    updateTabBadge(selectIndexList.first()) {
                                        badgeText = when (badgeText) {
                                            "" -> null
                                            else -> ""
                                        }
                                        badgeSolidColor = randomColor()
                                    }
                                }
                            }
                        }

                        updateTabBadge(0) {
                            badgeText = ""
                            badgeSolidColor = randomColor()
                            badgeGravity = Gravity.RIGHT or Gravity.TOP
                            badgeOffsetX = 10 * dpi
                            badgeOffsetY = 2 * dpi
                        }

                        updateTabBadge(1) {
                            badgeText = "9"
                            badgeSolidColor = randomColor()
                            badgeGravity = Gravity.CENTER
                            badgeOffsetX = 20 * dpi
                            badgeOffsetY = -6 * dpi
                        }

                        updateTabBadge(2) {
                            badgeText = "99+"
                            badgeSolidColor = randomColor()
                            badgeGravity = Gravity.CENTER
                            badgeOffsetX = 10 * dpi
                            badgeOffsetY = -8 * dpi
                            badgeTextSize = 9 * dp
                        }
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout7 else R.layout.item_segment_vertical_tab_layout7
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_segment_tab_layout_selector else R.layout.item_segment_verticalt_tab_layout_selector

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                    }
                }
            }

           pageItem()
        }
    }
}