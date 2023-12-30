package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isLeftSplit
import ca.josephroque.bowlingcompanion.core.model.isLeftSplitWithBonus
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class LeftSplitsSparedStatistic(
	var leftSplits: Int = 0,
	var leftSplitsSpared: Int = 0,
): TrackablePerSecondRoll, SecondRollStatistic {
	override val id = StatisticID.LEFT_SPLITS_SPARED
	override val denominatorTitleResourceId: Int = R.string.statistic_title_left_splits
	override val category = StatisticCategory.SPLITS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = LeftSplitsSparedStatistic()

	override var denominator: Int
		get() = leftSplits
		set(value) { leftSplits = value }

	override var numerator: Int
		get() = leftSplitsSpared
		set(value) { leftSplitsSpared = value }

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isLeftSplit() || (configuration.countSplitWithBonusAsSplit && firstRoll.pinsDowned.isLeftSplitWithBonus())) {
			leftSplits++

			if (secondRoll.pinsDowned.plus(firstRoll.pinsDowned).arePinsCleared()) {
				leftSplitsSpared++
			}
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean  = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}