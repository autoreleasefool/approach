package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyBowler
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyLeague
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacySeries
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam

interface LegacyMigrationRepository {
	suspend fun migrateTeams(teams: List<LegacyTeam>)
	suspend fun migrateBowlers(bowlers: List<LegacyBowler>)
	suspend fun migrateLeagues(leagues: List<LegacyLeague>)
	suspend fun migrateSeries(series: List<LegacySeries>)

	suspend fun recordCheckpoint()
}