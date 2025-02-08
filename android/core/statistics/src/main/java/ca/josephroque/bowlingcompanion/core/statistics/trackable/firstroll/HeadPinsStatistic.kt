package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.isHeadPin
import ca.josephroque.bowlingcompanion.core.model.isHeadPin2
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class HeadPinsStatistic(
	var headPins: Int = 0,
) : TrackablePerFirstRoll, CountingStatistic {
	override val id = StatisticID.HEAD_PINS
	override val category = StatisticCategory.HEAD_PINS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = HeadPinsStatistic()

	override var count: Int
		get() = headPins
		set(value) {
			headPins = value
		}

	override fun adjustByFirstRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration) {
		if (firstRoll.pinsDowned.isHeadPin() ||
			(configuration.countHeadPin2AsHeadPin && firstRoll.pinsDowned.isHeadPin2())
		) {
			headPins++
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
