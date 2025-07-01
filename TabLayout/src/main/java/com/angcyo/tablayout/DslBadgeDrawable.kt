package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import kotlin.math.max

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

    /**角标的文本(默认居中绘制文本,暂不支持修改), 空字符串会绘制成小圆点
     * null 不绘制角标
     * ""   空字符绘制圆点
     * 其他  正常绘制
     * */
    var badgeText: String? = null

    /**角标的文本大小*/
    var badgeTextSize: Float = 12 * dp
        set(value) {
            field = value
            textPaint.textSize = field
        }

    /**当[badgeText]只有1个字符时, 使用圆形背景*/
    var badgeAutoCircle: Boolean = true

    /**圆点状态时的半径大小*/
    var badgeCircleRadius = 4 * dpi

    /**原点状态下, 单独配置的偏移
     * 默认等于[badgeTextOffsetX] [badgeTextOffsetY]*/
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

    /**最小的高度大小, px. 大于0生效; 圆点时属性无效*/
    var badgeMinHeight = -2

    /**最小的宽度大小, px. 大于0生效; 圆点时属性无效;
     * -1 表示使用使用计算出来的高度值*/
    var badgeMinWidth = -2

    //计算属性
    val textWidth: Float
        get() = textPaint.textWidth(badgeText)

    //最大的宽度
    val maxWidth: Int
        get() = max(
            textWidth.toInt(),
            originDrawable?.minimumWidth ?: 0
        ) + badgePaddingLeft + badgePaddingRight

    //最大的高度
    val maxHeight: Int
        get() = max(
            textHeight.toInt(),
            originDrawable?.minimumHeight ?: 0
        ) + badgePaddingTop + badgePaddingBottom

    val textHeight: Float
        get() = textPaint.textHeight()

    /**是否是圆点状态*/
    val isCircle: Boolean
        get() = TextUtils.isEmpty(badgeText)

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
            gravity = if (isViewRtl) {
                when (badgeGravity) {
                    Gravity.RIGHT -> Gravity.LEFT
                    Gravity.LEFT -> Gravity.RIGHT
                    else -> badgeGravity
                }
            } else {
                badgeGravity
            }

            setGravityBounds(bounds)

            if (isCircle) {
                gravityOffsetX = badgeCircleOffsetX.toFloat()
                gravityOffsetY = badgeCircleOffsetY.toFloat()
            } else {
                gravityOffsetX = badgeOffsetX.toFloat()
                gravityOffsetY = badgeOffsetY.toFloat()
            }

            val textWidth = textPaint.textWidth(badgeText)
            val textHeight = textPaint.textHeight()

            val drawHeight = if (isCircle) {
                badgeCircleRadius.toFloat() * 2
            } else {
                val height = textHeight + badgePaddingTop + badgePaddingBottom
                if (badgeMinHeight > 0) {
                    max(height, badgeMinHeight.toFloat())
                } else {
                    height
                }
            }

            val drawWidth = if (isCircle) {
                badgeCircleRadius.toFloat() * 2
            } else {
                val width = textWidth + badgePaddingLeft + badgePaddingRight
                if (badgeMinWidth == -1) {
                    max(width, drawHeight)
                } else if (badgeMinWidth > 0) {
                    max(width, badgeMinWidth.toFloat())
                } else {
                    width
                }
            }

            applyGravity(drawWidth, drawHeight) { centerX, centerY ->

                if (isCircle) {
                    //圆点状态
                    textPaint.color = gradientSolidColor

                    //圆心计算
                    val cx: Float
                    val cy: Float
                    if (gravity.isGravityCenter()) {
                        cx = centerX
                        cy = centerY
                    } else {
                        cx = centerX + _gravityOffsetX
                        cy = centerY + _gravityOffsetY
                    }

                    //绘制圆
                    textPaint.color = gradientSolidColor
                    canvas.drawCircle(
                        cx,
                        cy,
                        badgeCircleRadius.toFloat(),
                        textPaint
                    )

                    //圆的描边
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
                    //文本+背景状态
                    val oldWidth = textPaint.strokeWidth
                    val oldStyle = textPaint.style

                    textPaint.color = badgeTextColor

                    val textDrawX: Float = centerX - textWidth / 2
                    val textDrawY: Float = centerY + textHeight / 2

                    val bgLeft = _gravityLeft.toInt()
                    val bgTop = _gravityTop.toInt()

                    //绘制背景
                    if (badgeAutoCircle && badgeText?.length == 1) {
                        if (gradientSolidColor != Color.TRANSPARENT) {
                            textPaint.style = Paint.Style.FILL
                            textPaint.color = gradientSolidColor
                            canvas.drawCircle(
                                centerX,
                                centerY,
                                max(maxWidth, maxHeight).toFloat() / 2,
                                textPaint
                            )
                        }
                        if (gradientStrokeWidth > 0 && gradientStrokeColor != Color.TRANSPARENT) {
                            textPaint.color = gradientStrokeColor
                            textPaint.style = Paint.Style.STROKE
                            canvas.drawCircle(
                                centerX,
                                centerY,
                                max(maxWidth, maxHeight).toFloat() / 2,
                                textPaint
                            )
                        }
                    } else {
                        originDrawable?.apply {
                            setBounds(
                                bgLeft, bgTop,
                                (bgLeft + drawWidth).toInt(),
                                (bgTop + drawHeight).toInt()
                            )
                            draw(canvas)
                        }
                    }

                    //绘制文本
                    textPaint.color = badgeTextColor
                    canvas.drawText(
                        badgeText!!,
                        textDrawX + badgeTextOffsetX,
                        textDrawY - textPaint.descent() + badgeTextOffsetY,
                        textPaint
                    )

                    //
                    textPaint.strokeWidth = oldWidth
                    textPaint.style = oldStyle
                }
            }
        }
    }

    override fun getIntrinsicWidth(): Int {
        val width = if (isCircle) {
            badgeCircleRadius * 2 + badgeCircleOffsetX
        } else if (badgeAutoCircle && badgeText?.length == 1) {
            max(maxWidth, maxHeight) + badgeCircleOffsetX
        } else {
            maxWidth + badgeOffsetX
        }
        return max(badgeMinWidth, width)
    }

    override fun getIntrinsicHeight(): Int {
        val height = if (isCircle) {
            badgeCircleRadius * 2 + badgeCircleOffsetY
        } else if (badgeAutoCircle && badgeText?.length == 1) {
            max(maxWidth, maxHeight) + badgeCircleOffsetY
        } else {
            maxHeight
        }
        return max(badgeMinHeight, height)
    }
}