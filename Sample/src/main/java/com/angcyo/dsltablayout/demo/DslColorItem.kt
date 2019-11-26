package com.angcyo.dsltablayout.demo

import android.graphics.Color
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class DslColorItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.item_color_layout
    }

    var itemColor = Color.WHITE
    var itemText: CharSequence? = null

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)
        itemHolder.itemView.setBackgroundColor(itemColor)
        itemHolder.tv(R.id.text_view).text = itemText
    }
}