package ca.josephroque.bowlingcompanion.core.statistics.trackable.foul

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.PercentageStatistic

data class FoulsStatistic(
	var fouls: Int = 0,
	var totalRolls: Int = 0,
): PercentageStatistic, TrackablePerFrame {
	override val titleResourceId = R.string.statistic_title_fouls
	override val category = StatisticCategory.FOULS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS

	override val numeratorTitleResourceId = R.string.statistic_title_fouls
	override val denominatorTitleResourceId = R.string.statistic_title_total_rolls
	override val includeNumeratorInFormattedValue = false

	override var numerator: Int
		get() = fouls
		set(value) { fouls = value }

	override var denominator: Int
		get() = totalRolls
		set(value) { totalRolls = value }

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		totalRolls += frame.rolls.size
		fouls += frame.rolls.count { it.didFoul }
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}