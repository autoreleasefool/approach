package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.gearlist.GearListUiState
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
	savedStateHandle: SavedStateHandle,
	bowlersRepository: BowlersRepository,
	leaguesRepository: LeaguesRepository,
	gearRepository: GearRepository,
): ViewModel() {
	private val bowlerId = UUID.fromString(savedStateHandle[BOWLER_ID])

	val bowlerDetailsState: StateFlow<BowlerDetailsUiState> =
		bowlersRepository.getBowlerDetails(bowlerId)
			.map(BowlerDetailsUiState::Success)
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

	val gearListState: StateFlow<GearListUiState> =
		gearRepository.getBowlerPreferredGear(bowlerId)
			.map(GearListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = GearListUiState.Loading,
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
		val details: BowlerDetails,
	): BowlerDetailsUiState

	fun bowlerId(): UUID? = when (this) {
		Loading -> null
		is Success -> this.details.id
	}
}
