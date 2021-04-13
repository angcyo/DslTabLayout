package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.angcyo.tablayout.DslTabLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/03
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DynamicActivity : AppCompatActivity() {
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
    }

    fun <T : View> find(@IdRes id: Int) = findViewById<T>(id)
}