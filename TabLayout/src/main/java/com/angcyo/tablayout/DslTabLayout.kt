package com.angcyo.tablayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * https://github.com/angcyo/DslTabLayout
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/23
 */

open class DslTabLayout(
    context: Context,
    val attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {

    /**在未指定[minHeight]的[wrap_content]情况下的高度*/
    var itemDefaultHeight = 40 * dpi

    /**item是否等宽*/
    var itemIsEquWidth = false

    /**item是否支持选择, 只限制点击事件, 不限制滚动事件*/
    var itemEnableSelector = true

    /**当子Item数量在此范围内时,开启等宽,此属性优先级最高
     * [~3] 小于等于3个
     * [3~] 大于等于3个
     * [3~5] 3<= <=5
     * */
    var itemEquWidthCountRange: IntRange? = null

    /**智能判断Item是否等宽.
     * 如果所有子项, 未撑满tab时, 则开启等宽模式.此属性会覆盖[itemIsEquWidth]*/
    var itemAutoEquWidth = false

    /**在等宽的情况下, 指定item的宽度, 小于0, 平分*/
    var itemWidth = -3

    /**是否绘制指示器*/
    var drawIndicator = true

    /**指示器*/
    var tabIndicator: DslTabIndicator = DslTabIndicator(this)
        set(value) {
            field = value
            field.initAttribute(context, attributeSet)
        }

    /**指示器动画时长*/
    var tabIndicatorAnimationDuration = 240L

    /**默认选中位置*/
    var tabDefaultIndex = 0

    /**回调监听器和样式配置器*/
    var tabLayoutConfig: DslTabLayoutConfig? = null
        set(value) {
            field = value

            field?.initAttribute(context, attributeSet)
        }

    /**边框绘制*/
    var tabBorder: DslTabBorder? = null
        set(value) {
            field = value
            field?.callback = this
            field?.initAttribute(context, attributeSet)
        }
    var drawBorder = false

    /**垂直分割线*/
    var tabDivider: DslTabDivider? = null
        set(value) {
            field = value
            field?.callback = this
            field?.initAttribute(context, attributeSet)
        }
    var drawDivider = false

    /**未读数角标*/
    var tabBadge: DslTabBadge? = null
        set(value) {
            field = value
            field?.callback = this
            field?.initAttribute(context, attributeSet)
        }
    var drawBadge = false

    /**快速角标配置项, 方便使用者*/
    val tabBadgeConfigMap = mutableMapOf<Int, TabBadgeConfig>()

    /**角标绘制配置*/
    var onTabBadgeConfig: (child: View, tabBadge: DslTabBadge, index: Int) -> TabBadgeConfig? =
        { _, tabBadge, index ->
            val badgeConfig = getBadgeConfig(index)
            if (!isInEditMode) {
                tabBadge.updateBadgeConfig(badgeConfig)
            }
            badgeConfig
        }

    /**是否绘制突出*/
    var drawHighlight = false

    /**选中突出提示*/
    var tabHighlight: DslTabHighlight? = null
        set(value) {
            field = value
            field?.callback = this
            field?.initAttribute(context, attributeSet)
        }

    /**如果使用了高凸模式. 请使用这个属性设置背景色*/
    var tabConvexBackgroundDrawable: Drawable? = null

    /**是否激活滑动选择模式*/
    var tabEnableSelectorMode = false

    /**布局的方向*/
    var orientation: Int = LinearLayout.HORIZONTAL

    /**布局时, 滚动到居中是否需要动画*/
    var layoutScrollAnim: Boolean = false

    /**滚动动画的时长*/
    var scrollAnimDuration = 250

    //<editor-fold desc="内部属性">

    //fling 速率阈值
    var _minFlingVelocity = 0
    var _maxFlingVelocity = 0

    //scroll 阈值
    var _touchSlop = 0

    //临时变量
    val _tempRect = Rect()

    //childView选择器
    val dslSelector: DslSelector by lazy {
        DslSelector().install(this) {
            onStyleItemView = { itemView, index, select ->
                tabLayoutConfig?.onStyleItemView?.invoke(itemView, index, select)
            }
            onSelectItemView = { itemView, index, select, fromUser ->
                tabLayoutConfig?.onSelectItemView?.invoke(itemView, index, select, fromUser)
                    ?: false
            }
            onSelectViewChange = { fromView, selectViewList, reselect, fromUser ->
                tabLayoutConfig?.onSelectViewChange?.invoke(
                    fromView,
                    selectViewList,
                    reselect,
                    fromUser
                )
            }
            onSelectIndexChange = { fromIndex, selectList, reselect, fromUser ->
                if (tabLayoutConfig == null) {
                    "选择:[$fromIndex]->${selectList} reselect:$reselect fromUser:$fromUser".logi()
                }

                val toIndex = selectList.lastOrNull() ?: -1
                _animateToItem(fromIndex, toIndex)

                _scrollToTarget(toIndex, tabIndicator.indicatorAnim)
                postInvalidate()

                //如果设置[tabLayoutConfig?.onSelectIndexChange], 那么会覆盖[_viewPagerDelegate]的操作.
                tabLayoutConfig?.onSelectIndexChange?.invoke(
                    fromIndex,
                    selectList,
                    reselect,
                    fromUser
                ) ?: _viewPagerDelegate?.onSetCurrentItem(fromIndex, toIndex, reselect, fromUser)
            }
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        itemIsEquWidth =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_item_is_equ_width, itemIsEquWidth)
        val maxEquWidthCount =
            typedArray.getInt(R.styleable.DslTabLayout_tab_item_equ_width_count, -1)
        if (maxEquWidthCount >= 0) {
            itemEquWidthCountRange = IntRange(maxEquWidthCount, Int.MAX_VALUE)
        }
        if (typedArray.hasValue(R.styleable.DslTabLayout_tab_item_equ_width_count_range)) {
            val equWidthCountRangeString =
                typedArray.getString(R.styleable.DslTabLayout_tab_item_equ_width_count_range)
            if (equWidthCountRangeString.isNullOrBlank()) {
                itemEquWidthCountRange = null
            } else {
                val rangeList = equWidthCountRangeString.split("~")
                if (rangeList.size() >= 2) {
                    val min = rangeList.getOrNull(0)?.toIntOrNull() ?: 0
                    val max = rangeList.getOrNull(1)?.toIntOrNull() ?: Int.MAX_VALUE
                    itemEquWidthCountRange = IntRange(min, max)
                } else {
                    val min = rangeList.getOrNull(0)?.toIntOrNull() ?: Int.MAX_VALUE
                    itemEquWidthCountRange = IntRange(min, Int.MAX_VALUE)
                }
            }
        }
        itemAutoEquWidth = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_item_auto_equ_width,
            itemAutoEquWidth
        )
        itemWidth =
            typedArray.getDimensionPixelOffset(R.styleable.DslTabLayout_tab_item_width, itemWidth)
        itemDefaultHeight = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_tab_item_default_height,
            itemDefaultHeight
        )
        tabDefaultIndex =
            typedArray.getInt(R.styleable.DslTabLayout_tab_default_index, tabDefaultIndex)

        drawIndicator =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_draw_indicator, drawIndicator)
        drawDivider =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_draw_divider, drawDivider)
        drawBorder =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_draw_border, drawBorder)
        drawBadge =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_draw_badge, drawBadge)
        drawHighlight =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_draw_highlight, drawHighlight)

        tabEnableSelectorMode =
            typedArray.getBoolean(
                R.styleable.DslTabLayout_tab_enable_selector_mode,
                tabEnableSelectorMode
            )

        tabConvexBackgroundDrawable =
            typedArray.getDrawable(R.styleable.DslTabLayout_tab_convex_background)

        orientation = typedArray.getInt(R.styleable.DslTabLayout_tab_orientation, orientation)

        layoutScrollAnim =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_layout_scroll_anim, layoutScrollAnim)
        scrollAnimDuration =
            typedArray.getInt(R.styleable.DslTabLayout_tab_scroll_anim_duration, scrollAnimDuration)

        //preview
        if (isInEditMode) {
            val layoutId =
                typedArray.getResourceId(R.styleable.DslTabLayout_tab_preview_item_layout_id, -1)
            val layoutCount =
                typedArray.getInt(R.styleable.DslTabLayout_tab_preview_item_count, 3)
            if (layoutId != -1) {
                for (i in 0 until layoutCount) {
                    inflate(layoutId, true).let {
                        if (it is TextView) {
                            if (it.text.isNullOrEmpty()) {
                                it.text = "Item $i"
                            } else {
                                it.text = "${it.text}/$i"
                            }
                        }
                    }
                }
            }
        }

        typedArray.recycle()

        val vc = ViewConfiguration.get(context)
        _minFlingVelocity = vc.scaledMinimumFlingVelocity
        _maxFlingVelocity = vc.scaledMaximumFlingVelocity
        //_touchSlop = vc.scaledTouchSlop

        if (drawIndicator) {
            //直接初始化的变量, 不会触发set方法.
            tabIndicator.initAttribute(context, attributeSet)
        }

        if (drawBorder) {
            tabBorder = DslTabBorder()
        }
        if (drawDivider) {
            tabDivider = DslTabDivider()
        }
        if (drawBadge) {
            tabBadge = DslTabBadge()
        }
        if (drawHighlight) {
            tabHighlight = DslTabHighlight(this)
        }

        //样式配置器
        tabLayoutConfig = DslTabLayoutConfig(this)

        //开启绘制
        setWillNotDraw(false)
    }

    //</editor-fold desc="内部属性">

    //<editor-fold desc="可操作性方法">

    /**当前选中item的索引*/
    val currentItemIndex: Int
        get() = dslSelector.dslSelectIndex

    /**当前选中的itemView*/
    val currentItemView: View?
        get() = dslSelector.visibleViewList.getOrNull(currentItemIndex)

    /**设置tab的位置*/
    fun setCurrentItem(index: Int, notify: Boolean = true, fromUser: Boolean = false) {
        if (currentItemIndex == index) {
            _scrollToTarget(index, tabIndicator.indicatorAnim)
            return
        }
        dslSelector.selector(index, true, notify, fromUser)
    }

    /**关联[ViewPagerDelegate]*/
    fun setupViewPager(viewPagerDelegate: ViewPagerDelegate) {
        _viewPagerDelegate = viewPagerDelegate
    }

    /**配置一个新的[DslTabLayoutConfig]给[DslTabLayout]*/
    fun setTabLayoutConfig(
        config: DslTabLayoutConfig = DslTabLayoutConfig(this),
        doIt: DslTabLayoutConfig.() -> Unit = {}
    ) {
        tabLayoutConfig = config
        configTabLayoutConfig(doIt)
    }

    /**配置[DslTabLayoutConfig]*/
    fun configTabLayoutConfig(config: DslTabLayoutConfig.() -> Unit = {}) {
        if (tabLayoutConfig == null) {
            tabLayoutConfig = DslTabLayoutConfig(this)
        }
        tabLayoutConfig?.config()
        dslSelector.updateStyle()
    }

    /**观察index的改变回调*/
    fun observeIndexChange(
        config: DslTabLayoutConfig.() -> Unit = {},
        action: (fromIndex: Int, toIndex: Int, reselect: Boolean, fromUser: Boolean) -> Unit
    ) {
        configTabLayoutConfig {
            config()
            onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                action(fromIndex, selectIndexList.firstOrNull() ?: -1, reselect, fromUser)
            }
        }
    }

    fun getBadgeConfig(index: Int): TabBadgeConfig {
        return tabBadgeConfigMap.getOrElse(index) {
            tabBadge?.defaultBadgeConfig?.copy() ?: TabBadgeConfig()
        }
    }

    fun updateTabBadge(index: Int, badgeText: String?) {
        updateTabBadge(index) {
            this.badgeText = badgeText
        }
    }

    /**更新角标*/
    fun updateTabBadge(index: Int, config: TabBadgeConfig.() -> Unit) {
        val badgeConfig = getBadgeConfig(index)
        tabBadgeConfigMap[index] = badgeConfig
        badgeConfig.config()
        postInvalidate()
    }

    //</editor-fold desc="可操作性方法">

    //<editor-fold desc="初始化相关">

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        updateTabLayout()
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        updateTabLayout()
    }

    open fun updateTabLayout() {
        dslSelector.updateVisibleList()
        dslSelector.updateStyle()
        dslSelector.updateClickListener()
    }

    override fun draw(canvas: Canvas) {
        //Log.e("angcyo", "...draw...")

        if (drawIndicator) {
            tabIndicator.setBounds(0, 0, measuredWidth, measuredHeight)
        }

        //自定义的背景
        tabConvexBackgroundDrawable?.apply {
            if (isHorizontal()) {
                setBounds(0, _maxConvexHeight, right - left, bottom - top)
            } else {
                setBounds(0, 0, measuredWidth - _maxConvexHeight, bottom - top)
            }

            if (scrollX or scrollY == 0) {
                draw(canvas)
            } else {
                canvas.holdLocation {
                    draw(canvas)
                }
            }
        }

        super.draw(canvas)

        //突出显示
        if (drawHighlight) {
            tabHighlight?.draw(canvas)
        }

        val visibleChildCount = dslSelector.visibleViewList.size

        //绘制在child的上面
        if (drawDivider) {
            if (isHorizontal()) {
                if (isLayoutRtl) {
                    var right = 0
                    tabDivider?.apply {
                        val top = paddingTop + dividerMarginTop
                        val bottom = measuredHeight - paddingBottom - dividerMarginBottom
                        dslSelector.visibleViewList.forEachIndexed { index, view ->

                            if (haveBeforeDivider(index, visibleChildCount)) {
                                right = view.right + dividerMarginLeft + dividerWidth
                                setBounds(right - dividerWidth, top, right, bottom)
                                draw(canvas)
                            }

                            if (haveAfterDivider(index, visibleChildCount)) {
                                right = view.right - view.measuredWidth - dividerMarginRight
                                setBounds(right - dividerWidth, top, right, bottom)
                                draw(canvas)
                            }

                        }
                    }
                } else {
                    var left = 0
                    tabDivider?.apply {
                        val top = paddingTop + dividerMarginTop
                        val bottom = measuredHeight - paddingBottom - dividerMarginBottom
                        dslSelector.visibleViewList.forEachIndexed { index, view ->

                            if (haveBeforeDivider(index, visibleChildCount)) {
                                left = view.left - dividerMarginRight - dividerWidth
                                setBounds(left, top, left + dividerWidth, bottom)
                                draw(canvas)
                            }

                            if (haveAfterDivider(index, visibleChildCount)) {
                                left = view.right + dividerMarginLeft
                                setBounds(left, top, left + dividerWidth, bottom)
                                draw(canvas)
                            }

                        }
                    }
                }
            } else {
                var top = 0
                tabDivider?.apply {
                    val left = paddingStart + dividerMarginLeft
                    val right = measuredWidth - paddingEnd - dividerMarginRight
                    dslSelector.visibleViewList.forEachIndexed { index, view ->

                        if (haveBeforeDivider(index, visibleChildCount)) {
                            top = view.top - dividerMarginBottom - dividerHeight
                            setBounds(left, top, right, top + dividerHeight)
                            draw(canvas)
                        }

                        if (haveAfterDivider(index, visibleChildCount)) {
                            top = view.bottom + dividerMarginTop
                            setBounds(left, top, right, top + dividerHeight)
                            draw(canvas)
                        }
                    }
                }
            }
        }
        if (drawBorder) {
            //边框不跟随滚动
            canvas.holdLocation {
                tabBorder?.draw(canvas)
            }
        }
        if (drawIndicator && tabIndicator.indicatorStyle.have(DslTabIndicator.INDICATOR_STYLE_FOREGROUND)) {
            //前景显示
            tabIndicator.draw(canvas)
        }
        if (drawBadge) {
            tabBadge?.apply {
                dslSelector.visibleViewList.forEachIndexed { index, child ->
                    val badgeConfig = onTabBadgeConfig(child, this, index)

                    var left: Int
                    var top: Int
                    var right: Int
                    var bottom: Int

                    var anchorView: View = child

                    if (badgeConfig != null && badgeConfig.badgeAnchorChildIndex >= 0) {
                        anchorView =
                            child.getChildOrNull(badgeConfig.badgeAnchorChildIndex) ?: child

                        anchorView.getLocationInParent(this@DslTabLayout, _tempRect)

                        left = _tempRect.left
                        top = _tempRect.top
                        right = _tempRect.right
                        bottom = _tempRect.bottom
                    } else {
                        left = anchorView.left
                        top = anchorView.top
                        right = anchorView.right
                        bottom = anchorView.bottom
                    }

                    if (badgeConfig != null && badgeConfig.badgeIgnoreChildPadding) {
                        left += anchorView.paddingStart
                        top += anchorView.paddingTop
                        right -= anchorView.paddingEnd
                        bottom -= anchorView.paddingBottom
                    }

                    setBounds(left, top, right, bottom)

                    updateOriginDrawable()

                    if (isInEditMode) {
                        badgeText = if (index == visibleChildCount - 1) {
                            //预览中, 强制最后一个角标为圆点类型, 方便查看预览.
                            ""
                        } else {
                            xmlBadgeText
                        }
                    }

                    draw(canvas)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawBorder) {
            //边框不跟随滚动
            canvas.holdLocation {
                tabBorder?.drawBorderBackground(canvas)
            }
        }

        //绘制在child的后面
        if (drawIndicator && !tabIndicator.indicatorStyle.have(DslTabIndicator.INDICATOR_STYLE_FOREGROUND)) {
            //背景绘制
            tabIndicator.draw(canvas)
        }
    }

    /**保持位置不变*/
    fun Canvas.holdLocation(action: () -> Unit) {
        translate(scrollX.toFloat(), scrollY.toFloat())
        action()
        translate(-scrollX.toFloat(), -scrollY.toFloat())
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        return super.drawChild(canvas, child, drawingTime)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) ||
                who == tabIndicator /*||
                who == tabBorder ||
                who == tabDivider ||
                who == tabConvexBackgroundDrawable*/ /*||
                who == tabBadge 防止循环绘制*/
    }

    //</editor-fold desc="初始化相关">

    //<editor-fold desc="布局相关">

    //所有child的总宽度, 不包含parent的padding
    var _childAllWidthSum = 0

    //最大的凸起高度
    var _maxConvexHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (dslSelector.dslSelectIndex < 0) {
            //还没有选中
            setCurrentItem(tabDefaultIndex)
        }

        if (isHorizontal()) {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec)
        } else {
            measureVertical(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun measureHorizontal(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        dslSelector.updateVisibleList()

        val visibleChildList = dslSelector.visibleViewList
        val visibleChildCount = visibleChildList.size

        //控制最小大小
        val tabMinHeight = if (suggestedMinimumHeight > 0) {
            suggestedMinimumHeight
        } else {
            itemDefaultHeight
        }

        if (visibleChildCount == 0) {
            setMeasuredDimension(
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                getDefaultSize(tabMinHeight, heightMeasureSpec)
            )
            return
        }

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        _maxConvexHeight = 0

        var childWidthSpec: Int = -1

        //记录child最大的height, 用来实现tabLayout wrap_content, 包括突出的大小
        var childMaxHeight = tabMinHeight //child最大的高度

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (heightSize == 0) {
                heightSize = Int.MAX_VALUE
            }
        }

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            if (widthSize == 0) {
                widthSize = Int.MAX_VALUE
            }
        }

        //分割线需要排除的宽度
        val dividerWidthExclude =
            if (drawDivider) tabDivider?.run { dividerWidth + dividerMarginLeft + dividerMarginRight }
                ?: 0 else 0

        //智能等宽判断
        if (itemAutoEquWidth) {
            var childMaxWidth = 0 //所有child宽度总和
            visibleChildList.forEachIndexed { index, child ->
                val lp: LayoutParams = child.layoutParams as LayoutParams
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                childMaxWidth += lp.marginStart + lp.marginEnd + child.measuredWidth

                if (drawDivider) {
                    if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                        childMaxWidth += dividerWidthExclude
                    }
                    if (tabDivider?.haveAfterDivider(index, visibleChildList.size) == true) {
                        childMaxWidth += dividerWidthExclude
                    }
                }
            }

            itemIsEquWidth = childMaxWidth <= widthSize
        }

        itemEquWidthCountRange?.let {
            itemIsEquWidth = it.contains(visibleChildCount)
        }

        //等宽时, child宽度的测量模式
        val childEquWidthSpec = if (itemIsEquWidth) {
            exactlyMeasure(
                if (itemWidth > 0) {
                    itemWidth
                } else {
                    var excludeWidth = paddingStart + paddingEnd
                    visibleChildList.forEachIndexed { index, child ->
                        if (drawDivider) {
                            if (tabDivider?.haveBeforeDivider(
                                    index,
                                    visibleChildList.size
                                ) == true
                            ) {
                                excludeWidth += dividerWidthExclude
                            }
                            if (tabDivider?.haveAfterDivider(
                                    index,
                                    visibleChildList.size
                                ) == true
                            ) {
                                excludeWidth += dividerWidthExclude
                            }
                        }
                        val lp = child.layoutParams as LayoutParams
                        excludeWidth += lp.marginStart + lp.marginEnd
                    }
                    (widthSize - excludeWidth) / visibleChildCount
                }
            )
        } else {
            -1
        }

        //...end

        _childAllWidthSum = 0

        //没有设置weight属性的child宽度总和, 用于计算剩余空间
        var allChildUsedWidth = 0

        fun measureChild(childView: View, heightSpec: Int? = null) {
            val lp = childView.layoutParams as LayoutParams

            //child高度测量模式
            var childHeightSpec: Int = -1

            val widthHeight = calcLayoutWidthHeight(
                lp.layoutWidth, lp.layoutHeight,
                widthSize, heightSize, 0, 0
            )

            if (heightMode == MeasureSpec.EXACTLY) {
                //固定高度
                childHeightSpec =
                    exactlyMeasure(heightSize - paddingTop - paddingBottom - lp.topMargin - lp.bottomMargin)
            } else {
                if (widthHeight[1] > 0) {
                    heightSize = widthHeight[1]
                    childHeightSpec = exactlyMeasure(heightSize)
                    heightSize += paddingTop + paddingBottom
                } else {
                    childHeightSpec = if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                        exactlyMeasure(tabMinHeight)
                    } else {
                        atmostMeasure(Int.MAX_VALUE)
                    }
                }
            }

            val childConvexHeight = lp.layoutConvexHeight

            //...end

            //计算宽度测量模式
            childWidthSpec //no op

            if (heightSpec != null) {
                childView.measure(childWidthSpec, heightSpec)
            } else {
                childView.measure(childWidthSpec, childHeightSpec)
            }
            if (childConvexHeight > 0) {
                _maxConvexHeight = max(_maxConvexHeight, childConvexHeight)
                //需要凸起
                val spec = exactlyMeasure(childView.measuredHeight + childConvexHeight)
                childView.measure(childWidthSpec, spec)
            }
            childMaxHeight = max(childMaxHeight, childView.measuredHeight)
        }

        visibleChildList.forEachIndexed { index, childView ->
            val lp = childView.layoutParams as LayoutParams
            var childUsedWidth = 0
            if (lp.weight < 0) {
                val widthHeight = calcLayoutWidthHeight(
                    lp.layoutWidth, lp.layoutHeight,
                    widthSize, heightSize, 0, 0
                )

                //计算宽度测量模式
                childWidthSpec = when {
                    itemIsEquWidth -> childEquWidthSpec
                    widthHeight[0] > 0 -> exactlyMeasure(widthHeight[0])
                    lp.width == ViewGroup.LayoutParams.MATCH_PARENT -> exactlyMeasure(widthSize - paddingStart - paddingEnd)
                    lp.width > 0 -> exactlyMeasure(lp.width)
                    else -> atmostMeasure(widthSize - paddingStart - paddingEnd)
                }

                measureChild(childView)

                childUsedWidth = childView.measuredWidth + lp.marginStart + lp.marginEnd
            } else {
                childUsedWidth = lp.marginStart + lp.marginEnd
            }

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                    childUsedWidth += dividerWidthExclude
                }
                if (tabDivider?.haveAfterDivider(index, visibleChildList.size) == true) {
                    childUsedWidth += dividerWidthExclude
                }
            }

            childMaxHeight = max(childMaxHeight, childView.measuredHeight)
            allChildUsedWidth += childUsedWidth
            _childAllWidthSum += childUsedWidth
        }

        //剩余空间
        val spaceSize = widthSize - allChildUsedWidth

        //计算weight
        visibleChildList.forEach { childView ->
            val lp = childView.layoutParams as LayoutParams
            if (lp.weight > 0) {
                val widthHeight = calcLayoutWidthHeight(
                    lp.layoutWidth, lp.layoutHeight,
                    widthSize, heightSize, 0, 0
                )

                //计算宽度测量模式
                childWidthSpec = when {
                    itemIsEquWidth -> childEquWidthSpec
                    spaceSize > 0 -> exactlyMeasure(spaceSize * lp.weight)
                    widthHeight[0] > 0 -> exactlyMeasure(allChildUsedWidth)
                    lp.width == ViewGroup.LayoutParams.MATCH_PARENT -> exactlyMeasure(widthSize - paddingStart - paddingEnd)
                    lp.width > 0 -> exactlyMeasure(lp.width)
                    else -> atmostMeasure(widthSize - paddingStart - paddingEnd)
                }

                measureChild(childView)

                childMaxHeight = max(childMaxHeight, childView.measuredHeight)

                //上面已经处理了分割线和margin的距离了
                _childAllWidthSum += childView.measuredWidth
            }
        }
        //...end

        if (heightMode == MeasureSpec.AT_MOST) {
            //wrap_content 情况下, 重新测量所有子view
            val childHeightSpec = exactlyMeasure(
                max(
                    childMaxHeight - _maxConvexHeight,
                    suggestedMinimumHeight - paddingTop - paddingBottom
                )
            )
            visibleChildList.forEach { childView ->
                measureChild(childView, childHeightSpec)
            }
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = min(_childAllWidthSum + paddingStart + paddingEnd, widthSize)
        }

        if (visibleChildList.isEmpty()) {
            heightSize = if (suggestedMinimumHeight > 0) {
                suggestedMinimumHeight
            } else {
                itemDefaultHeight
            }
        } else if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = max(
                childMaxHeight - _maxConvexHeight + paddingTop + paddingBottom,
                suggestedMinimumHeight
            )
        }

        setMeasuredDimension(widthSize, heightSize + _maxConvexHeight)
    }

    fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        dslSelector.updateVisibleList()

        val visibleChildList = dslSelector.visibleViewList
        val visibleChildCount = visibleChildList.size

        if (visibleChildCount == 0) {
            setMeasuredDimension(
                getDefaultSize(
                    if (suggestedMinimumHeight > 0) {
                        suggestedMinimumHeight
                    } else {
                        itemDefaultHeight
                    }, widthMeasureSpec
                ),
                getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
            )
            return
        }

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        _maxConvexHeight = 0

        //child高度测量模式
        var childHeightSpec: Int = -1
        var childWidthSpec: Int = -1

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (heightSize == 0) {
                heightSize = Int.MAX_VALUE
            }
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            //固定宽度
            childWidthSpec = exactlyMeasure(widthSize - paddingStart - paddingEnd)
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            if (widthSize == 0) {
                widthSize = Int.MAX_VALUE
            }
        }

        //分割线需要排除的宽度
        val dividerHeightExclude =
            if (drawDivider) tabDivider?.run { dividerHeight + dividerMarginTop + dividerMarginBottom }
                ?: 0 else 0

        //智能等宽判断
        if (itemAutoEquWidth) {
            var childMaxHeight = 0 //所有child高度总和
            visibleChildList.forEachIndexed { index, child ->
                val lp: LayoutParams = child.layoutParams as LayoutParams
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                childMaxHeight += lp.topMargin + lp.bottomMargin + child.measuredHeight

                if (drawDivider) {
                    if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                        childMaxHeight += dividerHeightExclude
                    }
                    if (tabDivider?.haveAfterDivider(index, visibleChildList.size) == true) {
                        childMaxHeight += dividerHeightExclude
                    }
                }
            }

            itemIsEquWidth = childMaxHeight <= heightSize
        }

        itemEquWidthCountRange?.let {
            itemIsEquWidth = it.contains(visibleChildCount)
        }

        //等宽时, child高度的测量模式
        val childEquHeightSpec = if (itemIsEquWidth) {
            exactlyMeasure(
                if (itemWidth > 0) {
                    itemWidth
                } else {
                    var excludeHeight = paddingTop + paddingBottom
                    visibleChildList.forEachIndexed { index, child ->
                        if (drawDivider) {
                            if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true
                            ) {
                                excludeHeight += dividerHeightExclude
                            }
                            if (tabDivider?.haveAfterDivider(index, visibleChildList.size) == true
                            ) {
                                excludeHeight += dividerHeightExclude
                            }
                        }
                        val lp = child.layoutParams as LayoutParams
                        excludeHeight += lp.topMargin + lp.bottomMargin
                    }
                    (heightSize - excludeHeight) / visibleChildCount
                }
            )
        } else {
            -1
        }

        //...end

        _childAllWidthSum = 0

        var wrapContentWidth = false

        //没有设置weight属性的child宽度总和, 用于计算剩余空间
        var allChildUsedHeight = 0

        fun measureChild(childView: View) {
            val lp = childView.layoutParams as LayoutParams

            //纵向布局, 不支持横向margin支持
            lp.marginStart = 0
            lp.marginEnd = 0

            val childConvexHeight = lp.layoutConvexHeight
            _maxConvexHeight = max(_maxConvexHeight, childConvexHeight)

            val widthHeight = calcLayoutWidthHeight(
                lp.layoutWidth, lp.layoutHeight,
                widthSize, heightSize, 0, 0
            )

            //计算宽度测量模式
            wrapContentWidth = false
            if (childWidthSpec == -1) {
                if (widthHeight[0] > 0) {
                    widthSize = widthHeight[0]
                    childWidthSpec = exactlyMeasure(widthSize)
                    widthSize += paddingStart + paddingEnd
                }
            }

            if (childWidthSpec == -1) {
                if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {

                    widthSize = if (suggestedMinimumWidth > 0) {
                        suggestedMinimumWidth
                    } else {
                        itemDefaultHeight
                    }

                    childWidthSpec = exactlyMeasure(widthSize)

                    widthSize += paddingStart + paddingEnd
                } else {
                    childWidthSpec = atmostMeasure(widthSize)
                    wrapContentWidth = true
                }
            }
            //...end

            //计算高度测量模式
            childHeightSpec //no op

            if (childConvexHeight > 0) {
                //需要凸起
                val childConvexWidthSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(childWidthSpec) + childConvexHeight,
                    MeasureSpec.getMode(childWidthSpec)
                )
                childView.measure(childConvexWidthSpec, childHeightSpec)
            } else {
                childView.measure(childWidthSpec, childHeightSpec)
            }

            if (wrapContentWidth) {
                widthSize = childView.measuredWidth
                childWidthSpec = exactlyMeasure(widthSize)
                widthSize += paddingStart + paddingEnd
            }
        }

        visibleChildList.forEachIndexed { index, childView ->
            val lp = childView.layoutParams as LayoutParams
            var childUsedHeight = 0
            if (lp.weight < 0) {
                val widthHeight = calcLayoutWidthHeight(
                    lp.layoutWidth, lp.layoutHeight,
                    widthSize, heightSize, 0, 0
                )

                //计算高度测量模式
                childHeightSpec = when {
                    itemIsEquWidth -> childEquHeightSpec
                    widthHeight[1] > 0 -> exactlyMeasure(widthHeight[1])
                    lp.height == ViewGroup.LayoutParams.MATCH_PARENT -> exactlyMeasure(heightSize - paddingTop - paddingBottom)
                    lp.height > 0 -> exactlyMeasure(lp.height)
                    else -> atmostMeasure(heightSize - paddingTop - paddingBottom)
                }

                measureChild(childView)

                childUsedHeight = childView.measuredHeight + lp.topMargin + lp.bottomMargin
            } else {
                childUsedHeight = lp.topMargin + lp.bottomMargin
            }

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                    childUsedHeight += dividerHeightExclude
                }
                if (tabDivider?.haveAfterDivider(index, visibleChildList.size) == true) {
                    childUsedHeight += dividerHeightExclude
                }
            }

            allChildUsedHeight += childUsedHeight
            _childAllWidthSum += childUsedHeight
        }

        //剩余空间
        val spaceSize = heightSize - allChildUsedHeight

        //计算weight
        visibleChildList.forEach { childView ->
            val lp = childView.layoutParams as LayoutParams
            if (lp.weight > 0) {
                val widthHeight = calcLayoutWidthHeight(
                    lp.layoutWidth, lp.layoutHeight,
                    widthSize, heightSize, 0, 0
                )

                //计算高度测量模式
                childHeightSpec = when {
                    itemIsEquWidth -> childEquHeightSpec
                    spaceSize > 0 -> exactlyMeasure(spaceSize * lp.weight)
                    widthHeight[1] > 0 -> exactlyMeasure(allChildUsedHeight)
                    lp.height == ViewGroup.LayoutParams.MATCH_PARENT -> exactlyMeasure(heightSize - paddingTop - paddingBottom)
                    lp.height > 0 -> exactlyMeasure(lp.height)
                    else -> atmostMeasure(heightSize - paddingTop - paddingBottom)
                }

                measureChild(childView)

                //上面已经处理了分割线和margin的距离了
                _childAllWidthSum += childView.measuredHeight
            }
        }
        //...end

        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = min(_childAllWidthSum + paddingTop + paddingBottom, heightSize)
        }

        if (visibleChildList.isEmpty()) {
            widthSize = if (suggestedMinimumWidth > 0) {
                suggestedMinimumWidth
            } else {
                itemDefaultHeight
            }
        }

        setMeasuredDimension(widthSize + _maxConvexHeight, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isHorizontal()) {
            layoutHorizontal(changed, l, t, r, b)
        } else {
            layoutVertical(changed, l, t, r, b)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //check
        restoreScroll()

        if (dslSelector.dslSelectIndex < 0) {
            //还没有选中
            setCurrentItem(tabDefaultIndex)
        } else {
            if (_overScroller.isFinished) {
                _scrollToTarget(dslSelector.dslSelectIndex, layoutScrollAnim)
            }
        }
    }

    val isLayoutRtl: Boolean
        get() = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

    var _layoutDirection: Int = -1

    //API 17
    override fun onRtlPropertiesChanged(layoutDirection: Int) {
        super.onRtlPropertiesChanged(layoutDirection)

        if (layoutDirection != _layoutDirection) {
            _layoutDirection = layoutDirection
            if (orientation == LinearLayout.HORIZONTAL) {
                requestLayout()
            }
        }
    }

    fun layoutHorizontal(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val isRtl = isLayoutRtl

        var left = paddingStart
        var right = measuredWidth - paddingEnd

        var childBottom = measuredHeight - paddingBottom

        val dividerExclude = if (drawDivider) tabDivider?.run {
            dividerWidth + dividerMarginLeft + dividerMarginRight
        } ?: 0 else 0

        val visibleChildList = dslSelector.visibleViewList
        visibleChildList.forEachIndexed { index, childView ->

            val lp = childView.layoutParams as LayoutParams
            val verticalGravity = lp.gravity and Gravity.VERTICAL_GRAVITY_MASK

            if (isRtl) {
                right -= lp.marginEnd
            } else {
                left += lp.marginStart
            }

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {

                    if (isRtl) {
                        right -= dividerExclude
                    } else {
                        left += dividerExclude
                    }
                }
            }

            childBottom = when (verticalGravity) {
                Gravity.CENTER_VERTICAL -> measuredHeight - paddingBottom -
                        ((measuredHeight - paddingTop - paddingBottom - _maxConvexHeight) / 2 -
                                childView.measuredHeight / 2)

                Gravity.BOTTOM -> measuredHeight - paddingBottom
                else -> paddingTop + lp.topMargin + childView.measuredHeight
            }

            if (isRtl) {
                childView.layout(
                    right - childView.measuredWidth,
                    childBottom - childView.measuredHeight,
                    right,
                    childBottom
                )
                right -= childView.measuredWidth + lp.marginStart
            } else {
                childView.layout(
                    left,
                    childBottom - childView.measuredHeight,
                    left + childView.measuredWidth,
                    childBottom
                )
                left += childView.measuredWidth + lp.marginEnd
            }
        }

        //check
        restoreScroll()

        if (dslSelector.dslSelectIndex < 0) {
            //还没有选中
            setCurrentItem(tabDefaultIndex)
        } else {
            if (_overScroller.isFinished) {
                _scrollToTarget(dslSelector.dslSelectIndex, layoutScrollAnim)
            }
        }
    }

    fun layoutVertical(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        var childLeft = paddingStart

        val dividerExclude =
            if (drawDivider) tabDivider?.run { dividerHeight + dividerMarginTop + dividerMarginBottom }
                ?: 0 else 0

        val visibleChildList = dslSelector.visibleViewList
        visibleChildList.forEachIndexed { index, childView ->

            val lp = childView.layoutParams as LayoutParams
            val layoutDirection = 0
            val absoluteGravity = GravityCompat.getAbsoluteGravity(lp.gravity, layoutDirection)
            val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

            top += lp.topMargin

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                    top += dividerExclude
                }
            }

            childLeft = when (horizontalGravity) {
                Gravity.CENTER_HORIZONTAL -> paddingStart + ((measuredWidth - paddingStart - paddingEnd - _maxConvexHeight) / 2 -
                        childView.measuredWidth / 2)

                Gravity.RIGHT -> measuredWidth - paddingRight - childView.measuredWidth - lp.rightMargin
                else -> paddingLeft + lp.leftMargin
            }

            /*默认水平居中显示*/
            childView.layout(
                childLeft,
                top,
                childLeft + childView.measuredWidth,
                top + childView.measuredHeight
            )

            top += childView.measuredHeight + lp.bottomMargin
        }
    }

    /**是否是横向布局*/
    fun isHorizontal() = orientation.isHorizontal()

    //</editor-fold desc="布局相关">

    //<editor-fold desc="LayoutParams 相关">

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return p?.run { LayoutParams(p) } ?: generateDefaultLayoutParams()
    }

    class LayoutParams : FrameLayout.LayoutParams {

        /**
         * 支持格式0.3pw 0.5ph, 表示[parent]的多少倍数
         * */
        var layoutWidth: String? = null
        var layoutHeight: String? = null

        /**凸出的高度*/
        var layoutConvexHeight: Int = 0

        /**
         * 宽高[WRAP_CONTENT]时, 内容view的定位索引
         * [TabIndicator.indicatorContentIndex]
         * */
        var indicatorContentIndex = -1
        var indicatorContentId = View.NO_ID

        /**[com.angcyo.tablayout.DslTabLayoutConfig.getOnGetTextStyleView]*/
        var contentTextViewIndex = -1

        /**[com.angcyo.tablayout.DslTabLayoutConfig.getTabTextViewId]*/
        var contentTextViewId = View.NO_ID

        /**[com.angcyo.tablayout.DslTabLayoutConfig.getOnGetIcoStyleView]*/
        var contentIconViewIndex = -1

        /**[com.angcyo.tablayout.DslTabLayoutConfig.getTabIconViewId]*/
        var contentIconViewId = View.NO_ID

        /**
         * 剩余空间占比, 1f表示占满剩余空间, 0.5f表示使用剩余空间的0.5倍
         * [android.widget.LinearLayout.LayoutParams.weight]*/
        var weight: Float = -1f

        /**突出需要绘制的Drawable
         * [com.angcyo.tablayout.DslTabHighlight.highlightDrawable]*/
        var highlightDrawable: Drawable? = null

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.DslTabLayout_Layout)
            layoutWidth = a.getString(R.styleable.DslTabLayout_Layout_layout_tab_width)
            layoutHeight = a.getString(R.styleable.DslTabLayout_Layout_layout_tab_height)
            layoutConvexHeight =
                a.getDimensionPixelOffset(
                    R.styleable.DslTabLayout_Layout_layout_tab_convex_height,
                    layoutConvexHeight
                )
            indicatorContentIndex = a.getInt(
                R.styleable.DslTabLayout_Layout_layout_tab_indicator_content_index,
                indicatorContentIndex
            )
            indicatorContentId = a.getResourceId(
                R.styleable.DslTabLayout_Layout_layout_tab_indicator_content_id,
                indicatorContentId
            )
            weight = a.getFloat(R.styleable.DslTabLayout_Layout_layout_tab_weight, weight)
            highlightDrawable =
                a.getDrawable(R.styleable.DslTabLayout_Layout_layout_highlight_drawable)

            contentTextViewIndex = a.getInt(
                R.styleable.DslTabLayout_Layout_layout_tab_text_view_index,
                contentTextViewIndex
            )
            contentIconViewIndex = a.getInt(
                R.styleable.DslTabLayout_Layout_layout_tab_text_view_index,
                contentIconViewIndex
            )
            contentTextViewId = a.getResourceId(
                R.styleable.DslTabLayout_Layout_layout_tab_text_view_id,
                contentTextViewId
            )
            contentIconViewId = a.getResourceId(
                R.styleable.DslTabLayout_Layout_layout_tab_icon_view_id,
                contentIconViewIndex
            )

            a.recycle()

            if (gravity == UNSPECIFIED_GRAVITY) {
                gravity = if (layoutConvexHeight > 0) {
                    Gravity.BOTTOM
                } else {
                    Gravity.CENTER
                }
            }
        }

        constructor(source: ViewGroup.LayoutParams) : super(source) {
            if (source is LayoutParams) {
                this.layoutWidth = source.layoutWidth
                this.layoutHeight = source.layoutHeight
                this.layoutConvexHeight = source.layoutConvexHeight
                this.weight = source.weight
                this.highlightDrawable = source.highlightDrawable
            }
        }

        constructor(width: Int, height: Int) : super(width, height)

        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
    }

    //</editor-fold desc="LayoutParams 相关">

    //<editor-fold desc="滚动相关">

    //滚动支持
    val _overScroller: OverScroller by lazy {
        OverScroller(context)
    }

    //手势检测
    val _gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (isHorizontal()) {
                    val absX = abs(velocityX)
                    if (absX > _minFlingVelocity) {
                        onFlingChange(velocityX)
                    }
                } else {
                    val absY = abs(velocityY)
                    if (absY > _minFlingVelocity) {
                        onFlingChange(velocityY)
                    }
                }

                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                var handle = false
                if (isHorizontal()) {
                    val absX = abs(distanceX)
                    if (absX > _touchSlop) {
                        handle = onScrollChange(distanceX)
                    }
                } else {
                    val absY = abs(distanceY)
                    if (absY > _touchSlop) {
                        handle = onScrollChange(distanceY)
                    }
                }
                return handle
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = false
        if (needScroll) {
            if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
                _overScroller.abortAnimation()
                _scrollAnimator.cancel()
            }
            if (isEnabled) {
                intercept = super.onInterceptTouchEvent(ev) || _gestureDetector.onTouchEvent(ev)
            }
        } else {
            if (isEnabled) {
                intercept = super.onInterceptTouchEvent(ev)
            }
        }
        return if (isEnabled) {
            if (itemEnableSelector) {
                intercept
            } else {
                true
            }
        } else {
            false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            if (needScroll) {
                _gestureDetector.onTouchEvent(event)
                if (event.actionMasked == MotionEvent.ACTION_CANCEL ||
                    event.actionMasked == MotionEvent.ACTION_UP
                ) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    _overScroller.abortAnimation()
                }
                return true
            } else {
                return (isEnabled && super.onTouchEvent(event))
            }
        } else {
            return false
        }
    }

    /**是否需要滚动*/
    val needScroll: Boolean
        get() = if (tabEnableSelectorMode) true else {
            if (isHorizontal()) {
                if (isLayoutRtl) {
                    minScrollX < 0
                } else {
                    maxScrollX > 0
                }
            } else {
                maxScrollY > 0
            }
        }

    /**[parent]宽度外的滚动距离*/
    val maxScrollX: Int
        get() = if (isLayoutRtl && isHorizontal()) {
            if (tabEnableSelectorMode) viewDrawWidth / 2 else 0
        } else {
            max(
                maxWidth - measuredWidth + if (tabEnableSelectorMode) viewDrawWidth / 2 else 0,
                0
            )
        }

    val maxScrollY: Int
        get() = max(
            maxHeight - measuredHeight + if (tabEnableSelectorMode) viewDrawHeight / 2 else 0,
            0
        )

    /**最小滚动的值*/
    val minScrollX: Int
        get() = if (isLayoutRtl && isHorizontal()) {
            min(
                -(maxWidth - measuredWidth + if (tabEnableSelectorMode) viewDrawWidth / 2 else 0),
                0
            )
        } else {
            if (tabEnableSelectorMode) -viewDrawWidth / 2 else 0
        }

    val minScrollY: Int
        get() = if (tabEnableSelectorMode) -viewDrawHeight / 2 else 0

    /**view最大的宽度*/
    val maxWidth: Int
        get() = _childAllWidthSum + paddingStart + paddingEnd

    val maxHeight: Int
        get() = _childAllWidthSum + paddingTop + paddingBottom

    open fun onFlingChange(velocity: Float /*瞬时值*/) {
        if (needScroll) {

            //速率小于0 , 手指向左滑动
            //速率大于0 , 手指向右滑动

            if (tabEnableSelectorMode) {
                if (isHorizontal() && isLayoutRtl) {
                    if (velocity < 0) {
                        setCurrentItem(dslSelector.dslSelectIndex - 1)
                    } else if (velocity > 0) {
                        setCurrentItem(dslSelector.dslSelectIndex + 1)
                    }
                } else {
                    if (velocity < 0) {
                        setCurrentItem(dslSelector.dslSelectIndex + 1)
                    } else if (velocity > 0) {
                        setCurrentItem(dslSelector.dslSelectIndex - 1)
                    }
                }
            } else {
                if (isHorizontal()) {
                    if (isLayoutRtl) {
                        startFling(-velocity.toInt(), minScrollX, 0)
                    } else {
                        startFling(-velocity.toInt(), 0, maxScrollX)
                    }
                } else {
                    startFling(-velocity.toInt(), 0, maxHeight)
                }
            }
        }
    }

    fun startFling(velocity: Int, min: Int, max: Int) {

        fun velocity(velocity: Int): Int {
            return if (velocity > 0) {
                clamp(velocity, _minFlingVelocity, _maxFlingVelocity)
            } else {
                clamp(velocity, -_maxFlingVelocity, -_minFlingVelocity)
            }
        }

        val v = velocity(velocity)

        _overScroller.abortAnimation()

        if (isHorizontal()) {
            _overScroller.fling(
                scrollX,
                scrollY,
                v,
                0,
                min,
                max,
                0,
                0,
                measuredWidth,
                0
            )
        } else {
            _overScroller.fling(
                scrollX,
                scrollY,
                0,
                v,
                0,
                0,
                min,
                max,
                0,
                measuredHeight
            )
        }
        postInvalidate()
    }

    fun startScroll(dv: Int) {
        _overScroller.abortAnimation()
        if (isHorizontal()) {
            _overScroller.startScroll(scrollX, scrollY, dv, 0, scrollAnimDuration)
        } else {
            _overScroller.startScroll(scrollX, scrollY, 0, dv, scrollAnimDuration)
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**检查是否需要重置滚动的位置*/
    fun restoreScroll() {
        if (itemIsEquWidth || !needScroll) {
            if (scrollX != 0 || scrollY != 0) {
                scrollTo(0, 0)
            }
        }
    }

    open fun onScrollChange(distance: Float): Boolean {
        if (needScroll) {

            //distance小于0 , 手指向右滑动
            //distance大于0 , 手指向左滑动

            parent.requestDisallowInterceptTouchEvent(true)

            if (tabEnableSelectorMode) {
                //滑动选择模式下, 不响应scroll事件
            } else {
                if (isHorizontal()) {
                    scrollBy(distance.toInt(), 0)
                } else {
                    scrollBy(0, distance.toInt())
                }
            }
            return true
        }
        return false
    }

    override fun scrollTo(x: Int, y: Int) {
        if (isHorizontal()) {
            when {
                x > maxScrollX -> super.scrollTo(maxScrollX, 0)
                x < minScrollX -> super.scrollTo(minScrollX, 0)
                else -> super.scrollTo(x, 0)
            }
        } else {
            when {
                y > maxScrollY -> super.scrollTo(0, maxScrollY)
                y < minScrollY -> super.scrollTo(0, minScrollY)
                else -> super.scrollTo(0, y)
            }
        }
    }

    override fun computeScroll() {
        if (_overScroller.computeScrollOffset()) {
            scrollTo(_overScroller.currX, _overScroller.currY)
            invalidate()
            if (_overScroller.currX < minScrollX || _overScroller.currX > maxScrollX) {
                _overScroller.abortAnimation()
            }
        }
    }

    fun _getViewTargetX(): Int {
        return when (tabIndicator.indicatorGravity) {
            DslTabIndicator.INDICATOR_GRAVITY_START -> paddingStart
            DslTabIndicator.INDICATOR_GRAVITY_END -> measuredWidth - paddingEnd
            else -> paddingStart + viewDrawWidth / 2
        }
    }

    fun _getViewTargetY(): Int {
        return when (tabIndicator.indicatorGravity) {
            DslTabIndicator.INDICATOR_GRAVITY_START -> paddingTop
            DslTabIndicator.INDICATOR_GRAVITY_END -> measuredHeight - paddingBottom
            else -> paddingTop + viewDrawHeight / 2
        }
    }

    /**将[index]位置显示在TabLayout的中心*/
    fun _scrollToTarget(index: Int, scrollAnim: Boolean) {
        if (!needScroll) {
            return
        }

        dslSelector.visibleViewList.getOrNull(index)?.let {
            if (!ViewCompat.isLaidOut(it)) {
                //没有布局
                return
            }
        }

        val dv = if (isHorizontal()) {
            val childTargetX = tabIndicator.getChildTargetX(index)
            val viewDrawTargetX = _getViewTargetX()
            when {
                tabEnableSelectorMode -> {
                    val viewCenterX = measuredWidth / 2
                    childTargetX - viewCenterX - scrollX
                }

                isLayoutRtl -> {
                    if (childTargetX < viewDrawTargetX) {
                        childTargetX - viewDrawTargetX - scrollX
                    } else {
                        -scrollX
                    }
                }

                else -> {
                    if (childTargetX > viewDrawTargetX) {
                        childTargetX - viewDrawTargetX - scrollX
                    } else {
                        -scrollX
                    }
                }
            }
        } else {
            //竖向
            val childTargetY = tabIndicator.getChildTargetY(index)
            val viewDrawTargetY = _getViewTargetY()
            when {
                tabEnableSelectorMode -> {
                    val viewCenterY = measuredHeight / 2
                    childTargetY - viewCenterY - scrollY
                }

                childTargetY > viewDrawTargetY -> {
                    childTargetY - viewDrawTargetY - scrollY
                }

                else -> {
                    if (tabIndicator.indicatorGravity == DslTabIndicator.INDICATOR_GRAVITY_END &&
                        childTargetY < viewDrawTargetY
                    ) {
                        childTargetY - viewDrawTargetY - scrollY
                    } else {
                        -scrollY
                    }
                }
            }
        }

        if (isHorizontal()) {
            if (isInEditMode || !scrollAnim) {
                _overScroller.abortAnimation()
                scrollBy(dv, 0)
            } else {
                startScroll(dv)
            }
        } else {
            if (isInEditMode || !scrollAnim) {
                _overScroller.abortAnimation()
                scrollBy(0, dv)
            } else {
                startScroll(dv)
            }
        }
    }

    //</editor-fold desc="滚动相关">

    //<editor-fold desc="动画相关">

    val _scrollAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            interpolator = LinearInterpolator()
            duration = tabIndicatorAnimationDuration
            addUpdateListener {
                _onAnimateValue(it.animatedValue as Float)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    _onAnimateValue(1f)
                    onAnimationEnd(animation)
                }

                override fun onAnimationEnd(animation: Animator) {
                    _onAnimateEnd()
                }
            })
        }
    }

    val isAnimatorStart: Boolean
        get() = _scrollAnimator.isStarted

    fun _animateToItem(fromIndex: Int, toIndex: Int) {
        if (toIndex == fromIndex) {
            return
        }

        //取消之前的动画
        _scrollAnimator.cancel()

        if (!tabIndicator.indicatorAnim) {
            //不需要动画
            _onAnimateEnd()
            return
        }

        if (fromIndex < 0) {
            //从一个不存在的位置, 到目标位置
            tabIndicator.currentIndex = toIndex
        } else {
            tabIndicator.currentIndex = fromIndex
        }
        tabIndicator._targetIndex = toIndex

        if (isInEditMode) {
            tabIndicator.currentIndex = toIndex
            return
        }

        if (tabIndicator.currentIndex == tabIndicator._targetIndex) {
            return
        }
        //"_animateToItem ${tabIndicator.currentIndex} ${tabIndicator._targetIndex}".loge()
        _scrollAnimator.setFloatValues(tabIndicator.positionOffset, 1f)
        _scrollAnimator.start()
    }

    fun _onAnimateValue(value: Float) {
        tabIndicator.positionOffset = value
        tabLayoutConfig?.onPageIndexScrolled(
            tabIndicator.currentIndex,
            tabIndicator._targetIndex,
            value
        )
        tabLayoutConfig?.let {
            dslSelector.visibleViewList.apply {
                val targetView = getOrNull(tabIndicator._targetIndex)
                if (targetView != null) {
                    it.onPageViewScrolled(
                        getOrNull(tabIndicator.currentIndex),
                        targetView,
                        value
                    )
                }
            }
        }
    }

    fun _onAnimateEnd() {
        tabIndicator.currentIndex = dslSelector.dslSelectIndex
        tabIndicator._targetIndex = tabIndicator.currentIndex
        tabIndicator.positionOffset = 0f
        //结束_viewPager的滚动动画, 系统没有直接结束的api, 固用此方法代替.
        //_viewPager?.setCurrentItem(tabIndicator.currentIndex, false)
    }

    //</editor-fold desc="动画相关">

    //<editor-fold desc="ViewPager 相关">

    var _viewPagerDelegate: ViewPagerDelegate? = null
    var _viewPagerScrollState = 0

    fun onPageScrollStateChanged(state: Int) {
        //"$state".logi()
        _viewPagerScrollState = state
        if (state == ViewPagerDelegate.SCROLL_STATE_IDLE) {
            _onAnimateEnd()
            dslSelector.updateStyle()
        }
    }

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (isAnimatorStart) {
            //动画已经开始了
            return
        }

        val currentItem = _viewPagerDelegate?.onGetCurrentItem() ?: 0
        //"$currentItem:$position $positionOffset $positionOffsetPixels state:$_viewPagerScrollState".logw()

        if (position < currentItem) {
            //Page 目标在左
            if (_viewPagerScrollState == ViewPagerDelegate.SCROLL_STATE_DRAGGING) {
                tabIndicator.currentIndex = position + 1
                tabIndicator._targetIndex = position
            }
            _onAnimateValue(1 - positionOffset)
        } else {
            //Page 目标在右
            if (_viewPagerScrollState == ViewPagerDelegate.SCROLL_STATE_DRAGGING) {
                tabIndicator.currentIndex = position
                tabIndicator._targetIndex = position + 1
            }
            _onAnimateValue(positionOffset)
        }
    }

    fun onPageSelected(position: Int) {
        setCurrentItem(position, true, false)
    }

    //</editor-fold desc="ViewPager 相关">

    //<editor-fold desc="状态恢复">

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val oldState: Parcelable? = state.getParcelable("old")
            super.onRestoreInstanceState(oldState)

            tabDefaultIndex = state.getInt("defaultIndex", tabDefaultIndex)
            val currentItemIndex = state.getInt("currentIndex", -1)
            dslSelector.dslSelectIndex = -1
            if (currentItemIndex > 0) {
                setCurrentItem(currentItemIndex, true, false)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable("old", state)
        bundle.putInt("defaultIndex", tabDefaultIndex)
        bundle.putInt("currentIndex", currentItemIndex)
        return bundle
    }

    //</editor-fold desc="状态恢复">
}

