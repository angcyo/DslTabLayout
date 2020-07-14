package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity

/**
 * 未读数, 未读小红点, 角标绘制Drawable
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslBadgeDrawable : DslGradientDrawable() {

    val dslGravity = DslGravity()

    /**重力*/
    var badgeGravity: Int = Gravity.CENTER //Gravity.TOP or Gravity.RIGHT

    /**角标文本颜色*/
    var badgeTextColor = Color.WHITE

    /**角标的文本, 空字符串会绘制成小圆点*/
    var badgeText: String? = null

    /**角标的文本大小*/
    var badgeTextSize: Float = 12 * dp
        set(value) {
            field = value
            textPaint.textSize = field
        }

    /**圆点状态时的半径大小*/
    var badgeCircleRadius = 4 * dpi

    /**原点状态下, 单独配置的偏移*/
    var badgeCircleOffsetX: Int = 0
    var badgeCircleOffsetY: Int = 0

    /**额外偏移距离, 会根据[Gravity]自动取负值*/
    var badgeOffsetX: Int = 0
    var badgeOffsetY: Int = 0

    /**文本偏移*/
    var badgeTextOffsetX: Int = 0
    var badgeTextOffsetY: Int = 0

    /**圆点状态时无效*/
    var badgePaddingLeft = 0
    var badgePaddingRight = 0
    var badgePaddingTop = 0
    var badgePaddingBottom = 0

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        super.initAttribute(context, attributeSet)
        updateOriginDrawable()
    }

    override fun draw(canvas: Canvas) {
        //super.draw(canvas)

        if (badgeText == null) {
            return
        }

        val isCircle = TextUtils.isEmpty(badgeText)

        with(dslGravity) {
            gravity = badgeGravity
            setGravityBounds(bounds)

            if (isCircle) {
                gravityOffsetX = badgeCircleOffsetX
                gravityOffsetY = badgeCircleOffsetY
            } else {
                gravityOffsetX = badgeOffsetX
                gravityOffsetY = badgeOffsetY
            }

            val textWidth = textPaint.textWidth(badgeText)
            val textHeight = textPaint.textHeight()

            val drawWidth = if (isCircle) {
                badgeCircleRadius.toFloat()
            } else {
                textWidth + badgePaddingLeft + badgePaddingRight
            }

            val drawHeight = if (isCircle) {
                badgeCircleRadius.toFloat()
            } else {
                textHeight + badgePaddingTop + badgePaddingBottom
            }

            applyGravity(drawWidth, drawHeight) { centerX, centerY ->

                if (isCircle) {
                    textPaint.color = gradientSolidColor

                    val cx: Float
                    val cy: Float
                    if (gravity.isCenter()) {
                        cx = centerX.toFloat()
                        cy = centerY.toFloat()
                    } else {
                        cx = centerX.toFloat() + _gravityOffsetX
                        cy = centerY.toFloat() + _gravityOffsetY
                    }

                    canvas.drawCircle(
                        cx,
                        cy,
                        badgeCircleRadius.toFloat(),
                        textPaint
                    )

                    if (gradientStrokeWidth > 0 && gradientStrokeColor != Color.TRANSPARENT) {
                        val oldWidth = textPaint.strokeWidth
                        val oldStyle = textPaint.style

                        textPaint.color = gradientStrokeColor
                        textPaint.strokeWidth = gradientStrokeWidth.toFloat()
                        textPaint.style = Paint.Style.STROKE

                        canvas.drawCircle(
                            cx,
                            cy,
                            badgeCircleRadius.toFloat(),
                            textPaint
                        )

                        textPaint.strokeWidth = oldWidth
                        textPaint.style = oldStyle
                    }

                } else {
                    textPaint.color = badgeTextColor

                    val textDrawX: Float = centerX - textWidth / 2
                    val textDrawY: Float = centerY + textHeight / 2

                    val bgLeft = _gravityLeft
                    val bgTop = _gravityTop

                    //绘制背景
                    originDrawable?.apply {
                        setBounds(
                            bgLeft, bgTop,
                            (bgLeft + drawWidth).toInt(),
                            (bgTop + drawHeight).toInt()
                        )
                        draw(canvas)
                    }

                    //绘制文本
                    canvas.drawText(
                        badgeText!!,
                        textDrawX + badgeTextOffsetX,
                        textDrawY - textPaint.descent() + badgeTextOffsetY,
                        textPaint
                    )
                }
            }
        }
    }
}