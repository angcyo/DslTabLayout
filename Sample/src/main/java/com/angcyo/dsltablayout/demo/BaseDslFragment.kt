package com.angcyo.dsltablayout.demo

import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
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
        dslAdapter.render(action = render)
    }

    fun show(f: Fragment, hide: Fragment? = null) {
        childFragmentManager.beginTransaction().apply {
            if (f.isAdded) {
                show(f)
                setMaxLifecycle(f, Lifecycle.State.RESUMED)
            } else {
                add(R.id.frame_container_layout, f)
            }
            hide?.let {
                if (it.isAdded) {
                    hide(it)
                    setMaxLifecycle(it, Lifecycle.State.STARTED)
                }
            }
            commitAllowingStateLoss()
        }
    }
}

fun LottieAnimationView.setLottieColorFilter(color: Int) {
    val filter = SimpleColorFilter(color)
    val keyPath = KeyPath("**")
    val callback = LottieValueCallback<ColorFilter>(filter)
    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}