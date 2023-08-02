package ca.josephroque.bowlingcompanion.core.database.legacy.migration

import kotlinx.coroutines.flow.Flow

interface MigrationManager {
	val currentStep: Flow<MigrationStep?>

	suspend fun beginMigration()
}

enum class MigrationStep {
	TEAMS,
	BOWLERS,
	LEAGUES,
}