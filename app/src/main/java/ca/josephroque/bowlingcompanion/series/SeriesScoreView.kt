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
import kotlinx.android.synthetic.main.view_series_score.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display a score and its match play result in unison.
 */
class SeriesScoreView : LinearLayout {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "SeriesScoreView"

        /** Tag to save super state. */
        private const val SUPER_STATE = "${TAG}_super_state"
        /** Tag to save state of score. */
        private const val SCORE = "${TAG}_score"
        /** Tag to save state of match play. */
        private const val MATCH_PLAY = "${TAG}_match_play"
        /** Tag to save state of score text color. */
        private const val SCORE_TEXT_COLOR = "${TAG}_score_text_color"
        /** Tag to save state of match play text color. */
        private const val MATCH_PLAY_TEXT_COLOR = "${TAG}_match_play_text_color"
    }

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_series_score, this, true)
    }

    /** @Override */
    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
            putInt(SCORE, score)
            putInt(SCORE_TEXT_COLOR, scoreTextColor)
            putInt(MATCH_PLAY, matchPlay.ordinal)
            putInt(MATCH_PLAY_TEXT_COLOR, matchPlayTextColor)
        }
    }

    /** @Override */
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

    /** Score to display in the view. */
    var score: Int = 0
        set(value) {
            tv_score.text = value.toString()
            field = value
        }

    /** Match play result to display in the view */
    var matchPlay: MatchPlayResult = MatchPlayResult.NONE
        set(value) {
            when (value) {
                MatchPlayResult.NONE -> tv_match_play.text = null
                MatchPlayResult.WON -> tv_match_play.text = context.getString(R.string.match_play_won_short)
                MatchPlayResult.LOST -> tv_match_play.text = context.getString(R.string.match_play_lost_short)
                MatchPlayResult.TIED -> tv_match_play.text = context.getString(R.string.match_play_tied_short)
            }
            tv_match_play.visibility = if (value == MatchPlayResult.NONE) View.GONE else View.VISIBLE
            field = value
        }

    /** Text color of the score text field. */
    var scoreTextColor: Int = Color.BLACK
        set(value) {
            tv_score.setTextColor(value)
            field = value
        }

    /** Text color of the match play result text field. */
    var matchPlayTextColor: Int = Color.BLACK
        set(value) {
            tv_match_play.setTextColor(value)
            field = value
        }
}
