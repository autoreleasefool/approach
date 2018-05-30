package ca.josephroque.bowlingcompanion.games.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
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
class PinLayout : ConstraintLayout {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "PinLayout"
    }

    /** IDs of the pin views. */
    private val pinViewIds = intArrayOf(R.id.iv_pin_1, R.id.iv_pin_2, R.id.iv_pin_3, R.id.iv_pin_4, R.id.iv_pin_5)
    /** Pins as instances of [ImageView]. */
    private var pinViews: Array<ImageView?>

    /** Delegate for interactions. */
    var delegate: PinLayoutInteractionDelegate? = null

    /** Indicates which pins were altered in the event. */
    private val pinAltered = BooleanArray(Game.NUMBER_OF_PINS)

    /** Indicates if the pin touched was enabled and if touch events should proceed. */
    private var initialStateSet: Boolean = false

    /** Initial state of the first pin touched. */
    private var initialState: Boolean = false

    /** True when the user is dragging, false otherwise. */
    private var isDragging: Boolean = false

    /** Amount of left edge to ignore touches on. */
    private val leftEdgeIgnoreWidth: Float = 20F
        get() {
            return field.times(resources.displayMetrics.density)
        }

    /**
     * Determine if a touch event is within a valid field to continue.
     *
     * @param event the touch event
     * @return true if the event is valid, false otherwise
     */
    private fun isTouchEventValid(event: MotionEvent): Boolean {
        return event.x >= 0 && event.x <= width
    }

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_pin_layout, this, true)

        pinViews = arrayOfNulls(pinViewIds.size)
        pinViewIds.forEachIndexed { index, id ->
            pinViews[index] = findViewById(id)
        }
    }

    /** @Override */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    /** @Override */
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

    /**
     * Set properties to indicate the a pin touched in the event.
     *
     * @param event the touch event from the user
     * @param firstPin true if this is the first pin touched in the event
     */
    private fun setPinTouched(event: MotionEvent, firstPin: Boolean) {
        val pinTouched = ((event.x / width) * 5).toInt()
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

        val pinState = delegate.getPinState(pinTouched)
        if (!initialStateSet) {
            if (firstPin) {
                initialStateSet = true
                initialState = pinState
            } else {
                return
            }
        } else if (pinState != initialState) {
            return
        }

        pinAltered[pinTouched] = true
        pinView.post {
            pinView.setImageResource(if (pinState) R.drawable.pin_enabled else R.drawable.pin_disabled)
        }
    }

    /**
     * Confirm which pins have been touched or not, and relay to the delegate.
     */
    private fun finalizePins() {
        isDragging = false
        initialStateSet = false

        val pins: MutableList<Int> = ArrayList()
        pinAltered.forEachIndexed { index, altered ->
            if (altered) {
                pins.add(index)
            }
        }

        delegate?.updatePinState(pins.toIntArray(), !initialState)
    }

    /**
     * Enable or disable a pin for touch events.
     *
     * @param pin the pin to enable or disable
     * @param enabled true to enable, false to disable
     */
    fun setPinEnabled(pin: Int, enabled: Boolean) {
        pinViews[pin]?.isEnabled = enabled
    }

    /**
     * Handle interactions with the [PinLayout]
     */
    interface PinLayoutInteractionDelegate {

        /**
         * Handle when a touch event has completed and some pins have changed state.
         *
         * @param pins array of indices to update
         * @param state new state for the pins
         */
        fun updatePinState(pins: IntArray, state: Boolean)

        /**
         * Get the up or down state of a pin.
         *
         * @param pin the pin to get the state of
         * @return true if the pin is knocked down, false if it is up
         */
        fun getPinState(pin: Int): Boolean
    }
}
