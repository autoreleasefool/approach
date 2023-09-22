package ca.josephroque.bowlingcompanion.feature.onboarding.newuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewUserOnboardingViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
): ViewModel() {

	private val _uiState: MutableStateFlow<NewUserOnboardingUiState> =
		MutableStateFlow(NewUserOnboardingUiState.ShowingWelcomeMessage)
	val uiState: StateFlow<NewUserOnboardingUiState> = _uiState.asStateFlow()

	fun handleEvent(event: NewUserOnboardingUiEvent) {
		when (event) {
			NewUserOnboardingUiEvent.GetStartedClicked ->
				_uiState.value = NewUserOnboardingUiState.ShowingLogbook(name = "")
			NewUserOnboardingUiEvent.AddBowlerClicked ->
				addBowler()
			is NewUserOnboardingUiEvent.NameChanged ->
				updateName(event.name)
		}
	}

	private fun addBowler() {
		when (val state = _uiState.value) {
			NewUserOnboardingUiState.ShowingWelcomeMessage, NewUserOnboardingUiState.Complete -> Unit
			is NewUserOnboardingUiState.ShowingLogbook -> {
				if (state.name.isNotBlank()) {
					viewModelScope.launch {
						bowlersRepository.insertBowler(Bowler(
							id = UUID.randomUUID(),
							name = state.name,
							kind = BowlerKind.PLAYABLE,
						))
						_uiState.value = NewUserOnboardingUiState.Complete
					}
				}
			}
		}
	}

	private fun updateName(name: String) {
		when (val state = _uiState.value) {
			NewUserOnboardingUiState.ShowingWelcomeMessage, NewUserOnboardingUiState.Complete -> Unit
			is NewUserOnboardingUiState.ShowingLogbook -> _uiState.value = state.copy(name = name)
		}
	}
}

sealed interface NewUserOnboardingUiState {
	data object ShowingWelcomeMessage: NewUserOnboardingUiState
	data object Complete: NewUserOnboardingUiState
	data class ShowingLogbook(
		val name: String
	): NewUserOnboardingUiState
}

sealed interface NewUserOnboardingUiEvent {
	data object GetStartedClicked: NewUserOnboardingUiEvent
	data object AddBowlerClicked: NewUserOnboardingUiEvent
	class NameChanged(val name: String): NewUserOnboardingUiEvent
}