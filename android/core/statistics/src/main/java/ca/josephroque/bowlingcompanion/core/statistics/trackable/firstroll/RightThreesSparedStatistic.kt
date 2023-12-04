package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isRightThree
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class RightThreesSparedStatistic(
	var rightThrees: Int = 0,
	var rightThreesSpared: Int = 0,
): TrackablePerSecondRoll, SecondRollStatistic {
	override val id = StatisticID.RIGHT_THREES_SPARED
	override val denominatorTitleResourceId: Int = R.string.statistic_title_right_threes
	override val category = StatisticCategory.THREES
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = RightThreesSparedStatistic()

	override var denominator: Int
		get() = rightThrees
		set(value) { rightThrees = value }

	override var numerator: Int
		get() = rightThreesSpared
		set(value) { rightThreesSpared = value }

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isRightThree()) {
			rightThrees++

			if (secondRoll.pinsDowned.plus(firstRoll.pinsDowned).arePinsCleared()) {
				rightThreesSpared++
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