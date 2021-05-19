package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.widget.LinearLayout

class VerticalActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show(MainFragment().apply {
            orientation = LinearLayout.VERTICAL
        })
    }
}
