package com.angcyo.dsltablayout.demo.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.eachChild
import com.angcyo.dsladapter.initDslAdapter
import com.angcyo.dsladapter.resetLayoutManager
import com.angcyo.dsltablayout.demo.BaseActivity
import com.angcyo.dsltablayout.demo.R
import com.angcyo.dsltablayout.demo.dslitem.DslVerticalHintItem
import com.angcyo.tablayout.DslTabLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/05/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class VerticalHintActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vertical_hint_layout)

        initByNormalAdapter()
        //initByDslAdapter()
    }

    fun initByDslAdapter() {
        val tabLayout = find<DslTabLayout>(R.id.tab_layout)
        /*tabLayout.tabHighlight?.apply {
            highlightDrawable = _drawable(R.mipmap.ic_hint_b)
            highlightHeightOffset = 20 * dpi
        }*/

        val recyclerView = find<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            resetLayoutManager("V")
            initDslAdapter {
                render {
                    tabLayout.eachChild { index, child ->
                        if (child is TextView) {
                            DslVerticalHintItem()() {
                                text = child.text
                            }
                        }
                    }
                }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    recyclerView.getChildAt(0)?.layoutParams?.let {
                        if (it is RecyclerView.LayoutParams) {
                            tabLayout.setCurrentItem(it.absoluteAdapterPosition)
                        }
                    }
                }
            })
        }

        tabLayout.configTabLayoutConfig {
            onSelectItemView = { itemView, index, select, fromUser ->
                if (select && itemView is TextView) {
                    if (fromUser) {
                        recyclerView?.layoutManager?.let {
                            if (it is LinearLayoutManager) {
                                it.scrollToPositionWithOffset(index, 0)
                            } else {
                                recyclerView.scrollToPosition(index)
                            }
                        }
                    }
                }
                false
            }
        }
    }

    fun initByNormalAdapter() {
        val tabLayout = find<DslTabLayout>(R.id.tab_layout)
        val recyclerView = find<RecyclerView>(R.id.recycler_view)

        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@VerticalHintActivity, LinearLayoutManager.VERTICAL, false)
            adapter = object : RecyclerView.Adapter<MyViewHolder>() {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.vertical_hint_item, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                    holder.update((tabLayout.getChildAt(position) as? TextView)?.text)
                }

                override fun getItemCount(): Int {
                    return tabLayout.childCount
                }
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    recyclerView.getChildAt(0)?.layoutParams?.let {
                        if (it is RecyclerView.LayoutParams) {
                            tabLayout.setCurrentItem(it.absoluteAdapterPosition)
                        }
                    }
                }
            })
        }

        tabLayout.configTabLayoutConfig {
            onSelectItemView = { itemView, index, select, fromUser ->
                if (select && itemView is TextView) {
                    if (fromUser) {
                        recyclerView?.layoutManager?.let {
                            if (it is LinearLayoutManager) {
                                it.scrollToPositionWithOffset(index, 0)
                            } else {
                                recyclerView.scrollToPosition(index)
                            }
                        }
                    }
                }
                false
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun update(text: CharSequence?) {
            itemView.findViewById<TextView>(R.id.text_view)?.text = text
        }

    }
}