package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.L
import com.angcyo.dsltablayout.demo.dslitem.DslColorItem
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class ColorFragment : BaseDslFragment() {

    var text = ""
    var color = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        text = arguments?.getString("text") ?: "默认文本"
        color = arguments?.getInt("color") ?: color
    }

    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)
        renderAdapter {
            DslColorItem()() {
                itemColor = color
                itemText = text
            }
        }
    }

    override fun onPause() {
        super.onPause()
        L.i("${hashCode()} $text 隐藏")
    }

    override fun onResume() {
        super.onResume()
        L.i("${hashCode()} $text 显示")
    }
}

fun colorFragment(text: String) = ColorFragment().apply {
    arguments = Bundle().apply {
        putString("text", "Fragment $text")
        putInt("color", randomColor())
    }
}

fun getColorFragmentList(count: Int): List<ColorFragment> {
    val result = mutableListOf<ColorFragment>()
    for (i in 0 until count) {
        result.add(colorFragment("$i"))
    }
    return result
}

fun randomColor(): Int = Color.rgb(nextInt(122, 222), nextInt(122, 222), nextInt(122, 222))