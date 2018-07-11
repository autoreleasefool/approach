package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import kotlinx.android.synthetic.main.view_game_header.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Header of a game which displays the game number and navigation buttons.
 */
class GameHeaderView : LinearLayout, View.OnClickListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameHeaderView"

        /** Tag to save super state. */
        private const val SUPER_STATE = "${TAG}_super_state"
        /** Tag to save state of current game. */
        private const val STATE_CURRENT_GAME = "${TAG}_current_game"
        /** Tag to save state of current frame. */
        private const val STATE_CURRENT_FRAME = "${TAG}_current_frame"
        /** Tag to save state of current ball. */
        private const val STATE_CURRENT_BALL = "${TAG}_current_ball"
    }

    /** Handle interactions with the view. */
    var delegate: GameHeaderInteractionDelegate? = null

    /** Current game to display in the header. */
    var currentGame: Int = 0
        set(value) {
            field = value
            tv_game_number.text = String.format(resources.getString(R.string.game_number), value + 1)
        }

    /**
     * The current ball. If [currentBall] and [currentFrame] are equal to 0, disable the previous
     * ball button. If they are equal to their max values, disable the next ball button.
     */
    var currentBall: Int = 0
        set(value) {
            tv_prev_ball.isEnabled = value != 0 || currentFrame != 0
            tv_next_ball.isEnabled = value != Frame.LAST_BALL || currentFrame != Game.LAST_FRAME
            field = value
        }

    /**
     * The current frame. If [currentBall] and [currentFrame] are equal to 0, disable the previous
     * ball button. If they are equal to their max values, disable the next ball button.
     */
    var currentFrame: Int = 0
        set(value) {
            tv_prev_ball.isEnabled = value != 0 || currentBall != 0
            tv_next_ball.isEnabled = value != Game.LAST_FRAME || currentBall != Frame.LAST_BALL
            field = value
        }

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_game_header, this, true)
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
        tv_game_number.text = String.format(resources.getString(R.string.game_number), currentGame + 1)
        tv_next_ball.setOnClickListener(this)
        tv_prev_ball.setOnClickListener(this)
    }

    /** @Override */
    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_CURRENT_GAME, currentGame)
            putInt(STATE_CURRENT_FRAME, currentFrame)
            putInt(STATE_CURRENT_BALL, currentBall)
        }
    }

    /** @Override */
    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            currentGame = state.getInt(STATE_CURRENT_GAME)
            currentFrame = state.getInt(STATE_CURRENT_FRAME)
            currentBall = state.getInt(STATE_CURRENT_BALL)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }

    /** @Override */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    /** @Override */
    override fun onClick(v: View?) {
        val view = v ?: return
        when (view.id) {
            R.id.tv_prev_ball -> delegate?.onPrevBall()
            R.id.tv_next_ball -> delegate?.onNextBall()
        }
    }

    /**
     * Handle interactions with the view.
     */
    interface GameHeaderInteractionDelegate {

        /**
         * Indicates user wishes to switch to the next ball.
         */
        fun onNextBall()

        /**
         * Indicates user wishes to switch to the previous ball.
         */
        fun onPrevBall()
    }
}
