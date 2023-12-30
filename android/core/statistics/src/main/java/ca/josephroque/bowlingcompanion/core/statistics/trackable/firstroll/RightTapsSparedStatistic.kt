package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isRightTap
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class RightTapsSparedStatistic(
	var rightTaps: Int = 0,
	var rightTapsSpared: Int = 0,
): TrackablePerSecondRoll, SecondRollStatistic {
	override val id = StatisticID.RIGHT_TAPS_SPARED
	override val denominatorTitleResourceId: Int = R.string.statistic_title_right_taps
	override val category = StatisticCategory.TAPS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = RightTapsSparedStatistic()

	override var denominator: Int
		get() = rightTaps
		set(value) { rightTaps = value }

	override var numerator: Int
		get() = rightTapsSpared
		set(value) { rightTapsSpared = value }

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isRightTap()) {
			rightTaps++

			if (secondRoll.pinsDowned.plus(firstRoll.pinsDowned).arePinsCleared()) {
				rightTapsSpared++
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