package ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isMiddleHit
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.FirstRollStatistic

data class MiddleHitsStatistic(
	var middleHits: Int = 0,
	override var totalRolls: Int = 0,
) : TrackablePerFirstRoll, FirstRollStatistic {
	override val id = StatisticID.MIDDLE_HITS
	override val category = StatisticCategory.MIDDLE_HITS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = MiddleHitsStatistic()

	override var numerator: Int
		get() = middleHits
		set(value) {
			middleHits = value
		}

	override fun tracksRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration): Boolean {
		return firstRoll.pinsDowned.isMiddleHit()
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Team -> true
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}
