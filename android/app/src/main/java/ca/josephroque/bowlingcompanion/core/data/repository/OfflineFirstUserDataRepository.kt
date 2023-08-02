package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.UserData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(): UserDataRepository {

	private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

	private val currentUserData
		get() = _userData.replayCache.firstOrNull() ?: UserData()

	override val userData: Flow<UserData> = _userData

	override suspend fun didCompleteOnboarding() {
		_userData.tryEmit(currentUserData.copy(isOnboardingComplete = true))
	}

	override suspend fun didCompleteLegacyMigration() {
		_userData.tryEmit(currentUserData.copy(isLegacyMigrationComplete = true))
	}
}