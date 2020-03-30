package com.angcyo.dsladapter

import android.support.v7.util.DiffUtil
import android.text.TextUtils

open class RDiffCallback<T : Any>(
    val oldDatas: List<T>? = null,
    val newDatas: List<T>? = null,
    val itemDiffCallback: RItemDiffCallback<T>? = null
) : DiffUtil.Callback() {

    companion object {
        fun getListSize(list: List<*>?): Int {
            return list?.size ?: 0
        }
    }

    override fun getOldListSize(): Int {
        return getListSize(
            oldDatas
        )
    }

    override fun getNewListSize(): Int {
        return getListSize(
            newDatas
        )
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return itemDiffCallback?.getChangePayload(
            oldDatas!![oldItemPosition],
            newDatas!![newItemPosition]
        )
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return itemDiffCallback?.areItemsTheSame(
            oldDatas!![oldItemPosition],
            newDatas!![newItemPosition]
        ) ?: false
    }

    /**
     * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
     * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
     */
    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return itemDiffCallback?.areContentsTheSame(
            oldDatas!![oldItemPosition],
            newDatas!![newItemPosition]
        ) ?: false
    }
}

interface RItemDiffCallback<T : Any> {
    fun getChangePayload(oldData: T, newData: T): Any? {
        return null
    }

    /**
     * 重写此方法, 判断数据是否相等,
     * 如果item不相同, 会先调用 notifyItemRangeRemoved, 再调用 notifyItemRangeInserted
     */
    fun areItemsTheSame(oldData: T, newData: T): Boolean {
        val oldClass: Class<*> = oldData.javaClass
        val newClass: Class<*> = newData.javaClass
        return if (oldClass.isAssignableFrom(newClass) || newClass.isAssignableFrom(oldClass)) {
            true
        } else TextUtils.equals(oldClass.simpleName, newClass.simpleName)
    }

    /**
     * 重写此方法, 判断内容是否相等,
     * 如果内容不相等, 会调用notifyItemRangeChanged
     */
    fun areContentsTheSame(oldData: T, newData: T): Boolean {
        val oldClass: Class<*> = oldData.javaClass
        val newClass: Class<*> = newData.javaClass
        return if (oldClass.isAssignableFrom(newClass) ||
            newClass.isAssignableFrom(oldClass) ||
            TextUtils.equals(oldClass.simpleName, newClass.simpleName)
        ) {
            oldData == newData
        } else false
    }
}