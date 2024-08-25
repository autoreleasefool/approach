package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.HighestOfStatistic

data class HighSingleStatistic(
	var highSingle: Int = 0,
) : TrackablePerGame, HighestOfStatistic {
	override val id = StatisticID.HIGH_SINGLE_GAME
	override val category = StatisticCategory.OVERALL
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = HighSingleStatistic()

	override var highest: Int
		get() = highSingle
		set(value) {
			highSingle = value
		}

	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		highSingle = maxOf(game.score, highSingle)
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Team -> true
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> false
	}
}
