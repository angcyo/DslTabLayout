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
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
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
    attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {

    /**在未指定[minHeight]的[wrap_content]情况下的高度*/
    var itemDefaultHeight = 40 * dpi

    /**item是否等宽*/
    var itemIsEquWidth = false

    /**在等宽的情况下, 指定item的宽度, 小于0, 平分*/
    var itemWidth = -3

    /**指示器*/
    var tabIndicator: TabIndicator

    /**指示器动画时长*/
    var tabIndicatorAnimationDuration = 240L

    /**默认选中位置*/
    var tabDefaultIndex = 0

    //<editor-fold desc="内部属性">

    //fling 速率阈值
    var _minFlingVelocity = 0
    var _maxFlingVelocity = 0

    //scroll 阈值
    var _touchSlop = 0

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)
        itemIsEquWidth =
            typedArray.getBoolean(R.styleable.DslTabLayout_r_item_is_equ_width, itemIsEquWidth)
        itemWidth =
            typedArray.getDimensionPixelOffset(R.styleable.DslTabLayout_r_item_width, itemWidth)
        itemDefaultHeight = typedArray.getDimensionPixelOffset(
            R.styleable.DslTabLayout_r_item_default_height,
            itemDefaultHeight
        )
        typedArray.recycle()

        val vc = ViewConfiguration.get(context)
        _minFlingVelocity = vc.scaledMinimumFlingVelocity
        _maxFlingVelocity = vc.scaledMaximumFlingVelocity
        //_touchSlop = vc.scaledTouchSlop

        tabIndicator = TabIndicator(this)
        tabIndicator.initAttribute(context, attributeSet)

        tabIndicator.indicatorDrawable =
            context.resources.getDrawable(R.drawable.indicator_bottom_line)

        //开启绘制
        setWillNotDraw(false)
    }

    //childView选择器
    val dslSelector: DslSelector by lazy {
        DslSelector().install(this) {

            onStyleItemView = { itemView, index, _ ->
                if (index >= 4) {
                    (itemView as? TextView)?.text = "!文本控件| $index"
                }
            }

            onSelectIndexChange = { fromIndex, selectList ->
                "选择:[$fromIndex]->${selectList}".logi()

                val toIndex = selectList.last()
                _animateToItem(fromIndex, toIndex)
                _viewPager?.setCurrentItem(toIndex, true)

                _scrollToCenter(toIndex)
                postInvalidate()
            }
        }
    }

    //</editor-fold desc="内部属性">

    //<editor-fold desc="可操作性方法">

    /**当前选中item的索引*/
    val currentItemIndex: Int
        get() = dslSelector.dslSelectIndex

    /**设置tab的位置*/
    fun setCurrentItem(index: Int) {
        dslSelector.selector(index)
    }

    /**自动关联[ViewPager]*/
    fun setupViewPager(viewPager: ViewPager) {
        if (_viewPager != viewPager) {
            _viewPager?.removeOnPageChangeListener(_onViewPageCChangeListener)
            _viewPager = viewPager
            viewPager.addOnPageChangeListener(_onViewPageCChangeListener)
        }
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
        dslSelector.updateStyle()
        dslSelector.updateClickListener()
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
    }

    override fun draw(canvas: Canvas) {
        tabIndicator.setBounds(0, 0, measuredWidth, measuredHeight)
        super.draw(canvas)
        //绘制在child的上面
        if (tabIndicator.indicatorStyle == TabIndicator.INDICATOR_STYLE_BOTTOM) {
            tabIndicator.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制在child的后面
        if (tabIndicator.indicatorStyle == TabIndicator.INDICATOR_STYLE_BACKGROUND) {
            tabIndicator.draw(canvas)
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who == tabIndicator
    }

    //</editor-fold desc="初始化相关">

    //<editor-fold desc="布局相关">

    //所有child的总宽度, 不包含parent的padding
    var _childAllWidthSum = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

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

        //...end

        _childAllWidthSum = 0

        var wrapContentHeight = false

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.visibility == View.GONE) {
                continue
            }

            val lp = childView.layoutParams as LayoutParams
            //不支持竖向margin支持
            lp.topMargin = 0
            lp.bottomMargin = 0

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
                itemIsEquWidth -> {
                    exactlyMeasure(if (itemWidth > 0) itemWidth else (widthSize - paddingLeft - paddingRight) / childCount)
                }
                widthHeight[0] > 0 -> {
                    exactlyMeasure(widthHeight[0])
                }
                else -> {
                    atmostMeasure(widthSize - paddingLeft - paddingRight)
                }
            }

            childView.measure(childWidthSpec, childHeightSpec)

            if (wrapContentHeight) {
                heightSize = childView.measuredHeight
                childHeightSpec = exactlyMeasure(heightSize)
                heightSize += paddingTop + paddingBottom
            }

            _childAllWidthSum += childView.measuredWidth + lp.leftMargin + lp.rightMargin
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = min(_childAllWidthSum + paddingLeft + paddingRight, widthSize)
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    //所有可见的View列表
    val _childVisibleViewList = mutableListOf<View>()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        _childVisibleViewList.clear()

        var left = paddingLeft
        var top: Int

        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            if (childView.visibility == View.GONE) {
                continue
            }

            if (childView.visibility == View.VISIBLE) {
                _childVisibleViewList.add(childView)
            }

            val lp = childView.layoutParams as LayoutParams

            left += lp.leftMargin

            top = if (lp.gravity.have(Gravity.CENTER_VERTICAL)) {
                measuredHeight / 2 - childView.measuredHeight / 2
            } else {
                paddingTop + (measuredHeight - paddingTop - paddingBottom) / 2 - childView.measuredHeight / 2
            }

            /*默认垂直居中显示*/
            childView.layout(
                left, top,
                left + childView.measuredWidth,
                top + childView.measuredHeight
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

        /**
         * 宽高[WRAP_CONTENT]时, 内容view的定位索引
         * [TabIndicator.indicatorContentIndex]
         * */
        var indicatorContentIndex = -1

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.DslTabLayout_Layout)
            layoutWidth = a.getString(R.styleable.DslTabLayout_Layout_r_layout_width)
            layoutHeight = a.getString(R.styleable.DslTabLayout_Layout_r_layout_height)
            indicatorContentIndex = a.getInt(
                R.styleable.DslTabLayout_Layout_r_indicator_content_index,
                indicatorContentIndex
            )
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams) : super(source) {
            if (source is LayoutParams) {
                this.layoutWidth = source.layoutWidth
                this.layoutHeight = source.layoutHeight
            }
        }

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
        get() = maxScrollX > 0

    /**[parent]宽度外的滚动距离*/
    val maxScrollX: Int
        get() = max(maxWidth - measuredWidth, 0)

    /**view最大的宽度*/
    val maxWidth: Int
        get() = _childAllWidthSum + paddingLeft + paddingRight

    open fun onFlingChange(velocity: Float /*瞬时值*/) {
        if (needScroll) {

            //速率小于0 , 手指向左滑动
            //速率大于0 , 手指向右滑动

            startFling(-velocity.toInt(), maxWidth)
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

            scrollBy(distance.toInt(), 0)

            parent.requestDisallowInterceptTouchEvent(true)

            return true
        }
        return false
    }

    override fun scrollTo(x: Int, y: Int) {
        when {
            x > maxScrollX -> super.scrollTo(maxScrollX, y)
            x < 0 -> super.scrollTo(0, y)
            else -> super.scrollTo(x, y)
        }
    }

    override fun computeScroll() {
        if (_overScroller.computeScrollOffset()) {
            scrollTo(_overScroller.currX, _overScroller.currY)
            postInvalidate()
            if (_overScroller.currX < 0 || _overScroller.currX > maxScrollX) {
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
        val viewCenterX = measuredWidth / 2

        if (childCenterX > viewCenterX) {
            startScroll(childCenterX - viewCenterX - scrollX)
        } else {
            startScroll(-scrollX)
        }
    }

    //</editor-fold desc="滚动相关">

    //<editor-fold desc="动画相关">

    val _scrollAnimator: ValueAnimator by lazy {
        ValueAnimator().apply {
            interpolator = LinearInterpolator()
            duration = tabIndicatorAnimationDuration
            addUpdateListener {
                tabIndicator.positionOffset = it.animatedValue as Float
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

        if (tabIndicator.currentIndex == tabIndicator._targetIndex) {
            return
        }
        "_animateToItem ${tabIndicator.currentIndex} ${tabIndicator._targetIndex}".loge()
        _scrollAnimator.setFloatValues(tabIndicator.positionOffset, 1f)
        _scrollAnimator.start()
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

    var _viewPager: ViewPager? = null
    var _viewPagerScrollState = ViewPager.SCROLL_STATE_IDLE
    val _onViewPageCChangeListener: ViewPager.SimpleOnPageChangeListener =
        object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                "$state".logi()
                _viewPagerScrollState = state
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    _onAnimateEnd()
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (isAnimatorStart) {
                    //动画已经开始了
                    return
                }

                val currentItem = _viewPager?.currentItem ?: 0
                "$currentItem:$position $positionOffset $positionOffsetPixels state:$_viewPagerScrollState".logw()

                if (position < currentItem) {
                    //Page 目标在左
                    if (_viewPagerScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                        tabIndicator._targetIndex = min(currentItem, position)
                    }
                    tabIndicator.positionOffset = 1 - positionOffset
                } else {
                    //Page 目标在右
                    if (_viewPagerScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                        tabIndicator._targetIndex = max(currentItem, position + 1)
                    }
                    tabIndicator.positionOffset = positionOffset
                }
            }

            override fun onPageSelected(position: Int) {
                setCurrentItem(position)
            }
        }

    //</editor-fold desc="ViewPager 相关">

}