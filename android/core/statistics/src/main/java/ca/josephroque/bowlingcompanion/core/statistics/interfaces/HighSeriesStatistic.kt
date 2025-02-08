package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeries
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeriesConfiguration

interface HighSeriesStatistic :
	HighestOfStatistic,
	TrackablePerSeries {
	val seriesSize: Int
	var highSeries: Int

	override val category: StatisticCategory
		get() = StatisticCategory.SERIES

	override val preferredTrendDirection
		get() = PreferredTrendDirection.UPWARDS

	override var highest: Int
		get() = highSeries
		set(value) {
			highSeries = value
		}

	override fun adjustBySeries(series: TrackableSeries, configuration: TrackablePerSeriesConfiguration) {
		if (series.numberOfGames == seriesSize) {
			highSeries = maxOf(series.total, highSeries)
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Team -> true
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> false
		is TrackableFilter.Source.Game -> false
	}
}
