package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {
	private val _bowlerToArchive: MutableStateFlow<BowlerListItem?> = MutableStateFlow(null)

	val bowlersListState: StateFlow<BowlersListUiState> =
		combine(
			bowlersRepository.getBowlersList(),
			_bowlerToArchive,
		) { bowlersList, bowlerToArchive ->
			BowlersListUiState.Success(
				list = bowlersList,
				bowlerToArchive = bowlerToArchive,
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = BowlersListUiState.Loading
		)

	fun archiveBowler(id: UUID?) {
		val bowlerToArchive = _bowlerToArchive.value
		if (bowlerToArchive != null && bowlerToArchive.id == id) {
			_bowlerToArchive.value = null
			viewModelScope.launch {
				withContext(ioDispatcher) {
					bowlersRepository.archiveBowler(bowlerToArchive.id, archivedOn = Clock.System.now())
				}
			}
		} else {
			_bowlerToArchive.value = id?.let {
				when (val state = bowlersListState.value) {
					BowlersListUiState.Loading -> null
					is BowlersListUiState.Success -> state.list.find { it.id == id }
				}
			}
		}
	}

	fun editStatisticsWidget() {
		// TODO: Navigate to statistics widget editor
	}
}