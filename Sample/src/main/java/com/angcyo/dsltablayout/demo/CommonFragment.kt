package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.tablayout.DslTabIndicator
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.isHorizontal
import com.angcyo.tablayout.isVertical

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
class CommonFragment : BaseTabLayoutFragment() {
    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)

        renderAdapter {

            commonItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_NONE
                    }
                }
            }

            commonItem {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 4 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                        tabIndicator.indicatorColor = randomColor()
                        configTabLayoutConfig {
                            tabSelectColor = tabIndicator.indicatorColor
                        }
                    }
                }
            }

            commonItem {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_common_tab_layout_convex else R.layout.item_common_vertical_tab_layout_convex
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        if (orientation.isHorizontal()) {
                            tabIndicator.indicatorWidth = 20 * dpi
                        } else {
                            tabIndicator.indicatorHeight = 20 * dpi
                        }
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorColor = randomColor()

                        if (isHorizontal()) {
                            tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                        } else {
                            tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                        }

                        configTabLayoutConfig {
                            tabEnableGradientColor = true
                            tabSelectColor = tabIndicator.indicatorColor
                        }
                    }
                }
            }

            commonItem {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_common_tab_layout_convex2 else R.layout.item_common_vertical_tab_layout_convex2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorHeight = -2
                        tabIndicator.indicatorYOffset = 0
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorColor = randomColor()
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BACKGROUND

                        configTabLayoutConfig {
                            tabEnableGradientColor = true
                            tabSelectColor = Color.WHITE
                        }
                    }
                }
            }

            //2...

//            commonItem {
//                  itemLayoutId =
//                      if (orientation.isHorizontal()) R.layout.item_common_tab_layout2 else R.layout.item_common_vertical_tab_layout2
//                itemTopInsert = 10 * dpi
//                itemBindOverride = { itemHolder, _, _, _ ->
//                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
//                        addTabLayout(this)
//                        tabIndicator.indicatorWidth = 20 * dpi
//                        tabIndicator.indicatorEnableFlow = true
//                        tabIndicator.indicatorColor = Color.WHITE
//                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
//
//                        configTabLayoutConfig {
//                            tabEnableGradientColor = true
//                            tabSelectColor = tabIndicator.indicatorColor
//                        }
//                    }
//                }
//            }

            commonItem {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_common_tab_layout2 else R.layout.item_common_vertical_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        if (isHorizontal()) {
                            tabIndicator.indicatorWidth = 20 * dpi
                        } else {
                            tabIndicator.indicatorHeight = 20 * dpi
                        }
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorColor = Color.WHITE
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        configTabLayoutConfig {
                            tabEnableGradientColor = true
                            tabSelectColor = tabIndicator.indicatorColor
                        }
                    }
                }
            }

            commonItem {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_common_tab_layout2 else R.layout.item_common_vertical_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        if (isHorizontal()) {
                            tabIndicator.indicatorWidth = -2
                            tabIndicator.indicatorHeight = -1
                            tabIndicator.indicatorHeightOffset = -20 * dpi
                            tabIndicator.indicatorWidthOffset = -10 * dpi
                        } else {
                            tabIndicator.indicatorHeight = -2
                            tabIndicator.indicatorWidth = -1
                            tabIndicator.indicatorHeightOffset = -10 * dpi
                            tabIndicator.indicatorWidthOffset = -20 * dpi
                        }

                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorColor = Color.parseColor("#40000000")
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.indicator_round_background_tran)
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BACKGROUND

                        configTabLayoutConfig {
                            tabEnableGradientColor = true
                        }
                    }
                }
            }

            commonItem {
                itemLayoutId =
                    if (orientation.isHorizontal()) R.layout.item_common_tab_layout2 else R.layout.item_common_vertical_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        if (isHorizontal()) {
                            tabIndicator.indicatorWidth = -2
                            tabIndicator.indicatorHeight = -1
                            tabIndicator.indicatorHeightOffset = -20 * dpi
                            tabIndicator.indicatorWidthOffset = -10 * dpi
                        } else {
                            tabIndicator.indicatorHeight = -2
                            tabIndicator.indicatorWidth = -1
                            tabIndicator.indicatorHeightOffset = -10 * dpi
                            tabIndicator.indicatorWidthOffset = -20 * dpi
                        }

                        tabIndicator.indicatorColor = Color.parseColor("#40000000")
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BACKGROUND
                    }
                }
            }

//            DslViewPagerItem(childFragmentManager)() {
//                itemBindOverride = { itemHolder, _, _, _ ->
//                    setViewPager(itemHolder.v(R.id.view_pager))
//                }
//            }
//
            page2Item()
        }
    }

    fun DslAdapter.commonItem(init: DslCommonTabLayoutItem.() -> Unit) {
        DslCommonTabLayoutItem()() {
            if (orientation.isVertical()) {
                itemLayoutId = R.layout.item_common_vertical_tab_layout
            }
            if (orientation.isHorizontal()) {
                itemTopInsert = 10 * dpi
            } else {
                itemLeftInsert = 10 * dpi
            }
            init()
        }
    }
}