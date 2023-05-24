package ca.josephroque.bowlingcompanion.statistics

import android.content.res.Resources
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Categorize similar statistics into groups to be displayed together
 * in a list.
 */
enum class StatisticsCategory : StatisticListItem {
    General,
    Overall,
    FirstBall,
    Fouls,
    PinsOnDeck,
    MatchPlay,
    Average,
    Series;

    fun getTitle(resources: Resources): String {
        return when (this) {
            General -> resources.getString(R.string.statistics_category_general)
            FirstBall -> resources.getString(R.string.statistics_category_first_ball)
            Fouls -> resources.getString(R.string.statistics_category_fouls)
            PinsOnDeck -> resources.getString(R.string.statistics_category_pins_on_deck)
            Average -> resources.getString(R.string.statistics_category_average)
            Series -> resources.getString(R.string.statistics_category_series)
            MatchPlay -> resources.getString(R.string.statistics_category_match_play)
            Overall -> resources.getString(R.string.statistics_category_overall)
        }
    }

    override val id: Long = this.ordinal.toLong()
}
