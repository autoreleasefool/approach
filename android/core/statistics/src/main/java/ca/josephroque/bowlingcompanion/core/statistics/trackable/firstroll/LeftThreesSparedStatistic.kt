package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isLeftThree
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class LeftThreesSparedStatistic(
	var leftThrees: Int = 0,
	var leftThreesSpared: Int = 0,
) : TrackablePerSecondRoll, SecondRollStatistic {
	override val id = StatisticID.LEFT_THREES_SPARED
	override val denominatorTitleResourceId: Int = R.string.statistic_title_left_threes
	override val category = StatisticCategory.THREES
	override val isEligibleForNewLabel = true
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = LeftThreesSparedStatistic()

	override var denominator: Int
		get() = leftThrees
		set(value) {
			leftThrees = value
		}

	override var numerator: Int
		get() = leftThreesSpared
		set(value) {
			leftThreesSpared = value
		}

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration,
	) {
		if (firstRoll.pinsDowned.isLeftThree()) {
			leftThrees++

			if (secondRoll.pinsDowned.plus(firstRoll.pinsDowned).arePinsCleared()) {
				leftThreesSpared++
			}
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
