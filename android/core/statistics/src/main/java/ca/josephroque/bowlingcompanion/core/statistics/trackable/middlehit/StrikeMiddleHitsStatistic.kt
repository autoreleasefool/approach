package ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isMiddleHit
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.PercentageStatistic
import ca.josephroque.bowlingcompanion.core.statistics.utils.firstRolls

data class StrikeMiddleHitsStatistic(
	var middleHits: Int = 0,
	var strikes: Int = 0,
): TrackablePerFrame, PercentageStatistic {
	override val id = StatisticID.STRIKE_MIDDLE_HITS
	override val category = StatisticCategory.MIDDLE_HITS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = StrikeMiddleHitsStatistic()

	override val numeratorTitleResourceId = R.string.statistic_title_strike_middle_hits
	override val denominatorTitleResourceId = R.string.statistic_title_middle_hits
	override val includeNumeratorInFormattedValue = true

	override var numerator: Int
		get() = strikes
		set(value) { strikes = value }

	override var denominator: Int
		get() = middleHits
		set(value) { middleHits = value }

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		for (roll in frame.firstRolls) {
			if (roll.pinsDowned.isMiddleHit()) {
				middleHits++

				if (roll.pinsDowned.arePinsCleared()) {
					strikes++
				}
			}
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}