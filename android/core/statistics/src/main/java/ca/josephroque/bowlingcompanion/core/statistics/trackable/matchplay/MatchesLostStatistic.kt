package ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.PercentageStatistic

data class MatchesLostStatistic(
	var matchesPlayed: Int = 0,
	var matchesLost: Int = 0,
): PercentageStatistic, TrackablePerGame {
	override val id = StatisticID.MATCHES_LOST
	override val category = StatisticCategory.MATCH_PLAY_RESULTS
	override val isEligibleForNewLabel = false
	override val preferredTrendDirection = PreferredTrendDirection.DOWNWARDS
	override fun emptyClone() = MatchesLostStatistic()

	override val numeratorTitleResourceId = R.string.statistic_title_match_play_losses
	override val denominatorTitleResourceId = R.string.statistic_title_match_plays
	override val includeNumeratorInFormattedValue = true

	override var numerator: Int
		get() = matchesLost
		set(value) { matchesLost = value }

	override var denominator: Int
		get() = matchesPlayed
		set(value) { matchesPlayed = value }

	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		val matchPlay = game.matchPlay ?: return
		matchesPlayed++
		when (matchPlay.result) {
			MatchPlayResult.LOST -> matchesLost++
			MatchPlayResult.TIED, MatchPlayResult.WON, null -> Unit
		}
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> false
	}
}