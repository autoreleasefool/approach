package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam

interface LegacyMigrationRepository {
	suspend fun migrateTeams(teams: List<LegacyTeam>)

	suspend fun recordCheckpoint()
}