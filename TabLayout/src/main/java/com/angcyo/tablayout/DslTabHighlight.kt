package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
open class DslTabHighlight(val tabLayout: DslTabLayout) : DslGradientDrawable() {

    /**需要绘制的Drawable*/
    var highlightDrawable: Drawable? = null

    /**宽度测量模式*/
    var highlightWidth = ViewGroup.LayoutParams.MATCH_PARENT

    /**高度测量模式*/
    var highlightHeight = ViewGroup.LayoutParams.MATCH_PARENT

    /**宽度补偿*/
    var highlightWidthOffset = 0

    /**高度补偿*/
    var highlightHeightOffset = 0

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        //super.initAttribute(context, attributeSet)

        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        highlightDrawable = typedArray.getDrawable(R.styleable.DslTabLayout_tab_highlight_drawable)

        highlightWidth = typedArray.getLayoutDimension(
            R.styleable.DslTabLayout_tab_highlight_width,
            highlightWidth
        )
        highlightHeight = typedArray.getLayoutDimension(
            R.styleable.DslTabLayout_tab_highlight_height,
            highlightHeight
        )

        highlightWidthOffset = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_highlight_width_offset,
            highlightWidthOffset
        )
        highlightHeightOffset = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_highlight_height_offset,
            highlightHeightOffset
        )

        typedArray.recycle()

        if (highlightDrawable == null && isValidConfig()) {
            updateOriginDrawable()
        }
    }

    override fun updateOriginDrawable(): GradientDrawable? {
        val drawable = super.updateOriginDrawable()
        highlightDrawable = originDrawable
        return drawable
    }

    override fun draw(canvas: Canvas) {
        //super.draw(canvas)
        val itemView = tabLayout.currentItemView
        if (itemView != null) {
            val lp = itemView.layoutParams

            if (lp is DslTabLayout.LayoutParams) {
                lp.highlightDrawable ?: highlightDrawable
            } else {
                highlightDrawable
            }?.apply {

                val drawWidth: Int = when (highlightWidth) {
                    ViewGroup.LayoutParams.MATCH_PARENT -> itemView.measuredWidth
                    ViewGroup.LayoutParams.WRAP_CONTENT -> intrinsicWidth
                    else -> highlightWidth
                } + highlightWidthOffset

                val drawHeight: Int = when (highlightHeight) {
                    ViewGroup.LayoutParams.MATCH_PARENT -> itemView.measuredHeight
                    ViewGroup.LayoutParams.WRAP_CONTENT -> intrinsicHeight
                    else -> highlightHeight
                } + highlightHeightOffset

                val centerX: Int = itemView.left + (itemView.right - itemView.left) / 2
                val centerY: Int = itemView.top + (itemView.bottom - itemView.top) / 2

                setBounds(
                    centerX - drawWidth / 2,
                    centerY - drawHeight / 2,
                    centerX + drawWidth / 2,
                    centerY + drawHeight / 2
                )

                draw(canvas)
                canvas.save()
                if (tabLayout.isHorizontal()) {
                    canvas.translate(itemView.left.toFloat(), 0f)
                } else {
                    canvas.translate(0f, itemView.top.toFloat())
                }
                itemView.draw(canvas)
                canvas.restore()
            }
        }
    }
}