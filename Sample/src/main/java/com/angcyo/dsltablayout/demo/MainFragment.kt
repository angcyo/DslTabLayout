package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.tablayout.DslTabLayout

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

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)
        viewHolder.v<DslTabLayout>(R.id.tab_layout).apply {
            configTabLayoutConfig {
                onSelectViewChange = { _, selectList, reselect ->
                    if (!reselect) {
                        selectList.lastOrNull()?.findViewById<LottieAnimationView>(R.id.lottie_view)
                            ?.apply {
                                removeAllUpdateListeners()
                                addAnimatorUpdateListener {
                                    //如果使用 高凸模式...需要使用顶层view的[invalidate]才能更新`高凸`部分的视图, 否则不需要
                                    //这和`高凸`模式的实现机制有关
                                    (viewHolder.v<DslTabLayout>(R.id.tab_layout).parent as View).invalidate()
                                }
                                playAnimation()
                            }
                    }
                }
            }

            setupViewPager(viewHolder.v(R.id.view_pager))
        }

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
}