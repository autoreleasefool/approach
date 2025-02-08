package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isRightThree
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class RightThreesStatistic(
	var rightThrees: Int = 0,
) : TrackablePerFirstRoll, CountingStatistic {
	override val id = StatisticID.RIGHT_THREES
	override val category = StatisticCategory.THREES
	override val isEligibleForNewLabel = true
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = RightThreesStatistic()

	override var count: Int
		get() = rightThrees
		set(value) {
			rightThrees = value
		}

	override fun adjustByFirstRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration) {
		if (firstRoll.pinsDowned.isRightThree()) {
			rightThrees++
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Team -> true
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}
