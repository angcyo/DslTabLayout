package com.angcyo.tablayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.OverScroller
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
    var onTabBadgeConfig: (child: View, tabBadge: DslTabBadge, index: Int) -> Unit =
        { _, tabBadge, index ->
            if (!isInEditMode) {
                tabBadge.updateBadgeConfig(getBadgeConfig(index))
            }
        }

    /**如果使用了高凸模式. 请使用这个属性设置背景色*/
    var tabConvexBackgroundDrawable: Drawable? = null

    /**是否激活滑动选择模式*/
    var tabEnableSelectorMode = false

    //<editor-fold desc="内部属性">

    //fling 速率阈值
    var _minFlingVelocity = 0
    var _maxFlingVelocity = 0

    //scroll 阈值
    var _touchSlop = 0

    //childView选择器
    val dslSelector: DslSelector by lazy {
        DslSelector().install(this) {
            onStyleItemView = { itemView, index, select ->
                tabLayoutConfig?.onStyleItemView?.invoke(itemView, index, select)
            }
            onSelectItemView = { itemView, index, select ->
                tabLayoutConfig?.onSelectItemView?.invoke(itemView, index, select) ?: false
            }
            onSelectViewChange = { fromView, selectViewList, reselect ->
                tabLayoutConfig?.onSelectViewChange?.invoke(fromView, selectViewList, reselect)
            }
            onSelectIndexChange = { fromIndex, selectList, reselect ->
                if (tabLayoutConfig == null) {
                    "选择:[$fromIndex]->${selectList} reselect:$reselect".logi()
                }

                val toIndex = selectList.last()
                _animateToItem(fromIndex, toIndex)

                _scrollToCenter(toIndex)
                postInvalidate()

                tabLayoutConfig?.onSelectIndexChange?.invoke(fromIndex, selectList, reselect)
                    ?: _viewPagerDelegate?.onSetCurrentItem(fromIndex, toIndex)
            }
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        itemIsEquWidth =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_item_is_equ_width, itemIsEquWidth)
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

        tabEnableSelectorMode =
            typedArray.getBoolean(
                R.styleable.DslTabLayout_tab_enable_selector_mode,
                tabEnableSelectorMode
            )

        tabConvexBackgroundDrawable =
            typedArray.getDrawable(R.styleable.DslTabLayout_tab_convex_background)

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

    /**设置tab的位置*/
    fun setCurrentItem(index: Int, notify: Boolean = true) {
        if (currentItemIndex == index) {
            return
        }
        dslSelector.selector(index, true, notify)
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

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        dslSelector.updateVisibleList()
        dslSelector.updateStyle()
        dslSelector.updateClickListener()
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        dslSelector.updateVisibleList()
    }

    override fun draw(canvas: Canvas) {
        //Log.e("angcyo", "...draw...")

        if (drawIndicator) {
            tabIndicator.setBounds(0, 0, measuredWidth, measuredHeight)
        }

        //自定义的背景
        tabConvexBackgroundDrawable?.apply {
            setBounds(0, _maxConvexHeight, right - left, bottom - top)

            if (scrollX or scrollY == 0) {
                draw(canvas)
            } else {
                canvas.translate(scrollX.toFloat(), scrollY.toFloat())
                draw(canvas)
                canvas.translate(-scrollX.toFloat(), -scrollY.toFloat())
            }
        }

        super.draw(canvas)

        //绘制在child的上面
        if (drawDivider) {
            var left = 0
            val visibleChildCount = dslSelector.visibleViewList.size
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
        if (drawBorder) {
            tabBorder?.draw(canvas)
        }
        if (drawIndicator && tabIndicator.indicatorStyle > 0x10) {
            tabIndicator.draw(canvas)
        }
        if (drawBadge) {
            tabBadge?.apply {
                dslSelector.visibleViewList.forEachIndexed { index, child ->
                    setBounds(child.left, child.top, child.right, child.bottom)
                    onTabBadgeConfig(child, this, index)
                    updateOriginDrawable()
                    draw(canvas)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawBorder) {
            tabBorder?.drawBorderBackground(canvas)
        }

        //绘制在child的后面
        if (drawIndicator && tabIndicator.indicatorStyle <= 0x10) {
            tabIndicator.draw(canvas)
        }
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
        dslSelector.updateVisibleList()

        val visibleChildList = dslSelector.visibleViewList
        val visibleChildCount = visibleChildList.size

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        _maxConvexHeight = 0

        //child高度测量模式
        var childHeightSpec: Int = -1
        var childWidthSpec: Int = -1

        if (heightMode == MeasureSpec.EXACTLY) {
            //固定高度
            childHeightSpec = exactlyMeasure(heightSize - paddingTop - paddingBottom)
        }

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
        val dividerExclude =
            if (drawDivider) tabDivider?.run { dividerWidth + dividerMarginLeft + dividerMarginRight }
                ?: 0 else 0

        //等宽时, child宽度的测量模式
        val childEquWidthSpec = if (itemIsEquWidth) {
            exactlyMeasure(
                if (itemWidth > 0) {
                    itemWidth
                } else {
                    var excludeWidth = paddingLeft + paddingRight
                    visibleChildList.forEachIndexed { index, child ->
                        if (drawDivider) {
                            if (tabDivider?.haveBeforeDivider(
                                    index,
                                    visibleChildList.size
                                ) == true
                            ) {
                                excludeWidth += dividerExclude
                            }
                            if (tabDivider?.haveAfterDivider(
                                    index,
                                    visibleChildList.size
                                ) == true
                            ) {
                                excludeWidth += dividerExclude
                            }
                        }
                        val lp = child.layoutParams as LayoutParams
                        excludeWidth += lp.leftMargin + lp.rightMargin
                    }

                    (widthSize - excludeWidth) / visibleChildCount
                }
            )
        } else {
            -1
        }

        //...end

        _childAllWidthSum = 0

        var wrapContentHeight = false

        visibleChildList.forEachIndexed { index, childView ->

            val lp = childView.layoutParams as LayoutParams
            //不支持竖向margin支持
            lp.topMargin = 0
            lp.bottomMargin = 0

            val childConvexHeight = lp.layoutConvexHeight

            val widthHeight = calcLayoutWidthHeight(
                lp.layoutWidth, lp.layoutHeight,
                widthSize, heightSize, 0, 0
            )

            //计算高度测量模式
            wrapContentHeight = false
            if (childHeightSpec == -1) {
                if (widthHeight[1] > 0) {
                    heightSize = widthHeight[1]
                    childHeightSpec = exactlyMeasure(heightSize)
                    heightSize += paddingTop + paddingBottom
                }
            }

            if (childHeightSpec == -1) {
                if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {

                    heightSize = if (suggestedMinimumHeight > 0) {
                        suggestedMinimumHeight
                    } else {
                        itemDefaultHeight
                    }

                    childHeightSpec = exactlyMeasure(heightSize)

                    heightSize += paddingTop + paddingBottom
                } else {
                    childHeightSpec = atmostMeasure(heightSize)
                    wrapContentHeight = true
                }
            }
            //...end

            //计算宽度测量模式
            childWidthSpec = when {
                itemIsEquWidth -> childEquWidthSpec
                widthHeight[0] > 0 -> exactlyMeasure(widthHeight[0])
                else -> atmostMeasure(widthSize - paddingLeft - paddingRight)
            }

            if (childConvexHeight > 0) {
                _maxConvexHeight = max(_maxConvexHeight, childConvexHeight)
                //需要凸起
                val childConvexHeightSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(childHeightSpec) + childConvexHeight,
                    MeasureSpec.getMode(childHeightSpec)
                )
                childView.measure(childWidthSpec, childConvexHeightSpec)
            } else {
                childView.measure(childWidthSpec, childHeightSpec)
            }

            if (wrapContentHeight) {
                heightSize = childView.measuredHeight
                childHeightSpec = exactlyMeasure(heightSize)
                heightSize += paddingTop + paddingBottom
            }

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(
                        index,
                        visibleChildList.size
                    ) == true
                ) {
                    _childAllWidthSum += dividerExclude
                }
                if (tabDivider?.haveAfterDivider(
                        index,
                        visibleChildList.size
                    ) == true
                ) {
                    _childAllWidthSum += dividerExclude
                }
            }

            _childAllWidthSum += childView.measuredWidth + lp.leftMargin + lp.rightMargin
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = min(_childAllWidthSum + paddingLeft + paddingRight, widthSize)
        }

        setMeasuredDimension(widthSize, heightSize + _maxConvexHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = paddingLeft
        var childBottom = measuredHeight - paddingBottom

        val dividerExclude =
            if (drawDivider) tabDivider?.run { dividerWidth + dividerMarginLeft + dividerMarginRight }
                ?: 0 else 0

        val visibleChildList = dslSelector.visibleViewList
        visibleChildList.forEachIndexed { index, childView ->

            val lp = childView.layoutParams as LayoutParams

            left += lp.leftMargin

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(
                        index,
                        visibleChildList.size
                    ) == true
                ) {
                    left += dividerExclude
                }
            }

            childBottom = if (lp.gravity.have(Gravity.CENTER_VERTICAL)) {
                measuredHeight - paddingBottom -
                        (measuredHeight - paddingLeft - paddingBottom - _maxConvexHeight) / 2 -
                        childView.measuredHeight / 2
            } else {
                measuredHeight - paddingBottom
            }

            /*默认垂直居中显示*/
            childView.layout(
                left, childBottom - childView.measuredHeight,
                left + childView.measuredWidth,
                childBottom
            )

            left += childView.measuredWidth + lp.rightMargin
        }

        if (dslSelector.dslSelectIndex < 0) {
            //还没有选中
            setCurrentItem(tabDefaultIndex)
        }
    }

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
            a.recycle()
        }

        constructor(source: ViewGroup.LayoutParams) : super(source) {
            if (source is LayoutParams) {
                this.layoutWidth = source.layoutWidth
                this.layoutHeight = source.layoutHeight
                this.layoutConvexHeight = source.layoutConvexHeight
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
    val _gestureDetector: GestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val absX = abs(velocityX)

                if (absX > _minFlingVelocity) {
                    onFlingChange(velocityX)
                }

                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                val absX = abs(distanceX)

                var handle = false
                if (absX > _touchSlop) {
                    handle = onScrollChange(distanceX)
                }
                return handle
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (needScroll) {
            if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
                _overScroller.abortAnimation()
                _scrollAnimator.cancel()
            }
            return super.onInterceptTouchEvent(ev) || _gestureDetector.onTouchEvent(ev)
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
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
            return super.onTouchEvent(event)
        }
    }

    /**是否需要滚动*/
    val needScroll: Boolean
        get() = if (tabEnableSelectorMode) true else maxScrollX > 0

    /**[parent]宽度外的滚动距离*/
    val maxScrollX: Int
        get() = max(
            maxWidth - measuredWidth + if (tabEnableSelectorMode) viewDrawWidth / 2 else 0,
            0
        )

    /**最小滚动的值*/
    val minScrollX: Int
        get() = if (tabEnableSelectorMode) -viewDrawWidth / 2 else 0

    /**view最大的宽度*/
    val maxWidth: Int
        get() = _childAllWidthSum + paddingLeft + paddingRight

    open fun onFlingChange(velocity: Float /*瞬时值*/) {
        if (needScroll) {

            //速率小于0 , 手指向左滑动
            //速率大于0 , 手指向右滑动

            if (tabEnableSelectorMode) {
                if (velocity < 0) {
                    setCurrentItem(dslSelector.dslSelectIndex + 1)
                } else if (velocity > 0) {
                    setCurrentItem(dslSelector.dslSelectIndex - 1)
                }
            } else {
                startFling(-velocity.toInt(), maxWidth)
            }
        }
    }

    fun startFling(velocityX: Int, maxX: Int) {

        fun velocity(velocity: Int): Int {
            return if (velocity > 0) {
                clamp(velocity, _minFlingVelocity, _maxFlingVelocity)
            } else {
                clamp(velocity, -_maxFlingVelocity, -_minFlingVelocity)
            }
        }

        val vX = velocity(velocityX)

        _overScroller.abortAnimation()
        _overScroller.fling(
            scrollX,
            scrollY,
            vX,
            0,
            0,
            maxX,
            0,
            0,
            measuredWidth,
            0
        )

        postInvalidate()
    }

    fun startScroll(dx: Int) {
        _overScroller.abortAnimation()
        _overScroller.startScroll(scrollX, scrollY, dx, 0)
    }

    open fun onScrollChange(distance: Float): Boolean {
        if (needScroll) {

            //distance小于0 , 手指向右滑动
            //distance大于0 , 手指向左滑动

            parent.requestDisallowInterceptTouchEvent(true)

            if (tabEnableSelectorMode) {
                //滑动选择模式下, 不响应scroll事件
            } else {
                scrollBy(distance.toInt(), 0)
            }
            return true
        }
        return false
    }

    override fun scrollTo(x: Int, y: Int) {
        when {
            x > maxScrollX -> super.scrollTo(maxScrollX, y)
            x < minScrollX -> super.scrollTo(minScrollX, y)
            else -> super.scrollTo(x, y)
        }
    }

    override fun computeScroll() {
        if (_overScroller.computeScrollOffset()) {
            scrollTo(_overScroller.currX, _overScroller.currY)
            postInvalidate()
            if (_overScroller.currX < minScrollX || _overScroller.currX > maxScrollX) {
                _overScroller.abortAnimation()
            }
        }
    }

    /**将[index]位置显示在TabLayout的中心*/
    fun _scrollToCenter(index: Int) {
        if (!needScroll) {
            return
        }

        val childCenterX = tabIndicator.getChildCenterX(index)
        val viewDrawCenterX = paddingLeft + viewDrawWidth / 2

        val dx = when {
            tabEnableSelectorMode -> {
                val viewCenterX = measuredWidth / 2
                childCenterX - viewCenterX - scrollX
            }
            childCenterX > viewDrawCenterX -> {
                childCenterX - viewDrawCenterX - scrollX
            }
            else -> {
                -scrollX
            }
        }

        if (isInEditMode) {
            scrollBy(dx, 0)
        } else {
            startScroll(dx)
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
                override fun onAnimationCancel(animation: Animator?) {
                    onAnimationEnd(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
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

        tabIndicator.currentIndex = max(0, fromIndex)
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
                tabIndicator._targetIndex = min(currentItem, position)
            }
            _onAnimateValue(1 - positionOffset)
        } else {
            //Page 目标在右
            if (_viewPagerScrollState == ViewPagerDelegate.SCROLL_STATE_DRAGGING) {
                tabIndicator._targetIndex = max(currentItem, position + 1)
            }
            _onAnimateValue(positionOffset)
        }
    }

    fun onPageSelected(position: Int) {
        setCurrentItem(position)
    }

    //</editor-fold desc="ViewPager 相关">
}

