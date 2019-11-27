package com.angcyo.tablayout

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.TextView

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabLayoutConfig : DslSelectorConfig() {

    /**选中的文本颜色*/
    var tabSelectorColor: Int = Color.WHITE //Color.parseColor("#333333")

    /**未选中的文本颜色*/
    var tabUnSelectorColor: Int = Color.parseColor("#999999")

    /**是否开启Bold, 文本加粗*/
    var tabTextBold = false

    /**是否开启颜色渐变效果*/
    var tabEnableGradientColor = false

    /**是否开启scale渐变效果*/
    var tabEnableGradientScale = false

    /**最小缩放的比例*/
    var tabMinScale = 0.8f
    /**大嘴缩放的比例*/
    var tabMaxScale = 1.2f

    init {
        onStyleItemView = { itemView, index, select ->
            onUpdateItemStyle(itemView, index, select)
        }
    }

    /**更新item的样式*/
    open fun onUpdateItemStyle(itemView: View, index: Int, select: Boolean) {
        //"$itemView\n$index\n$select".logw()

        (itemView as? TextView)?.apply {
            //文本加粗
            paint?.apply {
                flags = if (tabTextBold && select) {
                    paint.flags or Paint.FAKE_BOLD_TEXT_FLAG
                } else {
                    paint.flags and Paint.FAKE_BOLD_TEXT_FLAG.inv()
                }
            }

            //文本颜色
            setTextColor(if (select) tabSelectorColor else tabUnSelectorColor)
        }

        if (tabEnableGradientScale) {
            itemView.scaleX = if (select) tabMaxScale else tabMinScale
            itemView.scaleY = if (select) tabMaxScale else tabMinScale
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
            if (tabEnableGradientColor) {
                _gradientColor(fromView, tabSelectorColor, tabUnSelectorColor, positionOffset)
                _gradientColor(toView, tabUnSelectorColor, tabSelectorColor, positionOffset)
            }

            if (tabEnableGradientScale) {
                _gradientScale(fromView, tabMaxScale, tabMinScale, positionOffset)
                _gradientScale(toView, tabMinScale, tabMaxScale, positionOffset)
            }
        }
    }

    open fun _gradientColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        (view as? TextView)?.apply {
            setTextColor(evaluateColor(percent, startColor, endColor))
        }
    }

    open fun _gradientScale(view: View?, startScale: Float, endScale: Float, percent: Float) {
        view?.apply {
            (startScale + (endScale - startScale) * percent).let {
                scaleX = it
                scaleY = it
            }
        }
    }
}