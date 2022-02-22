package com.angcyo.tablayout

import android.graphics.Canvas

/**
 * 用来实现[DslTabIndicator]的自绘
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/21
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface ITabIndicatorDraw {

    /**绘制指示器
     * [positionOffset] 页面偏移量*/
    fun onDrawTabIndicator(
        tabIndicator: DslTabIndicator,
        canvas: Canvas,
        positionOffset: Float
    )

}