package com.angcyo.dsltablayout.demo.sample

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.dpi
import com.angcyo.dsladapter.inflate
import com.angcyo.dsltablayout.demo.BaseActivity
import com.angcyo.dsltablayout.demo.R
import com.angcyo.tablayout.DslTabLayout
import java.util.Random

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

        val tabLayout = find<DslTabLayout>(R.id.tab_layout)

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

        //动态添加
        find<DslTabLayout>(R.id.dynamic_tab_layout)?.apply {
            //模拟网络请求后填充数据 则 第一个显示不全
            postDelayed({
                val tabs = arrayOf(
                    "推荐",
                    "家具家纺",
                    "粮油副食",
                    "休闲食品",
                    "肉禽蛋品",
                    "时令水果",
                    "水产海鲜",
                    "饮料冲调",
                    "新鲜蔬菜",
                    "生活用品",
                    "生活电器",
                    "汽车用品",
                    "手机数码",
                    "休闲娱乐"
                )
                removeAllViews()
                for (tab in tabs) {
                    val textView = TextView(context)
                    textView.text = tab
                    textView.gravity = Gravity.CENTER
                    textView.setPadding(30, 0, 0, 16)
                    addView(textView)
                }
            }, 600)
        }

        //动态调整背景
        find<View>(R.id.border_solid_button)?.setOnClickListener {
            val stateTabLayout = find<DslTabLayout>(R.id.states_tab_layout)
            stateTabLayout.tabBorder?.updateBorderBackgroundSolidColor(randomColorIn())
            stateTabLayout.invalidate()
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

/**
 * 随机颜色, 设置一个最小值, 设置一个最大值, 第三个值在这2者之间随机改变
 */
fun randomColorIn(
    random: Random = Random(SystemClock.elapsedRealtime()),
    minValue: Int = 120,
    maxValue: Int = 250
): Int {
    val a = minValue + random.nextInt(maxValue - minValue)
    val list1: MutableList<Int> = ArrayList()
    val list2: MutableList<Int> = ArrayList()
    list1.add(a)
    list1.add(minValue)
    list1.add(maxValue)
    while (list2.size != 3) {
        val i = random.nextInt(list1.size)
        list2.add(list1.removeAt(i))
    }
    return Color.rgb(list2[0], list2[1], list2[2])
}