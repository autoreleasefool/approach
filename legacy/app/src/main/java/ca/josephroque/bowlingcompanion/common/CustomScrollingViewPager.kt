package ca.josephroque.bowlingcompanion.common

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Enable or disable scrolling in a ViewPager.
 */
class CustomScrollingViewPager : ViewPager {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    var scrollingEnabled = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return scrollingEnabled && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return scrollingEnabled && super.onInterceptTouchEvent(ev)
    }
}
