package com.angcyo.dsltablayout.demo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class DslViewPagerItem(val fragmentManager: FragmentManager) : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.item_view_pager_layout
    }

    val fragmentList = getColorFragmentList(10)

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        itemHolder.v<ViewPager>(R.id.view_pager)?.apply {
            adapter =
                object : FragmentStatePagerAdapter(fragmentManager) {
                    override fun getItem(position: Int): Fragment {
                        return fragmentList[position]
                    }

                    override fun getCount(): Int {
                        return fragmentList.size
                    }
                }

            itemHolder.clickItem {
                currentItem = if (currentItem >= 5) 0 else 5
            }
        }
    }
}