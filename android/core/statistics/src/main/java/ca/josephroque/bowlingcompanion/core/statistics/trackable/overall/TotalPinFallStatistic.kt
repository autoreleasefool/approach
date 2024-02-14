package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class TotalPinFallStatistic(
	var totalPinFall: Int = 0,
) : TrackablePerGame, CountingStatistic {
	override val id = StatisticID.TOTAL_PIN_FALL
	override val category = StatisticCategory.OVERALL
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = null
	override fun emptyClone() = TotalPinFallStatistic()

	override var count: Int
		get() = totalPinFall
		set(value) {
			totalPinFall = value
		}

	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		totalPinFall += game.score
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> false
	}
}
