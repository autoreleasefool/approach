package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.AcesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.ChopOffsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.HighSingleStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.series.HighSeriesOf3Statistic

fun allStatistics(source: TrackableFilter.Source? = null): List<Statistic> = listOf(
	// Overall
	HighSingleStatistic(),

	// Aces
	AcesStatistic(),

	// Chop offs
	ChopOffsStatistic(),

	// Series
	HighSeriesOf3Statistic(),
).filter {
	source == null || it.supportsSource(source)
}