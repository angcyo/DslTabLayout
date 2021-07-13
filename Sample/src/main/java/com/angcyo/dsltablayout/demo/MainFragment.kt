package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.dp
import com.angcyo.dsladapter.dpi
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.TabGradientCallback
import com.angcyo.tablayout.delegate.ViewPager1Delegate
import kotlin.random.Random.Default.nextInt

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentList.forEach {
            it.orientation = this.orientation
        }
    }

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)
        viewHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
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
                onSelectViewChange = { fromView, selectViewList, reselect, fromUser ->
                    val toView = selectViewList.first()
                    fromView?.apply { onGetTextStyleView(this, -1)?.visibility = View.GONE }
                    if (reselect) {
                        //重复选择
                    } else {
                        toView.findViewById<LottieAnimationView>(R.id.lottie_view)
                            ?.playAnimation()
                        onGetTextStyleView(toView, -1)?.visibility = View.VISIBLE
                    }
                    L.i("fromView:${fromView.hashCode()} toView:${toView.hashCode()}")
                }

                //选中index的回调
                onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                    val toIndex = selectIndexList.first()

                    tabLayout._viewPagerDelegate?.onSetCurrentItem(toIndex, toIndex)

                    L.i("TabLayout选中改变:[$fromIndex]->[$toIndex]")

                    updateTabBadge(selectIndexList.first()) {
                        if (reselect) {
                            badgeText = when (badgeText) {
                                "" -> null
                                else -> ""
                            }
                        }
                        badgeSolidColor = randomColor()
                    }

                    if (!reselect) {
                        _updateBadge()
                    }
                }

                ViewPager1Delegate.install(viewHolder.v(R.id.view_pager)!!, this@apply)

                viewHolder.v<ViewPager>(R.id.view_pager)?.adapter =
                    object : FragmentStatePagerAdapter(childFragmentManager) {
                        override fun getItem(position: Int): Fragment {
                            return fragmentList[position]
                        }

                        override fun getCount(): Int {
                            return fragmentList.size
                        }
                    }
            }

            //角标, 相同配置属性, 可以在xml指定

            //完全自定义配置角标, 会替换库中的默认行为
//            onTabBadgeConfig = { child, tabBadge, index ->
//                tabBadge.badgeGravity = when (index) {
////                    1 -> Gravity.LEFT
////                    2 -> Gravity.TOP or Gravity.RIGHT
//                    else -> Gravity.CENTER
//                }
//
//                tabBadge.badgeText = when (index) {
//                    1 -> "1"
//                    2 -> "99+"
//                    else -> ""
//                }
//
//                tabBadge.gradientSolidColor = when (index) {
//                    1 -> Color.BLUE
//                    2 -> Color.GREEN
//                    else -> Color.RED
//                }
//                tabBadge.updateOriginDrawable()
//
//                tabBadge.badgeOffsetX = 20 * dpi
//                tabBadge.badgeOffsetY = -20 * dpi
//            }

            //简单的更新角标配置
            updateTabBadge(0) {
                badgeText = ""
                badgeSolidColor = randomColor()
                badgeOffsetX = 10 * dpi
                badgeOffsetY = -20 * dpi
            }

            updateTabBadge(2) {
                badgeText = "99+"
                badgeSolidColor = randomColor()
                badgeTextSize = 9 * dp
            }

            _updateBadge()
        }
    }

    fun _updateBadge() {
        baseViewHolder.v<DslTabLayout>(R.id.tab_layout)?.apply {
            updateTabBadge(1) {
                badgeText = "${nextInt(1, 9)}"
                badgeSolidColor = randomColor()
            }
        }
    }
}
