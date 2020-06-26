package com.angcyo.dsltablayout.demo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/14
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class DslViewPager2Item(val fragment: Fragment) : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.item_view_pager2_layout
    }

    val fragmentList = getColorFragmentList(10)

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        itemHolder.v<ViewPager2>(R.id.view_pager)?.apply {
            adapter = object : FragmentStateAdapter(fragment) {
                override fun getItemCount(): Int {
                    return fragmentList.size
                }

                override fun createFragment(position: Int): Fragment {
                    return fragmentList[position]
                }
            }

            itemHolder.clickItem {
                currentItem = if (currentItem >= 5) 0 else 5
            }
        }
    }
}