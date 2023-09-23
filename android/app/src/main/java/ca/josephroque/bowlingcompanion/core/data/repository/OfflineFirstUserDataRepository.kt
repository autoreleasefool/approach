package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.datastore.ApproachPreferencesDataSource
import ca.josephroque.bowlingcompanion.core.model.UserData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
	private val approachPreferencesDataSource: ApproachPreferencesDataSource,
): UserDataRepository {

	override val userData: Flow<UserData> =
		approachPreferencesDataSource.userData


	override suspend fun didCompleteOnboarding() {
		approachPreferencesDataSource.setOnboardingComplete(true)
	}

	override suspend fun didCompleteLegacyMigration() {
		approachPreferencesDataSource.setLegacyMigrationComplete(true)
	}
}