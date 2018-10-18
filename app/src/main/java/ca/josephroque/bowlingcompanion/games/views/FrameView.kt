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
import ca.josephroque.bowlingcompanion.games.lane.Ball
import ca.josephroque.bowlingcompanion.settings.Settings
import ca.josephroque.bowlingcompanion.utils.isVisible
import kotlinx.android.synthetic.main.view_frame.view.frame as frame
import kotlinx.android.synthetic.main.view_frame.view.tv_frame_number as tvFrameNumber
import kotlinx.android.synthetic.main.view_frame.view.tv_score as tvScore

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays context for a single frame.
 */
class FrameView : LinearLayout, View.OnClickListener {

    companion object {
        @Suppress("unused")
        private const val TAG = "FrameView"

        private const val SUPER_STATE = "${TAG}_super_state"
        private const val STATE_FRAME_NUMBER = "${TAG}_frame_number"
        private const val STATE_CURRENT_BALL = "${TAG}_current_ball"
        private const val STATE_SCORE = "${TAG}_score"
        private const val STATE_CURRENT_FRAME = "${TAG}_current_frame"
    }

    private val ballViewIds = intArrayOf(R.id.tv_ball_1, R.id.tv_ball_2, R.id.tv_ball_3)
    private val foulViewIds = intArrayOf(R.id.tv_foul_1, R.id.tv_foul_2, R.id.tv_foul_3)

    var delegate: FrameInteractionDelegate? = null

    var frameNumber: Int = 0
        set(value) {
            field = value
            tvFrameNumber.text = (value + 1).toString()
        }

    var frameNumberVisible: Boolean = true
        set(value) {
            field = value
            tvFrameNumber.visibility = if (value) View.VISIBLE else View.GONE
        }

    var score: Int = 0
        set(value) {
            field = value
            tvScore.text = value.toString()
        }

    var currentBall: Int = 0
        set(value) {
            field = value
            updateCurrentFrame()
        }

    var isCurrentFrame: Boolean = false
        set(value) {
            field = value
            updateCurrentFrame()
        }

    var shouldHighlightMarks: Boolean = Settings.BooleanSetting.EnableStrikeHighlights.default
        set(value) {
            field = value
            for (i in 0..ballViewIds.lastIndex) {
                val ballView = findViewById<TextView>(ballViewIds[i])
                setBallText(i, ballView.text.toString())
            }
        }

    // MARK: Constructors

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

    // MARK: Lifecycle functions

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_FRAME_NUMBER, frameNumber)
            putInt(STATE_SCORE, score)
            putInt(STATE_CURRENT_BALL, currentBall)
            putBoolean(STATE_CURRENT_FRAME, isCurrentFrame)
        }
    }

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    // MARK: FrameView

    fun setBallText(ball: Int, text: String) {
        val ballView = findViewById<TextView>(ballViewIds[ball])
        ballView.text = text
        if (shouldHighlightMarks && (text == Ball.Strike.toString() || text == Ball.Spare.toString())) {
            ballView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else {
            ballView.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
        }
    }

    fun setFrameText(text: String) {
        tvScore.text = text
    }

    fun setFoulEnabled(ball: Int, enabled: Boolean) {
        findViewById<TextView>(foulViewIds[ball]).visibility = if (enabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    // MARK: OnClickListener

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

    // MARK: Private functions

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

    // MARK: FrameInteractionDelegate

    interface FrameInteractionDelegate {
        fun onBallSelected(ball: Int, frame: Int)
        fun onFrameSelected(frame: Int)
    }
}
