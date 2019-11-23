package com.angcyo.tablayout

import android.text.TextUtils
import android.util.Log
import android.view.View

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */

internal val View.dpi: Int
    get() = context.resources.displayMetrics.density.toInt()

internal val View.screenWidth: Int
    get() = context.resources.displayMetrics.widthPixels

internal val View.screenHeight: Int
    get() = context.resources.displayMetrics.heightPixels

/**Match_Parent*/
internal fun exactlyMeasure(size: Int): Int =
    View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

internal fun exactlyMeasure(size: Float): Int = exactlyMeasure(size.toInt())

/**Wrap_Content*/
internal fun atmostMeasure(size: Int): Int =
    View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)

internal fun Int.have(value: Int): Boolean = if (this == 0 || value == 0) {
    false
} else if (this == 0 && value == 0) {
    true
} else {
    ((this > 0 && value > 0) || (this < 0 && value < 0)) && this and value == value
}

internal fun clamp(value: Float, min: Float, max: Float): Float {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}

internal fun clamp(value: Int, min: Int, max: Int): Int {
    if (value < min) {
        return min
    } else if (value > max) {
        return max
    }
    return value
}

internal fun Any.logi() {
    Log.i("DslTabLayout", "$this")
}

internal fun Any.logw() {
    Log.w("DslTabLayout", "$this")
}

internal fun Any.loge() {
    Log.e("DslTabLayout", "$this")
}

internal fun View.calcLayoutWidthHeight(
    rLayoutWidth: String?, rLayoutHeight: String?,
    parentWidth: Int, parentHeight: Int,
    rLayoutWidthExclude: Int = 0, rLayoutHeightExclude: Int = 0
): IntArray {
    val size = intArrayOf(-1, -1)
    if (TextUtils.isEmpty(rLayoutWidth) && TextUtils.isEmpty(rLayoutHeight)) {
        return size
    }
    if (!TextUtils.isEmpty(rLayoutWidth)) {
        if (rLayoutWidth!!.contains("sw", true)) {
            val ratio = rLayoutWidth.replace("sw", "", true).toFloatOrNull()
            ratio?.let {
                size[0] = (ratio * (screenWidth - rLayoutWidthExclude)).toInt()
            }
        } else if (rLayoutWidth!!.contains("pw", true)) {
            val ratio = rLayoutWidth.replace("pw", "", true).toFloatOrNull()
            ratio?.let {
                size[0] = (ratio * (parentWidth - rLayoutWidthExclude)).toInt()
            }
        }
    }
    if (!TextUtils.isEmpty(rLayoutHeight)) {
        if (rLayoutHeight!!.contains("sh", true)) {
            val ratio = rLayoutHeight.replace("sh", "", true).toFloatOrNull()
            ratio?.let {
                size[1] = (ratio * (screenHeight - rLayoutHeightExclude)).toInt()
            }
        } else if (rLayoutHeight!!.contains("ph", true)) {
            val ratio = rLayoutHeight.replace("ph", "", true).toFloatOrNull()
            ratio?.let {
                size[1] = (ratio * (parentHeight - rLayoutHeightExclude)).toInt()
            }
        }
    }
    return size
}