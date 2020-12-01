package com.angcyo.dsltablayout.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslItemDecoration
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.HoverItemDecoration

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

    /**提供悬停功能*/
    var hoverItemDecoration = HoverItemDecoration()

    /**提供基本的分割线功能*/
    var baseDslItemDecoration = DslItemDecoration()

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
            addItemDecoration(baseDslItemDecoration)
            hoverItemDecoration.attachToRecyclerView(this)
        }
    }

    open fun renderAdapter(render: DslAdapter.() -> Unit) {
        dslAdapter.render()
    }
}