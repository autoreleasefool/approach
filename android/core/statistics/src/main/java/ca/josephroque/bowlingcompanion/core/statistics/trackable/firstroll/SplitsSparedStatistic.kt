package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isSplit
import ca.josephroque.bowlingcompanion.core.model.isSplitWithBonus
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class SplitsSparedStatistic(
	var splits: Int = 0,
	var splitsSpared: Int = 0,
): TrackablePerSecondRoll, SecondRollStatistic {
	override val id = StatisticID.SPLITS_SPARED
	override val denominatorTitleResourceId: Int = R.string.statistic_title_splits
	override val category = StatisticCategory.SPLITS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = SplitsSparedStatistic()

	override var denominator: Int
		get() = splits
		set(value) { splits = value }

	override var numerator: Int
		get() = splitsSpared
		set(value) { splitsSpared = value }

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isSplit() || (configuration.countSplitWithBonusAsSplit && firstRoll.pinsDowned.isSplitWithBonus())) {
			splits++

			if (secondRoll.pinsDowned.plus(firstRoll.pinsDowned).arePinsCleared()) {
				splitsSpared++
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