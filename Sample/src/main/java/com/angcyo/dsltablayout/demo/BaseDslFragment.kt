package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */
open class BaseDslFragment : Fragment() {

    var orientation = LinearLayout.HORIZONTAL

    lateinit var baseViewHolder: DslViewHolder
    lateinit var dslAdapter: DslAdapter

    open fun getBaseLayoutId(): Int = R.layout.base_dsl_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(getBaseLayoutId(), container, false)
        baseViewHolder = DslViewHolder(rootView)
        initBaseView(baseViewHolder, savedInstanceState)
        return rootView
    }

    open fun initBaseView(viewHolder: DslViewHolder, savedInstanceState: Bundle?) {
        dslAdapter = DslAdapter()
        viewHolder.rv(R.id.base_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(
                context,
                if (orientation == LinearLayout.HORIZONTAL) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = dslAdapter
        }
    }

    open fun renderAdapter(render: DslAdapter.() -> Unit) {
        dslAdapter.render()
    }
}