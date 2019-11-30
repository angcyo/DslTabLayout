package com.angcyo.dsltablayout.demo

import android.os.Bundle
import com.angcyo.dsladapter.DslViewHolder
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
                    }
                }
            }

            DslSegmentTabLayoutItem()() {
                itemLayoutId = R.layout.item_segment_tab_layout6
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {
                        addTabLayout(this)
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