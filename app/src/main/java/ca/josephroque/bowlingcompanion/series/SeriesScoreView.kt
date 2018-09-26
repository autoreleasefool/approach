package ca.josephroque.bowlingcompanion.series

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import kotlinx.android.synthetic.main.view_series_score.view.tv_score as tvScore
import kotlinx.android.synthetic.main.view_series_score.view.tv_match_play as tvMatchPlay

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display a score and its match play result in unison.
 */
class SeriesScoreView : LinearLayout {

    companion object {
        @Suppress("unused")
        private const val TAG = "SeriesScoreView"

        private const val SUPER_STATE = "${TAG}_super_state"
        private const val SCORE = "${TAG}_score"
        private const val MATCH_PLAY = "${TAG}_match_play"
        private const val SCORE_TEXT_COLOR = "${TAG}_score_text_color"
        private const val MATCH_PLAY_TEXT_COLOR = "${TAG}_match_play_text_color"
    }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_series_score, this, true)
    }

    // MARK: Lifecycle functions

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(SCORE, score)
            putInt(SCORE_TEXT_COLOR, scoreTextColor)
            putInt(MATCH_PLAY, matchPlay.ordinal)
            putInt(MATCH_PLAY_TEXT_COLOR, matchPlayTextColor)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            score = state.getInt(SCORE)
            scoreTextColor = state.getInt(SCORE_TEXT_COLOR)
            matchPlay = MatchPlayResult.fromInt(state.getInt(MATCH_PLAY))!!
            matchPlayTextColor = state.getInt(MATCH_PLAY_TEXT_COLOR)
            superState = state.getParcelable(SUPER_STATE)
        }

        super.onRestoreInstanceState(superState)
    }

    var score: Int = 0
        set(value) {
            tvScore.text = value.toString()
            field = value
        }

    var matchPlay: MatchPlayResult = MatchPlayResult.NONE
        set(value) {
            when (value) {
                MatchPlayResult.NONE -> tvMatchPlay.text = null
                MatchPlayResult.WON -> tvMatchPlay.text = context.getString(R.string.match_play_won_short)
                MatchPlayResult.LOST -> tvMatchPlay.text = context.getString(R.string.match_play_lost_short)
                MatchPlayResult.TIED -> tvMatchPlay.text = context.getString(R.string.match_play_tied_short)
            }
            tvMatchPlay.visibility = if (value == MatchPlayResult.NONE) View.GONE else View.VISIBLE
            field = value
        }

    var scoreTextColor: Int = Color.BLACK
        set(value) {
            tvScore.setTextColor(value)
            field = value
        }

    var matchPlayTextColor: Int = Color.BLACK
        set(value) {
            tvMatchPlay.setTextColor(value)
            field = value
        }
}
