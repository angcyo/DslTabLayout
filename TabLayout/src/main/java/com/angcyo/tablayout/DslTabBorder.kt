package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
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

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        super.initAttribute(context, attributeSet)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)

        gradientSolidColor =
            typedArray.getColor(R.styleable.DslTabLayout_dsl_border_solid_color, gradientSolidColor)
        gradientStrokeColor = typedArray.getColor(
            R.styleable.DslTabLayout_dsl_border_stroke_color,
            gradientStrokeColor
        )
        gradientStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_dsl_border_stroke_width,
            gradientStrokeWidth
        )
        val radiusSize =
            typedArray.getDimensionPixelOffset(R.styleable.DslTabLayout_dsl_border_radius_size, 0)

        cornerRadius(radiusSize.toFloat())

        originDrawable = typedArray.getDrawable(R.styleable.DslTabLayout_dsl_border_drawable)

        borderDrawItemBackground = typedArray.getBoolean(
            R.styleable.DslTabLayout_dsl_border_draw_item_background,
            borderDrawItemBackground
        )

        typedArray.recycle()

        if (originDrawable == null) {
            //无自定义的drawable, 那么自绘.
            updateDrawable()
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

            val drawable = GradientDrawable()

            with(drawable) {
                setColor(gradientStrokeColor)
                if (isFirst && isLast) {
                    cornerRadii = gradientRadii
                } else if (isFirst) {
                    cornerRadii = floatArrayOf(
                        gradientRadii[0],
                        gradientRadii[1],
                        0f,
                        0f,
                        0f,
                        0f,
                        gradientRadii[6],
                        gradientRadii[7]
                    )
                } else if (isLast) {
                    cornerRadii = floatArrayOf(
                        0f,
                        0f,
                        gradientRadii[2],
                        gradientRadii[3],
                        gradientRadii[4],
                        gradientRadii[5],
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