package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.TabGradientCallback

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class MainFragment : BaseDslFragment() {
    override fun getBaseLayoutId(): Int {
        return R.layout.fragment_main
    }

    val fragmentList = listOf(SlidingFragment(), CommonFragment(), SegmentFragment())

    fun LottieAnimationView.setLottieColorFilter(color: Int) {
        val filter = SimpleColorFilter(color)
        val keyPath = KeyPath("**")
        val callback = LottieValueCallback<ColorFilter>(filter)
        addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
    }

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)
        viewHolder.v<DslTabLayout>(R.id.tab_layout).apply {
            configTabLayoutConfig {
                onGetIcoStyleView = { itemView, _ ->
                    itemView.findViewById<LottieAnimationView>(R.id.lottie_view)
                }

                onGetTextStyleView = { itemView, _ ->
                    itemView.findViewById(R.id.button_view)
                }

                tabGradientCallback = object : TabGradientCallback() {
                    override fun onUpdateIcoColor(view: View?, color: Int) {
                        (view as? LottieAnimationView)?.run {
                            setLottieColorFilter(color)
                        } ?: super.onUpdateIcoColor(view, color)
                    }
                }

                //选中view的回调
                onSelectViewChange = { fromView, selectViewList, reselect ->
                    val toView = selectViewList.first()

                    if (reselect) {
                        //重复选择
                    } else {
                        toView.findViewById<LottieAnimationView>(R.id.lottie_view)
                            ?.playAnimation()
                    }
                }

                //选中index的回调
                onSelectIndexChange = { fromIndex, selectIndexList, _ ->
                    val toIndex = selectIndexList.first()

                    tabLayout._viewPager?.setCurrentItem(toIndex, true)

                    L.i("TabLayout选中改变:[$fromIndex]->[$toIndex]")
                }

                setupViewPager(viewHolder.v(R.id.view_pager))

                viewHolder.v<ViewPager>(R.id.view_pager).adapter =
                    object : FragmentStatePagerAdapter(childFragmentManager) {
                        override fun getItem(position: Int): Fragment {
                            return fragmentList[position]
                        }

                        override fun getCount(): Int {
                            return fragmentList.size
                        }
                    }
            }

            //角标
            onTabBadgeConfig = { child, tabBadge, index ->
                tabBadge.badgeGravity = when (index) {
//                    1 -> Gravity.LEFT
//                    2 -> Gravity.TOP or Gravity.RIGHT
                    else -> Gravity.CENTER
                }

                tabBadge.badgeText = when (index) {
                    1 -> "1"
                    2 -> "99+"
                    else -> ""
                }

                tabBadge.gradientSolidColor = when (index) {
                    1 -> Color.BLUE
                    2 -> Color.GREEN
                    else -> Color.RED
                }
                tabBadge.updateOriginDrawable()

                tabBadge.badgeOffsetX = 20 * dpi
                tabBadge.badgeOffsetY = -20 * dpi
            }
        }
    }
}
