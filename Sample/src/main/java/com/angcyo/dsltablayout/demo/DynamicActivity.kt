package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import com.airbnb.lottie.LottieAnimationView
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.dpi
import com.angcyo.dsladapter.inflate
import com.angcyo.tablayout.DslTabLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/03
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DynamicActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic)

        val tabLayout: DslTabLayout = find(R.id.tab_layout)

        tabLayout.configTabLayoutConfig {
            onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                Log.i(
                    "angcyo",
                    "选择:[$fromIndex]->${selectIndexList} reselect:$reselect fromUser:$fromUser"
                )
            }

            //选中view的回调
            onSelectViewChange = { fromView, selectViewList, reselect, fromUser ->
                val toView = selectViewList.first()
                L.i("fromView:${fromView.hashCode()} toView:${toView.hashCode()}")
            }
        }

        find<View>(R.id.remove_all)?.setOnClickListener {
            tabLayout.removeAllViews()
        }

        find<View>(R.id.add_all)?.setOnClickListener {
            for (i in 0..10) {
                tabLayout.inflate(R.layout.layout_text_view, false).apply {
                    find<TextView>(R.id.text_view)?.text = "NewItem$i"
                    tabLayout.addView(this)
                }
            }
        }

        find<View>(R.id.add_view)?.apply {
            setOnClickListener {
                tabLayout.addView(TextView(context).apply {
                    text = "Item ${tabLayout.childCount}"
                    gravity = Gravity.CENTER
                    textSize = 14f
                    background = ColorDrawable(Color.parseColor("#20000000"))
                    layoutParams = DslTabLayout.LayoutParams(-2, -2).apply {
                        leftMargin = 2 * dpi
                        rightMargin = 2 * dpi
                    }
                })
                tabLayout.setCurrentItem(tabLayout.childCount - 1)
            }
        }

        find<View>(R.id.equ_width_view)?.apply {
            setOnClickListener {
                tabLayout.itemAutoEquWidth = false
                tabLayout.itemIsEquWidth = !tabLayout.itemIsEquWidth
                tabLayout.requestLayout()
            }
        }

        //loop
        find<View>(R.id.loop_button)?.setOnClickListener {
            _currentIndex = 0
            loopSetCurrentItem()
        }
    }

    var _currentIndex = 0

    fun loopSetCurrentItem() {
        val slidingTabLayout: DslTabLayout = find(R.id.sliding_tab_layout)
        slidingTabLayout.setCurrentItem(++_currentIndex)
        if (_currentIndex < slidingTabLayout.childCount) {
            slidingTabLayout.postDelayed({
                loopSetCurrentItem()
            }, 100L * _currentIndex) //10L * _currentIndex
        }
    }
}