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
open class ViewPager2Delegate(
    val viewPager: ViewPager2,
    val dslTabLayout: DslTabLayout?,
    val forceSmoothScroll: Boolean? = null
) : ViewPager2.OnPageChangeCallback(), ViewPagerDelegate {

    companion object {

        /**
         * [forceSmoothScroll] 为 null, 只有切换左右page时, 才有VP的动画, 否则没有.
         * */
        fun install(
            viewPager: ViewPager2,
            dslTabLayout: DslTabLayout?,
            forceSmoothScroll: Boolean? = null
        ): ViewPager2Delegate {
            return ViewPager2Delegate(viewPager, dslTabLayout, forceSmoothScroll)
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
        val smoothScroll = forceSmoothScroll ?: ((toIndex - fromIndex).absoluteValue <= 1)
        viewPager.setCurrentItem(toIndex, smoothScroll)
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