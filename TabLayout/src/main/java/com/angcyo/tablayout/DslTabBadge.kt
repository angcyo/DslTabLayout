package com.angcyo.tablayout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity

/**
 * 角标
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabBadge : DslBadgeDrawable() {

    init {
        gradientSolidColor = Color.RED
        cornerRadius(10 * dp)
        badgePaddingLeft = 4 * dpi
        badgePaddingRight = 4 * dpi
    }

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        super.initAttribute(context, attributeSet)
    }
}

/**角标绘制参数配置*/
data class TabBadgeConfig(
    var badgeGravity: Int = Gravity.CENTER,
    /**角标的文本, 空字符串会绘制成小圆点*/
    var badgeText: String? = null,
    /**角标背景颜色*/
    var badgeSolidColor: Int = Color.RED,
    /**角标文本颜色*/
    var badgeTextColor: Int = Color.WHITE
)