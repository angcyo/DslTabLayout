package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat

/**
 * 边框
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabBorder : DslGradientDrawable() {

    /**
     * 是否要接管[itemView]背景的绘制
     * [updateItemBackground]
     * */
    var borderDrawItemBackground: Boolean = true

    var borderBackgroundDrawable: Drawable? = null

    /**宽度补偿*/
    var borderBackgroundWidthOffset: Int = 0
    /**高度补偿*/
    var borderBackgroundHeightOffset: Int = 0

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        super.initAttribute(context, attributeSet)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)

        val borderBackgroundColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_border_solid_color, gradientSolidColor)

        gradientStrokeColor = typedArray.getColor(
            R.styleable.DslTabLayout_tab_border_stroke_color,
            gradientStrokeColor
        )
        gradientStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_border_stroke_width,
            2 * dpi
        )
        val radiusSize =
            typedArray.getDimensionPixelOffset(R.styleable.DslTabLayout_tab_border_radius_size, 0)

        cornerRadius(radiusSize.toFloat())

        originDrawable = typedArray.getDrawable(R.styleable.DslTabLayout_tab_border_drawable)

        borderDrawItemBackground = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_border_draw_item_background,
            borderDrawItemBackground
        )

        borderBackgroundWidthOffset = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_border_item_background_width_offset,
            borderBackgroundWidthOffset
        )

        borderBackgroundHeightOffset = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_border_item_background_height_offset,
            borderBackgroundHeightOffset
        )

        typedArray.recycle()

        if (originDrawable == null) {
            //无自定义的drawable, 那么自绘.
            borderBackgroundDrawable = DslGradientDrawable().configDrawable {
                gradientSolidColor = borderBackgroundColor
                gradientRadii = this@DslTabBorder.gradientRadii
            }.originDrawable

            updateOriginDrawable()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        originDrawable?.apply {
            setBounds(
                paddingLeft,
                paddingBottom,
                viewWidth - paddingRight,
                viewHeight - paddingBottom
            )
            draw(canvas)
        }
    }

    fun drawBorderBackground(canvas: Canvas) {
        super.draw(canvas)

        borderBackgroundDrawable?.apply {
            setBounds(
                paddingLeft,
                paddingBottom,
                viewWidth - paddingRight,
                viewHeight - paddingBottom
            )
            draw(canvas)
        }
    }

    var itemSelectBgDrawable: Drawable? = null
    var itemDeselectBgDrawable: Drawable? = null

    /**开启边框绘制后, [itemView]的背景也需要负责设置*/
    open fun updateItemBackground(
        tabLayout: DslTabLayout,
        itemView: View,
        index: Int,
        select: Boolean
    ) {

        if (!borderDrawItemBackground) {
            return
        }

        if (select) {

            val isFirst = index == 0
            val isLast = index == tabLayout.dslSelector.visibleViewList.size - 1

            val drawable = DslGradientDrawable().configDrawable {
                gradientWidthOffset = borderBackgroundWidthOffset
                gradientHeightOffset = borderBackgroundHeightOffset

                gradientSolidColor = this@DslTabBorder.gradientStrokeColor

                if (isFirst && isLast) {
                    gradientRadii = this@DslTabBorder.gradientRadii
                } else if (isFirst) {
                    gradientRadii = floatArrayOf(
                        this@DslTabBorder.gradientRadii[0],
                        this@DslTabBorder.gradientRadii[1],
                        0f,
                        0f,
                        0f,
                        0f,
                        this@DslTabBorder.gradientRadii[6],
                        this@DslTabBorder.gradientRadii[7]
                    )
                } else if (isLast) {
                    gradientRadii = floatArrayOf(
                        0f,
                        0f,
                        this@DslTabBorder.gradientRadii[2],
                        this@DslTabBorder.gradientRadii[3],
                        this@DslTabBorder.gradientRadii[4],
                        this@DslTabBorder.gradientRadii[5],
                        0f,
                        0f
                    )
                }
            }

            itemSelectBgDrawable = drawable

            ViewCompat.setBackground(itemView, itemSelectBgDrawable)
        } else {
            ViewCompat.setBackground(itemView, itemDeselectBgDrawable)
        }
    }
}