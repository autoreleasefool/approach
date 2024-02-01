package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.onboarding

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding.AccessoriesOnboardingUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccessoriesOnboardingViewModel @Inject constructor(
	private val userDataRepository: UserDataRepository,
	@ApplicationScope private val externalScope: CoroutineScope,
): ApproachViewModel<AccessoriesOnboardingScreenEvent>() {
	fun handleAction(action: AccessoriesOnboardingScreenUiAction) {
		when (action) {
			AccessoriesOnboardingScreenUiAction.Dismissed -> finishAccessoriesOnboarding()
			is AccessoriesOnboardingScreenUiAction.AccessoriesOnboarding -> handleOnboardingAction(action.action)
		}
	}

	private fun handleOnboardingAction(action: AccessoriesOnboardingUiAction) {
		when (action) {
			AccessoriesOnboardingUiAction.GetStartedClicked -> finishAccessoriesOnboarding()
		}
	}

	private fun finishAccessoriesOnboarding() {
		externalScope.launch {
			userDataRepository.didOpenAccessoriesTab()
		}

		viewModelScope.launch {
			sendEvent(AccessoriesOnboardingScreenEvent.Dismissed)
		}
	}
}