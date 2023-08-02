package ca.josephroque.bowlingcompanion.core.model

data class UserData(
	val isOnboardingComplete: Boolean = false,
	val isLegacyMigrationComplete: Boolean = false,
)