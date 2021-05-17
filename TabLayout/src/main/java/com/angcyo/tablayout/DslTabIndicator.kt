package com.angcyo.tablayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max

/**
 * 指示器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabIndicator(val tabLayout: DslTabLayout) : DslGradientDrawable() {

    companion object {

        /**非颜色值*/
        const val NO_COLOR = -2

        /**不绘制指示器*/
        const val INDICATOR_STYLE_NONE = 0

        /**指示器绘制[itemView]的背部, [itemView] 请不要设置background, 否则可能看不见*/
        const val INDICATOR_STYLE_BACKGROUND = 0x1

        /**指示器绘制在[itemView]的顶部*/
        const val INDICATOR_STYLE_TOP = 0x11

        /**指示器绘制在[itemView]的底部*/
        const val INDICATOR_STYLE_BOTTOM = 0x12

        /**指示器重力在开始的位置(横向左边, 纵向上边)*/
        const val INDICATOR_GRAVITY_START = 0x1

        /**指示器重力在结束的位置(横向右边, 纵向下边)*/
        const val INDICATOR_GRAVITY_END = 0x2

        /**指示器重力在中间*/
        const val INDICATOR_GRAVITY_CENTER = 0x4
    }

    /**指示器绘制的样式*/
    var indicatorStyle = INDICATOR_STYLE_BOTTOM

    /**优先将指示器显示在[DslTabLayout]的什么位置
     * [INDICATOR_GRAVITY_START] 开始的位置
     * [INDICATOR_GRAVITY_END] 结束的位置
     * [INDICATOR_GRAVITY_CENTER] 中间的位置*/
    var indicatorGravity = INDICATOR_GRAVITY_CENTER

    /**
     * 指示器在流向下一个位置时, 是否采用[Flow]流线的方式改变宽度
     * */
    var indicatorEnableFlow: Boolean = false

    /**当目标和当前的索引差值<=此值时, [Flow]效果才有效*/
    var indicatorFlowStep: Int = 1

    /**指示器绘制实体*/
    var indicatorDrawable: Drawable? = null
        set(value) {
            field = tintDrawableColor(value, indicatorColor)
        }

    /**过滤[indicatorDrawable]的颜色*/
    var indicatorColor: Int = NO_COLOR
        set(value) {
            field = value
            indicatorDrawable = indicatorDrawable
        }

    /**
     * 指示器的宽度
     * WRAP_CONTENT: [childView]内容的宽度,
     * MATCH_PARENT: [childView]的宽度
     * 40dp: 固定值
     * */
    var indicatorWidth = 0

    /**宽度补偿*/
    var indicatorWidthOffset = 0

    /**
     * 指示器的高度
     * WRAP_CONTENT: [childView]内容的高度,
     * MATCH_PARENT: [childView]的高度
     * 40dp: 固定值
     * */
    var indicatorHeight = 0

    /**高度补偿*/
    var indicatorHeightOffset = 0

    /**XY轴方向补偿*/
    var indicatorXOffset = 0

    /**会根据[indicatorStyle]自动取负值*/
    var indicatorYOffset = 0

    /**
     * 宽高[WRAP_CONTENT]时, 内容view的定位索引
     * */
    var indicatorContentIndex = -1

    /**切换时是否需要动画的支持*/
    var indicatorAnim = true

    init {
        callback = tabLayout
    }

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)

        indicatorDrawable = typedArray.getDrawable(R.styleable.DslTabLayout_tab_indicator_drawable)
        indicatorColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_indicator_color, indicatorColor)
        indicatorStyle = typedArray.getInt(
            R.styleable.DslTabLayout_tab_indicator_style,
            if (tabLayout.isHorizontal()) INDICATOR_STYLE_BOTTOM else INDICATOR_STYLE_TOP
        )
        indicatorGravity = typedArray.getInt(
            R.styleable.DslTabLayout_tab_indicator_gravity,
            indicatorGravity
        )

        //初始化指示器的高度和宽度
        if (indicatorStyle == INDICATOR_STYLE_BACKGROUND) {
            if (tabLayout.isHorizontal()) {
                indicatorWidth = ViewGroup.LayoutParams.MATCH_PARENT
                indicatorHeight = -1
            } else {
                indicatorHeight = ViewGroup.LayoutParams.MATCH_PARENT
                indicatorWidth = -1
            }
            indicatorWidth = typedArray.getLayoutDimension(
                R.styleable.DslTabLayout_tab_indicator_width,
                indicatorWidth
            )
            indicatorHeight = typedArray.getLayoutDimension(
                R.styleable.DslTabLayout_tab_indicator_height,
                indicatorHeight
            )
            indicatorXOffset = typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_indicator_x_offset,
                indicatorXOffset
            )
            indicatorYOffset = typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_indicator_y_offset,
                indicatorYOffset
            )
        } else {
            indicatorWidth = typedArray.getLayoutDimension(
                R.styleable.DslTabLayout_tab_indicator_width,
                if (tabLayout.isHorizontal()) ViewGroup.LayoutParams.MATCH_PARENT else 3 * dpi
            )
            indicatorHeight = typedArray.getLayoutDimension(
                R.styleable.DslTabLayout_tab_indicator_height,
                if (tabLayout.isHorizontal()) 3 * dpi else ViewGroup.LayoutParams.MATCH_PARENT
            )
            indicatorXOffset = typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_indicator_x_offset,
                if (tabLayout.isHorizontal()) 0 else 2 * dpi
            )
            indicatorYOffset = typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_indicator_y_offset,
                if (tabLayout.isHorizontal()) 2 * dpi else 0
            )
        }

        indicatorFlowStep =
            typedArray.getInt(R.styleable.DslTabLayout_tab_indicator_flow_step, indicatorFlowStep)
        indicatorEnableFlow = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_indicator_enable_flow,
            indicatorEnableFlow
        )

        indicatorWidthOffset = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_indicator_width_offset,
            indicatorWidthOffset
        )
        indicatorHeightOffset = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_indicator_height_offset,
            indicatorHeightOffset
        )
        indicatorContentIndex = typedArray.getInt(
            R.styleable.DslTabLayout_tab_indicator_content_index,
            indicatorContentIndex
        )
        indicatorAnim = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_indicator_anim,
            indicatorAnim
        )

        //代码构建Drawable
        gradientShape =
            typedArray.getInt(R.styleable.DslTabLayout_tab_indicator_shape, gradientShape)
        gradientSolidColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_indicator_solid_color,
                gradientSolidColor
            )
        gradientStrokeColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_indicator_stroke_color,
                gradientStrokeColor
            )
        gradientStrokeWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_indicator_stroke_width,
            gradientStrokeWidth
        )
        gradientDashWidth = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_indicator_dash_width,
            gradientDashWidth.toInt()
        ).toFloat()
        gradientDashGap = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_indicator_dash_gap,
            gradientDashGap.toInt()
        ).toFloat()

        val gradientRadius =
            typedArray.getDimensionPixelOffset(R.styleable.DslTabLayout_tab_indicator_radius, 0)
        if (gradientRadius > 0) {
            Arrays.fill(gradientRadii, gradientRadius.toFloat())
        } else {
            typedArray.getString(R.styleable.DslTabLayout_tab_indicator_radii)?.let {
                _fillRadii(gradientRadii, it)
            }
        }

        val gradientColors =
            typedArray.getString(R.styleable.DslTabLayout_tab_indicator_gradient_colors)

        this.gradientColors = if (gradientColors.isNullOrEmpty()) {
            val startColor = typedArray.getColor(
                R.styleable.DslTabLayout_tab_indicator_gradient_start_color,
                Color.TRANSPARENT
            )
            val endColor = typedArray.getColor(
                R.styleable.DslTabLayout_tab_indicator_gradient_end_color,
                Color.TRANSPARENT
            )
            if (startColor != endColor) {
                intArrayOf(startColor, endColor)
            } else {
                this.gradientColors
            }
        } else {
            _fillColor(gradientColors) ?: this.gradientColors
        }
        //...end

        typedArray.recycle()

        if (indicatorDrawable == null && isValidConfig()) {
            updateOriginDrawable()
        }
    }

    override fun updateOriginDrawable(): GradientDrawable? {
        val drawable = super.updateOriginDrawable()
        indicatorDrawable = originDrawable
        return drawable
    }

    open fun tintDrawableColor(drawable: Drawable?, color: Int): Drawable? {
        if (drawable == null || color == NO_COLOR) {
            return drawable
        }
        return drawable.tintDrawableColor(color)
    }

    /**根据指定[index]索引, 获取目标[View]*/
    open fun targetChildView(
        index: Int,
        onChildView: (childView: View, contentChildView: View?) -> Unit
    ) {
        tabLayout.dslSelector.visibleViewList.getOrNull(index)?.also { childView ->
            val lp = childView.layoutParams as DslTabLayout.LayoutParams

            //如果child强制指定了index, 就用指定的.
            val contentIndex =
                if (lp.indicatorContentIndex >= 0) lp.indicatorContentIndex else indicatorContentIndex

            if (contentIndex >= 0 && childView is ViewGroup && contentIndex in 0 until childView.childCount) {
                //有指定
                val contentChildView = childView.getChildAt(contentIndex)
                onChildView(childView, contentChildView)
            } else {
                //没有指定
                onChildView(childView, null)
            }
        }
    }

    /**
     * [childview]对应的中心x坐标
     * */
    open fun getChildTargetX(index: Int, gravity: Int = indicatorGravity): Int {

        var result = if (index > 0) tabLayout.maxWidth else 0

        targetChildView(index) { childView, contentChildView ->
            result = if (contentChildView == null) {
                when (gravity) {
                    INDICATOR_GRAVITY_START -> childView.left
                    INDICATOR_GRAVITY_END -> childView.right
                    else -> childView.left + childView.paddingLeft + childView.viewDrawWidth / 2
                }
            } else {
                when (gravity) {
                    INDICATOR_GRAVITY_START -> childView.left + contentChildView.left
                    INDICATOR_GRAVITY_END -> childView.left + contentChildView.right
                    else -> childView.left + contentChildView.left + contentChildView.paddingLeft + contentChildView.viewDrawWidth / 2
                }
            }
        }

        return result
    }

    open fun getChildTargetY(index: Int, gravity: Int = indicatorGravity): Int {

        var result = if (index > 0) tabLayout.maxHeight else 0

        targetChildView(index) { childView, contentChildView ->
            result = if (contentChildView == null) {
                when (gravity) {
                    INDICATOR_GRAVITY_START -> childView.top
                    INDICATOR_GRAVITY_END -> childView.bottom
                    else -> childView.top + childView.paddingTop + childView.viewDrawHeight / 2
                }
            } else {
                when (gravity) {
                    INDICATOR_GRAVITY_START -> childView.top + contentChildView.top
                    INDICATOR_GRAVITY_END -> childView.top + childView.bottom
                    else -> childView.top + contentChildView.top + contentChildView.paddingTop + contentChildView.viewDrawHeight / 2
                }
            }
        }

        return result
    }

    open fun getIndicatorDrawWidth(index: Int): Int {
        var result = indicatorWidth

        when (indicatorWidth) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                tabLayout.dslSelector.visibleViewList.getOrNull(index)?.also { childView ->
                    val lp = childView.layoutParams as DslTabLayout.LayoutParams

                    //如果child强制指定了index, 就用指定的.
                    val contentIndex =
                        if (lp.indicatorContentIndex >= 0) lp.indicatorContentIndex else indicatorContentIndex

                    result = childView.viewDrawWidth

                    if (contentIndex >= 0) {
                        //有指定
                        if (childView is ViewGroup && contentIndex in 0 until childView.childCount) {
                            val contentChildView = childView.getChildAt(contentIndex)

                            result = contentChildView.viewDrawWidth
                        }
                    }
                }
            }
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                tabLayout.dslSelector.visibleViewList.getOrNull(index)?.also { childView ->
                    result = childView.measuredWidth
                }
            }
        }

        return result + indicatorWidthOffset
    }

    open fun getIndicatorDrawHeight(index: Int): Int {
        var result = indicatorHeight

        when (indicatorHeight) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                tabLayout.dslSelector.visibleViewList.getOrNull(index)?.also { childView ->
                    val lp = childView.layoutParams as DslTabLayout.LayoutParams

                    //如果child强制指定了index, 就用指定的.
                    val contentIndex =
                        if (lp.indicatorContentIndex >= 0) lp.indicatorContentIndex else indicatorContentIndex

                    result = childView.viewDrawHeight

                    if (contentIndex >= 0) {
                        //有指定
                        if (childView is ViewGroup && contentIndex in 0 until childView.childCount) {
                            val contentChildView = childView.getChildAt(contentIndex)

                            result = contentChildView.viewDrawHeight
                        }
                    }
                }
            }
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                tabLayout.dslSelector.visibleViewList.getOrNull(index)?.also { childView ->
                    result = childView.measuredHeight
                }
            }
        }

        return result + indicatorHeightOffset
    }

    override fun draw(canvas: Canvas) {
        //super.draw(canvas)
        if (!isVisible || indicatorStyle == INDICATOR_STYLE_NONE || indicatorDrawable == null) {
            //不绘制
            return
        }

        if (tabLayout.isHorizontal()) {
            drawHorizontal(canvas)
        } else {
            drawVertical(canvas)
        }
    }

    fun drawHorizontal(canvas: Canvas) {
        val childSize = tabLayout.dslSelector.visibleViewList.size

        var currentIndex = currentIndex

        if (_targetIndex in 0 until childSize) {
            currentIndex = max(0, currentIndex)
        }

        if (currentIndex in 0 until childSize) {

        } else {
            //无效的index
            return
        }

        //"绘制$currentIndex:$currentSelectIndex $positionOffset".logi()

        val drawTargetX = getChildTargetX(currentIndex, INDICATOR_GRAVITY_CENTER)
        val drawWidth = getIndicatorDrawWidth(currentIndex)
        val drawHeight = getIndicatorDrawHeight(currentIndex)

        val drawLeft = drawTargetX - drawWidth / 2 + indicatorXOffset

        //动画过程中的left
        var animLeft = drawLeft
        //width
        var animWidth = drawWidth
        //动画执行过程中, 高度额外变大的值
        var animExHeight = 0

        if (_targetIndex in 0 until childSize && _targetIndex != currentIndex) {

            //动画过程参数计算变量
            val animStartLeft = drawLeft
            val animStartWidth = drawWidth
            val animEndWidth = getIndicatorDrawWidth(_targetIndex)
            val animEndLeft = getChildTargetX(
                _targetIndex,
                INDICATOR_GRAVITY_CENTER
            ) - animEndWidth / 2 + indicatorXOffset
            val animEndHeight = getIndicatorDrawHeight(_targetIndex)

            if (indicatorEnableFlow && (_targetIndex - currentIndex).absoluteValue <= indicatorFlowStep) {
                //激活了流动效果

                val flowEndWidth: Int
                if (_targetIndex > currentIndex) {
                    flowEndWidth = animEndLeft - animStartLeft + animEndWidth

                    //目标在右边
                    animLeft = if (positionOffset >= 0.5) {
                        (animStartLeft + (animEndLeft - animStartLeft) * (positionOffset - 0.5) / 0.5f).toInt()
                    } else {
                        animStartLeft
                    }
                } else {
                    flowEndWidth = animStartLeft - animEndLeft + animStartWidth

                    //目标在左边
                    animLeft = if (positionOffset >= 0.5) {
                        animEndLeft
                    } else {
                        (animStartLeft - (animStartLeft - animEndLeft) * positionOffset / 0.5f).toInt()
                    }
                }

                animWidth = if (positionOffset >= 0.5) {
                    (flowEndWidth - (flowEndWidth - animEndWidth) * (positionOffset - 0.5) / 0.5f).toInt()
                } else {
                    (animStartWidth + (flowEndWidth - animStartWidth) * positionOffset / 0.5f).toInt()
                }
            } else {
                if (_targetIndex > currentIndex) {
                    //目标在右边
                    animLeft =
                        (animStartLeft + (animEndLeft - animStartLeft) * positionOffset).toInt()
                } else {
                    //目标在左边
                    animLeft =
                        (animStartLeft - (animStartLeft - animEndLeft) * positionOffset).toInt()
                }

                //动画过程中的宽度
                animWidth =
                    (animStartWidth + (animEndWidth - animStartWidth) * positionOffset).toInt()
            }

            animExHeight = ((animEndHeight - drawHeight) * positionOffset).toInt()
        }

        val drawTop = when (indicatorStyle) {
            INDICATOR_STYLE_BOTTOM -> {
                //底部绘制
                viewHeight - drawHeight - indicatorYOffset
            }
            INDICATOR_STYLE_TOP -> {
                //顶部绘制
                0 + indicatorYOffset
            }
            else -> {
                //居中绘制
                paddingTop + viewDrawHeight / 2 - drawHeight / 2 + indicatorYOffset -
                        animExHeight +
                        (tabLayout._maxConvexHeight - _childConvexHeight(currentIndex)) / 2
            }
        }

        indicatorDrawable?.apply {
            setBounds(
                animLeft,
                drawTop,
                animLeft + animWidth,
                drawTop + drawHeight + animExHeight
            )
            draw(canvas)
        }
    }

    fun drawVertical(canvas: Canvas) {
        val childSize = tabLayout.dslSelector.visibleViewList.size

        var currentIndex = currentIndex

        if (_targetIndex in 0 until childSize) {
            currentIndex = max(0, currentIndex)
        }

        if (currentIndex in 0 until childSize) {

        } else {
            //无效的index
            return
        }

        //"绘制$currentIndex:$currentSelectIndex $positionOffset".logi()

        val drawTargetY = getChildTargetY(currentIndex, INDICATOR_GRAVITY_CENTER)
        val drawWidth = getIndicatorDrawWidth(currentIndex)
        val drawHeight = getIndicatorDrawHeight(currentIndex)

        val drawTop = drawTargetY - drawHeight / 2 + indicatorYOffset

        //动画过程中的top
        var animTop = drawTop
        //height
        var animHeight = drawHeight
        //动画执行过程中, 宽度额外变大的值
        var animExWidth = 0

        if (_targetIndex in 0 until childSize && _targetIndex != currentIndex) {

            //动画过程参数计算变量
            val animStartTop = drawTop
            val animStartHeight = drawHeight
            val animEndHeight = getIndicatorDrawHeight(_targetIndex)
            val animEndTop = getChildTargetY(
                _targetIndex,
                INDICATOR_GRAVITY_CENTER
            ) - animEndHeight / 2 + indicatorYOffset
            val animEndWidth = getIndicatorDrawWidth(_targetIndex)

            if (indicatorEnableFlow && (_targetIndex - currentIndex).absoluteValue <= indicatorFlowStep) {
                //激活了流动效果

                val flowEndHeight: Int
                if (_targetIndex > currentIndex) {
                    flowEndHeight = animEndTop - animStartTop + animEndHeight

                    //目标在下边
                    animTop = if (positionOffset >= 0.5) {
                        (animStartTop + (animEndTop - animStartTop) * (positionOffset - 0.5) / 0.5f).toInt()
                    } else {
                        animStartTop
                    }
                } else {
                    flowEndHeight = animStartTop - animEndTop + animStartHeight

                    //目标在上边
                    animTop = if (positionOffset >= 0.5) {
                        animEndTop
                    } else {
                        (animStartTop - (animStartTop - animEndTop) * positionOffset / 0.5f).toInt()
                    }
                }

                animHeight = if (positionOffset >= 0.5) {
                    (flowEndHeight - (flowEndHeight - animEndHeight) * (positionOffset - 0.5) / 0.5f).toInt()
                } else {
                    (animStartHeight + (flowEndHeight - animStartHeight) * positionOffset / 0.5f).toInt()
                }
            } else {
                if (_targetIndex > currentIndex) {
                    //目标在下边
                    animTop = (animStartTop + (animEndTop - animStartTop) * positionOffset).toInt()
                } else {
                    //目标在上边
                    animTop = (animStartTop - (animStartTop - animEndTop) * positionOffset).toInt()
                }

                //动画过程中的宽度
                animHeight =
                    (animStartHeight + (animEndHeight - animStartHeight) * positionOffset).toInt()
            }

            animExWidth = ((animEndWidth - drawWidth) * positionOffset).toInt()
        }

        val drawLeft = when (indicatorStyle) {
            INDICATOR_STYLE_BOTTOM -> {
                //右边/底部绘制
                viewWidth - drawWidth - indicatorXOffset
            }
            INDICATOR_STYLE_TOP -> {
                //左边/顶部绘制
                0 + indicatorXOffset
            }
            else -> {
                //居中绘制
                paddingLeft + indicatorXOffset + (viewDrawWidth / 2 - drawWidth / 2) -
                        (tabLayout._maxConvexHeight - _childConvexHeight(currentIndex)) / 2
            }
        }

        indicatorDrawable?.apply {
            setBounds(
                drawLeft,
                animTop,
                drawLeft + drawWidth + animExWidth,
                animTop + animHeight
            )
            draw(canvas)
        }
    }

    fun _childConvexHeight(index: Int): Int {
        if (attachView is ViewGroup) {
            ((attachView as ViewGroup).getChildAt(index).layoutParams as? DslTabLayout.LayoutParams)?.apply {
                return layoutConvexHeight
            }
        }
        return 0
    }

    /**
     * 距离[_targetIndex]的偏移比例.[0->1]的过程
     * */
    var positionOffset: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    /**当前绘制的index*/
    var currentIndex: Int = -1

    /**滚动目标的index*/
    var _targetIndex = -1
}