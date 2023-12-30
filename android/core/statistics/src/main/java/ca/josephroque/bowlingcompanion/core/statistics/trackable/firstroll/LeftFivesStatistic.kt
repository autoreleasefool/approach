package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isLeftFive
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class LeftFivesStatistic(
	var leftFives: Int = 0,
): TrackablePerFirstRoll, CountingStatistic {
	override val id = StatisticID.LEFT_FIVES
	override val category = StatisticCategory.FIVES
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = LeftFivesStatistic()

	override var count: Int
		get() = leftFives
		set(value) { leftFives = value }

	override fun adjustByFirstRoll(
		firstRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isLeftFive()) {
			leftFives++
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}