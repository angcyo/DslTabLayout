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

    /**使用中心坐标作为定位参考, 否则就是四条边*/
    var gravityRelativeCenter: Boolean = true

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

    //根据gravity调整后的offset
    var _gravityOffsetX = 0
    var _gravityOffsetY = 0

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

        //调整offset
        _gravityOffsetX = when (horizontalGravity) {
            Gravity.RIGHT -> -gravityOffsetX
            Gravity.END -> -gravityOffsetX
            else -> gravityOffsetX
        }
        _gravityOffsetY = when (verticalGravity) {
            Gravity.BOTTOM -> -gravityOffsetY
            else -> gravityOffsetY
        }

        //计算居中的坐标
        val centerX = when (horizontalGravity) {
            Gravity.CENTER_HORIZONTAL -> (gravityBounds.left + gravityBounds.width() / 2 + _gravityOffsetX).toInt()
            Gravity.RIGHT -> (gravityBounds.right + _gravityOffsetX - if (gravityRelativeCenter) 0f else _targetWidth / 2).toInt()
            Gravity.END -> (gravityBounds.right + _gravityOffsetX - if (gravityRelativeCenter) 0f else _targetWidth / 2).toInt()
            else -> (gravityBounds.left + _gravityOffsetX + if (gravityRelativeCenter) 0f else _targetWidth / 2).toInt()
        }

        val centerY = when (verticalGravity) {
            Gravity.CENTER_VERTICAL -> (gravityBounds.top + gravityBounds.height() / 2 + _gravityOffsetY).toInt()
            Gravity.BOTTOM -> (gravityBounds.bottom + _gravityOffsetY - if (gravityRelativeCenter) 0f else _targetHeight / 2).toInt()
            else -> (gravityBounds.top + _gravityOffsetY + if (gravityRelativeCenter) 0f else _targetHeight / 2).toInt()
        }

        //缓存
        _horizontalGravity = horizontalGravity
        _verticalGravity = verticalGravity
        _isCenterGravity = horizontalGravity == Gravity.CENTER_HORIZONTAL &&
                verticalGravity == Gravity.CENTER_VERTICAL

        _gravityLeft = (centerX - _targetWidth / 2).toInt()
        _gravityRight = (centerX + _targetWidth / 2).toInt()
        _gravityTop = (centerY - _targetHeight / 2).toInt()
        _gravityBottom = (centerY + _targetHeight / 2).toInt()

        //回调
        callback(centerX, centerY)
    }
}

/**
 * 默认计算出的是目标中心点坐标参考距离
 * [com.angcyo.drawable.DslGravity.getGravityRelativeCenter]
 * */
fun dslGravity(
    rect: RectF, //计算的矩形
    gravity: Int,  //重力
    width: Float, //放置目标的宽度
    height: Float, //放置目标的高度
    offsetX: Int = 0, //额外的偏移,会根据[gravity]进行左右|上下取反
    offsetY: Int = 0,
    callback: (dslGravity: DslGravity, centerX: Int, centerY: Int) -> Unit
): DslGravity {
    val _dslGravity = DslGravity()
    _dslGravity.setGravityBounds(rect)
    _config(_dslGravity, gravity, width, height, offsetX, offsetY, callback)
    return _dslGravity
}

/**
 * 默认计算出的是目标中心点坐标参考距离
 * [com.angcyo.drawable.DslGravity.getGravityRelativeCenter]
 * */
fun dslGravity(
    rect: Rect, //计算的矩形
    gravity: Int,  //重力
    width: Float, //放置目标的宽度
    height: Float, //放置目标的高度
    offsetX: Int = 0, //额外的偏移,会根据[gravity]进行左右|上下取反
    offsetY: Int = 0,
    callback: (dslGravity: DslGravity, centerX: Int, centerY: Int) -> Unit
): DslGravity {
    val _dslGravity = DslGravity()
    _dslGravity.setGravityBounds(rect)
    _config(_dslGravity, gravity, width, height, offsetX, offsetY, callback)
    return _dslGravity
}

private fun _config(
    _dslGravity: DslGravity,
    gravity: Int,  //重力
    width: Float, //放置目标的宽度
    height: Float, //放置目标的高度
    offsetX: Int = 0, //额外的偏移,会根据[gravity]进行左右|上下取反
    offsetY: Int = 0,
    callback: (dslGravity: DslGravity, centerX: Int, centerY: Int) -> Unit
) {
    _dslGravity.gravity = gravity
    _dslGravity.gravityOffsetX = offsetX
    _dslGravity.gravityOffsetY = offsetY
    _dslGravity.applyGravity(width, height) { centerX, centerY ->
        callback(_dslGravity, centerX, centerY)
    }
}

/**居中*/
fun Int.isCenter(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return verticalGravity == Gravity.CENTER_VERTICAL && horizontalGravity == Gravity.CENTER_HORIZONTAL
}

fun Int.isLeft(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return horizontalGravity == Gravity.LEFT
}

fun Int.isRight(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return horizontalGravity == Gravity.RIGHT
}

fun Int.isTop(): Boolean {
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    return verticalGravity == Gravity.TOP
}

fun Int.isBottom(): Boolean {
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    return verticalGravity == Gravity.BOTTOM
}