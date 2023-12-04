package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isFive
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class FivesSparedStatistic(
	var fives: Int = 0,
	var fivesSpared: Int = 0,
): TrackablePerSecondRoll, SecondRollStatistic {
	override val id = StatisticID.FIVES_SPARED
	override val denominatorTitleResourceId: Int = R.string.statistic_title_fives
	override val category = StatisticCategory.FIVES
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = FivesSparedStatistic()

	override var denominator: Int
		get() = fives
		set(value) { fives = value }

	override var numerator: Int
		get() = fivesSpared
		set(value) { fivesSpared = value }

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isFive()) {
			fives++

			if (secondRoll.pinsDowned.plus(firstRoll.pinsDowned).arePinsCleared()) {
				fivesSpared++
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