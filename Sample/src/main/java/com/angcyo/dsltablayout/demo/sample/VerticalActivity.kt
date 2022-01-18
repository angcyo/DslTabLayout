package com.angcyo.dsltablayout.demo.sample

import android.os.Bundle
import android.widget.LinearLayout
import com.angcyo.dsltablayout.demo.BaseActivity
import com.angcyo.dsltablayout.demo.MainFragment
import com.angcyo.dsltablayout.demo.R
import com.angcyo.dsltablayout.demo.show

class VerticalActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show(MainFragment().apply {
            orientation = LinearLayout.VERTICAL
        })
    }
}
