package com.angcyo.tablayout

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.math.MathUtils

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
internal val dpi: Int
    get() = dp.toInt()

internal val dp: Float
    get() = Resources.getSystem().displayMetrics.density

internal val View.dpi: Int
    get() = context.resources.displayMetrics.density.toInt()

internal val View.screenWidth: Int
    get() = context.resources.displayMetrics.widthPixels

internal val View.screenHeight: Int
    get() = context.resources.displayMetrics.heightPixels

internal val View.viewDrawWidth: Int
    get() = measuredWidth - paddingLeft - paddingRight

internal val View.viewDrawHeight: Int
    get() = measuredHeight - paddingTop - paddingBottom

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

internal fun Int.remove(value: Int): Int = this and value.inv()

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

internal fun evaluateColor(fraction: Float /*0-1*/, startColor: Int, endColor: Int): Int {
    val fr = MathUtils.clamp(fraction, 0f, 1f)
    val startA = startColor shr 24 and 0xff
    val startR = startColor shr 16 and 0xff
    val startG = startColor shr 8 and 0xff
    val startB = startColor and 0xff
    val endA = endColor shr 24 and 0xff
    val endR = endColor shr 16 and 0xff
    val endG = endColor shr 8 and 0xff
    val endB = endColor and 0xff
    return startA + (fr * (endA - startA)).toInt() shl 24 or
            (startR + (fr * (endR - startR)).toInt() shl 16) or
            (startG + (fr * (endG - startG)).toInt() shl 8) or
            startB + (fr * (endB - startB)).toInt()
}

internal fun Drawable?.tintDrawableColor(color: Int): Drawable? {

    if (this == null) {
        return this
    }

    val wrappedDrawable =
        DrawableCompat.wrap(this).mutate()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        DrawableCompat.setTint(wrappedDrawable, color)
    } else {
        wrappedDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    return wrappedDrawable
}

internal fun View?.tintDrawableColor(color: Int) {
    when (this) {
        is TextView -> {
            val drawables = arrayOfNulls<Drawable?>(4)
            compoundDrawables.forEachIndexed { index, drawable ->
                drawables[index] = drawable?.tintDrawableColor(color)
            }
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
        }
        is ImageView -> {
            setImageDrawable(drawable?.tintDrawableColor(color))
        }
    }
}

internal fun Paint?.textWidth(text: String?): Float {
    if (TextUtils.isEmpty(text)) {
        return 0f
    }
    return this?.run {
        measureText(text)
    } ?: 0f
}

internal fun Paint?.textHeight(): Float = this?.run { descent() - ascent() } ?: 0f

internal fun View.getChildOrNull(index: Int): View? {
    return if (this is ViewGroup) {
        return if (index in 0 until childCount) {
            getChildAt(index)
        } else {
            null
        }
    } else {
        this
    }
}

/**获取[View]在指定[parent]中的矩形坐标*/
internal fun View.getLocationInParent(parentView: View? = null, result: Rect = Rect()): Rect {
    val parent: View? = parentView ?: (parent as? View)

    if (parent == null) {
        getViewRect(result)
    } else {
        result.set(0, 0, 0, 0)
        if (this != parent) {
            fun doIt(view: View, parent: View, rect: Rect) {
                val viewParent = view.parent
                if (viewParent is View) {
                    rect.left += view.left
                    rect.top += view.top
                    if (viewParent != parent) {
                        doIt(viewParent, parent, rect)
                    }
                }
            }
            doIt(this, parent, result)
        }
        result.right = result.left + this.measuredWidth
        result.bottom = result.top + this.measuredHeight
    }

    return result
}

/**
 * 获取View, 相对于手机屏幕的矩形
 * */
internal fun View.getViewRect(result: Rect = Rect()): Rect {
    var offsetX = 0
    var offsetY = 0

    //横屏, 并且显示了虚拟导航栏的时候. 需要左边偏移
    //只计算一次
    (context as? Activity)?.let {
        it.window.decorView.getGlobalVisibleRect(result)
        if (result.width() > result.height()) {
            //横屏了
            offsetX = navBarHeight(it)
        }
    }

    return getViewRect(offsetX, offsetY, result)
}

/**
 * 获取View, 相对于手机屏幕的矩形, 带皮阿尼一
 * */
internal fun View.getViewRect(offsetX: Int, offsetY: Int, result: Rect = Rect()): Rect {
    //可见位置的坐标, 超出屏幕的距离会被剃掉
    //getGlobalVisibleRect(r)
    val r2 = IntArray(2)
    //val r3 = IntArray(2)
    //相对于屏幕的坐标
    getLocationOnScreen(r2)
    //相对于窗口的坐标
    //getLocationInWindow(r3)

    val left = r2[0] + offsetX
    val top = r2[1] + offsetY

    result.set(left, top, left + measuredWidth, top + measuredHeight)
    return result
}


/**
 * 导航栏的高度(如果显示了)
 */
internal fun navBarHeight(context: Context): Int {
    var result = 0

    if (context is Activity) {
        val decorRect = Rect()
        val windowRect = Rect()

        context.window.decorView.getGlobalVisibleRect(decorRect)
        context.window.findViewById<View>(Window.ID_ANDROID_CONTENT)
            .getGlobalVisibleRect(windowRect)

        if (decorRect.width() > decorRect.height()) {
            //横屏
            result = decorRect.width() - windowRect.width()
        } else {
            //竖屏
            result = decorRect.bottom - windowRect.bottom
        }
    }

    return result
}

fun Collection<*>?.size() = this?.size ?: 0

/**判断2个列表中的数据是否改变过*/
internal fun <T> List<T>?.isChange(other: List<T>?): Boolean {
    if (this.size() != other.size()) {
        return true
    }
    this?.forEachIndexed { index, t ->
        if (t != other?.getOrNull(index)) {
            return true
        }
    }
    return false
}

fun Int.isHorizontal() = this == LinearLayout.HORIZONTAL

fun Int.isVertical() = this == LinearLayout.VERTICAL

internal fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = true): View {
    if (layoutId == -1) {
        return this
    }
    val rootView = LayoutInflater.from(context).inflate(layoutId, this, false)
    if (attachToRoot) {
        addView(rootView)
    }
    return rootView
}