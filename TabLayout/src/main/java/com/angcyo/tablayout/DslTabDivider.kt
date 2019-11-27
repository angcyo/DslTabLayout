package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet

/**
 * 垂直分割线
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabDivider : DslDrawable() {

    /**分割线的颜色*/
    var dividerColor: Int = Color.WHITE

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        super.initAttribute(context, attributeSet)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}