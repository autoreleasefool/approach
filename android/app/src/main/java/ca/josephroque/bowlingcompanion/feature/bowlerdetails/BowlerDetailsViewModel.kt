package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.leagueslist.LeaguesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BowlerDetailsViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	bowlersRepository: BowlersRepository,
	leaguesRepository: LeaguesRepository,
): ViewModel() {
	private val bowlerId = UUID.fromString(savedStateHandle[BOWLER_ID])
		?: UUID.randomUUID().also { savedStateHandle[BOWLER_ID] = it }

	val bowlerDetailsState: StateFlow<BowlerDetailsUiState> =
		bowlersRepository.getBowler(bowlerId)
			.map {
				BowlerDetailsUiState.Success(it.name)
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = BowlerDetailsUiState.Loading,
			)

	val leaguesListState: StateFlow<LeaguesListUiState> =
		leaguesRepository.getLeaguesList(bowlerId)
			.map(LeaguesListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = LeaguesListUiState.Loading,
			)

	fun navigateToLeague(id: UUID) {
		// TODO: navigate to league
	}

	fun editStatisticsWidget() {
		// TODO: edit statistics widget
	}
}

sealed interface BowlerDetailsUiState {
	data object Loading: BowlerDetailsUiState
	data class Success(
		val bowlerName: String,
	): BowlerDetailsUiState
}
