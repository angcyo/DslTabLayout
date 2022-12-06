package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.view.View

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/04
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class TestFragment : BaseDslFragment() {

    override fun getBaseLayoutId(): Int {
        return R.layout.fragment_test_wrap //R.layout.item_common_vertical_tab_layout_convex ///R.layout.fragment_test
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*baseViewHolder.click(R.id.remove_all) {
            baseViewHolder.group(R.id.tab_layout)?.removeAllViews()
        }

        baseViewHolder.click(R.id.add_view) {
            baseViewHolder.group(R.id.tab_layout)?.addView(TextView(context).apply {
                text = "Item ${baseViewHolder.group(R.id.tab_layout)?.childCount}"
                gravity = Gravity.CENTER
                textSize = 14f
            })
        }

        renderAdapter {
            DslViewPager2Item(this@TestFragment)() {
                itemLayoutId = R.layout.item_view_pager2_vertical_layout
            }
        }*/
    }
}