package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isChop
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class ChopOffsStatistic(
	var chopOffs: Int = 0,
): TrackablePerFirstRoll, CountingStatistic {
	override val id = StatisticID.CHOPS
	override val category = StatisticCategory.CHOPS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = ChopOffsStatistic()

	override var count: Int
		get() = chopOffs
		set(value) { chopOffs = value }

	override fun adjustByFirstRoll(
		firstRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isChop()) {
			chopOffs++
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}