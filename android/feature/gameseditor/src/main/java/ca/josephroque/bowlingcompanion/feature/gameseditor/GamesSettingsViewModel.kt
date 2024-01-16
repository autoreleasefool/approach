package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesSettingsViewModel @Inject constructor(
	gamesRepository: GamesRepository,
	savedStateHandle: SavedStateHandle,
): ApproachViewModel<GamesSettingsScreenEvent>() {
	private val seriesId = Route.GameSettings.getSeries(savedStateHandle)!!
	private val _currentGameId = MutableStateFlow(Route.GameSettings.getCurrentGame(savedStateHandle)!!)

	val uiState: StateFlow<GamesSettingsScreenUiState> = combine(
		_currentGameId,
		gamesRepository.getGamesList(seriesId)
	) { currentGameId, games ->
		GamesSettingsScreenUiState.Loaded(
			GamesSettingsUiState(
				currentGameId = currentGameId,
				games = games,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = GamesSettingsScreenUiState.Loading,
	)

	fun handleAction(action: GamesSettingsScreenUiAction) {
		when (action) {
			is GamesSettingsScreenUiAction.GamesSettings -> handleGamesSettingsAction(action.action)
		}
	}

	private fun handleGamesSettingsAction(action: GamesSettingsUiAction) {
		when (action) {
			GamesSettingsUiAction.BackClicked -> dismiss()
			is GamesSettingsUiAction.GameClicked -> setCurrentGame(action.gameId)
		}
	}

	private fun dismiss() {
		sendEvent(GamesSettingsScreenEvent.DismissedWithResult(_currentGameId.value))
	}

	private fun setCurrentGame(gameId: UUID) {
		_currentGameId.value = gameId
	}
}