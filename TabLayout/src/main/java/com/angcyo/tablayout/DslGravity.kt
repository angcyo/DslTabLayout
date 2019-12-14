package com.angcyo.tablayout

import android.graphics.Rect
import android.graphics.RectF
import android.view.Gravity

/**
 * [android.view.Gravity] 辅助计算类
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class DslGravity {

    /**束缚范围*/
    val gravityBounds: RectF = RectF()

    /**束缚重力*/
    var gravity: Int = Gravity.LEFT or Gravity.TOP

    /**额外偏移距离, 会根据[Gravity]自动取负值*/
    var gravityOffsetX: Int = 0
    var gravityOffsetY: Int = 0

    fun setGravityBounds(rectF: RectF) {
        gravityBounds.set(rectF)
    }

    fun setGravityBounds(rect: Rect) {
        gravityBounds.set(rect)
    }

    fun setGravityBounds(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        gravityBounds.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    fun setGravityBounds(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        gravityBounds.set(left, top, right, bottom)
    }

    //计算后的属性
    var _horizontalGravity: Int = Gravity.LEFT
    var _verticalGravity: Int = Gravity.TOP
    var _isCenterGravity: Boolean = false
    var _targetWidth = 0f
    var _targetHeight = 0f
    var _gravityLeft = 0
    var _gravityTop = 0
    var _gravityRight = 0
    var _gravityBottom = 0

    /**根据[gravity]返回在[gravityBounds]中的[left] [top]位置*/
    fun applyGravity(
        width: Float = _targetWidth,
        height: Float = _targetHeight,
        callback: (centerX: Int, centerY: Int) -> Unit
    ) {

        _targetWidth = width
        _targetHeight = height

        val layoutDirection = 0
        val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

        val centerX = when (horizontalGravity) {
            Gravity.CENTER_HORIZONTAL -> (gravityBounds.left + gravityBounds.width() / 2 + gravityOffsetX).toInt()
            Gravity.RIGHT -> (gravityBounds.right - _targetWidth / 2 - gravityOffsetX).toInt()
            else -> (gravityBounds.left + _targetWidth / 2 + gravityOffsetX).toInt()
        }

        val centerY = when (verticalGravity) {
            Gravity.CENTER_VERTICAL -> (gravityBounds.top + gravityBounds.height() / 2 + gravityOffsetY).toInt()
            Gravity.BOTTOM -> (gravityBounds.bottom - _targetHeight / 2 - gravityOffsetY).toInt()
            else -> (gravityBounds.top + _targetHeight / 2 + gravityOffsetY).toInt()
        }

        _horizontalGravity = horizontalGravity
        _verticalGravity = verticalGravity
        _isCenterGravity = horizontalGravity == Gravity.CENTER_HORIZONTAL &&
                verticalGravity == Gravity.CENTER_VERTICAL

        _gravityLeft = (centerX - _targetWidth / 2).toInt()
        _gravityRight = (centerX + _targetWidth / 2).toInt()
        _gravityTop = (centerY - _targetHeight / 2).toInt()
        _gravityBottom = (centerY + _targetHeight / 2).toInt()

        callback(centerX, centerY)
    }
}