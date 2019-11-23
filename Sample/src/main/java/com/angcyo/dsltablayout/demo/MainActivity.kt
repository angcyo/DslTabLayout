package com.angcyo.dsltablayout.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show(SlidingFragment())
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
