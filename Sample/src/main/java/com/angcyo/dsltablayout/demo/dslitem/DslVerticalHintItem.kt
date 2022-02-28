package com.angcyo.dsltablayout.demo.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsltablayout.demo.R

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/28
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class DslVerticalHintItem : DslAdapterItem() {

    var text: CharSequence? = null

    init {
        itemLayoutId = R.layout.vertical_hint_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.text_view)?.text = text
    }

}