package com.angcyo.dsltablayout.demo.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.nowTime
import com.angcyo.dsltablayout.demo.*
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.TabGradientCallback
import com.angcyo.tablayout.delegate.ViewPager1Delegate

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/11
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class SampleMainFragment : BaseDslFragment() {

    var isLogin = false

    val fragmentList = mutableListOf<Fragment?>(null, null, null)

    override fun getBaseLayoutId(): Int = R.layout.sample_fragment_main

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        //super.initBaseView(viewHolder, savedInstanceState)

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

                onSelectItemView = { itemView, index, select, fromUser ->
                    if (index == 2) {
                        //拦截
                        if (isLogin) {
                            false
                        } else {
                            Toast.makeText(context, "再点一次即可切换", Toast.LENGTH_SHORT).show()
                            isLogin = true
                            true
                        }
                    } else {
                        false
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
                }

                //选中index的回调
                onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                    val toIndex = selectIndexList.first()
                    L.i("TabLayout选中改变:[$fromIndex]->[$toIndex]")
                    fragmentList[toIndex] =
                        fragmentList.getOrNull(toIndex) ?: colorFragment("Text $toIndex ${nowTime()}")
                    show(fragmentList[toIndex]!!, fragmentList.getOrNull(fromIndex))
                }
            }
        }
    }
}