package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.AcesStatistic

fun allStatistics(source: TrackableFilter.Source? = null): List<Statistic> = listOf(
	AcesStatistic(),
).filter {
	source == null || it.supportsSource(source)
}