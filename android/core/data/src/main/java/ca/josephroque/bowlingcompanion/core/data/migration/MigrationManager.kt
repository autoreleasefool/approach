package ca.josephroque.bowlingcompanion.core.data.migration

import kotlinx.coroutines.flow.Flow

interface MigrationManager {
	val currentStep: Flow<MigrationStep?>

	suspend fun beginMigration()
}

enum class MigrationStep {
	TEAMS,
	BOWLERS,
	TEAM_BOWLERS,
	LEAGUES,
	SERIES,
	GAMES,
	MATCH_PLAYS,
	FRAMES,
}