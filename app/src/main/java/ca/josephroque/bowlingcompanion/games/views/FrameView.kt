package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import kotlinx.android.synthetic.main.view_frame.view.*
import kotlinx.android.synthetic.main.view_game_header.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays context for a single frame.
 */
class FrameView : ConstraintLayout {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "FrameView"
    }

    /** IDs for ball views. */
    private val ballViewIds = intArrayOf(R.id.tv_ball_1, R.id.tv_ball_2, R.id.tv_ball_3)
    /** IDs for foul views. */
    private val foulViewIds = intArrayOf(R.id.tv_foul_1, R.id.tv_foul_2, R.id.tv_foul_3)

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_frame, this, true)
    }

    /**
     * Set the text for the given ball.
     *
     * @param ball ball to set text for
     * @param text text to set
     */
    fun setBallText(ball: Int, text: String) {
        findViewById<TextView>(ballViewIds[ball]).text = text
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

    /** Score of the frame. */
    var score: Int = 0
        set(value) { tv_score.text = score.toString() }
}