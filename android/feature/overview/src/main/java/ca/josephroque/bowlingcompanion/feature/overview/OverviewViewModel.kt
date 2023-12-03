package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STATISTICS_WIDGET_CONTEXT = "overview"

@HiltViewModel
class OverviewViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
): ApproachViewModel<OverviewScreenEvent>() {
	private val _bowlerToArchive: MutableStateFlow<BowlerListItem?> = MutableStateFlow(null)

	private val _bowlersListState: Flow<BowlersListUiState> =
		combine(
			bowlersRepository.getBowlersList(),
			_bowlerToArchive,
		) { bowlersList, bowlerToArchive ->
			BowlersListUiState(
				list = bowlersList,
				bowlerToArchive = bowlerToArchive,
			)
		}

	val uiState: StateFlow<OverviewScreenUiState> = _bowlersListState.map {
		OverviewScreenUiState.Loaded(
			overview = OverviewUiState(
				bowlersList = it,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = OverviewScreenUiState.Loading
	)

	fun handleAction(action: OverviewScreenUiAction) {
		when (action) {
			is OverviewScreenUiAction.OverviewAction -> handleOverviewAction(action.action)
		}
	}

	private fun handleOverviewAction(action: OverviewUiAction) {
		when (action) {
			is OverviewUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			is OverviewUiAction.EditStatisticsWidgetClicked -> sendEvent(OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT))
			is OverviewUiAction.BowlersListAction -> handleBowlersListAction(action.action)
		}
	}

	private fun handleBowlersListAction(action: BowlersListUiAction) {
		when (action) {
			is BowlersListUiAction.BowlerClicked -> sendEvent(OverviewScreenEvent.ShowBowlerDetails(action.id))
			is BowlersListUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			is BowlersListUiAction.BowlerEdited -> sendEvent(OverviewScreenEvent.EditBowler(action.id))
			is BowlersListUiAction.BowlerArchived -> setBowlerArchivePrompt(action.bowler)
			is BowlersListUiAction.ConfirmArchiveClicked -> archiveBowler()
			is BowlersListUiAction.DismissArchiveClicked -> setBowlerArchivePrompt(null)
		}
	}

	private fun setBowlerArchivePrompt(bowler: BowlerListItem?) {
		_bowlerToArchive.value = bowler
	}

	private fun archiveBowler() {
		val bowlerToArchive = _bowlerToArchive.value ?: return
		viewModelScope.launch {
			bowlersRepository.archiveBowler(bowlerToArchive.id)
			setBowlerArchivePrompt(null)
		}
	}
}