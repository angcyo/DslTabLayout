package com.angcyo.dsltablayout.demo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.angcyo.dsladapter.L
import com.angcyo.dsltablayout.demo.sample.DynamicActivity
import com.angcyo.dsltablayout.demo.sample.HorizontalHintActivity
import com.angcyo.dsltablayout.demo.sample.SampleActivity
import com.angcyo.dsltablayout.demo.sample.TestActivity
import com.angcyo.dsltablayout.demo.sample.VerticalActivity
import com.angcyo.dsltablayout.demo.sample.VerticalHintActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.debug = BuildConfig.DEBUG

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

            R.id.action_vertical_hint -> {
                startActivity(Intent(this, VerticalHintActivity::class.java))
                true
            }

            R.id.action_horizontal_hint -> {
                startActivity(Intent(this, HorizontalHintActivity::class.java))
                true
            }

            R.id.action_sample -> {
                startActivity(Intent(this, SampleActivity::class.java))
                true
            }

            R.id.action_test -> {
                startActivity(Intent(this, TestActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (BuildConfig.DEBUG) {
            finish()
        }
    }
}