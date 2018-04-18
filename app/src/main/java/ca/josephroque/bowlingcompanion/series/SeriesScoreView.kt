package ca.josephroque.bowlingcompanion.series

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.MatchPlayResult

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

    val scoreText: TextView = findViewById(R.id.tv_score)

    val matchPlayText: TextView = findViewById(R.id.tv_match_play)

    var score: Int = 0
        set(value) {
            scoreText.text = value.toString()
            field = value
        }

    var matchPlay: MatchPlayResult = MatchPlayResult.NONE
        set(value) {
            when (value) {
                MatchPlayResult.NONE -> matchPlayText.text = null
                MatchPlayResult.WON -> matchPlayText.text = context.getString(R.string.match_play_won_short)
                MatchPlayResult.LOST -> matchPlayText.text = context.getString(R.string.match_play_lost_short)
                MatchPlayResult.TIED -> matchPlayText.text = context.getString(R.string.match_play_tied_short)
            }
            matchPlayText.visibility = if (value == MatchPlayResult.NONE) View.GONE else View.VISIBLE
            field = value
        }
}