package ca.josephroque.bowlingcompanion.games.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Game

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display pins and handle touch events for the pins.
 */
class PinLayout : LinearLayout {

    companion object {
        @Suppress("unused")
        private const val TAG = "PinLayout"
    }

    private val pinViewIds = intArrayOf(R.id.iv_pin_1, R.id.iv_pin_2, R.id.iv_pin_3, R.id.iv_pin_4, R.id.iv_pin_5)
    private var pinViews: Array<ImageView?>

    var delegate: PinLayoutInteractionDelegate? = null

    private val pinAltered = BooleanArray(Game.NUMBER_OF_PINS)

    private var initialStateSet: Boolean = false

    private var initialPinDown: Boolean = false

    private var isDragging: Boolean = false

    private val leftEdgeIgnoreWidth: Float = 20F
        get() {
            return field.times(resources.displayMetrics.density)
        }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_pin_layout, this, true)

        pinViews = arrayOfNulls(pinViewIds.size)
        pinViewIds.forEachIndexed { index, id ->
            pinViews[index] = findViewById(id)
        }
    }

    // MARK: Lifecycle functions

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val ev = event ?: return false
        if (ev.x < leftEdgeIgnoreWidth) {
            event.action = MotionEvent.ACTION_CANCEL
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                pinAltered.forEachIndexed { index, _ -> pinAltered[index] = false }

                if (!isTouchEventValid(event)) {
                    return true
                }

                setPinTouched(event, true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!initialStateSet || !isTouchEventValid(event)) {
                    return true
                }

                // Flip pins while user is dragging
                setPinTouched(event, false)
            }
            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    return true
                }

                finalizePins()
            }
        }
        return true
    }

    // MARK: PinLayout

    fun setPinEnabled(pin: Int, enabled: Boolean) {
        pinViews[pin]?.isEnabled = enabled
    }

    fun updatePinImage(pin: Int, isDown: Boolean) {
        pinViews[pin]?.post { pinViews[pin]?.setImageResource(if (isDown) R.drawable.pin_disabled else R.drawable.pin_enabled) }
    }

    // MARK: Private functions

    private fun isTouchEventValid(event: MotionEvent): Boolean {
        return event.x >= 0 && event.x <= width
    }

    private fun setPinTouched(event: MotionEvent, firstPin: Boolean) {
        val pinTouched = Math.max(0, Math.min(((event.x / width) * pinViews.size).toInt(), pinViews.lastIndex))
        val pinView = pinViews[pinTouched] ?: return
        val delegate = delegate ?: return

        if (pinAltered[pinTouched]) {
            return
        }

        if (!pinView.isEnabled) {
            if (firstPin) {
                initialStateSet = false
            }
            return
        }

        val pinDown = delegate.isPinDown(pinTouched)
        if (!initialStateSet) {
            if (firstPin) {
                initialStateSet = true
                initialPinDown = pinDown
            } else {
                return
            }
        } else if (pinDown != initialPinDown) {
            return
        }

        pinAltered[pinTouched] = true
        // Use the opposite image since we are toggling the pins from down to up or up to down
        updatePinImage(pinTouched, !pinDown)
    }

    private fun finalizePins() {
        isDragging = false
        initialStateSet = false

        val pins: MutableList<Int> = ArrayList()
        pinAltered.forEachIndexed { index, altered ->
            if (altered) {
                pins.add(index)
            }
        }

        delegate?.setPins(pins.toIntArray(), !initialPinDown)
    }

    // MARK: PinLayoutInteractionDelegate

    interface PinLayoutInteractionDelegate {
        fun setPins(pins: IntArray, isDown: Boolean)
        fun isPinDown(pin: Int): Boolean
    }
}
