package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.view_frame.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays context for a single frame.
 */
class FrameView : LinearLayout, View.OnClickListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "FrameView"

        /** Tag to save super state. */
        private const val SUPER_STATE = "${TAG}_super_state"
        /** Tag to save state of the frame this view represents. */
        private const val STATE_FRAME_NUMBER = "${TAG}_frame_number"
        /** Tag to save state of current ball. */
        private const val STATE_CURRENT_BALL = "${TAG}_current_ball"
        /** Tag to save state of score. */
        private const val STATE_SCORE = "${TAG}_score"
        /** Tag to save state of current frame. */
        private const val STATE_CURRENT_FRAME = "${TAG}_current_frame"
    }

    /** IDs for ball views. */
    private val ballViewIds = intArrayOf(R.id.tv_ball_1, R.id.tv_ball_2, R.id.tv_ball_3)
    /** IDs for foul views. */
    private val foulViewIds = intArrayOf(R.id.tv_foul_1, R.id.tv_foul_2, R.id.tv_foul_3)
    /** Listener for events. */
    var delegate: FrameInteractionDelegate? = null

    /** Frame number to display beneath the score. */
    var frameNumber: Int = 0
        set(value) {
            field = value
            tv_frame_number.text = (value + 1).toString()
        }

    /** Score of the frame. */
    var score: Int = 0
        set(value) {
            field = value
            tv_score.text = value.toString()
        }

    /** Current active ball in the frame. */
    var currentBall: Int = 0
        set(value) {
            field = value
            updateCurrentFrame()
        }

    /** True if this is the current active frame, false otherwise. */
    var isCurrentFrame: Boolean = false
        set(value) {
            field = value
            updateCurrentFrame()
        }

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_frame, this, true)

        val frameAttr = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.Frame,
                0, 0)

        try {
            frameNumber = frameAttr.getInt(R.styleable.Frame_frameNumber, frameNumber)
        } finally {
            frameAttr.recycle()
        }

        ballViewIds.forEach {
            findViewById<TextView>(it).setOnClickListener(this)
        }
        frame.setOnClickListener(this)
    }

    /** @Override */
    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_FRAME_NUMBER, frameNumber)
            putInt(STATE_SCORE, score)
            putInt(STATE_CURRENT_BALL, currentBall)
            putBoolean(STATE_CURRENT_FRAME, isCurrentFrame)
        }
    }

    /** @Override */
    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            frameNumber = state.getInt(STATE_FRAME_NUMBER)
            score = state.getInt(STATE_SCORE)
            currentBall = state.getInt(STATE_CURRENT_BALL)
            isCurrentFrame = state.getBoolean(STATE_CURRENT_FRAME)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }

    /** @Override */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    /**
     * Set the text for the given ball.
     *
     * @param ball ball to set text for
     * @param text text to set
     */
    fun setBallText(ball: Int, text: String) {
        // TODO: should highlight
        findViewById<TextView>(ballViewIds[ball]).text = text
    }

    /**
     * Set the score for the frame.
     *
     * @param text the score to set
     */
    fun setFrameText(text: String) {
        tv_score.text = text
    }

    /**
     * Enable or disable the foul indicator for a given ball.
     *
     * @param ball ball to enable or disable foul for
     * @param enabled true to enable, false to disable
     */
    fun setFoulEnabled(ball: Int, enabled: Boolean) {
        findViewById<TextView>(foulViewIds[ball]).visibility = if (enabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    /**
     * Update background colors of the views depending on if this is the current frame or not.
     */
    private fun updateCurrentFrame() {
        ballViewIds.forEachIndexed { index, i ->
            val backgroundDrawable = ContextCompat.getDrawable(context,
                    if (currentBall == index && isCurrentFrame) R.drawable.frame_background_active else R.drawable.frame_background_inactive)
            findViewById<TextView>(i).background = backgroundDrawable
        }

        val backgroundDrawable = ContextCompat.getDrawable(context,
                if (isCurrentFrame) R.drawable.frame_background_active else R.drawable.frame_background_inactive)
        frame.background = backgroundDrawable
    }

    /** @Override */
    override fun onClick(v: View?) {
        val view = v ?: return

        val ballIdIndex = ballViewIds.indexOf(view.id)
        if (ballIdIndex > -1) {
            delegate?.onBallSelected(ballIdIndex, frameNumber)
            return
        }

        val isFrame = frame.id == view.id
        if (isFrame) {
            delegate?.onFrameSelected(frameNumber)
        }
    }

    /**
     * Handle interactions with the view.
     */
    interface FrameInteractionDelegate {

        /**
         * Invoked when a ball view is touched by the user.
         *
         * @param ball the ball selected (zero-based)
         * @param frame the frame number (zero-based)
         */
        fun onBallSelected(ball: Int, frame: Int)

        /**
         * Invoked when a frame view is touched by the user.
         *
         * @param frame the frame number (zero-based)
         */
        fun onFrameSelected(frame: Int)
    }
}
