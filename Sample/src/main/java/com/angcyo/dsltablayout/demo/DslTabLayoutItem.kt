package com.angcyo.dsltablayout.demo

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
class DslTabLayoutItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.item_tab_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)
    }
}