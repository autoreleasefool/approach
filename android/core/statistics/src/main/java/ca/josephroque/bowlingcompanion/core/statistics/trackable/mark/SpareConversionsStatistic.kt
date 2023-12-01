package ca.josephroque.bowlingcompanion.core.statistics.trackable.mark

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isAce
import ca.josephroque.bowlingcompanion.core.model.isHeadPin
import ca.josephroque.bowlingcompanion.core.model.isHeadPin2
import ca.josephroque.bowlingcompanion.core.model.isSplit
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.SecondRollStatistic

data class SpareConversionsStatistic(
	var spareChances: Int = 0,
	var spares: Int = 0,
): TrackablePerSecondRoll, SecondRollStatistic {
	override val titleResourceId = R.string.statistic_title_spare_conversions
	override val category = StatisticCategory.STRIKES_AND_SPARES
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS

	override val denominatorTitleResourceId = R.string.statistic_title_spare_chances

	override var numerator: Int
		get() = spares
		set(value) { spares = value }

	override var denominator: Int
		get() = spareChances
		set(value) { spareChances = value }

	override fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		val didSpare = secondRoll.pinsDowned.union(firstRoll.pinsDowned).arePinsCleared()

		// Don't add a spare change if the first ball was a split / head pin / aces, unless the second shot was a spare
		if (!didSpare && (firstRoll.pinsDowned.isAce() || firstRoll.pinsDowned.isSplit() || firstRoll.pinsDowned.isHeadPin() || firstRoll.pinsDowned.isHeadPin2()) ){
			return
		}

		spareChances++
		if (didSpare) {
			spares++
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}