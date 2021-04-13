package com.angcyo.dsltablayout.demo

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show(MainFragment())

        //show(SegmentFragment())
        //show(CommonFragment())
        //show(SlidingFragment())

        //show(TestFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this, DynamicActivity::class.java))
                true
            }
            R.id.action_vertical -> {
                startActivity(Intent(this, VerticalActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
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

@ColorInt
fun String.toColorInt(): Int = Color.parseColor(this)

val density: Float = Resources.getSystem()?.displayMetrics?.density ?: 0f
val dp: Float = Resources.getSystem()?.displayMetrics?.density ?: 0f
val dpi: Int = Resources.getSystem()?.displayMetrics?.density?.toInt() ?: 0

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = true): View {
    if (layoutId == -1) {
        return this
    }
    val rootView = LayoutInflater.from(context).inflate(layoutId, this, false)
    if (attachToRoot) {
        addView(rootView)
    }
    return rootView
}