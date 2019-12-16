package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

    /**额外偏移距离, 会根据[Gravity]自动取负值*/
    var badgeOffsetX: Int = 0
    var badgeOffsetY: Int = 0

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

        with(dslGravity) {
            gravity = badgeGravity
            setGravityBounds(bounds)
            gravityOffsetX = badgeOffsetX
            gravityOffsetY = badgeOffsetY

            val textWidth = textPaint.textWidth(badgeText)
            val textHeight = textPaint.textHeight()

            val drawWidth = textWidth + badgePaddingLeft + badgePaddingRight
            val drawHeight = textHeight + badgePaddingTop + badgePaddingBottom

            applyGravity(drawWidth, drawHeight) { centerX, centerY ->

                if (TextUtils.isEmpty(badgeText)) {
                    textPaint.color = gradientSolidColor

                    canvas.drawCircle(
                        centerX.toFloat(),
                        centerY.toFloat(),
                        badgeCircleRadius.toFloat(),
                        textPaint
                    )
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
                        textDrawX,
                        textDrawY - textPaint.descent(),
                        textPaint
                    )
                }
            }
        }
    }
}