package com.angcyo.dsltablayout.demo

import android.os.Bundle
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
class SlidingFragment : BaseDslFragment() {
    override fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, savedInstanceState)

        renderAdapter {
            DslTabLayoutItem()() {

            }
        }
    }
}