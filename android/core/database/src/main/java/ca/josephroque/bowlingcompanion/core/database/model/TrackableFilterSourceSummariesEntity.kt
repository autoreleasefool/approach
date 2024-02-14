package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.SeriesSummary
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableFilterSourceSummariesEntity(
	@Embedded(prefix = "bowler_") val bowlerSummary: BowlerSummary,
	@Embedded(prefix = "league_") val leagueSummary: LeagueSummary?,
	@Embedded(prefix = "series_") val seriesSummary: SeriesSummary?,
	@Embedded(prefix = "game_") val gameSummary: GameSummary?,
) {
	fun asModel(): TrackableFilter.SourceSummaries = TrackableFilter.SourceSummaries(
		bowler = bowlerSummary,
		league = leagueSummary,
		series = seriesSummary,
		game = gameSummary,
	)
}
