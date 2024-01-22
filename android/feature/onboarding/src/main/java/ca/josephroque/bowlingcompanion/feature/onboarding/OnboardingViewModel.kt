package ca.josephroque.bowlingcompanion.feature.onboarding

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.app.AppOnboardingCompleted
import ca.josephroque.bowlingcompanion.core.analytics.trackable.onboarding.OnboardingErrorReported
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import ca.josephroque.bowlingcompanion.core.common.utils.runWithMinimumDuration
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.migration.MigrationService
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyDatabaseHelper
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.AppNameChangeUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.DataImportUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.ImportErrorUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val migrationService: MigrationService,
	private val userDataRepository: UserDataRepository,
	private val systemInfoService: SystemInfoService,
	private val analyticsClient: AnalyticsClient,
	fileManager: FileManager,
): ApproachViewModel<OnboardingScreenEvent>() {
	private val _legacyDatabasePath =
		migrationService.getLegacyDatabasePath(LegacyDatabaseHelper.DATABASE_NAME)

	private val _uiState: MutableStateFlow<OnboardingScreenUiState> = MutableStateFlow(
		if (fileManager.fileExists(_legacyDatabasePath)) {
			OnboardingScreenUiState.LegacyUser()
		} else {
			OnboardingScreenUiState.NewUser()
		}
	)
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			userDataRepository.userData.collect {
				if (it.isOnboardingComplete) {
					sendEvent(OnboardingScreenEvent.FinishedOnboarding)
				}
			}
		}
	}

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

				// FIXME: Move this launch-based activity somewhere better
				// Only needed on first launch of the app
				userDataRepository.setAllStatisticIDsSeen()

				userDataRepository.didCompleteOnboarding()
				analyticsClient.trackEvent(AppOnboardingCompleted)
			}
		}
	}

	private fun handleLegacyUserOnboardingAction(action: LegacyUserOnboardingUiAction) {
		when (action) {
			LegacyUserOnboardingUiAction.NewApproachHeaderClicked -> showApproachHeader()
			LegacyUserOnboardingUiAction.NewApproachHeaderAnimationFinished -> showApproachDetails()
			is LegacyUserOnboardingUiAction.DataImport -> handleDataImportAction(action.action)
			is LegacyUserOnboardingUiAction.AppNameChange -> handleAppNameChangeAction(action.action)
			is LegacyUserOnboardingUiAction.ImportError -> handleImportErrorAction(action.action)
		}
	}

	private fun handleDataImportAction(action: DataImportUiAction) {
		when (action) {
			DataImportUiAction.SendEmailClicked -> analyticsClient.trackEvent(OnboardingErrorReported)
		}
	}

	private fun handleAppNameChangeAction(action: AppNameChangeUiAction) {
		when (action) {
			AppNameChangeUiAction.GetStartedClicked -> startDataImport()
		}
	}

	private fun handleImportErrorAction(action: ImportErrorUiAction) {
		when (action) {
			ImportErrorUiAction.RetryClicked -> startDataImport()
			ImportErrorUiAction.SendEmailClicked -> analyticsClient.trackEvent(OnboardingErrorReported)
		}
	}

	private fun showApproachHeader() {
		_uiState.update {
			if (it !is OnboardingScreenUiState.LegacyUser) return@update it
			it.copy(
				legacyUser = LegacyUserOnboardingUiState.AppNameChange(
					isShowingLegacyHeader = false,
					isShowingApproachHeader = true,
					isShowingDetails = false,
				),
			)
		}
	}

	private fun showApproachDetails() {
		_uiState.update {
			if (it !is OnboardingScreenUiState.LegacyUser) return@update it
			it.copy(
				legacyUser = LegacyUserOnboardingUiState.AppNameChange(
					isShowingLegacyHeader = false,
					isShowingApproachHeader = true,
					isShowingDetails = true,
				),
			)
		}
	}

	private fun startDataImport() {
		_uiState.update {
			if (it !is OnboardingScreenUiState.LegacyUser) return@update it
			it.copy(
				legacyUser = LegacyUserOnboardingUiState.DataImport(
					versionName = systemInfoService.versionName,
					versionCode = systemInfoService.versionCode,
				)
			)
		}

		viewModelScope.launch {
			try {
				runWithMinimumDuration(1_000) {
					migrationService.migrateDefaultLegacyDatabase()
				}

				userDataRepository.didCompleteOnboarding()
				analyticsClient.trackEvent(AppOnboardingCompleted)
			} catch (e: Exception) {
				_uiState.update {
					if (it !is OnboardingScreenUiState.LegacyUser) return@update it
					it.copy(
						legacyUser = LegacyUserOnboardingUiState.ImportError(
							versionName = systemInfoService.versionName,
							versionCode = systemInfoService.versionCode,
							message = e.localizedMessage ?: "An unknown error occurred.",
							exception = e,
							legacyDbUri = migrationService.getLegacyDatabaseUri(LegacyDatabaseHelper.DATABASE_NAME),
						)
					)
				}
			}
		}
	}
}