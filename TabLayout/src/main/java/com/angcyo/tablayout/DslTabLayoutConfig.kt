package com.angcyo.tablayout

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import com.angcyo.tablayout.DslTabIndicator.Companion.NO_COLOR
import kotlin.math.max
import kotlin.math.min

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabLayoutConfig(val tabLayout: DslTabLayout) : DslSelectorConfig() {

    /**是否开启文本颜色*/
    var tabEnableTextColor = true
        set(value) {
            field = value
            if (field) {
                tabEnableIcoColor = true
            }
        }

    /**是否开启颜色渐变效果*/
    var tabEnableGradientColor = false
        set(value) {
            field = value
            if (field) {
                tabEnableIcoGradientColor = true
            }
        }

    /**是否激活指示器的颜色渐变效果*/
    var tabEnableIndicatorGradientColor = false

    /**选中的文本颜色*/
    var tabSelectColor: Int = Color.WHITE //Color.parseColor("#333333")

    /**未选中的文本颜色*/
    var tabDeselectColor: Int = Color.parseColor("#999999")

    /**是否开启Bold, 文本加粗*/
    var tabEnableTextBold = false

    /**是否使用粗体字体的方式设置粗体, 否则使用[Paint.FAKE_BOLD_TEXT_FLAG]
     * 需要先激活[tabEnableTextBold]*/
    var tabUseTypefaceBold = false

    /**是否开启图标颜色*/
    var tabEnableIcoColor = true

    /**是否开启图标颜色渐变效果*/
    var tabEnableIcoGradientColor = false

    /**选中的图标颜色*/
    var tabIcoSelectColor: Int = NO_COLOR
        get() {
            return if (field == NO_COLOR) tabSelectColor else field
        }

    /**未选中的图标颜色*/
    var tabIcoDeselectColor: Int = NO_COLOR
        get() {
            return if (field == NO_COLOR) tabDeselectColor else field
        }

    /**是否开启scale渐变效果*/
    var tabEnableGradientScale = false

    /**最小缩放的比例*/
    var tabMinScale = 0.8f

    /**最大缩放的比例*/
    var tabMaxScale = 1.2f

    /**是否开启字体大小渐变效果*/
    var tabEnableGradientTextSize = true

    /**tab中文本字体未选中时的字体大小, >0时激活*/
    var tabTextMinSize = -1f

    /**tab中文本字体选中时的字体大小, >0时激活*/
    var tabTextMaxSize = -1f

    /**渐变效果实现的回调*/
    var tabGradientCallback = TabGradientCallback()

    /**指定文本控件的id, 所有文本属性改变, 将会发生在这个控件上.
     * 如果指定的控件不存在, 控件会降权至[ItemView]*/
    @IdRes
    var tabTextViewId: Int = View.NO_ID

    /**指定图标控件的id*/
    @IdRes
    var tabIconViewId: Int = View.NO_ID

    /**返回用于配置文本样式的控件*/
    var onGetTextStyleView: (itemView: View, index: Int) -> TextView? = { itemView, _ ->
        if (tabTextViewId == View.NO_ID) {
            var tv: TextView? = if (itemView is TextView) itemView else null

            if (tabLayout.tabIndicator.indicatorContentIndex != -1) {
                itemView.getChildOrNull(tabLayout.tabIndicator.indicatorContentIndex)?.let {
                    if (it is TextView) {
                        tv = it
                    }
                }
            }

            if (tabLayout.tabIndicator.indicatorContentId != View.NO_ID) {
                itemView.findViewById<View>(tabLayout.tabIndicator.indicatorContentId)?.let {
                    if (it is TextView) {
                        tv = it
                    }
                }
            }

            val lp = itemView.layoutParams
            if (lp is DslTabLayout.LayoutParams) {
                if (lp.indicatorContentIndex != -1 && itemView is ViewGroup) {
                    itemView.getChildOrNull(lp.indicatorContentIndex)?.let {
                        if (it is TextView) {
                            tv = it
                        }
                    }
                }

                if (lp.indicatorContentId != View.NO_ID) {
                    itemView.findViewById<View>(lp.indicatorContentId)?.let {
                        if (it is TextView) {
                            tv = it
                        }
                    }
                }

                if (lp.contentTextViewIndex != -1 && itemView is ViewGroup) {
                    itemView.getChildOrNull(lp.contentTextViewIndex)?.let {
                        if (it is TextView) {
                            tv = it
                        }
                    }
                }

                if (lp.contentTextViewId != View.NO_ID) {
                    itemView.findViewById<View>(lp.contentTextViewId)?.let {
                        if (it is TextView) {
                            tv = it
                        }
                    }
                }
            }
            tv
        } else {
            itemView.findViewById(tabTextViewId)
        }
    }

    /**返回用于配置ico样式的控件*/
    var onGetIcoStyleView: (itemView: View, index: Int) -> View? = { itemView, _ ->
        if (tabIconViewId == View.NO_ID) {
            var iv: View? = itemView

            if (tabLayout.tabIndicator.indicatorContentIndex != -1) {
                itemView.getChildOrNull(tabLayout.tabIndicator.indicatorContentIndex)?.let {
                    iv = it
                }
            }

            if (tabLayout.tabIndicator.indicatorContentId != View.NO_ID) {
                itemView.findViewById<View>(tabLayout.tabIndicator.indicatorContentId)?.let {
                    iv = it
                }
            }

            val lp = itemView.layoutParams
            if (lp is DslTabLayout.LayoutParams) {
                if (lp.indicatorContentIndex != -1 && itemView is ViewGroup) {
                    iv = itemView.getChildOrNull(lp.indicatorContentIndex)
                }

                if (lp.indicatorContentId != View.NO_ID) {
                    itemView.findViewById<View>(lp.indicatorContentId)?.let {
                        iv = it
                    }
                }

                if (lp.contentIconViewIndex != -1 && itemView is ViewGroup) {
                    iv = itemView.getChildOrNull(lp.contentIconViewIndex)
                }

                if (lp.contentIconViewId != View.NO_ID) {
                    itemView.findViewById<View>(lp.contentIconViewId)?.let {
                        iv = it
                    }
                }
            }
            iv
        } else {
            itemView.findViewById(tabIconViewId)
        }
    }

    /**获取渐变结束时,指示器的颜色.*/
    var onGetGradientIndicatorColor: (fromIndex: Int, toIndex: Int, positionOffset: Float) -> Int =
        { fromIndex, toIndex, positionOffset ->
            tabLayout.tabIndicator.indicatorColor
        }

    init {
        onStyleItemView = { itemView, index, select ->
            onUpdateItemStyle(itemView, index, select)
        }
        onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
            val toIndex = selectIndexList.last()
            tabLayout._viewPagerDelegate?.onSetCurrentItem(fromIndex, toIndex, reselect, fromUser)
        }
    }

    /**xml属性读取*/
    open fun initAttribute(context: Context, attributeSet: AttributeSet? = null) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)

        tabSelectColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_select_color, tabSelectColor)
        tabDeselectColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_deselect_color,
                tabDeselectColor
            )
        tabIcoSelectColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_ico_select_color, NO_COLOR)
        tabIcoDeselectColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_ico_deselect_color, NO_COLOR)

        tabEnableTextColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_text_color,
            tabEnableTextColor
        )
        tabEnableIndicatorGradientColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_indicator_gradient_color,
            tabEnableIndicatorGradientColor
        )
        tabEnableGradientColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_gradient_color,
            tabEnableGradientColor
        )
        tabEnableIcoColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_ico_color,
            tabEnableIcoColor
        )
        tabEnableIcoGradientColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_ico_gradient_color,
            tabEnableIcoGradientColor
        )

        tabEnableTextBold = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_text_bold,
            tabEnableTextBold
        )

        tabUseTypefaceBold = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_use_typeface_bold,
            tabUseTypefaceBold
        )

        tabEnableGradientScale = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_gradient_scale,
            tabEnableGradientScale
        )
        tabMinScale = typedArray.getFloat(R.styleable.DslTabLayout_tab_min_scale, tabMinScale)
        tabMaxScale = typedArray.getFloat(R.styleable.DslTabLayout_tab_max_scale, tabMaxScale)

        tabEnableGradientTextSize = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_gradient_text_size,
            tabEnableGradientTextSize
        )
        if (typedArray.hasValue(R.styleable.DslTabLayout_tab_text_min_size)) {
            tabTextMinSize = typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_text_min_size,
                tabTextMinSize.toInt()
            ).toFloat()
        }
        if (typedArray.hasValue(R.styleable.DslTabLayout_tab_text_max_size)) {
            tabTextMaxSize = typedArray.getDimensionPixelOffset(
                R.styleable.DslTabLayout_tab_text_max_size,
                tabTextMaxSize.toInt()
            ).toFloat()
        }

        tabTextViewId =
            typedArray.getResourceId(R.styleable.DslTabLayout_tab_text_view_id, tabTextViewId)
        tabIconViewId =
            typedArray.getResourceId(R.styleable.DslTabLayout_tab_icon_view_id, tabIconViewId)

        typedArray.recycle()
    }

    /**更新item的样式*/
    open fun onUpdateItemStyle(itemView: View, index: Int, select: Boolean) {
        //"$itemView\n$index\n$select".logw()

        (onGetTextStyleView(itemView, index))?.apply {
            //文本加粗
            paint?.apply {
                if (tabEnableTextBold && select) {
                    //设置粗体
                    if (tabUseTypefaceBold) {
                        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    } else {
                        flags = flags or Paint.FAKE_BOLD_TEXT_FLAG
                        isFakeBoldText = true
                    }
                } else {
                    //取消粗体
                    if (tabUseTypefaceBold) {
                        typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    } else {
                        flags = flags and Paint.FAKE_BOLD_TEXT_FLAG.inv()
                        isFakeBoldText = false
                    }
                }
            }

            if (tabEnableTextColor) {
                //文本颜色
                setTextColor(if (select) tabSelectColor else tabDeselectColor)
            }

            if (tabTextMaxSize > 0 || tabTextMinSize > 0) {
                //文本字体大小
                val minTextSize = min(tabTextMinSize, tabTextMaxSize)
                val maxTextSize = max(tabTextMinSize, tabTextMaxSize)
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if (select) maxTextSize else minTextSize
                )
            }
        }

        if (tabEnableIcoColor) {
            onGetIcoStyleView(itemView, index)?.apply {
                _updateIcoColor(this, if (select) tabIcoSelectColor else tabIcoDeselectColor)
            }
        }

        if (tabEnableGradientScale) {
            itemView.scaleX = if (select) tabMaxScale else tabMinScale
            itemView.scaleY = if (select) tabMaxScale else tabMinScale
        }

        if (tabLayout.drawBorder) {
            tabLayout.tabBorder?.updateItemBackground(tabLayout, itemView, index, select)
        }
    }

    /**
     * [DslTabLayout]滚动时回调.
     * */
    open fun onPageIndexScrolled(fromIndex: Int, toIndex: Int, positionOffset: Float) {

    }

    /**
     * [onPageIndexScrolled]
     * */
    open fun onPageViewScrolled(fromView: View?, toView: View, positionOffset: Float) {
        //"$fromView\n$toView\n$positionOffset".logi()

        if (fromView != toView) {

            val fromIndex = tabLayout.tabIndicator.currentIndex
            val toIndex = tabLayout.tabIndicator._targetIndex

            if (tabEnableIndicatorGradientColor) {
                val startColor = onGetGradientIndicatorColor(fromIndex, fromIndex, 0f)
                val endColor = onGetGradientIndicatorColor(fromIndex, toIndex, positionOffset)

                tabLayout.tabIndicator.indicatorColor =
                    evaluateColor(positionOffset, startColor, endColor)
            }

            if (tabEnableGradientColor) {
                //文本渐变
                fromView?.apply {
                    _gradientColor(
                        onGetTextStyleView(this, fromIndex),
                        tabSelectColor,
                        tabDeselectColor,
                        positionOffset
                    )
                }
                _gradientColor(
                    onGetTextStyleView(toView, toIndex),
                    tabDeselectColor,
                    tabSelectColor,
                    positionOffset
                )
            }

            if (tabEnableIcoGradientColor) {
                //图标渐变
                fromView?.apply {
                    _gradientIcoColor(
                        onGetIcoStyleView(this, fromIndex),
                        tabIcoSelectColor,
                        tabIcoDeselectColor,
                        positionOffset
                    )
                }

                _gradientIcoColor(
                    onGetIcoStyleView(toView, toIndex),
                    tabIcoDeselectColor,
                    tabIcoSelectColor,
                    positionOffset
                )
            }

            if (tabEnableGradientScale) {
                //scale渐变
                _gradientScale(fromView, tabMaxScale, tabMinScale, positionOffset)
                _gradientScale(toView, tabMinScale, tabMaxScale, positionOffset)
            }

            if (tabEnableGradientTextSize &&
                tabTextMaxSize > 0 &&
                tabTextMinSize > 0 &&
                tabTextMinSize != tabTextMaxSize
            ) {

                //文本字体大小渐变
                _gradientTextSize(
                    fromView?.run { onGetTextStyleView(this, fromIndex) },
                    tabTextMaxSize,
                    tabTextMinSize,
                    positionOffset
                )
                _gradientTextSize(
                    onGetTextStyleView(toView, toIndex),
                    tabTextMinSize,
                    tabTextMaxSize,
                    positionOffset
                )

                if (toIndex == tabLayout.dslSelector.visibleViewList.lastIndex || toIndex == 0) {
                    tabLayout._scrollToTarget(toIndex, false)
                }
            }
        }
    }

    open fun _gradientColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        tabGradientCallback.onGradientColor(view, startColor, endColor, percent)
    }

    open fun _gradientIcoColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        tabGradientCallback.onGradientIcoColor(view, startColor, endColor, percent)
    }

    open fun _gradientScale(view: View?, startScale: Float, endScale: Float, percent: Float) {
        tabGradientCallback.onGradientScale(view, startScale, endScale, percent)
    }

    open fun _gradientTextSize(
        view: TextView?,
        startTextSize: Float,
        endTextSize: Float,
        percent: Float
    ) {
        tabGradientCallback.onGradientTextSize(view, startTextSize, endTextSize, percent)
    }

    open fun _updateIcoColor(view: View?, color: Int) {
        tabGradientCallback.onUpdateIcoColor(view, color)
    }
}

open class TabGradientCallback {

    open fun onGradientColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        (view as? TextView)?.apply {
            setTextColor(evaluateColor(percent, startColor, endColor))
        }
    }

    open fun onGradientIcoColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        onUpdateIcoColor(view, evaluateColor(percent, startColor, endColor))
    }

    open fun onUpdateIcoColor(view: View?, color: Int) {
        view?.tintDrawableColor(color)
    }

    open fun onGradientScale(view: View?, startScale: Float, endScale: Float, percent: Float) {
        view?.apply {
            (startScale + (endScale - startScale) * percent).let {
                scaleX = it
                scaleY = it
            }
        }
    }

    open fun onGradientTextSize(
        view: TextView?,
        startTextSize: Float,
        endTextSize: Float,
        percent: Float
    ) {
        view?.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                (startTextSize + (endTextSize - startTextSize) * percent)
            )
        }
    }
}