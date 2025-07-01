package com.angcyo.tablayout

import android.graphics.Rect
import android.graphics.RectF
import android.view.Gravity
import androidx.core.view.GravityCompat

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

    /**使用中心坐标作为定位参考, 否则就是四条边
     * 就是将目标的中点放在[gravityBounds]的[gravity]位置*/
    var gravityRelativeCenter: Boolean = true

    /**额外偏移距离, 会根据[Gravity]自动取负值*/
    var gravityOffsetX: Float = 0f
    var gravityOffsetY: Float = 0f

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

    /**计算后的属性, 可以直接读取使用*/
    var _gravityLeft = 0f
    var _gravityTop = 0f
    var _gravityRight = 0f
    var _gravityBottom = 0f

    //根据gravity调整后的offset
    var _gravityOffsetX = 0f
    var _gravityOffsetY = 0f

    /**根据[gravity]返回在[gravityBounds]中的[left] [top]位置*/
    fun applyGravity(
        width: Float = _targetWidth,
        height: Float = _targetHeight,
        callback: (centerX: Float, centerY: Float) -> Unit
    ) {

        _targetWidth = width
        _targetHeight = height

        val layoutDirection = 0
        val absoluteGravity = GravityCompat.getAbsoluteGravity(gravity, layoutDirection)
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
            Gravity.CENTER_HORIZONTAL -> gravityBounds.left + gravityBounds.width() / 2 + _gravityOffsetX
            Gravity.RIGHT -> gravityBounds.right + _gravityOffsetX - if (gravityRelativeCenter) 0f else _targetWidth / 2
            Gravity.END -> gravityBounds.right + _gravityOffsetX - if (gravityRelativeCenter) 0f else _targetWidth / 2
            else -> gravityBounds.left + _gravityOffsetX + if (gravityRelativeCenter) 0f else _targetWidth / 2
        }

        val centerY = when (verticalGravity) {
            Gravity.CENTER_VERTICAL -> gravityBounds.top + gravityBounds.height() / 2 + _gravityOffsetY
            Gravity.BOTTOM -> gravityBounds.bottom + _gravityOffsetY - if (gravityRelativeCenter) 0f else _targetHeight / 2
            else -> gravityBounds.top + _gravityOffsetY + if (gravityRelativeCenter) 0f else _targetHeight / 2
        }

        //缓存
        _horizontalGravity = horizontalGravity
        _verticalGravity = verticalGravity
        _isCenterGravity = horizontalGravity == Gravity.CENTER_HORIZONTAL &&
                verticalGravity == Gravity.CENTER_VERTICAL

        _gravityLeft = centerX - _targetWidth / 2
        _gravityRight = centerX + _targetWidth / 2
        _gravityTop = centerY - _targetHeight / 2
        _gravityBottom = centerY + _targetHeight / 2

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
    offsetX: Float = 0f, //额外的偏移,会根据[gravity]进行左右|上下取反
    offsetY: Float = 0f,
    gravityRelativeCenter: Boolean = true,
    callback: (dslGravity: DslGravity, centerX: Float, centerY: Float) -> Unit
): DslGravity {
    val _dslGravity = DslGravity()
    _dslGravity.setGravityBounds(rect)
    _config(_dslGravity, gravity, width, height, offsetX, offsetY, gravityRelativeCenter, callback)
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
    offsetX: Float = 0f, //额外的偏移,会根据[gravity]进行左右|上下取反
    offsetY: Float = 0f,
    gravityRelativeCenter: Boolean = true,
    callback: (dslGravity: DslGravity, centerX: Float, centerY: Float) -> Unit
): DslGravity {
    val _dslGravity = DslGravity()
    _dslGravity.setGravityBounds(rect)
    _config(_dslGravity, gravity, width, height, offsetX, offsetY, gravityRelativeCenter, callback)
    return _dslGravity
}

private fun _config(
    _dslGravity: DslGravity,
    gravity: Int,  //重力
    width: Float, //放置目标的宽度
    height: Float, //放置目标的高度
    offsetX: Float = 0f, //额外的偏移,会根据[gravity]进行左右|上下取反
    offsetY: Float = 0f,
    gravityRelativeCenter: Boolean = true,
    callback: (dslGravity: DslGravity, centerX: Float, centerY: Float) -> Unit
) {
    _dslGravity.gravity = gravity
    _dslGravity.gravityOffsetX = offsetX
    _dslGravity.gravityOffsetY = offsetY
    _dslGravity.gravityRelativeCenter = gravityRelativeCenter
    _dslGravity.applyGravity(width, height) { centerX, centerY ->
        callback(_dslGravity, centerX, centerY)
    }
}

/**Gravity居中*/
fun Int.isGravityCenter(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return verticalGravity == Gravity.CENTER_VERTICAL && horizontalGravity == Gravity.CENTER_HORIZONTAL
}

/**Gravity水平居中*/
fun Int.isGravityCenterHorizontal(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return horizontalGravity == Gravity.CENTER_HORIZONTAL
}

/**Gravity垂直居中*/
fun Int.isGravityCenterVertical(): Boolean {
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    return verticalGravity == Gravity.CENTER_VERTICAL
}

/**Gravity左*/
fun Int.isGravityLeft(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return horizontalGravity == Gravity.LEFT
}

/**Gravity右*/
fun Int.isGravityRight(): Boolean {
    val layoutDirection = 0
    val absoluteGravity = Gravity.getAbsoluteGravity(this, layoutDirection)
    val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

    return horizontalGravity == Gravity.RIGHT
}

/**Gravity上*/
fun Int.isGravityTop(): Boolean {
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    return verticalGravity == Gravity.TOP
}

/**Gravity下*/
fun Int.isGravityBottom(): Boolean {
    val verticalGravity = this and Gravity.VERTICAL_GRAVITY_MASK
    return verticalGravity == Gravity.BOTTOM
}

/**获取垂直方向上的Gravity*/
val Int._verticalGravity: Int
    get() = this and Gravity.VERTICAL_GRAVITY_MASK

/**获取水平方向上的Gravity*/
val Int._horizontalGravity: Int
    get() = this and Gravity.HORIZONTAL_GRAVITY_MASK