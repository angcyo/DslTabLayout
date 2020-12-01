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
import androidx.core.view.GestureDetectorCompat
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

    /**智能判断Item是否等宽, 如果所有子项, 未撑满tab时, 开启等宽模式.此属性会覆盖[itemIsEquWidth]*/
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

    /**如果使用了高凸模式. 请使用这个属性设置背景色*/
    var tabConvexBackgroundDrawable: Drawable? = null

    /**是否激活滑动选择模式*/
    var tabEnableSelectorMode = false

    /**布局的方向*/
    var orientation: Int = LinearLayout.HORIZONTAL

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

                val toIndex = selectList.last()
                _animateToItem(fromIndex, toIndex)

                _scrollToCenter(toIndex, tabIndicator.indicatorAnim)
                postInvalidate()

                tabLayoutConfig?.onSelectIndexChange?.invoke(
                    fromIndex,
                    selectList,
                    reselect,
                    fromUser
                ) ?: _viewPagerDelegate?.onSetCurrentItem(fromIndex, toIndex)
            }
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        itemIsEquWidth =
            typedArray.getBoolean(R.styleable.DslTabLayout_tab_item_is_equ_width, itemIsEquWidth)
        itemAutoEquWidth =
            typedArray.getBoolean(
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

        tabEnableSelectorMode =
            typedArray.getBoolean(
                R.styleable.DslTabLayout_tab_enable_selector_mode,
                tabEnableSelectorMode
            )

        tabConvexBackgroundDrawable =
            typedArray.getDrawable(R.styleable.DslTabLayout_tab_convex_background)

        orientation = typedArray.getInt(R.styleable.DslTabLayout_tab_orientation, orientation)

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
    fun setCurrentItem(index: Int, notify: Boolean = true, fromUser: Boolean = false) {
        if (currentItemIndex == index) {
            _scrollToCenter(index, tabIndicator.indicatorAnim)
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
            if (isHorizontal()) {
                setBounds(0, _maxConvexHeight, right - left, bottom - top)
            } else {
                setBounds(0, 0, measuredWidth - _maxConvexHeight, bottom - top)
            }

            if (scrollX or scrollY == 0) {
                draw(canvas)
            } else {
                canvas.translate(scrollX.toFloat(), scrollY.toFloat())
                draw(canvas)
                canvas.translate(-scrollX.toFloat(), -scrollY.toFloat())
            }
        }

        super.draw(canvas)

        val visibleChildCount = dslSelector.visibleViewList.size

        //绘制在child的上面
        if (drawDivider) {
            if (isHorizontal()) {
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
            } else {
                var top = 0
                tabDivider?.apply {
                    val left = paddingLeft + dividerMarginLeft
                    val right = measuredWidth - paddingRight - dividerMarginRight
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
            tabBorder?.draw(canvas)
        }
        if (drawIndicator && tabIndicator.indicatorStyle > 0x10) {
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
                        left += anchorView.paddingLeft
                        top += anchorView.paddingTop
                        right -= anchorView.paddingRight
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

        if (visibleChildCount == 0) {
            setMeasuredDimension(
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
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
        val dividerWidthExclude =
            if (drawDivider) tabDivider?.run { dividerWidth + dividerMarginLeft + dividerMarginRight }
                ?: 0 else 0

        //智能等宽判断
        if (itemAutoEquWidth) {
            var childMaxWidth = 0 //所有child宽度总和
            visibleChildList.forEachIndexed { index, child ->
                val lp: LayoutParams = child.layoutParams as LayoutParams
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                childMaxWidth += lp.leftMargin + lp.rightMargin + child.measuredWidth

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

        //没有设置weight属性的child宽度总和, 用于计算剩余空间
        var allChildUsedWidth = 0

        fun measureChild(childView: View) {
            val lp = childView.layoutParams as LayoutParams

            //横向布局, 不支持竖向margin支持
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
            childWidthSpec //no op

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
                    lp.width == ViewGroup.LayoutParams.MATCH_PARENT -> exactlyMeasure(widthSize - paddingLeft - paddingRight)
                    lp.width > 0 -> exactlyMeasure(lp.width)
                    else -> atmostMeasure(widthSize - paddingLeft - paddingRight)
                }

                measureChild(childView)

                childUsedWidth = childView.measuredWidth + lp.leftMargin + lp.rightMargin
            } else {
                childUsedWidth = lp.leftMargin + lp.rightMargin
            }

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                    childUsedWidth += dividerWidthExclude
                }
                if (tabDivider?.haveAfterDivider(index, visibleChildList.size) == true) {
                    childUsedWidth += dividerWidthExclude
                }
            }

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
                    lp.width == ViewGroup.LayoutParams.MATCH_PARENT -> exactlyMeasure(widthSize - paddingLeft - paddingRight)
                    lp.width > 0 -> exactlyMeasure(lp.width)
                    else -> atmostMeasure(widthSize - paddingLeft - paddingRight)
                }

                measureChild(childView)

                //上面已经处理了分割线和margin的距离了
                _childAllWidthSum += childView.measuredWidth
            }
        }
        //...end

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = min(_childAllWidthSum + paddingLeft + paddingRight, widthSize)
        }

        if (heightMode == MeasureSpec.AT_MOST && visibleChildList.isEmpty()) {
            heightSize = if (suggestedMinimumHeight > 0) {
                suggestedMinimumHeight
            } else {
                itemDefaultHeight
            }
        }

        setMeasuredDimension(widthSize, heightSize + _maxConvexHeight)
    }

    fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        dslSelector.updateVisibleList()

        val visibleChildList = dslSelector.visibleViewList
        val visibleChildCount = visibleChildList.size

        if (visibleChildCount == 0) {
            setMeasuredDimension(
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
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
            childWidthSpec = exactlyMeasure(widthSize - paddingLeft - paddingRight)
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
            lp.leftMargin = 0
            lp.rightMargin = 0

            val childConvexHeight = lp.layoutConvexHeight

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
                    widthSize += paddingLeft + paddingRight
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

                    widthSize += paddingLeft + paddingRight
                } else {
                    childWidthSpec = atmostMeasure(widthSize)
                    wrapContentWidth = true
                }
            }
            //...end

            //计算高度测量模式
            childHeightSpec //no op

            if (childConvexHeight > 0) {
                _maxConvexHeight = max(_maxConvexHeight, childConvexHeight)
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
                widthSize += paddingLeft + paddingRight
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

        if (widthMode == MeasureSpec.AT_MOST && visibleChildList.isEmpty()) {
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

    fun layoutHorizontal(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
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
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                    left += dividerExclude
                }
            }

            childBottom = if (lp.gravity.have(Gravity.CENTER_VERTICAL)) {
                measuredHeight - paddingBottom -
                        ((measuredHeight - paddingTop - paddingBottom - _maxConvexHeight) / 2 -
                                childView.measuredHeight / 2)
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

        //check
        restoreScroll()

        if (dslSelector.dslSelectIndex < 0) {
            //还没有选中
            setCurrentItem(tabDefaultIndex)
        } else {
            _scrollToCenter(dslSelector.dslSelectIndex, false)
        }
    }

    fun layoutVertical(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        var childLeft = paddingLeft

        val dividerExclude =
            if (drawDivider) tabDivider?.run { dividerHeight + dividerMarginTop + dividerMarginBottom }
                ?: 0 else 0

        val visibleChildList = dslSelector.visibleViewList
        visibleChildList.forEachIndexed { index, childView ->

            val lp = childView.layoutParams as LayoutParams

            top += lp.topMargin

            if (drawDivider) {
                if (tabDivider?.haveBeforeDivider(index, visibleChildList.size) == true) {
                    top += dividerExclude
                }
            }

            childLeft = if (lp.gravity.have(Gravity.CENTER_HORIZONTAL)) {
                paddingLeft + ((measuredWidth - paddingLeft - paddingRight - _maxConvexHeight) / 2 -
                        childView.measuredWidth / 2)
            } else {
                paddingLeft
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

        //check
        restoreScroll()

        if (dslSelector.dslSelectIndex < 0) {
            //还没有选中
            setCurrentItem(tabDefaultIndex)
        } else {
            _scrollToCenter(dslSelector.dslSelectIndex, false)
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

        /**[android.widget.LinearLayout.LayoutParams.weight]*/
        var weight: Float = -1f

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
            weight = a.getFloat(R.styleable.DslTabLayout_Layout_layout_tab_weight, weight)
            a.recycle()
        }

        constructor(source: ViewGroup.LayoutParams) : super(source) {
            if (source is LayoutParams) {
                this.layoutWidth = source.layoutWidth
                this.layoutHeight = source.layoutHeight
                this.layoutConvexHeight = source.layoutConvexHeight
                this.weight = source.weight
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
                e1: MotionEvent?,
                e2: MotionEvent?,
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
                e1: MotionEvent?,
                e2: MotionEvent?,
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
        get() = if (tabEnableSelectorMode) true else {
            if (isHorizontal()) {
                maxScrollX > 0
            } else {
                maxScrollY > 0
            }
        }

    /**[parent]宽度外的滚动距离*/
    val maxScrollX: Int
        get() = max(
            maxWidth - measuredWidth + if (tabEnableSelectorMode) viewDrawWidth / 2 else 0,
            0
        )

    val maxScrollY: Int
        get() = max(
            maxHeight - measuredHeight + if (tabEnableSelectorMode) viewDrawHeight / 2 else 0,
            0
        )

    /**最小滚动的值*/
    val minScrollX: Int
        get() = if (tabEnableSelectorMode) -viewDrawWidth / 2 else 0

    val minScrollY: Int
        get() = if (tabEnableSelectorMode) -viewDrawHeight / 2 else 0

    /**view最大的宽度*/
    val maxWidth: Int
        get() = _childAllWidthSum + paddingLeft + paddingRight

    val maxHeight: Int
        get() = _childAllWidthSum + paddingTop + paddingBottom

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
                if (isHorizontal()) {
                    startFling(-velocity.toInt(), maxWidth)
                } else {
                    startFling(-velocity.toInt(), maxHeight)
                }
            }
        }
    }

    fun startFling(velocity: Int, max: Int) {

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
                0,
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
                0,
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
            _overScroller.startScroll(scrollX, scrollY, dv, 0)
        } else {
            _overScroller.startScroll(scrollX, scrollY, 0, dv)
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**检查是否需要重置滚动的位置*/
    fun restoreScroll() {
        if (itemIsEquWidth) {
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

    /**将[index]位置显示在TabLayout的中心*/
    fun _scrollToCenter(index: Int, scrollAnim: Boolean) {
        if (!needScroll) {
            return
        }

        val dx = if (isHorizontal()) {
            val childCenterX = tabIndicator.getChildCenterX(index)
            val viewDrawCenterX = paddingLeft + viewDrawWidth / 2
            when {
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
        } else {
            //竖向
            val childCenterY = tabIndicator.getChildCenterY(index)
            val viewDrawCenterY = paddingTop + viewDrawHeight / 2
            when {
                tabEnableSelectorMode -> {
                    val viewCenterY = measuredHeight / 2
                    childCenterY - viewCenterY - scrollY
                }
                childCenterY > viewDrawCenterY -> {
                    childCenterY - viewDrawCenterY - scrollY
                }
                else -> {
                    -scrollY
                }
            }
        }

        if (isHorizontal()) {
            if (isInEditMode || !scrollAnim) {
                scrollBy(dx, 0)
            } else {
                startScroll(dx)
            }
        } else {
            if (isInEditMode || !scrollAnim) {
                scrollBy(0, dx)
            } else {
                startScroll(dx)
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
                override fun onAnimationCancel(animation: Animator?) {
                    _onAnimateValue(1f)
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

