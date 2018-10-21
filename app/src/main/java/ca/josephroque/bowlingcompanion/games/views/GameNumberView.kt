package ca.josephroque.bowlingcompanion.games.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.view_game_number.view.tv_game_number as tvGameNumber

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Draw the number of a game at a standard size
 */
class GameNumberView : ConstraintLayout {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameNumberView"

        private const val SUPER_STATE = "${TAG}_super_state"
        private const val STATE_GAME_NUMBER = "${TAG}_game_number"
    }

    var gameNumber: Int = 0
        set(value) {
            field = value
            tvGameNumber.text = context.resources.getString(R.string.game_number).format(gameNumber)
        }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_game_number, this, true)

        val frameAttr = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.Frame,
                0, 0)

        try {
            gameNumber = frameAttr.getInt(R.styleable.Game_gameNumber, gameNumber)
        } finally {
            frameAttr.recycle()
        }
    }

    // MARK: Lifecycle functions

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(STATE_GAME_NUMBER, gameNumber)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            gameNumber = state.getInt(STATE_GAME_NUMBER)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }
}
