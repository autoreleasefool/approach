package ca.josephroque.bowlingcompanion.core.statistics.trackable.overall

import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.AveragingStatistic

data class GameAverageStatistic(
	var totalPinFall: Int = 0,
	var totalGames: Int = 0,
): TrackablePerGame, AveragingStatistic {
	override val titleResourceId = R.string.statistic_title_game_average
	override val category = StatisticCategory.OVERALL
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS

	override var total: Int
		get() = totalPinFall
		set(value) { totalPinFall = value }

	override var divisor: Int
		get() = totalGames
		set(value) { totalGames = value }

	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		if (game.score > 0) {
			totalPinFall += game.score
			totalGames++
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> false
	}
}