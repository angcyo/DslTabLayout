package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.widget.TextView
import com.angcyo.tablayout.DslTabLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class HorizontalHintActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.horizontal_hint_layout)

        val tabLayout = find<DslTabLayout>(R.id.tab_layout)
        /*tabLayout.tabHighlight?.apply {
            highlightDrawable = _drawable(R.mipmap.ic_hint_b)
            highlightHeightOffset = 20 * dpi
        }*/

        val textView = find<TextView>(R.id.text_view)
        tabLayout.configTabLayoutConfig {
            onSelectItemView = { itemView, index, select, fromUser ->
                if (select && itemView is TextView) {
                    textView.text = itemView.text
                }
                false
            }
        }
    }
}