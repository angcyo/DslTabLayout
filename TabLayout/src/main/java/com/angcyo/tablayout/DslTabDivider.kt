package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * 垂直分割线
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabDivider : DslGradientDrawable() {

    var dividerWidth = 2 * dpi
    var dividerHeight = 2 * dpi
    var dividerMarginLeft = 0
    var dividerMarginRight = 0
    var dividerMarginTop = 0
    var dividerMarginBottom = 0

    /**
     * [LinearLayout.SHOW_DIVIDER_BEGINNING]
     * [LinearLayout.SHOW_DIVIDER_MIDDLE]
     * [LinearLayout.SHOW_DIVIDER_END]
     * */
    var dividerShowMode = LinearLayout.SHOW_DIVIDER_MIDDLE

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        super.initAttribute(context, attributeSet)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)

        dividerWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_width,
            dividerWidth
        )
        dividerHeight = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_height,
            dividerHeight
        )
        dividerMarginLeft = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_margin_left,
            dividerMarginLeft
        )
        dividerMarginRight = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_margin_right,
            dividerMarginRight
        )
        dividerMarginTop = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_margin_top,
            dividerMarginTop
        )
        dividerMarginBottom = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_margin_bottom,
            dividerMarginBottom
        )

        if (typedArray.hasValue(R.styleable.DslTabLayout_tab_divider_solid_color)) {
            gradientSolidColor = typedArray.getColor(
                R.styleable.DslTabLayout_tab_divider_solid_color,
                gradientSolidColor
            )
        } else if (typedArray.hasValue(R.styleable.DslTabLayout_tab_border_stroke_color)) {
            gradientSolidColor = typedArray.getColor(
                R.styleable.DslTabLayout_tab_border_stroke_color,
                gradientSolidColor
            )
        } else {
            gradientSolidColor = typedArray.getColor(
                R.styleable.DslTabLayout_tab_deselect_color,
                gradientSolidColor
            )
        }

        gradientStrokeColor = typedArray.getColor(
            R.styleable.DslTabLayout_tab_divider_stroke_color,
            gradientStrokeColor
        )
        gradientStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_divider_stroke_width,
            0
        )
        val radiusSize =
            typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_divider_radius_size,
                2 * dpi
            )

        cornerRadius(radiusSize.toFloat())

        originDrawable = typedArray.getDrawable(R.styleable.DslTabLayout_tab_divider_drawable)

        dividerShowMode =
            typedArray.getInt(R.styleable.DslTabLayout_tab_divider_show_mode, dividerShowMode)

        typedArray.recycle()

        if (originDrawable == null) {
            //无自定义的drawable, 那么自绘.

            updateOriginDrawable()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        originDrawable?.apply {
            bounds = this@DslTabDivider.bounds
            draw(canvas)
        }
    }

    val _tabLayout: DslTabLayout?
        get() = if (callback is DslTabLayout) callback as DslTabLayout else null

    /**
     * [childIndex]位置前面是否需要分割线
     * */
    open fun haveBeforeDivider(childIndex: Int, childCount: Int): Boolean {
        val tabLayout = _tabLayout
        if (tabLayout != null && tabLayout.isHorizontal() && tabLayout.isLayoutRtl) {
            if (childIndex == 0) {
                return dividerShowMode and LinearLayout.SHOW_DIVIDER_END != 0
            }
            return dividerShowMode and LinearLayout.SHOW_DIVIDER_MIDDLE != 0
        }

        if (childIndex == 0) {
            return dividerShowMode and LinearLayout.SHOW_DIVIDER_BEGINNING != 0
        }
        return dividerShowMode and LinearLayout.SHOW_DIVIDER_MIDDLE != 0
    }

    /**
     * [childIndex]位置后面是否需要分割线
     * */
    open fun haveAfterDivider(childIndex: Int, childCount: Int): Boolean {
        val tabLayout = _tabLayout
        if (tabLayout != null && tabLayout.isHorizontal() && tabLayout.isLayoutRtl) {
            if (childIndex == childCount - 1) {
                return dividerShowMode and LinearLayout.SHOW_DIVIDER_BEGINNING != 0
            }
        }

        if (childIndex == childCount - 1) {
            return dividerShowMode and LinearLayout.SHOW_DIVIDER_END != 0
        }
        return false
    }
}