package ca.josephroque.bowlingcompanion.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.data.migration.MigrationManager
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyDatabaseHelper
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val migrationManager: MigrationManager,
	private val userDataRepository: UserDataRepository,
	fileManager: FileManager,
): ViewModel() {
	private val _uiState: MutableStateFlow<OnboardingScreenUiState> = MutableStateFlow(
		if (fileManager.fileExists(fileManager.getDatabasePath(LegacyDatabaseHelper.DATABASE_NAME))) {
			OnboardingScreenUiState.LegacyUser()
		} else {
			OnboardingScreenUiState.NewUser()
		}
	)
	val uiState = _uiState.asStateFlow()

	private val _events: MutableStateFlow<OnboardingScreenEvent?> = MutableStateFlow(null)
	val events = combine(
		userDataRepository.userData,
		_events,
	) { userData, events ->
		if (userData.isOnboardingComplete) {
			OnboardingScreenEvent.FinishedOnboarding
		} else {
			events
		}
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = null,
		)

	fun handleAction(action: OnboardingScreenUiAction) {
		when (action) {
			is OnboardingScreenUiAction.NewUserOnboardingAction ->
				handleNewUserOnboardingAction(action.action)
			is OnboardingScreenUiAction.LegacyUserOnboardingAction ->
				handleLegacyUserOnboardingAction(action.action)
		}
	}

	private fun handleNewUserOnboardingAction(action: NewUserOnboardingUiAction) {
		when (action) {
			NewUserOnboardingUiAction.GetStartedClicked -> showLogbook()
			is NewUserOnboardingUiAction.AddBowlerClicked -> addBowler()
			is NewUserOnboardingUiAction.NameChanged -> updateName(action.name)
		}
	}

	private fun showLogbook() {
		val state = _uiState.value as? OnboardingScreenUiState.NewUser ?: return
		_uiState.value = state.copy(newUser = NewUserOnboardingUiState.ShowingLogbook(name = ""))
	}

	private fun updateName(name: String) {
		val state = _uiState.value as? OnboardingScreenUiState.NewUser ?: return
		val newUserState = state.newUser as? NewUserOnboardingUiState.ShowingLogbook ?: return

		_uiState.value = state.copy(newUser = newUserState.copy(name = name))
	}

	private fun addBowler() {
		val state = _uiState.value as? OnboardingScreenUiState.NewUser ?: return
		val newUserState = state.newUser as? NewUserOnboardingUiState.ShowingLogbook ?: return

		if (newUserState.name.isNotBlank()) {
			viewModelScope.launch {
				bowlersRepository.insertBowler(
					BowlerCreate(
						id = UUID.randomUUID(),
						name = newUserState.name,
						kind = BowlerKind.PLAYABLE,
					)
				)

				userDataRepository.didCompleteOnboarding()
			}
		}
	}

	private fun handleLegacyUserOnboardingAction(action: LegacyUserOnboardingUiAction) {
		when (action) {
			LegacyUserOnboardingUiAction.NewApproachHeaderClicked -> showApproachHeader()
			LegacyUserOnboardingUiAction.NewApproachHeaderAnimationFinished -> showApproachDetails()
			LegacyUserOnboardingUiAction.GetStartedClicked -> startDataImport()
		}
	}

	private fun showApproachHeader() {
		val state = _uiState.value as? OnboardingScreenUiState.LegacyUser ?: return
		if (state.legacyUser !is LegacyUserOnboardingUiState.Started) return

		_uiState.value = OnboardingScreenUiState.LegacyUser(
			legacyUser = LegacyUserOnboardingUiState.ShowingApproachHeader(isDetailsVisible = false),
		)
	}

	private fun showApproachDetails() {
		val state = _uiState.value as? OnboardingScreenUiState.LegacyUser ?: return
		val legacyUserState = state.legacyUser as? LegacyUserOnboardingUiState.ShowingApproachHeader ?: return

		_uiState.value = OnboardingScreenUiState.LegacyUser(
			legacyUser = legacyUserState.copy(isDetailsVisible = true),
		)
	}

	private fun startDataImport() {
		val state = _uiState.value as? OnboardingScreenUiState.LegacyUser ?: return
		if (state.legacyUser is LegacyUserOnboardingUiState.ImportingData) return

		_uiState.value = state.copy(legacyUser = LegacyUserOnboardingUiState.ImportingData)

		viewModelScope.launch {
			migrationManager.beginMigration()
		}

		viewModelScope.launch {
			// TODO: Show these steps in UI?
			// Currently, we wait for all steps to finish and when there are no more this will proceed
			migrationManager.currentStep
				.takeWhile { it != null }
				.collect {}

			userDataRepository.didCompleteOnboarding()
		}
	}
}