package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import android.net.Uri

sealed interface LegacyUserOnboardingUiState {
	data class AppNameChange(
		val isShowingLegacyHeader: Boolean = true,
		val isShowingApproachHeader: Boolean = false,
		val isShowingDetails: Boolean = false,
	) : LegacyUserOnboardingUiState

	data class DataImport(
		val versionName: String = "",
		val versionCode: String = "",
	) : LegacyUserOnboardingUiState

	data class ImportError(
		val versionName: String = "",
		val versionCode: String = "",
		val message: String = "",
		val exception: Exception? = null,
		val legacyDbUri: Uri? = null,
	) : LegacyUserOnboardingUiState
}

sealed interface LegacyUserOnboardingUiAction {
	data class AppNameChange(val action: AppNameChangeUiAction) : LegacyUserOnboardingUiAction
	data class DataImport(val action: DataImportUiAction) : LegacyUserOnboardingUiAction
	data class ImportError(val action: ImportErrorUiAction) : LegacyUserOnboardingUiAction

	data object NewApproachHeaderClicked : LegacyUserOnboardingUiAction
	data object NewApproachHeaderAnimationFinished : LegacyUserOnboardingUiAction
}

sealed interface AppNameChangeUiAction {
	data object GetStartedClicked : AppNameChangeUiAction
}

sealed interface DataImportUiAction {
	data object SendEmailClicked : DataImportUiAction
}

sealed interface ImportErrorUiAction {
	data object RetryClicked : ImportErrorUiAction
	data object SendEmailClicked : ImportErrorUiAction
}
