package com.angcyo.dsltablayout.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.DslViewHolder
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
}

fun getColorFragmentList(count: Int): List<ColorFragment> {
    val result = mutableListOf<ColorFragment>()
    for (i in 0 until count) {
        ColorFragment().apply {
            arguments = Bundle().apply {
                putString("text", "Fragment $i")
                putInt("color", Color.rgb(nextInt(122, 222), nextInt(122, 222), nextInt(122, 222)))
            }
            result.add(this)
        }
    }
    return result
}