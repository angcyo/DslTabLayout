package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout

class VerticalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show(MainFragment().apply {
            orientation = LinearLayout.VERTICAL
        })
    }
}
