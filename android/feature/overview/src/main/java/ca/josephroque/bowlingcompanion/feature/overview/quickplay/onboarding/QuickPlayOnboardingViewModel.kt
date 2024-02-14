package ca.josephroque.bowlingcompanion.feature.overview.quickplay.onboarding

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding.QuickPlayOnboardingUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class QuickPlayOnboardingViewModel @Inject constructor(
	private val userDataRepository: UserDataRepository,
	@ApplicationScope private val coroutineScope: CoroutineScope,
) : ApproachViewModel<QuickPlayOnboardingScreenEvent>() {

	val uiState = MutableStateFlow(QuickPlayOnboardingScreenUiState.Loaded)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = QuickPlayOnboardingScreenUiState.Loading,
		)

	fun handleAction(action: QuickPlayOnboardingScreenUiAction) {
		when (action) {
			is QuickPlayOnboardingScreenUiAction.QuickPlayOnboarding -> handleQuickPlayOnboardingAction(
				action.action,
			)
		}
	}

	private fun handleQuickPlayOnboardingAction(action: QuickPlayOnboardingUiAction) {
		when (action) {
			QuickPlayOnboardingUiAction.BackClicked, QuickPlayOnboardingUiAction.DoneClicked -> dismiss()
		}
	}

	private fun dismiss() {
		sendEvent(QuickPlayOnboardingScreenEvent.Dismissed)

		coroutineScope.launch {
			userDataRepository.didDismissQuickPlayTip()
		}
	}
}
