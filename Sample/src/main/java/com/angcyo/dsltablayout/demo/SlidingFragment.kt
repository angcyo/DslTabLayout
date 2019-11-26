package com.angcyo.dsltablayout.demo

import android.os.Bundle
import androidx.core.graphics.toColorInt
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.TabIndicator

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
            DslTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        setBackgroundColor("#666466".toColorInt())
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.indicator_white_line)
                        tabIndicator.indicatorStyle = TabIndicator.INDICATOR_STYLE_TOP
                    }
                }
            }

            //指示器在底部
            DslTabLayoutItem()() {
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        setBackgroundColor("#7F3737".toColorInt())
                        addTabLayout(this)
                        tabIndicator.indicatorDrawable =
                            getDrawable(R.drawable.indicator_white_line)
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