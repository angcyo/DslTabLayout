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
        badgePaddingLeft = 4 * dpi
        badgePaddingRight = 4 * dpi
        badgeTextSize = 12 * dp
    }

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        gradientSolidColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_badge_solid_color, gradientSolidColor)
        badgeTextColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_badge_text_color, badgeTextColor)
        badgeGravity = typedArray.getInt(R.styleable.DslTabLayout_tab_badge_gravity, badgeGravity)
        badgeOffsetX = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_offset_x,
            badgeOffsetX
        )
        badgeCircleRadius = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_circle_radius,
            badgeCircleRadius
        )

        val badgeRadius = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_radius,
            10 * dpi
        )
        cornerRadius(badgeRadius.toFloat())

        badgeOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_offset_y,
            badgeOffsetY
        )
        badgePaddingLeft = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_left,
            badgePaddingLeft
        )
        badgePaddingRight = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_right,
            badgePaddingRight
        )
        badgePaddingTop = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_top,
            badgePaddingTop
        )
        badgePaddingBottom = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_bottom,
            badgePaddingBottom
        )
        badgeText = typedArray.getString(R.styleable.DslTabLayout_tab_badge_text)
        badgeTextSize = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_text_size,
            badgeTextSize.toInt()
        ).toFloat()
        typedArray.recycle()
        super.initAttribute(context, attributeSet)
    }
}

/**角标绘制参数配置*/
data class TabBadgeConfig(
    /**角标的文本, 空字符串会绘制成小圆点*/
    var badgeText: String? = null,
    var badgeGravity: Int = Gravity.CENTER,
    /**角标背景颜色*/
    var badgeSolidColor: Int = Color.RED,
    /**角标文本颜色*/
    var badgeTextColor: Int = Color.WHITE
)