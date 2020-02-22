package com.angcyo.tablayout.delegate2

import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.ViewPagerDelegate
import kotlin.math.absoluteValue

/**
 * 兼容[ViewPager2]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/14
 */
open class ViewPager2Delegate(val viewPager: ViewPager2, val dslTabLayout: DslTabLayout?) :
    ViewPager2.OnPageChangeCallback(), ViewPagerDelegate {

    companion object {
        fun install(viewPager: ViewPager2, dslTabLayout: DslTabLayout?) {
            ViewPager2Delegate(
                viewPager,
                dslTabLayout
            )
        }
    }

    init {
        viewPager.registerOnPageChangeCallback(this)
        dslTabLayout?.setupViewPager(this)
    }

    override fun onGetCurrentItem(): Int {
        return viewPager.currentItem
    }

    override fun onSetCurrentItem(fromIndex: Int, toIndex: Int) {
        viewPager.setCurrentItem(toIndex, (toIndex - fromIndex).absoluteValue <= 1)
    }

    override fun onPageScrollStateChanged(state: Int) {
        dslTabLayout?.onPageScrollStateChanged(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        dslTabLayout?.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        dslTabLayout?.onPageSelected(position)
    }
}