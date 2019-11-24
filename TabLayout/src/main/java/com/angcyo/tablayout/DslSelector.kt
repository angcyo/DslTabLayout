package com.angcyo.tablayout

import android.view.View

/**
 * 用来操作[ViewGroup]中的[child], 支持单选, 多选, 拦截.
 * 操作的都是可见性为[VISIBLE]的[View]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/24
 */

open class DslSelector {

    /**设置[Listener]时, 就通知*/
    var dslNotifyFirst: Boolean = true

    /**是否是多选模式*/
    var dslMultiMode: Boolean = false

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
    var onSelectIndexChange: (fromIndex: View?, selectList: List<Int>) -> Unit =
        { fromIndex, selectList ->

        }
}