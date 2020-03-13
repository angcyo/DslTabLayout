package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.view.Gravity
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.dp
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabLayout

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
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout2
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout3
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout4
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout5
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
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
                        }
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout6
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
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
                itemLayoutId = R.layout.item_segment_tab_layout7
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout_selector
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                    }
                }
            }

            DslViewPagerItem(childFragmentManager)() {
                onItemBindOverride = { itemHolder, _, _ ->
                    setViewPager(itemHolder.v(R.id.view_pager))
                }
            }
        }
    }
}