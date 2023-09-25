package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.LEAGUE_ID
import ca.josephroque.bowlingcompanion.feature.serieslist.SeriesChartable
import ca.josephroque.bowlingcompanion.feature.serieslist.SeriesListUiState
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
class LeagueDetailsViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	leaguesRepository: LeaguesRepository,
	seriesRepository: SeriesRepository,
): ViewModel() {
	private val leagueId = UUID.fromString(savedStateHandle[LEAGUE_ID])
		?: UUID.randomUUID().also { savedStateHandle[LEAGUE_ID] = it }

	val leagueDetailsState: StateFlow<LeagueDetailsUiState> =
		leaguesRepository.getLeagueDetails(leagueId)
			.map { LeagueDetailsUiState.Success(it) }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = LeagueDetailsUiState.Loading,
			)

	val seriesListState: StateFlow<SeriesListUiState> =
		seriesRepository.getSeriesList(leagueId)
			.map { it.map(SeriesListItem::chartable) }
			.map(SeriesListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = SeriesListUiState.Loading,
			)
}

fun SeriesListItem.chartable(): SeriesChartable =
	SeriesChartable(
		id = series.id,
		date = series.date,
		preBowl = series.preBowl,
		total = series.total,
		numberOfGames = scores.size,
		scores = ChartEntryModelProducer(
			scores.mapIndexed { index, value -> entryOf(index.toFloat(), value.score.toFloat()) }
		)
		.getModel()
	)

sealed interface LeagueDetailsUiState {
	data object Loading: LeagueDetailsUiState
	data class Success(
		val details: LeagueDetails,
	): LeagueDetailsUiState
}