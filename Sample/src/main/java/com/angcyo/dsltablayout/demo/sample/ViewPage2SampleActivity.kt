package com.angcyo.dsltablayout.demo.sample

import android.os.Bundle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.dsltablayout.demo.BaseActivity
import com.angcyo.dsltablayout.demo.CommonFragment
import com.angcyo.dsltablayout.demo.R
import com.angcyo.dsltablayout.demo.SegmentFragment
import com.angcyo.dsltablayout.demo.SlidingFragment

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @date 2025/11/28
 */
class ViewPage2SampleActivity : BaseActivity() {

    val fragmentList = listOf(SlidingFragment(), CommonFragment(), SegmentFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_page2)

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): androidx.fragment.app.Fragment {
                return fragmentList[position]
            }
        }
    }

}