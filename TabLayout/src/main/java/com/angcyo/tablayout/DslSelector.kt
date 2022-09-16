package com.angcyo.tablayout

import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton

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
                if (view.isSe()) {
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
                if (view.isSe() || index == dslSelectIndex) {
                    field.add(view)
                }
            }
            return field
        }

    //child 点击事件处理
    val _onChildClickListener = View.OnClickListener {
        val index = visibleViewList.indexOf(it)
        val select =
            if (dslSelectorConfig.dslMultiMode || dslSelectorConfig.dslMinSelectLimit < 1) {
                !it.isSe()
            } else {
                true
            }

        if (!interceptSelector(index, select, true)) {
            selector(
                visibleViewList.indexOf(it),
                select,
                notify = true,
                fromUser = true,
                forceNotify = it is CompoundButton && dslSelectorConfig.dslMultiMode
            )
        }
    }

    /**兼容[CompoundButton]*/
    val _onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        buttonView.isChecked = buttonView.isSelected //恢复状态 不做任何处理, 在[OnClickListener]中处理
        /*val index = visibleViewList.indexOf(buttonView)

        if (interceptSelector(index, isChecked, false)) {
            //拦截了此操作
            buttonView.isChecked = !isChecked //恢复状态
        }

        val selectorViewList = selectorViewList
        val sum = selectorViewList.size
        //Limit 过滤
        if (buttonView.isChecked) {
            if (sum > dslSelectorConfig.dslMaxSelectLimit) {
                //不允许选择
                buttonView.isChecked = false //恢复状态
            }
        } else {
            //取消选择, 检查是否达到了 limit
            if (sum < dslSelectorConfig.dslMinSelectLimit) {
                //不允许取消选择
                buttonView.isChecked = true //恢复状态
            }
        }

        if (isChecked) {
            //已经选中了控件
        } else {
            //已经取消了控件
        }*/
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
            val selector = dslSelectIndex == index || view.isSe()
            dslSelectorConfig.onStyleItemView(view, index, selector)
        }
    }

    /**更新child的点击事件*/
    fun updateClickListener() {
        parent?.apply {
            for (i in 0 until childCount) {
                getChildAt(i)?.let {
                    it.setOnClickListener(_onChildClickListener)
                    if (it is CompoundButton) {
                        it.setOnCheckedChangeListener(_onCheckedChangeListener)
                    }
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
        if (dslSelectIndex in visibleViewList.indices) {
            if (!visibleViewList[dslSelectIndex].isSe()) {
                visibleViewList[dslSelectIndex].setSe(true)
            }
        } else {
            //如果当前选中的索引, 不在可见视图列表中
            dslSelectIndex = -1
        }
        return visibleViewList
    }

    /**
     * 操作单个
     * @param index 操作目标的索引值
     * @param select 选中 or 取消选中
     * @param notify 是否需要通知事件
     * @param forceNotify 是否强制通知事件.child使用[CompoundButton]时, 推荐使用
     * */
    fun selector(
        index: Int,
        select: Boolean = true,
        notify: Boolean = true,
        fromUser: Boolean = false,
        forceNotify: Boolean = false
    ) {
        val oldSelectorList = selectorIndexList.toList()
        val lastSelectorIndex: Int? = oldSelectorList.lastOrNull()
        val reselect = !dslSelectorConfig.dslMultiMode &&
                oldSelectorList.isNotEmpty() &&
                oldSelectorList.contains(index)

        var needNotify = _selector(index, select, fromUser) || forceNotify

        if (!oldSelectorList.isChange(selectorIndexList)) {
            //选中项, 未改变时不通知
            needNotify = false
        }

        if (needNotify || reselect) {
            dslSelectIndex = selectorIndexList.lastOrNull() ?: -1
            if (notify) {
                notifySelectChange(lastSelectorIndex ?: -1, reselect, fromUser)
            }
        }
    }

    /**选择所有
     * [select] true:选择所有, false:取消所有*/
    fun selectorAll(
        select: Boolean = true,
        notify: Boolean = true,
        fromUser: Boolean = true
    ) {
        val indexList = visibleViewList.mapIndexedTo(mutableListOf()) { index, _ ->
            index
        }
        selector(indexList, select, notify, fromUser)
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
        val oldSelectorIndexList = selectorIndexList
        val lastSelectorIndex: Int? = oldSelectorIndexList.lastOrNull()

        var result = false

        indexList.forEach {
            result = _selector(it, select, fromUser) || result
        }

        if (result) {
            dslSelectIndex = selectorIndexList.lastOrNull() ?: -1
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

                    //取消之前选中
                    selectorIndexList.forEach {
                        if (it != index) {
                            visibleViewList[it].setSe(false)
                        }
                    }

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
        selectorView.setSe(select)

        if (dslSelectorConfig.dslMultiMode) {
            //多选
        } else {
            //单选

            //取消之前
            selectorViewList.forEach { view ->
                //更新样式
                val indexOf = visibleViewList.indexOf(view)
                if (indexOf != index &&
                    !dslSelectorConfig.onSelectItemView(view, indexOf, false, fromUser)
                ) {
                    view.setSe(false)
                    dslSelectorConfig.onStyleItemView(view, indexOf, false)
                }
            }
        }

        dslSelectorConfig.onStyleItemView(selectorView, index, select)

        return true
    }

    /**是否选中状态*/
    fun View.isSe(): Boolean {
        return isSelected || if (this is CompoundButton) isChecked else false
    }

    fun View.setSe(se: Boolean) {
        isSelected = se
        if (this is CompoundButton) isChecked = se
    }
}

/**
 * Dsl配置项
 * */
open class DslSelectorConfig {

    /**取消选择时, 最小需要保持多个选中. 可以决定单选时, 是否允许取消所有选中*/
    var dslMinSelectLimit = 1

    /**多选时, 最大允许多个选中*/
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
     * 选中[View]改变回调, 优先于[onSelectIndexChange]触发, 区别在于参数类型不一样
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

/**[DslSelector]组件*/
fun dslSelector(viewGroup: ViewGroup, config: DslSelectorConfig.() -> Unit = {}): DslSelector {
    return DslSelector().apply {
        install(viewGroup, config)
    }
}