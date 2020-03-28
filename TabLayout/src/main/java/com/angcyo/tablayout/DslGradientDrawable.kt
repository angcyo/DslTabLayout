package com.angcyo.tablayout

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.annotation.IntDef
import java.util.*

/**
 * 用来构建GradientDrawable
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslGradientDrawable : AbsDslDrawable() {

    /**形状*/
    @Shape
    var gradientShape = GradientDrawable.RECTANGLE

    /**填充的颜色*/
    var gradientSolidColor = Color.TRANSPARENT

    /**边框的颜色*/
    var gradientStrokeColor = Color.TRANSPARENT

    /**边框的宽度*/
    var gradientStrokeWidth = 0

    /**蚂蚁线的宽度*/
    var gradientDashWidth = 0f

    /**蚂蚁线之间的间距*/
    var gradientDashGap = 0f

    /**
     * 四个角, 8个设置点的圆角信息
     * 从 左上y轴->左上x轴->右上x轴->右上y轴..., 开始设置.
     */
    var gradientRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    /**颜色渐变*/
    var gradientColors: IntArray? = null
    var gradientColorsOffsets: FloatArray? = null

    /**渐变中心点坐标*/
    var gradientCenterX = 0.5f
    var gradientCenterY = 0.5f

    /**渐变半径, 非比例值, 是px值. [GradientDrawable.RADIAL_GRADIENT]类型才有效*/
    var gradientRadius = 0.5f

    /** 渐变方向, 默认从左到右 */
    var gradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT

    /** 渐变类型 */
    @GradientType
    var gradientType = GradientDrawable.LINEAR_GRADIENT

    /**真正绘制的[Drawable]*/
    var originDrawable: Drawable? = null

    /**宽度补偿*/
    var gradientWidthOffset: Int = 0

    /**高度补偿*/
    var gradientHeightOffset: Int = 0

    /**当前的配置, 是否能生成有效的[GradientDrawable]*/
    open fun isValidConfig(): Boolean {
        return gradientSolidColor != Color.TRANSPARENT ||
                gradientStrokeColor != Color.TRANSPARENT ||
                gradientColors != null
    }

    fun _fillRadii(array: FloatArray, radii: String?) {
        if (radii.isNullOrEmpty()) {
            return
        }
        val split = radii.split(",")
        if (split.size != 8) {
            throw IllegalArgumentException("radii 需要8个值.")
        } else {
            val dp = Resources.getSystem().displayMetrics.density
            for (i in split.indices) {
                array[i] = split[i].toFloat() * dp
            }
        }
    }

    fun fillRadii(radius: Float) {
        Arrays.fill(gradientRadii, radius)
    }

    fun fillRadii(radius: Int) {
        _fillRadii(gradientRadii, radius.toFloat())
    }

    fun _fillRadii(array: FloatArray, radius: Float) {
        Arrays.fill(array, radius)
    }

    fun _fillRadii(array: FloatArray, radius: Int) {
        _fillRadii(array, radius.toFloat())
    }

    fun _fillColor(colors: String?): IntArray? {
        if (colors.isNullOrEmpty()) {
            return null
        }
        val split = colors.split(",")

        return IntArray(split.size) {
            val str = split[it]
            if (str.startsWith("#")) {
                Color.parseColor(str)
            } else {
                str.toInt()
            }
        }
    }

    /**构建或者更新[originDrawable]*/
    open fun updateOriginDrawable(): GradientDrawable? {
        val drawable: GradientDrawable? = when (originDrawable) {
            null -> GradientDrawable()
            is GradientDrawable -> originDrawable as GradientDrawable
            else -> {
                null
            }
        }

        drawable?.apply {
            bounds = this@DslGradientDrawable.bounds

            shape = gradientShape
            setStroke(
                gradientStrokeWidth,
                gradientStrokeColor,
                gradientDashWidth,
                gradientDashGap
            )
            setColor(gradientSolidColor)
            cornerRadii = gradientRadii

            if (gradientColors != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setGradientCenter(
                        this@DslGradientDrawable.gradientCenterX,
                        this@DslGradientDrawable.gradientCenterY
                    )
                }
                gradientRadius = this@DslGradientDrawable.gradientRadius
                gradientType = this@DslGradientDrawable.gradientType
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    orientation = gradientOrientation
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setColors(gradientColors, gradientColorsOffsets)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    colors = gradientColors
                }
            }

            originDrawable = this
            invalidateSelf()
        }

        return drawable
    }

    open fun configDrawable(config: DslGradientDrawable.() -> Unit): DslGradientDrawable {
        this.config()
        updateOriginDrawable()
        return this
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        originDrawable?.apply {
            setBounds(
                this@DslGradientDrawable.bounds.left - gradientWidthOffset / 2,
                this@DslGradientDrawable.bounds.top - gradientHeightOffset / 2,
                this@DslGradientDrawable.bounds.right + gradientWidthOffset / 2,
                this@DslGradientDrawable.bounds.bottom + gradientHeightOffset / 2
            )
            draw(canvas)
        }
    }

    //<editor-fold desc="圆角相关配置">

    /**
     * 4个角, 8个点 圆角配置
     */
    fun cornerRadii(radii: FloatArray) {
        gradientRadii = radii
    }

    fun cornerRadius(radii: Float) {
        Arrays.fill(gradientRadii, radii)
    }

    fun cornerRadius(
        leftTop: Float = 0f,
        rightTop: Float = 0f,
        rightBottom: Float = 0f,
        leftBottom: Float = 0f
    ) {
        gradientRadii[0] = leftTop
        gradientRadii[1] = leftTop
        gradientRadii[2] = rightTop
        gradientRadii[3] = rightTop
        gradientRadii[4] = rightBottom
        gradientRadii[5] = rightBottom
        gradientRadii[6] = leftBottom
        gradientRadii[7] = leftBottom
    }

    /**
     * 只配置左边的圆角
     */
    fun cornerRadiiLeft(radii: Float) {
        gradientRadii[0] = radii
        gradientRadii[1] = radii
        gradientRadii[6] = radii
        gradientRadii[7] = radii
    }

    fun cornerRadiiRight(radii: Float) {
        gradientRadii[2] = radii
        gradientRadii[3] = radii
        gradientRadii[4] = radii
        gradientRadii[5] = radii
    }

    fun cornerRadiiTop(radii: Float) {
        gradientRadii[0] = radii
        gradientRadii[1] = radii
        gradientRadii[2] = radii
        gradientRadii[3] = radii
    }

    fun cornerRadiiBottom(radii: Float) {
        gradientRadii[4] = radii
        gradientRadii[5] = radii
        gradientRadii[6] = radii
        gradientRadii[7] = radii
    }

    //</editor-fold desc="圆角相关配置">

    //<editor-fold desc="传递属性">
    override fun setColorFilter(colorFilter: ColorFilter?) {
        super.setColorFilter(colorFilter)
        originDrawable?.colorFilter = colorFilter
    }

    override fun setTintList(tint: ColorStateList?) {
        super.setTintList(tint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            originDrawable?.setTintList(tint)
        }
    }

    override fun setState(stateSet: IntArray): Boolean {
        return originDrawable?.setState(stateSet) ?: super.setState(stateSet)
    }

    override fun getState(): IntArray {
        return originDrawable?.state ?: super.getState()
    }

    //</editor-fold desc="传递属性">
}

@IntDef(
    GradientDrawable.RECTANGLE,
    GradientDrawable.OVAL,
    GradientDrawable.LINE,
    GradientDrawable.RING
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Shape

@IntDef(
    GradientDrawable.LINEAR_GRADIENT,
    GradientDrawable.RADIAL_GRADIENT,
    GradientDrawable.SWEEP_GRADIENT
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class GradientType

/**快速创建[GradientDrawable]*/
fun dslGradientDrawable(action: DslGradientDrawable.() -> Unit): GradientDrawable {
    return DslGradientDrawable().run {
        action()
        updateOriginDrawable()!!
    }
}