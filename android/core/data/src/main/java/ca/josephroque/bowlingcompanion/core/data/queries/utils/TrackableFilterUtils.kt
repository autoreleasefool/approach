package ca.josephroque.bowlingcompanion.core.data.queries.utils

import ca.josephroque.bowlingcompanion.core.common.utils.asBytes
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter

fun TrackableFilter.Source.buildWhereClause(
	leagueTableAlias: String,
	seriesTableAlias: String,
	gameTableAlias: String,
): List<String> = when (this) {
	is TrackableFilter.Source.Bowler -> listOf("$leagueTableAlias.bowler_id = ?source")
	is TrackableFilter.Source.League -> listOf("$seriesTableAlias.league_id = ?source")
	is TrackableFilter.Source.Series -> listOf("$gameTableAlias.series_id = ?source")
	is TrackableFilter.Source.Game -> listOf("$gameTableAlias.id = ?source")
}

fun TrackableFilter.Source.whereClauseArgs(): Map<String, Any> = mapOf(
	"source" to id.asBytes(),
)