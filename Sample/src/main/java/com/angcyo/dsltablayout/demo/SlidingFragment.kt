package com.angcyo.dsltablayout.demo

import android.os.Bundle
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.DslTabIndicator

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
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 4 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -1
                        tabIndicator.indicatorHeight = 4 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            //指示器在底部
            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
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
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -2
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 10 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = -1
                        tabIndicator.indicatorHeight = 4 * dpi
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM
                    }
                }
            }

            //指示器在背部
            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
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
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
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
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 10 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        setTabLayoutConfig {
                            tabSelectColor = resources.getColor(R.color.colorAccent)
                            tabDeselectColor = resources.getColor(R.color.colorPrimaryDark)
                        }
                    }
                }
            }
            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 10 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        setTabLayoutConfig {
                            tabEnableGradientScale = true
                            tabEnableGradientColor = true
                            tabSelectColor = resources.getColor(R.color.colorAccent)
                        }
                    }
                }
            }
            DslSlidingTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
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