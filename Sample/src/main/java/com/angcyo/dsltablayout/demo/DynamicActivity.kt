package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.angcyo.dsladapter.inflate
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

        val tabLayout: DslTabLayout = findViewById(R.id.tab_layout)

        tabLayout.configTabLayoutConfig {
            onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                Log.i(
                    "angcyo",
                    "选择:[$fromIndex]->${selectIndexList} reselect:$reselect fromUser:$fromUser"
                )
            }
        }

        findViewById<View>(R.id.remove_all)?.setOnClickListener {
            tabLayout.removeAllViews()
        }

        findViewById<View>(R.id.add_all)?.setOnClickListener {
            for (i in 0..10) {
                tabLayout.inflate(R.layout.layout_text_view, false).apply {
                    findViewById<TextView>(R.id.text_view)?.text = "NewItem$i"
                    tabLayout.addView(this)
                }
            }
        }

        findViewById<View>(R.id.add_view)?.apply {
            setOnClickListener {
                tabLayout?.addView(TextView(context).apply {
                    text = "Item ${findViewById<ViewGroup>(R.id.tab_layout)?.childCount}"
                    gravity = Gravity.CENTER
                    textSize = 14f
                })
            }
        }
    }
}