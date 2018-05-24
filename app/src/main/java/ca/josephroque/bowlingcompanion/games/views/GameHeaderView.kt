package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
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
class GameHeaderView : LinearLayout {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameHeaderView"

        /** Tag to save super state. */
        private const val SUPER_STATE = "${TAG}_super_state"
        /** Tag to save state of current game. */
        private const val CURRENT_GAME = "${TAG}_current_game"
        /** Tag to save state of current frame. */
        private const val CURRENT_FRAME = "${TAG}_current_frame"
        /** Tag to save state of current ball. */
        private const val CURRENT_BALL = "${TAG}_current_ball"
    }

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val rootView = LayoutInflater.from(context).inflate(R.layout.view_game_header, this, true)
        rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
        rootView.tv_game_number.text = String.format(resources.getString(R.string.game_number), currentGame)
    }

    /** @Override */
    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(CURRENT_GAME, currentGame)
        bundle.putInt(CURRENT_FRAME, currentFrame)
        bundle.putInt(CURRENT_BALL, currentBall)
        return bundle
    }

    /** @Override */
    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            currentGame = state.getInt(CURRENT_GAME)
            currentFrame = state.getInt(CURRENT_FRAME)
            currentBall = state.getInt(CURRENT_BALL)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }

    /** Current game to display in the header. */
    var currentGame: Int = 1
        set(value) {
            tv_game_number.text = String.format(resources.getString(R.string.game_number), value)
            field = value
        }

    /**
     * The current ball. If [currentBall] and [currentFrame] are equal to 1, disable the previous
     * ball button. If they are equal to their max values, disable the next ball button.
     */
    var currentBall: Int = 1
        set(value) {
            tv_prev_ball.isEnabled = value != 1 || currentFrame != 1
            tv_next_ball.isEnabled = value != Frame.NUMBER_OF_BALLS || currentFrame != Game.NUMBER_OF_FRAMES
            field = value
        }

    /**
     * The current frame. If [currentBall] and [currentFrame] are equal to 1, disable the previous
     * ball button. If they are equal to their max values, disable the next ball button.
     */
    var currentFrame: Int = 1
        set(value) {
            tv_prev_ball.isEnabled = value != 1 || currentBall != 1
            tv_next_ball.isEnabled = value != Game.NUMBER_OF_FRAMES|| currentBall != Frame.NUMBER_OF_BALLS
            field = value
        }
}
