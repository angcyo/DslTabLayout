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
            visibleViewList.forEachIndexed { index, view ->
                if (view.isSelected || index == dslSelectIndex) {
                    field.add(view)
                }
            }
            return field
        }

    //child 点击事件处理
    val _onChildClickListener = View.OnClickListener {
        val index = visibleViewList.indexOf(it)
        val select = if (dslSelectorConfig.dslMultiMode) {
            !it.isSelected
        } else {
            true
        }

        if (!interceptSelector(index, select, true)) {
            selector(visibleViewList.indexOf(it), select, notify = true, fromUser = true)
        }
    }

    /**当前选中的索引*/
    var dslSelectIndex = -1

    /**安装*/
    fun install(viewGroup: ViewGroup, config: DslSelectorConfig.() -> Unit = {}): DslSelector {
        dslSelectIndex = -1
        parent = viewGroup
        updateVisibleList()
        dslSelectorConfig.config()

        updateStyle()
        updateClickListener()

        if (dslSelectIndex in 0 until visibleViewList.size) {
            selector(dslSelectIndex)
        }

        return this
    }

    /**更新样式*/
    fun updateStyle() {
        visibleViewList.forEachIndexed { index, view ->
            val selector = dslSelectIndex == index || view.isSelected
            dslSelectorConfig.onStyleItemView(view, index, selector)
        }
    }

    /**更新child的点击事件*/
    fun updateClickListener() {
        parent?.apply {
            for (i in 0 until childCount) {
                getChildAt(i)?.let {
                    it.setOnClickListener(_onChildClickListener)
                }
            }
        }
    }

    /**更新可见视图列表*/
    fun updateVisibleList(): List<View> {
        visibleViewList.clear()
        parent?.apply {
            for (i in 0 until childCount) {
                getChildAt(i)?.let {
                    if (it.visibility == View.VISIBLE) {
                        visibleViewList.add(it)
                    }
                }
            }
        }
        return visibleViewList
    }

    /**
     * 操作单个
     * @param index 操作目标的索引值
     * @param select 选中 or 取消选中
     * @param notify 是否需要通知事件
     * */
    fun selector(
        index: Int,
        select: Boolean = true,
        notify: Boolean = true,
        fromUser: Boolean = false
    ) {
        val selectorIndexList = selectorIndexList
        val lastSelectorIndex: Int? = selectorIndexList.lastOrNull()
        val reselect = !dslSelectorConfig.dslMultiMode &&
                selectorIndexList.isNotEmpty() &&
                selectorIndexList.contains(index)

        if (_selector(index, select, fromUser)) {
            dslSelectIndex = this.selectorIndexList.lastOrNull() ?: -1
            if (notify) {
                notifySelectChange(lastSelectorIndex ?: -1, reselect, fromUser)
            }
        }
    }

    /**
     * 操作多个
     * @param select 选中 or 取消选中
     * [selector]
     * */
    fun selector(
        indexList: MutableList<Int>,
        select: Boolean = true,
        notify: Boolean = true,
        fromUser: Boolean = false
    ) {
        val selectorIndexList = selectorIndexList
        val lastSelectorIndex: Int? = selectorIndexList.lastOrNull()

        var result = false

        indexList.forEach {
            result = result || _selector(it, select, fromUser)
        }

        if (result) {
            dslSelectIndex = this.selectorIndexList.lastOrNull() ?: -1
            if (notify) {
                notifySelectChange(lastSelectorIndex ?: -1, false, fromUser)
            }
        }
    }

    /**通知事件*/
    fun notifySelectChange(lastSelectorIndex: Int, reselect: Boolean, fromUser: Boolean) {
        val indexSelectorList = selectorIndexList
        dslSelectorConfig.onSelectViewChange(
            visibleViewList.getOrNull(lastSelectorIndex),
            selectorViewList,
            reselect,
            fromUser
        )
        dslSelectorConfig.onSelectIndexChange(
            lastSelectorIndex,
            indexSelectorList,
            reselect,
            fromUser
        )
    }

    /**当前的操作是否被拦截*/
    fun interceptSelector(index: Int, select: Boolean, fromUser: Boolean): Boolean {
        val visibleViewList = visibleViewList
        if (index !in 0 until visibleViewList.size) {
            return true
        }
        return dslSelectorConfig.onSelectItemView(visibleViewList[index], index, select, fromUser)
    }

    /**@return 是否发生过改变*/
    fun _selector(index: Int, select: Boolean, fromUser: Boolean): Boolean {
        val visibleViewList = visibleViewList
        //超范围过滤
        if (index !in 0 until visibleViewList.size) {
            "index out of list.".logi()
            return false
        }

        val selectorIndexList = selectorIndexList
        val selectorViewList = selectorViewList

        if (selectorIndexList.isNotEmpty()) {
            if (select) {
                //需要选中某项

                if (dslSelectorConfig.dslMultiMode) {
                    //多选模式
                    if (selectorIndexList.contains(index)) {
                        //已经选中
                        return false
                    }
                } else {
                    //单选模式
                    if (selectorIndexList.contains(index)) {
                        //已经选中
                        return true
                    }
                }

            } else {
                //需要取消选中
                if (!selectorIndexList.contains(index)) {
                    //目标已经是未选中
                    return false
                }
            }
        }

        //Limit 过滤
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
        selectorView.isSelected = select

        if (dslSelectorConfig.dslMultiMode) {
            //多选
        } else {
            //单选

            //取消之前
            selectorViewList.forEach { view ->
                //更新样式
                val indexOf = visibleViewList.indexOf(view)
                if (!dslSelectorConfig.onSelectItemView(view, indexOf, false, fromUser)) {
                    view.isSelected = false
                    dslSelectorConfig.onStyleItemView(view, indexOf, false)
                }
            }
        }

        dslSelectorConfig.onStyleItemView(selectorView, index, select)

        return true
    }
}

/**
 * Dsl配置项
 * */
open class DslSelectorConfig {

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
        { _, _, _ ->

        }

    /**
     * 选中[View]改变回调
     * @param fromView 单选模式下有效, 表示之前选中的[View]
     * @param reselect 是否是重复选择, 只在单选模式下有效
     * @param fromUser 是否是用户产生的回调, 而非代码设置
     * */
    var onSelectViewChange: (fromView: View?, selectViewList: List<View>, reselect: Boolean, fromUser: Boolean) -> Unit =
        { _, _, _, _ ->

        }

    /**
     * 选中改变回调
     * [onSelectViewChange]
     * @param fromIndex 单选模式下有效, 表示之前选中的[View], 在可见性[child]列表中的索引
     * */
    var onSelectIndexChange: (fromIndex: Int, selectIndexList: List<Int>, reselect: Boolean, fromUser: Boolean) -> Unit =
        { fromIndex, selectList, reselect, fromUser ->
            "选择:[$fromIndex]->${selectList} reselect:$reselect fromUser:$fromUser".logi()
        }

    /**
     * 当需要选中[itemView]时回调, 返回[true]表示拦截默认处理
     * @param itemView 操作的[View]
     * @param index [itemView]在可见性view列表中的索引. 非ViewGroup中的索引
     * @param select 选中 or 取消选中
     * @return true 表示拦截默认处理
     * */
    var onSelectItemView: (itemView: View, index: Int, select: Boolean, fromUser: Boolean) -> Boolean =
        { _, _, _, _ ->
            false
        }
}
