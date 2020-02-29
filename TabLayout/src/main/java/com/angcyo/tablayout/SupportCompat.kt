package com.angcyo.tablayout

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import com.angcyo.tablayout.annotation.ColorInt

/**
 * 移除AndroidX的依赖, 相关兼容方法移植在此.
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
object SupportCompat {

    /**[androidx.core.view.ViewCompat.setBackground]*/
    fun setBackground(view: View, background: Drawable?) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.background = background
        } else {
            view.setBackgroundDrawable(background)
        }
    }

    /**[androidx.core.graphics.drawable.DrawableCompat.wrap]*/
    fun wrap(drawable: Drawable?): Drawable? {
        return drawable
    }

    /**[androidx.core.graphics.drawable.DrawableCompat.setTint]*/
    fun setTint(drawable: Drawable?, @ColorInt tint: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            drawable?.setTint(tint)
        } /*else if (drawable is TintAwareDrawable) {
            (drawable as TintAwareDrawable).setTint(tint)
        }*/
    }

    /**[androidx.core.math.MathUtils.clamp(float, float, float)]*/
    fun clamp(value: Float, min: Float, max: Float): Float {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }
}

