package com.angcyo.dsladapter.filter

import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslDataFilter

/**
 * Diff计算后, 数据过滤拦截器. 可用于实现最大显示9个数据, 自动切换空状态等
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
interface FilterAfterInterceptor {
    fun intercept(chain: FilterAfterChain): List<DslAdapterItem>
}

data class FilterAfterChain(
    val dslAdapter: DslAdapter,
    val dslDataFilter: DslDataFilter,
    /**最原始的数据源*/
    val originList: List<DslAdapterItem>,
    /**上一个过滤链过滤后的数据源*/
    var requestList: List<DslAdapterItem>,
    /**是否中断后续链的调用*/
    var interruptChain: Boolean = false
)