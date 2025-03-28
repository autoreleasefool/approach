package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isRightSplit
import ca.josephroque.bowlingcompanion.core.model.isRightSplitWithBonus
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class RightSplitsStatistic(
	var rightSplits: Int = 0,
) : TrackablePerFirstRoll, CountingStatistic {
	override val id = StatisticID.RIGHT_SPLITS
	override val category = StatisticCategory.SPLITS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = RightSplitsStatistic()

	override var count: Int
		get() = rightSplits
		set(value) {
			rightSplits = value
		}

	override fun adjustByFirstRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration) {
		if (firstRoll.pinsDowned.isRightSplit() ||
			(configuration.countSplitWithBonusAsSplit && firstRoll.pinsDowned.isRightSplitWithBonus())
		) {
			rightSplits++
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
