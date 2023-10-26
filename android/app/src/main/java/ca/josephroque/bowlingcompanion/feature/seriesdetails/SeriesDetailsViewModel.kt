package ca.josephroque.bowlingcompanion.feature.seriesdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesDetailsProperties
import ca.josephroque.bowlingcompanion.feature.gameslist.GamesListUiState
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.SERIES_ID
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	seriesRepository: SeriesRepository,
	gamesRepository: GamesRepository,
): ViewModel() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])

	val seriesDetailsState: StateFlow<SeriesDetailsUiState> =
		seriesRepository.getSeriesDetails(seriesId)
			.map {
				SeriesDetailsUiState.Success(
					it.properties,
					ChartEntryModelProducer(
						it.scores.mapIndexed { index, value -> entryOf(index.toFloat(), value.toFloat()) }
					).getModel()
				)
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = SeriesDetailsUiState.Loading,
			)

	var gamesListState: StateFlow<GamesListUiState> =
		gamesRepository.getGamesList(seriesId)
			.map(GamesListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = GamesListUiState.Loading,
			)
}

sealed interface SeriesDetailsUiState {
	data object Loading: SeriesDetailsUiState
	data class Success(
		val details: SeriesDetailsProperties,
		val scores: ChartEntryModel?,
	): SeriesDetailsUiState
}