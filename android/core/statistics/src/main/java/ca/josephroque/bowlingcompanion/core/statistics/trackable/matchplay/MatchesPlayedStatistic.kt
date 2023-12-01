package ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic

data class MatchesPlayedStatistic(
	var matchesPlayed: Int = 0,
): CountingStatistic, TrackablePerGame {
	override val titleResourceId = R.string.statistic_title_match_plays
	override val category = StatisticCategory.MATCH_PLAY_RESULTS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = null

	override var count: Int
		get() = matchesPlayed
		set(value) { matchesPlayed = value }

	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		game.matchPlay ?: return
		matchesPlayed++
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> false
	}
}