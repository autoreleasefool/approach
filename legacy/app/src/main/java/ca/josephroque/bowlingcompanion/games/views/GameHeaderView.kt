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
import kotlinx.android.synthetic.main.view_game_header.view.tv_game_number as gameNumber
import kotlinx.android.synthetic.main.view_game_header.view.tv_next_ball as nextBall
import kotlinx.android.synthetic.main.view_game_header.view.tv_prev_ball as prevBall

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Header of a game which displays the game number and navigation buttons.
 */
class GameHeaderView : LinearLayout, View.OnClickListener {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameHeaderView"

        private const val SUPER_STATE = "${TAG}_super_state"
        private const val STATE_CURRENT_GAME = "${TAG}_current_game"
        private const val STATE_NEXT_FRAME = "${TAG}_next_frame"
        private const val STATE_PREV_FRAME = "${TAG}_prev_frame"
        private const val STATE_MANUAL_SCORE = "${TAG}_manual"
    }

    var delegate: GameHeaderInteractionDelegate? = null

    var currentGame: Int = 0
        set(value) { gameNumber.text = String.format(resources.getString(R.string.game_number), value + 1) }

    var hasPreviousFrame: Boolean = false
        set(value) {
            field = value
            invalidateButtons()
        }

    var hasNextFrame: Boolean = true
        set(value) {
            field = value
            invalidateButtons()
        }

    var isManualScoreSet: Boolean = false
        set(value) {
            field = value
            invalidateButtons()
        }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_game_header, this, true)
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
        gameNumber.text = String.format(resources.getString(R.string.game_number), currentGame + 1)
        nextBall.setOnClickListener(this)
        prevBall.setOnClickListener(this)
    }

    // MARK: Lifecycle functions

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_CURRENT_GAME, currentGame)
            putBoolean(STATE_NEXT_FRAME, hasNextFrame)
            putBoolean(STATE_PREV_FRAME, hasPreviousFrame)
            putBoolean(STATE_MANUAL_SCORE, isManualScoreSet)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            currentGame = state.getInt(STATE_CURRENT_GAME)
            hasNextFrame = state.getBoolean(STATE_NEXT_FRAME)
            hasPreviousFrame = state.getBoolean(STATE_PREV_FRAME)
            isManualScoreSet = state.getBoolean(STATE_MANUAL_SCORE)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    // MARK: OnClickListener

    override fun onClick(v: View?) {
        val view = v ?: return
        when (view.id) {
            R.id.tv_prev_ball -> delegate?.onPrevBall()
            R.id.tv_next_ball -> delegate?.onNextBall()
        }
    }

    // MARK: Private functions

    private fun invalidateButtons() {
        val prevVisibility = if (!isManualScoreSet && hasPreviousFrame) View.VISIBLE else View.INVISIBLE
        prevBall.post { prevBall.visibility = prevVisibility }

        val nextVisibility = if (hasNextFrame) View.VISIBLE else View.INVISIBLE
        nextBall.post { nextBall.visibility = nextVisibility }
    }

    // MARK: GameHeaderInteractionDelegate

    interface GameHeaderInteractionDelegate {
        fun onNextBall()
        fun onPrevBall()
    }
}
