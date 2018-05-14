package ca.josephroque.bowlingcompanion.series

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.MatchPlayResult
import kotlinx.android.synthetic.main.view_series_score.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display a score and its match play result in unison.
 */
class SeriesScoreView: LinearLayout {

    /** Required constructors */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_series_score, this, true)
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
