package com.angcyo.dsltablayout.demo

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.tablayout.DslTabLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
class DslSegmentTabLayoutItem : DslAdapterItem() {

    init {
        itemLayoutId = R.layout.item_segment_tab_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        itemHolder.v<DslTabLayout>(R.id.tab_layout).apply {

        }
    }
}