package com.angcyo.dsladapter.filter

import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslDataFilter
import com.angcyo.dsladapter.FilterParams

/**
 * Diff计算数据过滤拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/05
 */
interface FilterInterceptor {

    /**数据源过滤拦截*/
    fun intercept(chain: FilterChain): List<DslAdapterItem>
}

data class FilterChain(
    val dslAdapter: DslAdapter,
    val dslDataFilter: DslDataFilter,
    /**过滤参数*/
    val filterParams: FilterParams,
    /**最原始的数据源*/
    val originList: List<DslAdapterItem>,
    /**上一个过滤链过滤后的数据源*/
    var requestList: List<DslAdapterItem>,
    /**是否中断后续链的调用*/
    var interruptChain: Boolean = false
)