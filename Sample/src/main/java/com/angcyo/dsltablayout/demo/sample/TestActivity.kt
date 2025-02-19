package com.angcyo.dsltablayout.demo.sample

import android.os.Bundle
import android.view.View
import com.angcyo.dsltablayout.demo.BaseActivity
import com.angcyo.dsltablayout.demo.R
import com.angcyo.tablayout.DslTabLayout

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2025/02/19
 */
class TestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val tabLayout = find<DslTabLayout>(R.id.tab_layout)
        val testButton = find<View>(R.id.test_button)

        testButton.setOnClickListener {
            tabLayout.setTabLayoutConfig()
        }
    }
}