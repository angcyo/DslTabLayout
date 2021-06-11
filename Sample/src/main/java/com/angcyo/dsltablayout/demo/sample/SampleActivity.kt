package com.angcyo.dsltablayout.demo.sample

import android.os.Bundle
import com.angcyo.dsltablayout.demo.BaseActivity
import com.angcyo.dsltablayout.demo.MainFragment
import com.angcyo.dsltablayout.demo.R
import com.angcyo.dsltablayout.demo.show

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/11
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class SampleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show(SampleMainFragment())
    }
}