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
import kotlinx.android.synthetic.main.view_game_footer.view.pins_divider as pinsDivider
import kotlinx.android.synthetic.main.view_game_footer.view.iv_clear_pins as clearPinsIcon
import kotlinx.android.synthetic.main.view_game_footer.view.iv_foul as foulIcon
import kotlinx.android.synthetic.main.view_game_footer.view.iv_lock as lockIcon
import kotlinx.android.synthetic.main.view_game_footer.view.iv_match_play as matchPlayIcon
import kotlinx.android.synthetic.main.view_game_footer.view.iv_fullscreen as fullscreenIcon

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Footer view to display game and frame controls at the foot of the game details.
 */
class GameFooterView : ConstraintLayout {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameFooterView"

        private const val SUPER_STATE = "${TAG}_super_state"
        private const val STATE_CURRENT_BALL = "${TAG}_current_ball"
        private const val STATE_MATCH_PLAY_RESULT = "${TAG}_match_play_result"
        private const val STATE_LOCK = "${TAG}_lock"
        private const val STATE_FOUL = "${TAG}_foul"
        private const val STATE_MANUAL_SCORE = "${TAG}_manual"
        private const val STATE_FULLSCREEN = "${TAG}_fullscreen"

        enum class Clear {
            Strike, Spare, Fifteen;

            val image: Int
                get() = when (this) {
                    Strike -> R.drawable.ic_clear_pins_strike
                    Spare -> R.drawable.ic_clear_pins_spare
                    Fifteen -> R.drawable.ic_clear_pins_fifteen
                }

            companion object {
                private val map = Clear.values().associateBy(Clear::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    var delegate: GameFooterInteractionDelegate? = null

    var isFullscreen: Boolean = false
        set(value) {
            field = value
            fullscreenIcon.setImageResource(if (value) R.drawable.ic_fullscreen_exit else R.drawable.ic_fullscreen)
        }

    var clear: Clear = Clear.Strike
        set(value) {
            field = value
            clearPinsIcon.setImageResource(value.image)
        }

    var matchPlayResult: MatchPlayResult = MatchPlayResult.NONE
        set(value) {
            field = value
            matchPlayIcon.setImageResource(value.getIcon())
        }

    var isGameLocked: Boolean = false
        set(value) {
            field = value
            lockIcon.setImageResource(if (value) R.drawable.ic_lock else R.drawable.ic_lock_open)
        }

    var isFoulActive: Boolean = false
        set(value) {
            field = value
            foulIcon.setImageResource(if (value) R.drawable.ic_foul_active else R.drawable.ic_foul_inactive)
        }

    var isManualScoreSet: Boolean = false
        set(value) {
            field = value
            val visible = if (value) View.GONE else View.VISIBLE
            clearPinsIcon.visibility = visible
            foulIcon.visibility = visible
            pinsDivider.visibility = visible
        }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.iv_fullscreen -> delegate?.onFullscreenToggle()
            R.id.iv_clear_pins -> delegate?.onClearPins()
            R.id.iv_foul -> delegate?.onFoulToggle()
            R.id.iv_lock -> delegate?.onLockToggle()
            R.id.iv_match_play -> delegate?.onMatchPlaySettings()
        }
    }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_game_footer, this, true)

        fullscreenIcon.setOnClickListener(onClickListener)
        clearPinsIcon.setOnClickListener(onClickListener)
        foulIcon.setOnClickListener(onClickListener)
        lockIcon.setOnClickListener(onClickListener)
        matchPlayIcon.setOnClickListener(onClickListener)
    }

    // MARK: Lifecycle functions

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_CURRENT_BALL, clear.ordinal)
            putInt(STATE_MATCH_PLAY_RESULT, matchPlayResult.ordinal)
            putBoolean(STATE_LOCK, isGameLocked)
            putBoolean(STATE_FOUL, isFoulActive)
            putBoolean(STATE_MANUAL_SCORE, isManualScoreSet)
            putBoolean(STATE_FULLSCREEN, isFullscreen)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            clear = Clear.fromInt(state.getInt(STATE_CURRENT_BALL))!!
            matchPlayResult = MatchPlayResult.fromInt(state.getInt(STATE_MATCH_PLAY_RESULT))!!
            isGameLocked = state.getBoolean(STATE_LOCK)
            isFoulActive = state.getBoolean(STATE_FOUL)
            isManualScoreSet = state.getBoolean(STATE_MANUAL_SCORE)
            isFullscreen = state.getBoolean(STATE_FULLSCREEN)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

    // MARK: GameFooterInteractionDelegate

    interface GameFooterInteractionDelegate {
        fun onLockToggle()
        fun onClearPins()
        fun onFoulToggle()
        fun onMatchPlaySettings()
        fun onFullscreenToggle()
    }
}
