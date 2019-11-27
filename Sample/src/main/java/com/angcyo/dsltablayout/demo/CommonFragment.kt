package com.angcyo.dsltablayout.demo

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

            //            DslCommonTabLayoutItem()() {
//                onItemBindOverride = { itemHolder, _, _ ->
//                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
//                        addTabLayout(this)
//                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_NONE
//                    }
//                }
//            }
//
            DslCommonTabLayoutItem()() {
                itemTopInsert = 10 * dpi
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 4 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            DslCommonTabLayoutItem()() {
                itemTopInsert = 10 * dpi
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
                        tabIndicator.indicatorWidth = 20 * dpi
                        tabIndicator.indicatorEnableFlow = true
                        tabIndicator.indicatorColor = randomColor()
                        tabIndicator.indicatorStyle = DslTabIndicator.INDICATOR_STYLE_BOTTOM

                        configTabLayoutConfig {
                            tabEnableGradientColor = true
                            tabSelectColor = tabIndicator.indicatorColor
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