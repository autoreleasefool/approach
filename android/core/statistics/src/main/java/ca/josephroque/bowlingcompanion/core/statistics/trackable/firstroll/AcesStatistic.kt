package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isAce
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class AcesStatistic(
	var aces: Int = 0,
): TrackablePerFirstRoll, CountingStatistic {
	override val titleResourceId: Int = R.string.statistic_title_aces
	override val category: StatisticCategory = StatisticCategory.ACES
	override val isEligibleForNewLabel: Boolean = false
	override val preferredTrendDirection: PreferredTrendDirection = PreferredTrendDirection.DOWNWARDS

	override var count: Int
		get() = aces
		set(value) { aces = value }

	override fun adjustByFirstRoll(
		firstRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration
	) {
		if (firstRoll.pinsDowned.isAce()) {
			aces++
		}
	}
}