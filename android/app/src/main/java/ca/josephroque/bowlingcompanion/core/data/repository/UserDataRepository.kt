package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
	val userData: Flow<UserData>

	suspend fun didCompleteOnboarding()
	suspend fun didCompleteLegacyMigration()
}