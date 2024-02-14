package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class TotalRollsStatistic(
	var totalRolls: Int = 0,
) : TrackablePerFrame, CountingStatistic {
	override val id = StatisticID.TOTAL_ROLLS
	override val category = StatisticCategory.OVERALL
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = null
	override fun emptyClone() = TotalRollsStatistic()

	override var count: Int
		get() = totalRolls
		set(value) {
			totalRolls = value
		}

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		totalRolls += frame.totalRolls
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}
