package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabIndicator
import com.angcyo.tablayout.DslTabLayout

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

            DslCommonTabLayoutItem()() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_NONE
                    }
                }
            }

            DslCommonTabLayoutItem()() {
                itemTopInsert = 10 * dpi
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

            DslCommonTabLayoutItem()() {
                itemLayoutId = R.layout.item_common_tab_layout_convex
                itemTopInsert = 10 * dpi
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 20 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorColor = randomColor()
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        configTabLayoutConfig {
                            tabEnableGradientColor = true
                            tabSelectColor = tabIndicator.indicatorColor

                            onGetTextStyleView = { itemView, index ->
                                itemView.findViewById(R.id.text_view)
                            }
                            onGetIcoStyleView = { itemView, index ->
                                itemView.findViewById(R.id.text_view)
                            }
                        }
                    }
                }
            }

            DslCommonTabLayoutItem()() {
                itemLayoutId = R.layout.item_common_tab_layout_convex2
                itemTopInsert = 10 * dpi
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

//            DslCommonTabLayoutItem()() {
//                itemLayoutId = R.layout.item_common_tab_layout2
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

            DslCommonTabLayoutItem()() {
                itemLayoutId = R.layout.item_common_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 20 * dpi
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

            DslCommonTabLayoutItem()() {
                itemLayoutId = R.layout.item_common_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorHeight = -1
                        tabIndicator.indicatorHeightOffset = -20 * dpi
                        tabIndicator.indicatorWidthOffset = -10 * dpi
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

            DslCommonTabLayoutItem()() {
                itemLayoutId = R.layout.item_common_tab_layout2
                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorHeight = -1
                        tabIndicator.indicatorHeightOffset = -20 * dpi
                        tabIndicator.indicatorWidthOffset = -10 * dpi
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
            DslViewPager2Item(this@CommonFragment)() {
                itemBindOverride = { itemHolder, _, _, _ ->
                    setViewPager2(itemHolder.v(R.id.view_pager)!!)
                }
            }
        }
    }
}