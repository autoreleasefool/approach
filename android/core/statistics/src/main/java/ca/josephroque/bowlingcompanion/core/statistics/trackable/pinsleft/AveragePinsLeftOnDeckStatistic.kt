package ca.josephroque.bowlingcompanion.core.statistics.trackable.pinsleft

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.pinCount
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.AveragingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.utils.pinsLeftOnDeck

data class AveragePinsLeftOnDeckStatistic(
	var totalPinsLeftOnDeck: Int = 0,
	var gamesPlayed: MutableSet<GameID> = mutableSetOf(),
) : TrackablePerFrame,
	AveragingStatistic {
	override val id = StatisticID.AVERAGE_PINS_LEFT_ON_DECK
	override val category = StatisticCategory.PINS_LEFT_ON_DECK
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = AveragePinsLeftOnDeckStatistic()

	override var total: Int
		get() = totalPinsLeftOnDeck
		set(value) {
			totalPinsLeftOnDeck = value
		}

	@Suppress("UNUSED_PARAMETER")
	override var divisor: Int
		get() = gamesPlayed.size
		set(value) { /* No-op */ }

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		if (frame.rolls.isEmpty()) {
			return
		}
		totalPinsLeftOnDeck += frame.pinsLeftOnDeck.pinCount()
		gamesPlayed.add(frame.gameId)
	}

	override fun aggregateWithStatistic(statistic: Statistic) {
		if (statistic !is AveragePinsLeftOnDeckStatistic) {
			return
		}
		totalPinsLeftOnDeck += statistic.totalPinsLeftOnDeck
		gamesPlayed.addAll(statistic.gamesPlayed)
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Team -> true
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> false
	}
}
