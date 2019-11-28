package com.angcyo.dsltablayout.demo

import android.os.Bundle
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

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)
        viewHolder.v<DslTabLayout>(R.id.tab_layout).configTabLayoutConfig {
            onSelectViewChange = { _, selectList, _ ->
                selectList.lastOrNull()?.findViewById<LottieAnimationView>(R.id.lottie_view)
                    ?.apply {
                        playAnimation()
                        addAnimatorUpdateListener {
                            //如果使用 高凸模式...需要使用[requestLayout]才能更新`高凸`部分的视图, 否则不需要
                            //这和`高凸`模式的实现机制有关
                            requestLayout()
                        }
                    }
            }
        }
    }
}