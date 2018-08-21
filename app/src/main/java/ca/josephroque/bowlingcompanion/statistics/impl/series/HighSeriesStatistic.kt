package ca.josephroque.bowlingcompanion.statistics.impl.series

import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.statistics.IntegerStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Parent class for statistics which are high series of different number of games.
 */
abstract class HighSeriesStatistic(override var value: Int) : IntegerStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(series: Series) {
        if (series.numberOfGames == seriesSize) {
            value = maxOf(series.total, value)
        }
    }

    // MARK: Overrides

    override fun isModifiedBy(series: Series) = true
    override val category = StatisticsCategory.Series

    // MARK: HighSeriesStatistic

    /** Size of the series to get statistics on */
    abstract val seriesSize: Int
}
