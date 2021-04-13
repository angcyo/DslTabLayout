package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.support.v4.view.ViewPager
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.delegate.ViewPager1Delegate
import com.angcyo.tablayout.isVertical

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
open class BaseTabLayoutFragment : BaseDslFragment() {

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)
    }

    val tabLayoutList = mutableListOf<DslTabLayout>()

    fun addTabLayout(tabLayout: DslTabLayout) {
        if (!tabLayoutList.contains(tabLayout)) {
            tabLayoutList.add(tabLayout)
        }
    }

    fun setViewPager(viewPager: ViewPager) {
        tabLayoutList.forEach {
            ViewPager1Delegate.install(viewPager, it)
        }
    }

    fun DslAdapter.pageItem() {
        DslViewPagerItem(childFragmentManager)() {
            if (orientation.isVertical()) itemLayoutId = R.layout.item_view_pager_vertical_layout
            itemBindOverride = { itemHolder, _, _, _ ->
                setViewPager(itemHolder.v(R.id.view_pager)!!)
            }
        }
    }
}