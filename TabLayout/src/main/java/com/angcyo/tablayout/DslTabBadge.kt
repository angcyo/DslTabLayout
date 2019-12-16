package com.angcyo.tablayout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.Px

/**
 * 角标
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabBadge : DslBadgeDrawable() {

    /**角标默认配置项*/
    val defaultBadgeConfig = TabBadgeConfig()

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        gradientSolidColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_badge_solid_color,
                defaultBadgeConfig.badgeSolidColor
            )
        defaultBadgeConfig.badgeSolidColor = gradientSolidColor

        badgeTextColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_badge_text_color,
                defaultBadgeConfig.badgeTextColor
            )
        defaultBadgeConfig.badgeTextColor = badgeTextColor

        badgeGravity = typedArray.getInt(
            R.styleable.DslTabLayout_tab_badge_gravity,
            defaultBadgeConfig.badgeGravity
        )
        defaultBadgeConfig.badgeGravity = badgeGravity

        badgeOffsetX = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_offset_x,
            defaultBadgeConfig.badgeOffsetX
        )
        defaultBadgeConfig.badgeOffsetX = badgeOffsetX

        badgeCircleRadius = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_circle_radius,
            defaultBadgeConfig.badgeCircleRadius
        )
        defaultBadgeConfig.badgeCircleRadius = badgeCircleRadius

        val badgeRadius = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_radius,
            defaultBadgeConfig.badgeRadius
        )
        cornerRadius(badgeRadius.toFloat())
        defaultBadgeConfig.badgeRadius = badgeRadius

        badgeOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_offset_y,
            defaultBadgeConfig.badgeOffsetY
        )
        defaultBadgeConfig.badgeOffsetY = badgeOffsetY

        badgePaddingLeft = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_left,
            defaultBadgeConfig.badgePaddingLeft
        )
        defaultBadgeConfig.badgePaddingLeft = badgePaddingLeft

        badgePaddingRight = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_right,
            defaultBadgeConfig.badgePaddingRight
        )
        defaultBadgeConfig.badgePaddingRight = badgePaddingRight

        badgePaddingTop = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_top,
            defaultBadgeConfig.badgePaddingTop
        )
        defaultBadgeConfig.badgePaddingTop = badgePaddingTop

        badgePaddingBottom = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_padding_bottom,
            defaultBadgeConfig.badgePaddingBottom
        )
        defaultBadgeConfig.badgePaddingBottom = badgePaddingBottom

        badgeText = typedArray.getString(R.styleable.DslTabLayout_tab_badge_text)

        badgeTextSize = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_text_size,
            defaultBadgeConfig.badgeTextSize.toInt()
        ).toFloat()
        defaultBadgeConfig.badgeTextSize = badgeTextSize

        typedArray.recycle()
        super.initAttribute(context, attributeSet)
    }

    /**使用指定配置, 更新[DslBadgeDrawable]*/
    fun updateBadgeConfig(badgeConfig: TabBadgeConfig) {
        gradientSolidColor = badgeConfig.badgeSolidColor
        badgeTextColor = badgeConfig.badgeTextColor
        badgeGravity = badgeConfig.badgeGravity
        badgeOffsetX = badgeConfig.badgeOffsetX
        badgeOffsetY = badgeConfig.badgeOffsetY
        badgeCircleRadius = badgeConfig.badgeCircleRadius
        badgePaddingLeft = badgeConfig.badgePaddingLeft
        badgePaddingRight = badgeConfig.badgePaddingRight
        badgePaddingTop = badgeConfig.badgePaddingTop
        badgePaddingBottom = badgeConfig.badgePaddingBottom
        badgeTextSize = badgeConfig.badgeTextSize
        cornerRadius(badgeConfig.badgeRadius.toFloat())

        badgeText = badgeConfig.badgeText
    }
}

/**角标绘制参数配置*/
data class TabBadgeConfig(
    /**角标的文本, 空字符串会绘制成小圆点*/
    var badgeText: String? = null,
    /**重力*/
    var badgeGravity: Int = Gravity.CENTER,
    /**角标背景颜色*/
    var badgeSolidColor: Int = Color.RED,
    /**角标文本颜色*/
    var badgeTextColor: Int = Color.WHITE,
    /**角标文本字体大小*/
    @Px
    var badgeTextSize: Float = 12 * dp,
    /**圆点状态时的半径大小*/
    var badgeCircleRadius: Int = 4 * dpi,
    /**圆角大小*/
    var badgeRadius: Int = 10 * dpi,
    /**额外偏移距离, 会根据[Gravity]自动取负值*/
    var badgeOffsetX: Int = 0,
    var badgeOffsetY: Int = 0,
    /**圆点状态时无效*/
    var badgePaddingLeft: Int = 4 * dpi,
    var badgePaddingRight: Int = 4 * dpi,
    var badgePaddingTop: Int = 0,
    var badgePaddingBottom: Int = 0
)