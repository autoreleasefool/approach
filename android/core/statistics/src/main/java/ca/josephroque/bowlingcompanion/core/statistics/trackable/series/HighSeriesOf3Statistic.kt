package ca.josephroque.bowlingcompanion.core.statistics.trackable.series

import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeries
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeriesConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.HighestOfStatistic

data class HighSeriesOf3Statistic(
	var highSeries: Int = 0,
): TrackablePerSeries, HighestOfStatistic {
	override val titleResourceId = R.string.statistic_title_high_series_of_3
	override val category = StatisticCategory.SERIES
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS

	override var highest: Int
		get() = highSeries
		set(value) { highSeries = value }

	override fun adjustBySeries(
		series: TrackableSeries,
		configuration: TrackablePerSeriesConfiguration
	) {
		if (series.numberOfGames == 3) {
			highSeries = maxOf(series.total, highSeries)
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> false
		is TrackableFilter.Source.Game -> false
	}
}