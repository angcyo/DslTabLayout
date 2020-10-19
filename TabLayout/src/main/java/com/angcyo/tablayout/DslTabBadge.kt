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

    /**预览的角标属性*/
    var xmlBadgeText: String? = null

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
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

        gradientStrokeColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_badge_stroke_color,
                defaultBadgeConfig.badgeStrokeColor
            )
        defaultBadgeConfig.badgeStrokeColor = gradientStrokeColor

        gradientStrokeWidth =
            typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_badge_stroke_width,
                defaultBadgeConfig.badgeStrokeWidth
            )
        defaultBadgeConfig.badgeStrokeWidth = gradientStrokeWidth

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
        badgeOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_offset_y,
            defaultBadgeConfig.badgeOffsetY
        )
        defaultBadgeConfig.badgeOffsetY = badgeOffsetY

        badgeCircleOffsetX = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_circle_offset_x,
            defaultBadgeConfig.badgeOffsetX
        )
        defaultBadgeConfig.badgeCircleOffsetX = badgeCircleOffsetX
        badgeCircleOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_circle_offset_y,
            defaultBadgeConfig.badgeOffsetY
        )
        defaultBadgeConfig.badgeCircleOffsetY = badgeCircleOffsetY

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

        xmlBadgeText = typedArray.getString(R.styleable.DslTabLayout_tab_badge_text)

        badgeTextSize = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_badge_text_size,
            defaultBadgeConfig.badgeTextSize.toInt()
        ).toFloat()
        defaultBadgeConfig.badgeTextSize = badgeTextSize

        defaultBadgeConfig.badgeAnchorChildIndex =
            typedArray.getInteger(
                R.styleable.DslTabLayout_tab_badge_anchor_child_index,
                defaultBadgeConfig.badgeAnchorChildIndex
            )
        defaultBadgeConfig.badgeIgnoreChildPadding = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_badge_ignore_child_padding,
            defaultBadgeConfig.badgeIgnoreChildPadding
        )

        defaultBadgeConfig.badgeMinWidth = typedArray.getLayoutDimension(
            R.styleable.DslTabLayout_tab_badge_min_width,
            defaultBadgeConfig.badgeMinWidth
        )

        defaultBadgeConfig.badgeMinHeight = typedArray.getLayoutDimension(
            R.styleable.DslTabLayout_tab_badge_min_height,
            defaultBadgeConfig.badgeMinHeight
        )

        typedArray.recycle()
        super.initAttribute(context, attributeSet)
    }

    /**使用指定配置, 更新[DslBadgeDrawable]*/
    fun updateBadgeConfig(badgeConfig: TabBadgeConfig) {
        gradientSolidColor = badgeConfig.badgeSolidColor
        gradientStrokeColor = badgeConfig.badgeStrokeColor
        gradientStrokeWidth = badgeConfig.badgeStrokeWidth
        badgeTextColor = badgeConfig.badgeTextColor
        badgeGravity = badgeConfig.badgeGravity
        badgeOffsetX = badgeConfig.badgeOffsetX
        badgeOffsetY = badgeConfig.badgeOffsetY
        badgeCircleOffsetX = badgeConfig.badgeCircleOffsetX
        badgeCircleOffsetY = badgeConfig.badgeCircleOffsetY
        badgeCircleRadius = badgeConfig.badgeCircleRadius
        badgePaddingLeft = badgeConfig.badgePaddingLeft
        badgePaddingRight = badgeConfig.badgePaddingRight
        badgePaddingTop = badgeConfig.badgePaddingTop
        badgePaddingBottom = badgeConfig.badgePaddingBottom
        badgeTextSize = badgeConfig.badgeTextSize
        cornerRadius(badgeConfig.badgeRadius.toFloat())
        badgeMinHeight = badgeConfig.badgeMinHeight
        badgeMinWidth = badgeConfig.badgeMinWidth
        badgeText = badgeConfig.badgeText
    }
}

/**角标绘制参数配置*/
data class TabBadgeConfig(
    /**角标的文本(默认居中绘制文本,暂不支持修改), 空字符串会绘制成小圆点
     * null 不绘制角标
     * ""   空字符绘制圆点
     * 其他  正常绘制
     * */
    var badgeText: String? = null,
    /**重力*/
    var badgeGravity: Int = Gravity.CENTER,
    /**角标背景颜色*/
    var badgeSolidColor: Int = Color.RED,
    /**角标边框颜色*/
    var badgeStrokeColor: Int = Color.TRANSPARENT,
    /**角标边框宽度*/
    var badgeStrokeWidth: Int = 0,

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
    var badgeCircleOffsetX: Int = 0,
    var badgeCircleOffsetY: Int = 0,
    /**圆点状态时无效*/
    var badgePaddingLeft: Int = 4 * dpi,
    var badgePaddingRight: Int = 4 * dpi,
    var badgePaddingTop: Int = 0,
    var badgePaddingBottom: Int = 0,

    var badgeAnchorChildIndex: Int = -1,
    var badgeIgnoreChildPadding: Boolean = true,

    /**最小的高度大小, px. 大于0生效; 圆点时属性无效*/
    var badgeMinHeight: Int = -2,

    /**最小的宽度大小, px. 大于0生效; 圆点时属性无效;
     * -1 表示使用使用计算出来的高度值*/
    var badgeMinWidth: Int = -1
)