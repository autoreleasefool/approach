package ca.josephroque.bowlingcompanion.statistics

import android.content.res.Resources
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Categorize similar statistics into groups to be displayed together
 * in a list.
 */
enum class StatisticsCategory : StatisticListItem {
    General,
    FirstBall,
    Fouls,
    PinsOnDeck,
    Average,
    MatchPlay,
    Overall;

    /**
     * Get the title of the category.
     *
     * @param resources to get string
     * @return the title from the app resources
     */
    fun getTitle(resources: Resources): String {
        return when (this) {
            General -> resources.getString(R.string.statistics_category_general)
            FirstBall -> resources.getString(R.string.statistics_category_first_ball)
            Fouls -> resources.getString(R.string.statistics_category_fouls)
            PinsOnDeck -> resources.getString(R.string.statistics_category_pins_on_deck)
            Average -> resources.getString(R.string.statistics_category_average)
            MatchPlay -> resources.getString(R.string.statistics_category_match_play)
            Overall -> resources.getString(R.string.statistics_category_overall)
        }
    }

    /** @Override */
    override val id: Long = this.ordinal.toLong()

    /**
     * Filter a list of [Statistic] to only those which belong to this category.
     *
     * @param statistics the list of statistics to filter
     * @return a filtered list of [Statistic]
     */
    fun filterStatistics(statistics: List<Statistic>): List<Statistic> = statistics.filter { this == it.category }
}
