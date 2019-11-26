package com.angcyo.tablayout

import android.view.View

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabLayoutConfig : DslSelectorConfig() {

    /**
     * [DslTabLayout]滚动时回调.
     * */
    open fun onPageIndexScrolled(fromIndex: Int, toIndex: Int, positionOffset: Float) {

    }

    /**
     * [onPageIndexScrolled]
     * */
    open fun onPageViewScrolled(fromView: View?, toView: View, positionOffset: Float) {

    }
}