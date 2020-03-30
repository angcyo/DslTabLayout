package com.angcyo.button

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.angcyo.dsltablayout.demo.R
import com.angcyo.dsltablayout.demo.toColorInt
import com.angcyo.tablayout.DslGradientDrawable
import java.util.*

/**
 * 支持配置 普通状态 按压状态 选择状态 Check状态 焦点状态 禁止状态
 * [android.R.attr.state_pressed]
 * [android.R.attr.state_selected]
 * [android.R.attr.state_checked]
 * [android.R.attr.state_enabled]
 * [android.R.attr.state_focused]
 *
 * 选择状态 和 Check状态 是同样的
 *
 * https://github.com/angcyo/DslButton
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class DslButton : AppCompatTextView {

    companion object {
        val ATTR_NORMAL = intArrayOf()
        val ATTR_PRESSED = intArrayOf(android.R.attr.state_pressed)
        val ATTR_SELECTED = intArrayOf(android.R.attr.state_selected)
        val ATTR_CHECKED = intArrayOf(android.R.attr.state_checked)
        val ATTR_FOCUSED = intArrayOf(android.R.attr.state_focused)
        val ATTR_DISABLE = intArrayOf(-android.R.attr.state_enabled)

        private const val NO_COLOR = -2
    }

    var buttonGradientStartColor = Color.TRANSPARENT
    var buttonGradientEndColor = Color.TRANSPARENT

    /**正常状态*/
    var normalDrawable: Drawable? = null
    var normalTextColor: Int = Color.WHITE
    var normalShape = GradientDrawable.RECTANGLE
    var normalSolidColor = NO_COLOR
    var normalStrokeColor = NO_COLOR
    var normalStrokeWidth = 0
    var normalDashWidth = 0f
    var normalDashGap = 0f
    var normalRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var normalGradientColors: IntArray? = null
    var normalGradientColorsOffsets: FloatArray? = null
    var normalGradientCenterX = 0.5f
    var normalGradientCenterY = 0.5f
    var normalGradientRadius = 0.5f
    var normalGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
    var normalGradientType = GradientDrawable.LINEAR_GRADIENT

    /**按下状态*/
    var pressDrawable: Drawable? = null
    var pressTextColor: Int = Color.WHITE
    var pressShape = GradientDrawable.RECTANGLE
    var pressSolidColor = NO_COLOR
    var pressStrokeColor = NO_COLOR
    var pressStrokeWidth = 0
    var pressDashWidth = 0f
    var pressDashGap = 0f
    var pressRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var pressGradientColors: IntArray? = null
//    var pressGradientColorsOffsets: FloatArray? = null
//    var pressGradientCenterX = 0.5f
//    var pressGradientCenterY = 0.5f
//    var pressGradientRadius = 0.5f
//    var pressGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
//    var pressGradientType = GradientDrawable.LINEAR_GRADIENT

    /**选中状态*/
    var selectDrawable: Drawable? = null
    var selectTextColor: Int = Color.WHITE
    var selectShape = GradientDrawable.RECTANGLE
    var selectSolidColor = NO_COLOR
    var selectStrokeColor = NO_COLOR
    var selectStrokeWidth = 0
    var selectDashWidth = 0f
    var selectDashGap = 0f
    var selectRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var selectGradientColors: IntArray? = null
//    var selectGradientColorsOffsets: FloatArray? = null
//    var selectGradientCenterX = 0.5f
//    var selectGradientCenterY = 0.5f
//    var selectGradientRadius = 0.5f
//    var selectGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
//    var selectGradientType = GradientDrawable.LINEAR_GRADIENT

//    /**check状态*/
//    var checkDrawable: Drawable? = null
//    var checkTextColor: Int = Color.WHITE
//    var checkShape = GradientDrawable.RECTANGLE
//    var checkSolidColor = NO_COLOR
//    var checkStrokeColor = NO_COLOR
//    var checkStrokeWidth = 0
//    var checkDashWidth = 0f
//    var checkDashGap = 0f
//    var checkRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
//    var checkGradientColors: IntArray? = null
//    var checkGradientColorsOffsets: FloatArray? = null
//    var checkGradientCenterX = 0.5f
//    var checkGradientCenterY = 0.5f
//    var checkGradientRadius = 0.5f
//    var checkGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
//    var checkGradientType = GradientDrawable.LINEAR_GRADIENT

    /**焦点状态*/
    var focusDrawable: Drawable? = null
    var focusTextColor: Int = Color.WHITE
    var focusShape = GradientDrawable.RECTANGLE
    var focusSolidColor = NO_COLOR
    var focusStrokeColor = NO_COLOR
    var focusStrokeWidth = 0
    var focusDashWidth = 0f
    var focusDashGap = 0f
    var focusRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var focusGradientColors: IntArray? = null
//    var focusGradientColorsOffsets: FloatArray? = null
//    var focusGradientCenterX = 0.5f
//    var focusGradientCenterY = 0.5f
//    var focusGradientRadius = 0.5f
//    var focusGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
//    var focusGradientType = GradientDrawable.LINEAR_GRADIENT

    /**禁用状态*/
    var disableDrawable: Drawable? = null
    var disableTextColor: Int = Color.WHITE
    var disableShape = GradientDrawable.RECTANGLE
    var disableSolidColor = NO_COLOR
    var disableStrokeColor = NO_COLOR
    var disableStrokeWidth = 0
    var disableDashWidth = 0f
    var disableDashGap = 0f
    var disableRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var disableGradientColors: IntArray? = null
//    var disableGradientColorsOffsets: FloatArray? = null
//    var disableGradientCenterX = 0.5f
//    var disableGradientCenterY = 0.5f
//    var disableGradientRadius = 0.5f
//    var disableGradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
//    var disableGradientType = GradientDrawable.LINEAR_GRADIENT

    /**是否使用Ripple, 需要API>=21*/
    var enableRipple = true
    var rippleColor = "#20000000".toColorInt()

    /**接管文本样式设置*/
    var enableTextStyle = true

    /**接管背景样式设置*/
    var enableBackgroundStyle = false

    constructor(context: Context) : super(context) {
        initAttribute(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttribute(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttribute(context, attrs, defStyleAttr)
    }

    /**xml属性读取*/
    fun initAttribute(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet,
                R.styleable.DslButton, defStyleAttr, 0)

        enableRipple =
            typedArray.getBoolean(R.styleable.DslButton_button_enable_ripple, enableRipple)
        enableTextStyle =
            typedArray.getBoolean(R.styleable.DslButton_button_enable_text_style, enableTextStyle)
        enableBackgroundStyle =
            typedArray.getBoolean(
                R.styleable.DslButton_button_enable_background_style,
                background == null //智能开启
            )
        rippleColor = typedArray.getColor(R.styleable.DslButton_button_ripple_color, rippleColor)

        //向下覆盖属性
        initButtonAttr(typedArray)

        //正常状态
        normalDrawable = typedArray.getDrawable(R.styleable.DslButton_button_normal_drawable)
        normalTextColor =
            typedArray.getColor(R.styleable.DslButton_button_normal_text_color, normalTextColor)
        normalShape = typedArray.getInt(R.styleable.DslButton_button_normal_shape, normalShape)
        normalSolidColor =
            typedArray.getColor(R.styleable.DslButton_button_normal_solid_color, normalSolidColor)
        normalStrokeColor =
            typedArray.getColor(R.styleable.DslButton_button_normal_stroke_color, normalStrokeColor)
        normalStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_normal_stroke_width,
            normalStrokeWidth
        )
        normalDashWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_normal_dash_width,
            normalDashWidth.toInt()
        ).toFloat()
        normalDashGap = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_normal_dash_gap,
            normalDashGap.toInt()
        ).toFloat()

//        normalGradientType =
//            typedArray.getInt(R.styleable.DslButton_button_normal_gradient_type, normalGradientType)
//
//        val normalOrientation =
//            typedArray.getInt(
//                R.styleable.DslButton_button_normal_gradient_orientation,
//                normalGradientOrientation.ordinal
//            )
//        normalGradientOrientation = GradientDrawable.Orientation.values()[normalOrientation]

        _initRadius(
            typedArray,
            R.styleable.DslButton_button_normal_radius,
            R.styleable.DslButton_button_normal_radii,
            normalRadii
        )

        normalGradientColors = _initGradientColors(
            typedArray,
            R.styleable.DslButton_button_normal_gradient_colors,
            R.styleable.DslButton_button_normal_gradient_start_color,
            R.styleable.DslButton_button_normal_gradient_end_color,
            normalGradientColors
        )

        //按下状态
        pressDrawable = typedArray.getDrawable(R.styleable.DslButton_button_press_drawable)
        pressTextColor =
            typedArray.getColor(R.styleable.DslButton_button_press_text_color, pressTextColor)
        pressShape = typedArray.getInt(R.styleable.DslButton_button_press_shape, pressShape)
        pressSolidColor =
            typedArray.getColor(R.styleable.DslButton_button_press_solid_color, pressSolidColor)
        pressStrokeColor =
            typedArray.getColor(R.styleable.DslButton_button_press_stroke_color, pressStrokeColor)
        pressStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_press_stroke_width,
            pressStrokeWidth
        )
        pressDashWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_press_dash_width,
            pressDashWidth.toInt()
        ).toFloat()
        pressDashGap = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_press_dash_gap,
            pressDashGap.toInt()
        ).toFloat()

        _initRadius(
            typedArray,
            R.styleable.DslButton_button_press_radius,
            R.styleable.DslButton_button_press_radii,
            pressRadii
        )

        pressGradientColors = _initGradientColors(
            typedArray,
            R.styleable.DslButton_button_press_gradient_colors,
            R.styleable.DslButton_button_press_gradient_start_color,
            R.styleable.DslButton_button_press_gradient_end_color,
            pressGradientColors
        )

        //选择状态
        selectDrawable = typedArray.getDrawable(R.styleable.DslButton_button_select_drawable)
        selectTextColor =
            typedArray.getColor(R.styleable.DslButton_button_select_text_color, selectTextColor)
        selectShape = typedArray.getInt(R.styleable.DslButton_button_select_shape, selectShape)
        selectSolidColor =
            typedArray.getColor(R.styleable.DslButton_button_select_solid_color, selectSolidColor)
        selectStrokeColor =
            typedArray.getColor(R.styleable.DslButton_button_select_stroke_color, selectStrokeColor)
        selectStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_select_stroke_width,
            selectStrokeWidth
        )
        selectDashWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_select_dash_width,
            selectDashWidth.toInt()
        ).toFloat()
        selectDashGap = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_select_dash_gap,
            selectDashGap.toInt()
        ).toFloat()

        _initRadius(
            typedArray,
            R.styleable.DslButton_button_select_radius,
            R.styleable.DslButton_button_select_radii,
            selectRadii
        )

        selectGradientColors = _initGradientColors(
            typedArray,
            R.styleable.DslButton_button_select_gradient_colors,
            R.styleable.DslButton_button_select_gradient_start_color,
            R.styleable.DslButton_button_select_gradient_end_color,
            selectGradientColors
        )

        //禁用状态
        disableDrawable = typedArray.getDrawable(R.styleable.DslButton_button_disable_drawable)
        disableTextColor =
            typedArray.getColor(R.styleable.DslButton_button_disable_text_color, disableTextColor)
        disableShape = typedArray.getInt(R.styleable.DslButton_button_disable_shape, disableShape)
        disableSolidColor =
            typedArray.getColor(R.styleable.DslButton_button_disable_solid_color, disableSolidColor)
        disableStrokeColor =
            typedArray.getColor(
                R.styleable.DslButton_button_disable_stroke_color,
                disableStrokeColor
            )
        disableStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_disable_stroke_width,
            disableStrokeWidth
        )
        disableDashWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_disable_dash_width,
            disableDashWidth.toInt()
        ).toFloat()
        disableDashGap = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_disable_dash_gap,
            disableDashGap.toInt()
        ).toFloat()

        _initRadius(
            typedArray,
            R.styleable.DslButton_button_disable_radius,
            R.styleable.DslButton_button_disable_radii,
            disableRadii
        )

        disableGradientColors = _initGradientColors(
            typedArray,
            R.styleable.DslButton_button_disable_gradient_colors,
            R.styleable.DslButton_button_disable_gradient_start_color,
            R.styleable.DslButton_button_disable_gradient_end_color,
            disableGradientColors
        )

        //焦点状态
        focusDrawable = typedArray.getDrawable(R.styleable.DslButton_button_focus_drawable)
        focusTextColor =
            typedArray.getColor(R.styleable.DslButton_button_focus_text_color, focusTextColor)
        focusShape = typedArray.getInt(R.styleable.DslButton_button_focus_shape, focusShape)
        focusSolidColor =
            typedArray.getColor(R.styleable.DslButton_button_focus_solid_color, focusSolidColor)
        focusStrokeColor =
            typedArray.getColor(
                R.styleable.DslButton_button_focus_stroke_color,
                focusStrokeColor
            )
        focusStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_focus_stroke_width,
            focusStrokeWidth
        )
        focusDashWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_focus_dash_width,
            focusDashWidth.toInt()
        ).toFloat()
        focusDashGap = typedArray.getDimensionPixelOffset(
            R.styleable.DslButton_button_focus_dash_gap,
            focusDashGap.toInt()
        ).toFloat()

        _initRadius(
            typedArray,
            R.styleable.DslButton_button_focus_radius,
            R.styleable.DslButton_button_focus_radii,
            focusRadii
        )

        focusGradientColors = _initGradientColors(
            typedArray,
            R.styleable.DslButton_button_focus_gradient_colors,
            R.styleable.DslButton_button_focus_gradient_start_color,
            R.styleable.DslButton_button_focus_gradient_end_color,
            focusGradientColors
        )

        val selected = typedArray.getBoolean(R.styleable.DslButton_button_is_selected, isSelected)

        typedArray.recycle()

        if (normalDrawable == null &&
            (normalGradientColors != null || normalSolidColor != NO_COLOR || normalStrokeColor != NO_COLOR)
        ) {
            updateNormalDrawable()
        }
        if (pressDrawable == null &&
            (pressGradientColors != null || pressSolidColor != NO_COLOR || pressStrokeColor != NO_COLOR)
        ) {
            updatePressDrawable()
        }
        if (selectDrawable == null &&
            (selectGradientColors != null || selectSolidColor != NO_COLOR || selectStrokeColor != NO_COLOR)
        ) {
            updateSelectDrawable()
        }
        if (focusDrawable == null &&
            (focusGradientColors != null || focusSolidColor != NO_COLOR || focusStrokeColor != NO_COLOR)
        ) {
            updateFocusDrawable()
        }
        if (disableDrawable == null &&
            (disableGradientColors != null || disableSolidColor != NO_COLOR || disableStrokeColor != NO_COLOR)
        ) {
            updateDisableDrawable()
        }

        updateButton()

        isSelected = selected
    }

    fun _initRadius(typedArray: TypedArray, radiusIndex: Int, radiiIndex: Int, values: FloatArray) {
        if (typedArray.hasValue(radiusIndex)) {
            val normalRadius = typedArray.getDimensionPixelOffset(radiusIndex, 0)
            Arrays.fill(values, normalRadius.toFloat())
        } else if (typedArray.hasValue(radiiIndex)) {
            typedArray.getString(radiiIndex)?.let {
                _fillRadii(values, it)
            }
        }
    }

    fun _initGradientColors(
        typedArray: TypedArray,
        attrIndex: Int,
        startIndex: Int,
        endIndex: Int,
        defaultValue: IntArray?
    ): IntArray? {
        return if (typedArray.hasValue(attrIndex)) {
            val normalColors = typedArray.getString(attrIndex)
            _fillColor(normalColors)
        } else if (typedArray.hasValue(startIndex) || typedArray.hasValue(endIndex)) {
            val startColor = typedArray.getColor(startIndex, Color.TRANSPARENT)
            val endColor = typedArray.getColor(endIndex, Color.TRANSPARENT)
            intArrayOf(startColor, endColor)
        } else {
            defaultValue
        }
    }

    open fun initButtonAttr(typedArray: TypedArray) {
        val bTextColor =
            typedArray.getColor(R.styleable.DslButton_button_text_color, currentTextColor)
        setButtonTextColor(bTextColor)

        if (typedArray.hasValue(R.styleable.DslButton_button_shape)) {
            val bShape = typedArray.getInt(R.styleable.DslButton_button_shape, normalShape)
            setButtonShape(bShape)
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_solid_color)) {
            val bSolidColor =
                typedArray.getColor(R.styleable.DslButton_button_solid_color, normalSolidColor)
            setButtonSolidColor(bSolidColor)
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_stroke_color)) {
            val bStrokeColor =
                typedArray.getColor(R.styleable.DslButton_button_stroke_color, normalStrokeColor)
            setButtonStrokeColor(bStrokeColor)
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_stroke_width)) {
            val bStrokeWidth = typedArray.getDimensionPixelOffset(
                R.styleable.DslButton_button_stroke_width,
                normalStrokeWidth
            )
            setButtonStrokeWidth(bStrokeWidth)
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_dash_width)) {
            val bDashWidth = typedArray.getDimensionPixelOffset(
                R.styleable.DslButton_button_dash_width,
                normalDashWidth.toInt()
            ).toFloat()
            setButtonDashWidth(bDashWidth)
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_dash_gap)) {
            val bDashGap = typedArray.getDimensionPixelOffset(
                R.styleable.DslButton_button_dash_gap,
                normalDashGap.toInt()
            ).toFloat()
            setButtonDashGap(bDashGap)
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_gradient_type)) {
            val bGradientType =
                typedArray.getInt(R.styleable.DslButton_button_gradient_type, normalGradientType)

            normalGradientType = bGradientType
//            pressGradientType = bGradientType
//            focusGradientType = bGradientType
//            selectGradientType = bGradientType
//            checkGradientType = bGradientType
//            disableGradientType = bGradientType
        }

        if (typedArray.hasValue(R.styleable.DslButton_button_gradient_orientation)) {
            val bOrientation =
                typedArray.getInt(
                    R.styleable.DslButton_button_gradient_orientation,
                    normalGradientOrientation.ordinal
                )
            val bGradientOrientation = GradientDrawable.Orientation.values()[bOrientation]

            normalGradientOrientation = bGradientOrientation
//            pressGradientOrientation = bGradientOrientation
//            focusGradientOrientation = bGradientOrientation
//            selectGradientOrientation = bGradientOrientation
//            checkGradientOrientation = bGradientOrientation
//            disableGradientOrientation = bGradientOrientation
        }

        val bRadius =
            typedArray.getDimensionPixelOffset(R.styleable.DslButton_button_radius, 0)
        if (bRadius > 0) {
            setButtonRadius(bRadius.toFloat())
        } else {
            typedArray.getString(R.styleable.DslButton_button_radii)?.let {
                _fillRadii(normalRadii, it)
                _fillRadii(pressRadii, it)
                _fillRadii(focusRadii, it)
                _fillRadii(selectRadii, it)
//                _fillRadii(checkRadii, it)
                _fillRadii(disableRadii, it)
            }
        }

        val bGradientColors =
            if (typedArray.hasValue(R.styleable.DslButton_button_gradient_colors)) {
                val normalColors =
                    typedArray.getString(R.styleable.DslButton_button_gradient_colors)
                _fillColor(normalColors)
            } else if (typedArray.hasValue(R.styleable.DslButton_button_gradient_start_color) || typedArray.hasValue(
                    R.styleable.DslButton_button_gradient_end_color
                )
            ) {
                buttonGradientStartColor = typedArray.getColor(
                    R.styleable.DslButton_button_gradient_start_color,
                    buttonGradientStartColor
                )
                buttonGradientEndColor = typedArray.getColor(
                    R.styleable.DslButton_button_gradient_end_color,
                    buttonGradientEndColor
                )
                intArrayOf(buttonGradientStartColor, buttonGradientEndColor)
            } else {
                normalGradientColors
            }

        setButtonGradientColors(bGradientColors)
    }

    /**向下覆盖, 文本颜色*/
    fun setButtonTextColor(bTextColor: Int) {
        normalTextColor = bTextColor
        pressTextColor = bTextColor
        focusTextColor = bTextColor
        selectTextColor = bTextColor
//            checkTextColor = bTextColor
        disableTextColor = bTextColor
    }

    fun setButtonShape(bShape: Int) {
        normalShape = bShape
        pressShape = bShape
        focusShape = bShape
        selectShape = bShape
//            checkShape = bShape
        disableShape = bShape
    }

    fun setButtonSolidColor(bSolidColor: Int) {
        normalSolidColor = bSolidColor
        pressSolidColor = bSolidColor
        focusSolidColor = bSolidColor
        selectSolidColor = bSolidColor
//            checkSolidColor = bSolidColor
        disableSolidColor = bSolidColor
    }

    fun setButtonStrokeColor(bStrokeColor: Int) {
        normalStrokeColor = bStrokeColor
        pressStrokeColor = bStrokeColor
        focusStrokeColor = bStrokeColor
        selectStrokeColor = bStrokeColor
//            checkStrokeColor = bStrokeColor
        disableStrokeColor = bStrokeColor
    }

    fun setButtonStrokeWidth(bStrokeWidth: Int) {
        normalStrokeWidth = bStrokeWidth
        pressStrokeWidth = bStrokeWidth
        focusStrokeWidth = bStrokeWidth
        selectStrokeWidth = bStrokeWidth
//            checkStrokeWidth = bStrokeWidth
        disableStrokeWidth = bStrokeWidth
    }

    fun setButtonDashWidth(bDashWidth: Float) {
        normalDashWidth = bDashWidth
        pressDashWidth = bDashWidth
        focusDashWidth = bDashWidth
        selectDashWidth = bDashWidth
//            checkDashWidth = bDashWidth
        disableDashWidth = bDashWidth
    }

    fun setButtonDashGap(bDashGap: Float) {
        normalDashGap = bDashGap
        pressDashGap = bDashGap
        focusDashGap = bDashGap
        selectDashGap = bDashGap
//            checkDashGap = bDashGap
        disableDashGap = bDashGap
    }

    fun setButtonRadius(bRadius: Float) {
        Arrays.fill(normalRadii, bRadius)
        Arrays.fill(pressRadii, bRadius)
        Arrays.fill(focusRadii, bRadius)
        Arrays.fill(selectRadii, bRadius)
//            Arrays.fill(checkRadii, bRadius)
        Arrays.fill(disableRadii, bRadius)
    }

    fun setButtonGradientColors(bGradientColors: IntArray?) {
        normalGradientColors = bGradientColors
        pressGradientColors =
            bGradientColors?.run { IntArray(bGradientColors.size) { bGradientColors[it] } }
        focusGradientColors =
            bGradientColors?.run { IntArray(bGradientColors.size) { bGradientColors[it] } }
        selectGradientColors =
            bGradientColors?.run { IntArray(bGradientColors.size) { bGradientColors[it] } }
//        checkGradientColors =
//            bGradientColors?.run { IntArray(bGradientColors.size) { bGradientColors[it] } }
        disableGradientColors =
            bGradientColors?.run { IntArray(bGradientColors.size) { bGradientColors[it] } }
    }

    fun _fillRadii(array: FloatArray, radii: String?) {
        if (radii.isNullOrEmpty()) {
            return
        }
        val split = radii.split(",")
        if (split.size != 8) {
            throw IllegalArgumentException("radii 需要8个值.")
        } else {
            val dp = context.resources.displayMetrics.density
            for (i in split.indices) {
                array[i] = split[i].toFloat() * dp
            }
        }
    }

    fun _fillColor(colors: String?): IntArray? {
        if (colors.isNullOrEmpty()) {
            return null
        }
        val split = colors.split(",")

        return IntArray(split.size) { split[it].toColorInt() }
    }

    fun Int.color(df: Int = Color.TRANSPARENT): Int = if (this == NO_COLOR) df else this

    open fun updateNormalDrawable() {
        (if (normalDrawable is DslGradientDrawable) {
            normalDrawable as DslGradientDrawable
        } else {
            DslGradientDrawable()
        }).apply {
            gradientShape = normalShape
            gradientSolidColor = normalSolidColor.color()
            gradientStrokeColor = normalStrokeColor.color()
            gradientStrokeWidth = normalStrokeWidth
            gradientDashWidth = normalDashWidth
            gradientDashGap = normalDashGap
            gradientRadii = normalRadii
            gradientColors = normalGradientColors
            gradientColorsOffsets = normalGradientColorsOffsets
            gradientCenterX = normalGradientCenterX
            gradientCenterY = normalGradientCenterY
            gradientRadius = normalGradientRadius
            gradientOrientation = normalGradientOrientation
            gradientType = normalGradientType
            normalDrawable = updateOriginDrawable()
        }
    }

    open fun updateDisableDrawable() {
        (if (disableDrawable is DslGradientDrawable) {
            disableDrawable as DslGradientDrawable
        } else {
            DslGradientDrawable()
        }).apply {
            gradientShape = disableShape
            gradientSolidColor = disableSolidColor.color()
            gradientStrokeColor = disableStrokeColor.color()
            gradientStrokeWidth = disableStrokeWidth
            gradientDashWidth = disableDashWidth
            gradientDashGap = disableDashGap
            gradientRadii = disableRadii
            gradientColors = disableGradientColors
            gradientColorsOffsets = normalGradientColorsOffsets
            gradientCenterX = normalGradientCenterX
            gradientCenterY = normalGradientCenterY
            gradientRadius = normalGradientRadius
            gradientOrientation = normalGradientOrientation
            gradientType = normalGradientType
            disableDrawable = updateOriginDrawable()
        }
    }

    open fun updateFocusDrawable() {
        (if (focusDrawable is DslGradientDrawable) {
            focusDrawable as DslGradientDrawable
        } else {
            DslGradientDrawable()
        }).apply {
            gradientShape = focusShape
            gradientSolidColor = focusSolidColor.color()
            gradientStrokeColor = focusStrokeColor.color()
            gradientStrokeWidth = focusStrokeWidth
            gradientDashWidth = focusDashWidth
            gradientDashGap = focusDashGap
            gradientRadii = focusRadii
            gradientColors = focusGradientColors
            gradientColorsOffsets = normalGradientColorsOffsets
            gradientCenterX = normalGradientCenterX
            gradientCenterY = normalGradientCenterY
            gradientRadius = normalGradientRadius
            gradientOrientation = normalGradientOrientation
            gradientType = normalGradientType
            focusDrawable = updateOriginDrawable()
        }
    }

    open fun updatePressDrawable() {
        (if (pressDrawable is DslGradientDrawable) {
            pressDrawable as DslGradientDrawable
        } else {
            DslGradientDrawable()
        }).apply {
            gradientShape = pressShape
            gradientSolidColor = pressSolidColor.color()
            gradientStrokeColor = pressStrokeColor.color()
            gradientStrokeWidth = pressStrokeWidth
            gradientDashWidth = pressDashWidth
            gradientDashGap = pressDashGap
            gradientRadii = pressRadii
            gradientColors = pressGradientColors
            gradientColorsOffsets = normalGradientColorsOffsets
            gradientCenterX = normalGradientCenterX
            gradientCenterY = normalGradientCenterY
            gradientRadius = normalGradientRadius
            gradientOrientation = normalGradientOrientation
            gradientType = normalGradientType
            pressDrawable = updateOriginDrawable()
        }
    }

    open fun updateSelectDrawable() {
        (if (selectDrawable is DslGradientDrawable) {
            selectDrawable as DslGradientDrawable
        } else {
            DslGradientDrawable()
        }).apply {
            gradientShape = selectShape
            gradientSolidColor = selectSolidColor.color()
            gradientStrokeColor = selectStrokeColor.color()
            gradientStrokeWidth = selectStrokeWidth
            gradientDashWidth = selectDashWidth
            gradientDashGap = selectDashGap
            gradientRadii = selectRadii
            gradientColors = selectGradientColors
            gradientColorsOffsets = normalGradientColorsOffsets
            gradientCenterX = normalGradientCenterX
            gradientCenterY = normalGradientCenterY
            gradientRadius = normalGradientRadius
            gradientOrientation = normalGradientOrientation
            gradientType = normalGradientType
            selectDrawable = updateOriginDrawable()
        }
    }

    open fun updateCheckDrawable() {
        //nothing
    }

    open fun updateDrawable() {
        updateNormalDrawable()
        updateDisableDrawable()
        updateFocusDrawable()
        updatePressDrawable()
        updateSelectDrawable()
        updateCheckDrawable()
        updateButton()
    }

    open fun updateButton() {
        if (enableTextStyle) {
            //文本状态颜色
            val textColorList =
                ColorStateList(
                    arrayOf(
                        ATTR_DISABLE,
                        ATTR_FOCUSED,
                        ATTR_PRESSED,
                        ATTR_SELECTED,
                        ATTR_CHECKED,
                        ATTR_NORMAL
                    ),
                    intArrayOf(
                        disableTextColor,
                        focusTextColor,
                        pressTextColor,
                        selectTextColor,
                        selectTextColor,
                        normalTextColor
                    )
                )
            setTextColor(textColorList)
        }

        if (enableBackgroundStyle) {

            //背景状态颜色
            val backgroundDrawable: Drawable?
            val stateDrawable = StateListDrawable()
            var contentDrawable: Drawable? = null

            var stateCount = 0

            disableDrawable?.run { stateCount++; stateDrawable.addState(ATTR_DISABLE, this) }
            focusDrawable?.run { stateCount++; stateDrawable.addState(ATTR_FOCUSED, this) }
            pressDrawable?.run { stateCount++; stateDrawable.addState(ATTR_PRESSED, this) }
            selectDrawable?.run { stateCount++; stateDrawable.addState(ATTR_SELECTED, this) }
            normalDrawable?.run { stateCount++; stateDrawable.addState(ATTR_NORMAL, this) }

            if (stateCount > 0) {
                contentDrawable = stateDrawable
            }

            backgroundDrawable =
                if (enableRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    RippleDrawable(ColorStateList.valueOf(rippleColor), contentDrawable, null)
                } else {
                    contentDrawable
                }

            ViewCompat.setBackground(this, backgroundDrawable)
        }
    }
}

/**颜色State, 请注意状态的顺序. 容易的状态, 放在最后*/
fun colorState(vararg pair: Pair<IntArray, Int>): ColorStateList {
    val statesList = mutableListOf<IntArray>()
    val colorsList = mutableListOf<Int>()

    pair.forEach {
        statesList.add(it.first)
        colorsList.add(it.second)
    }

    return ColorStateList(statesList.toTypedArray(), colorsList.toIntArray())
}