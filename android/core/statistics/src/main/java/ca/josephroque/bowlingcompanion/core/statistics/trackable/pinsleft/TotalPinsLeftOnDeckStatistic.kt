package ca.josephroque.bowlingcompanion.core.statistics.trackable.pinsleft

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.pinCount
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.utils.pinsLeftOnDeck

data class TotalPinsLeftOnDeckStatistic(
	var totalPinsLeftOnDeck: Int = 0,
) : TrackablePerFrame, CountingStatistic {
	override val id = StatisticID.TOTAL_PINS_LEFT_ON_DECK
	override val category = StatisticCategory.PINS_LEFT_ON_DECK
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = null
	override fun emptyClone() = TotalPinsLeftOnDeckStatistic()

	override var count: Int
		get() = totalPinsLeftOnDeck
		set(value) {
			totalPinsLeftOnDeck = value
		}

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		if (frame.rolls.isEmpty()) {
			return
		}
		totalPinsLeftOnDeck += frame.pinsLeftOnDeck.pinCount()
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}
