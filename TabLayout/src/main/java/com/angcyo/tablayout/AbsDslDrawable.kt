package com.angcyo.tablayout

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat

/**
 * 基础自绘Drawable
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

abstract class AbsDslDrawable : Drawable() {

    companion object {
        /**不绘制*/
        const val DRAW_TYPE_DRAW_NONE = 0x00

        /**[android.view.View.draw]*/
        const val DRAW_TYPE_DRAW_AFTER = 0x01
        const val DRAW_TYPE_DRAW_BEFORE = 0x02

        /**[android.view.View.onDraw]*/
        const val DRAW_TYPE_ON_DRAW_AFTER = 0x04
        const val DRAW_TYPE_ON_DRAW_BEFORE = 0x08
    }

    /**画笔*/
    val textPaint: TextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
            style = Paint.Style.FILL
            textSize = 12 * dp
        }
    }

    val drawRect = Rect()
    val drawRectF = RectF()

    /**需要在那个方法中触发绘制*/
    var drawType = DRAW_TYPE_ON_DRAW_AFTER

    /**xml属性读取*/
    open fun initAttribute(
        context: Context,
        attributeSet: AttributeSet? = null
    ) {
        //val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.xxx)
        //typedArray.recycle()
    }

    //<editor-fold desc="View相关方法">

    /**附着的[View]*/
    val attachView: View?
        get() = if (callback is View) callback as? View else null

    val isInEditMode: Boolean
        get() = attachView?.isInEditMode ?: false
    val paddingLeft: Int
        get() = attachView?.paddingLeft ?: 0
    val paddingRight: Int
        get() = attachView?.paddingRight ?: 0
    val paddingTop: Int
        get() = attachView?.paddingTop ?: 0
    val paddingBottom: Int
        get() = attachView?.paddingBottom ?: 0
    val viewHeight: Int
        get() = attachView?.measuredHeight ?: 0
    val viewWidth: Int
        get() = attachView?.measuredWidth ?: 0
    val viewDrawHeight: Int
        get() = viewHeight - paddingTop - paddingBottom
    val viewDrawWidth: Int
        get() = viewWidth - paddingLeft - paddingRight

    val isViewRtl: Boolean
        get() = attachView != null && ViewCompat.getLayoutDirection(attachView!!) == ViewCompat.LAYOUT_DIRECTION_RTL

    //</editor-fold desc="View相关方法">

    //<editor-fold desc="基类方法">

    /**核心方法, 绘制*/
    override fun draw(canvas: Canvas) {

    }

    override fun getIntrinsicWidth(): Int {
        return super.getIntrinsicWidth()
    }

    override fun getMinimumWidth(): Int {
        return super.getMinimumWidth()
    }

    override fun getIntrinsicHeight(): Int {
        return super.getIntrinsicHeight()
    }

    override fun getMinimumHeight(): Int {
        return super.getMinimumHeight()
    }

    override fun setAlpha(alpha: Int) {
        if (textPaint.alpha != alpha) {
            textPaint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun getAlpha(): Int {
        return textPaint.alpha
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
    }

    override fun setBounds(bounds: Rect) {
        super.setBounds(bounds)
    }

    //不透明度
    override fun getOpacity(): Int {
        return if (alpha < 255) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE
    }

    override fun getColorFilter(): ColorFilter? {
        return textPaint.colorFilter
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun mutate(): Drawable {
        return super.mutate()
    }

    override fun setDither(dither: Boolean) {
        textPaint.isDither = dither
        invalidateSelf()
    }

    override fun setFilterBitmap(filter: Boolean) {
        textPaint.isFilterBitmap = filter
        invalidateSelf()
    }

    override fun isFilterBitmap(): Boolean {
        return textPaint.isFilterBitmap
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
    }

    override fun onLevelChange(level: Int): Boolean {
        return super.onLevelChange(level)
    }

    override fun onStateChange(state: IntArray): Boolean {
        return super.onStateChange(state)
    }

    override fun setTintList(tint: ColorStateList?) {
        super.setTintList(tint)
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        super.setTintMode(tintMode)
    }

    override fun setTintBlendMode(blendMode: BlendMode?) {
        super.setTintBlendMode(blendMode)
    }

    override fun setHotspot(x: Float, y: Float) {
        super.setHotspot(x, y)
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setHotspotBounds(left, top, right, bottom)
    }

    override fun getHotspotBounds(outRect: Rect) {
        super.getHotspotBounds(outRect)
    }

    //</editor-fold desc="基类方法">
}