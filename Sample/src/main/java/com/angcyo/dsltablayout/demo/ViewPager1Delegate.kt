package com.angcyo.dsltablayout.demo

import androidx.viewpager.widget.ViewPager
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.ViewPagerDelegate
import kotlin.math.absoluteValue

/**
 * 兼容[ViewPager]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/14
 */
class ViewPager1Delegate(val viewPager: ViewPager, val dslTabLayout: DslTabLayout) :
    ViewPager.OnPageChangeListener, ViewPagerDelegate {

    init {
        viewPager.addOnPageChangeListener(this)
    }

    override fun onGetCurrentItem(): Int {
        return viewPager.currentItem
    }

    override fun onSetCurrentItem(fromIndex: Int, toIndex: Int) {
        viewPager.setCurrentItem(toIndex, (toIndex - fromIndex).absoluteValue <= 1)
    }

    override fun onPageScrollStateChanged(state: Int) {
        dslTabLayout.onPageScrollStateChanged(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        dslTabLayout.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        dslTabLayout.onPageSelected(position)
    }
}