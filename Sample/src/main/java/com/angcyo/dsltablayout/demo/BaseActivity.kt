package com.angcyo.dsltablayout.demo

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun _drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

    fun <T : View> find(@IdRes id: Int) = findViewById<T>(id)
}

fun FragmentActivity.show(fragment: Fragment) {
    supportFragmentManager.show(fragment)
}

fun Fragment.show(fragment: Fragment) {
    childFragmentManager.show(fragment)
}

fun FragmentManager.show(fragment: Fragment) {
    beginTransaction().apply {
        add(R.id.frame_layout, fragment, fragment.javaClass.simpleName)
        commitNowAllowingStateLoss()
    }
}

fun Context.getDrawable2(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Fragment.getDrawable(@DrawableRes id: Int): Drawable? {
    return requireContext().getDrawable2(id)
}