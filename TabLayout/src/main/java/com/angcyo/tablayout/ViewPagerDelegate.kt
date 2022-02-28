package com.angcyo.tablayout

/**
 * 不依赖ViewPager和ViewPager2
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/14
 */
interface ViewPagerDelegate {
    companion object {
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SETTLING = 2
    }

    /**获取当前页面索引*/
    fun onGetCurrentItem(): Int

    /**设置当前的页面*/
    fun onSetCurrentItem(fromIndex: Int, toIndex: Int, reselect: Boolean, fromUser: Boolean)
}