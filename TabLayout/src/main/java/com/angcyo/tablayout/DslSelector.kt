package com.angcyo.tablayout

import android.view.View
import android.view.ViewGroup

/**
 * 用来操作[ViewGroup]中的[child], 支持单选, 多选, 拦截.
 * 操作的都是可见性为[VISIBLE]的[View]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/24
 */

open class DslSelector {

    var parent: ViewGroup? = null
    var dslSelectorConfig: DslSelectorConfig = DslSelectorConfig()

    //可见view列表
    val visibleViewList: MutableList<View> = mutableListOf()
        get() {
            field.clear()

            for (i in 0 until (parent?.childCount ?: 0)) {
                parent?.getChildAt(i)?.apply {
                    if (visibility == View.VISIBLE) {
                        field.add(this)
                    }
                }
            }

            return field
        }

    /**
     * 选中的索引列表
     * */
    val selectorIndexList: MutableList<Int> = mutableListOf()
        get() {
            field.clear()
            visibleViewList.forEachIndexed { index, view ->
                if (view.isSelected) {
                    field.add(index)
                }
            }

            return field
        }

    /**
     * 选中的View列表
     * */
    val selectorViewList: MutableList<View> = mutableListOf()
        get() {
            field.clear()
            visibleViewList.forEachIndexed { _, view ->
                if (view.isSelected) {
                    field.add(view)
                }
            }
            return field
        }

    val _onChildClickListener = View.OnClickListener {
        selector(visibleViewList.indexOf(it), !it.isSelected)
    }

    /**安装*/
    fun install(viewGroup: ViewGroup, config: DslSelectorConfig.() -> Unit = {}): DslSelector {
        parent = viewGroup
        dslSelectorConfig.config()

        updateStyle()
        updateClickListener()

        if (dslSelectorConfig.dslSelectIndex in 0 until visibleViewList.size) {
            selector(dslSelectorConfig.dslSelectIndex)
        }

        return this
    }

    /**更新样式*/
    fun updateStyle() {
        visibleViewList.forEachIndexed { index, view ->
            val selector = dslSelectorConfig.dslSelectIndex == index || view.isSelected
            dslSelectorConfig.onStyleItemView(view, index, selector)
        }
    }

    /**更新child的点击事件*/
    fun updateClickListener() {
        for (i in 0 until (parent?.childCount ?: 0)) {
            parent?.getChildAt(i)?.apply {
                setOnClickListener(_onChildClickListener)
            }
        }
    }

    /**
     * 操作单个
     * @param select 选中 or 取消选中
     * */
    fun selector(index: Int, select: Boolean = true) {
        val lastSelectorView: View? = selectorViewList.lastOrNull()
        val lastSelectorIndex: Int? = selectorIndexList.lastOrNull()

        if (_selector(index, select)) {
            val indexSelectorList = selectorIndexList
            dslSelectorConfig.dslSelectIndex = indexSelectorList.lastOrNull() ?: -1
            dslSelectorConfig.onSelectViewChange(lastSelectorView, selectorViewList)
            dslSelectorConfig.onSelectIndexChange(lastSelectorIndex ?: -1, indexSelectorList)
        }
    }

    /**
     * 操作多个
     * @param select 选中 or 取消选中
     * [selector]
     * */
    fun selector(indexList: MutableList<Int>, select: Boolean = true) {
        val lastSelectorView: View? = selectorViewList.lastOrNull()
        val lastSelectorIndex: Int? = selectorIndexList.lastOrNull()

        var result = false

        indexList.forEach {
            result = result || _selector(it, select)
        }

        if (result) {
            val indexSelectorList = selectorIndexList
            dslSelectorConfig.dslSelectIndex = indexSelectorList.lastOrNull() ?: -1
            dslSelectorConfig.onSelectViewChange(lastSelectorView, selectorViewList)
            dslSelectorConfig.onSelectIndexChange(lastSelectorIndex ?: -1, indexSelectorList)
        }
    }

    /**@return 是否发生过改变*/
    fun _selector(index: Int, select: Boolean): Boolean {

        val selectorIndexList = selectorIndexList

        if (select && selectorIndexList.contains(index)) {
            return false
        } else if (!select && !selectorIndexList.contains(index)) {
            return false
        }

        val visibleViewList = visibleViewList

        if (index in 0 until visibleViewList.size) {

        } else {
            "index out of list.".logi()
            return false
        }

        var result = false

        val selectorViewList = selectorViewList

        if (select) {
            val sum = selectorViewList.size + 1
            if (sum > dslSelectorConfig.dslMaxSelectLimit) {
                //不允许选择
                return false
            }
        } else {
            //取消选择, 检查是否达到了 limit
            val sum = selectorViewList.size - 1
            if (sum < dslSelectorConfig.dslMinSelectLimit) {
                //不允许取消选择
                return false
            }
        }

        val selectorView = visibleViewList[index]

        //更新选中样式
        if (!dslSelectorConfig.onSelectItemView(selectorView, index, select)) {
            selectorView.isSelected = select

            if (dslSelectorConfig.dslMultiMode) {
                //多选
            } else {
                //单选

                //取消之前
                selectorViewList.forEach { view ->
                    //更新样式
                    val indexOf = visibleViewList.indexOf(view)
                    if (!dslSelectorConfig.onSelectItemView(view, indexOf, false)) {
                        view.isSelected = false
                        dslSelectorConfig.onStyleItemView(view, indexOf, false)
                    }
                }
            }

            dslSelectorConfig.onStyleItemView(selectorView, index, select)
            result = true
        }

        return result
    }
}

/**
 * Dsl配置项
 * */
class DslSelectorConfig {

    /**当前选中的索引*/
    var dslSelectIndex = 0

    /**取消选择时, 最小需要保持多个选中*/
    var dslMinSelectLimit = 1

    /**选择时, 最大允许多个选中*/
    var dslMaxSelectLimit = Int.MAX_VALUE

    /**是否是多选模式*/
    var dslMultiMode: Boolean = false

    /**
     * 用来初始化[itemView]的样式
     * [onSelectItemView]
     * */
    var onStyleItemView: (itemView: View, index: Int, select: Boolean) -> Unit =
        { itemView, index, select ->

        }

    /**
     * 选中[View]改变回调
     * @param fromView 单选模式下有效, 表示之前选中的[View]
     * */
    var onSelectViewChange: (fromView: View?, selectList: List<View>) -> Unit =
        { fromView, selectList ->

        }

    /**
     * 选中改变回调
     * @param fromIndex 单选模式下有效, 表示之前选中的[View], 在可见性[child]列表中的索引
     * */
    var onSelectIndexChange: (fromIndex: Int, selectList: List<Int>) -> Unit =
        { fromIndex, selectList ->
            "选择:[$fromIndex]->${selectList}".logi()
        }

    /**
     * 当需要选中[itemView]时回调, 返回[true]表示拦截默认处理
     * @param itemView 操作的[View]
     * @param index [itemView]在可见性view列表中的索引. 非ViewGroup中的索引
     * @param select 选中 or 取消选中
     * */
    var onSelectItemView: (itemView: View, index: Int, select: Boolean) -> Boolean =
        { itemView, index, select ->
            false
        }
}
