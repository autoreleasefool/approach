package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.AcesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.ChopOffsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.HighSingleStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.NumberOfGamesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.TotalPinFallStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.TotalRollsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.series.HighSeriesOf3Statistic

fun allStatistics(
	source: TrackableFilter.Source? = null,
	supportingWidgets: Boolean? = null,
): List<Statistic> = listOf(
	// Overall
	HighSingleStatistic(),
	TotalPinFallStatistic(),
	NumberOfGamesStatistic(),
	GameAverageStatistic(),
	TotalRollsStatistic(),

	// Aces
	AcesStatistic(),

	// Chop offs
	ChopOffsStatistic(),

	// Series
	HighSeriesOf3Statistic(),
).filter {
	source == null || it.supportsSource(source)
}.filter {
	supportingWidgets == null || !supportingWidgets || it.supportsWidgets
}