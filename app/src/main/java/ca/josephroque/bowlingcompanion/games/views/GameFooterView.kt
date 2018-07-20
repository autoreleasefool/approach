package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import kotlinx.android.synthetic.main.view_game_footer.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Footer view to display game and frame controls at the foot of the game details.
 */
class GameFooterView : ConstraintLayout, View.OnClickListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameFooterView"

        /** Tag to save super state. */
        private const val SUPER_STATE = "${TAG}_super_state"
        /** Tag to save state of current ball. */
        private const val STATE_CURRENT_BALL = "${TAG}_current_ball"
        /** Tag to save state of match play results. */
        private const val STATE_MATCH_PLAY_RESULT = "${TAG}_match_play_result"
        /** Tag to save the current lock state. */
        private const val STATE_LOCK = "${TAG}_lock"
        /** Tag to save the current foul state. */
        private const val STATE_FOUL = "${TAG}_foul"
    }

    /** Delegate for interactions. */
    var delegate: GameFooterInteractionDelegate? = null

    /** The current ball, which determines state of the clear pin button. */
    var currentBall: Int = 0
        set(value) {
            field = value
            iv_clear_pins.setImageResource(when (value) {
                1 -> R.drawable.ic_clear_pins_spare
                2 -> R.drawable.ic_clear_pins_fifteen
                else -> R.drawable.ic_clear_pins_strike
            })
        }

    /** The current match play result, which determines state of the match play button. */
    var matchPlayResult: MatchPlayResult = MatchPlayResult.NONE
        set(value) {
            field = value
            iv_match_play.setImageResource(value.getIcon())
        }

    /** Indicates if the current game is locked, which determines state of the game lock button. */
    var isGameLocked: Boolean = false
        set(value) {
            field = value
            iv_lock.setImageResource(if (value) R.drawable.ic_lock else R.drawable.ic_lock_open)
        }

    /**
     * Indicates if the current ball has the foul enabled, which determines state of
     * the foul button.
     */
    var isFoulActive: Boolean = false
        set(value) {
            field = value
            iv_foul.setImageResource(if (value) R.drawable.ic_foul_active else R.drawable.ic_foul_inactive)
        }

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_game_footer, this, true)

        iv_clear_pins.setOnClickListener(this)
        iv_foul.setOnClickListener(this)
        iv_lock.setOnClickListener(this)
        iv_match_play.setOnClickListener(this)
    }

    /** @Override */
    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_CURRENT_BALL, currentBall)
            putInt(STATE_MATCH_PLAY_RESULT, matchPlayResult.ordinal)
            putBoolean(STATE_LOCK, isGameLocked)
            putBoolean(STATE_FOUL, isFoulActive)
        }
    }

    /** @Override */
    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            currentBall = state.getInt(STATE_CURRENT_BALL)
            matchPlayResult = MatchPlayResult.fromInt(state.getInt(STATE_MATCH_PLAY_RESULT))!!
            isGameLocked = state.getBoolean(STATE_LOCK)
            isFoulActive = state.getBoolean(STATE_FOUL)
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
            R.id.iv_clear_pins -> delegate?.onClearPins()
            R.id.iv_foul -> delegate?.onFoulToggle()
            R.id.iv_lock -> delegate?.onLockToggle()
            R.id.iv_match_play -> delegate?.onMatchPlaySettings()
        }
    }
    /**
     * Handle interactions with this view.
     */
    interface GameFooterInteractionDelegate {
        /**
         * Indicate the user wishes to lock or unlock the game.
         */
        fun onLockToggle()

        /**
         * Indicate the user wishes to clear the pins.
         */
        fun onClearPins()

        /**
         * Indicate the user wishes to add or remove a foul.
         */
        fun onFoulToggle()

        /**
         * Indicate the user wishes to adjust match play settings.
         */
        fun onMatchPlaySettings()
    }
}
