package ca.josephroque.bowlingcompanion.feature.seriesform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SeriesFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val alleysRepository: AlleysRepository,
	private val seriesRepository: SeriesRepository,
	private val leaguesRepository: LeaguesRepository,
): ApproachViewModel<SeriesFormScreenEvent>() {
	private val _uiState: MutableStateFlow<SeriesFormScreenUiState> =
		MutableStateFlow(SeriesFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is SeriesFormScreenUiState.Loading

	private val leagueId = Route.AddSeries.getLeague(savedStateHandle)
	private val seriesId = Route.EditSeries.getSeries(savedStateHandle)

	fun handleAction(action: SeriesFormScreenUiAction) {
		when (action) {
			SeriesFormScreenUiAction.LoadSeries -> loadSeries()
			is SeriesFormScreenUiAction.AlleyUpdated -> updateAlley(alleyId = action.alleyId)
			is SeriesFormScreenUiAction.SeriesForm -> handleSeriesFormAction(action.action)
		}
	}

	private fun handleSeriesFormAction(action: SeriesFormUiAction) {
		when (action) {
			SeriesFormUiAction.BackClicked -> handleBackClicked()
			SeriesFormUiAction.DoneClicked -> saveSeries()
			SeriesFormUiAction.ArchiveClicked -> setArchiveSeriesPrompt(isVisible = true)
			SeriesFormUiAction.ConfirmArchiveClicked -> archiveSeries()
			SeriesFormUiAction.DismissArchiveClicked -> setArchiveSeriesPrompt(isVisible = false)
			SeriesFormUiAction.AlleyClicked -> editAlley()
			SeriesFormUiAction.DateClicked -> setDatePicker(isVisible = true)
			SeriesFormUiAction.DatePickerDismissed -> setDatePicker(isVisible = false)
			SeriesFormUiAction.DiscardChangesClicked -> dismiss()
			SeriesFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is SeriesFormUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is SeriesFormUiAction.DateChanged -> updateDate(action.date)
			is SeriesFormUiAction.PreBowlChanged -> updatePreBowl(action.preBowl)
			is SeriesFormUiAction.ExcludeFromStatisticsChanged -> updateExcludeFromStatistics(action.excludeFromStatistics)
		}
	}

	private fun loadSeries() {
		if (hasLoadedInitialState) return

		viewModelScope.launch {
			val series = seriesId?.let { seriesRepository.getSeriesDetails(it).first() }
			val league = (leagueId ?: series?.properties?.leagueId)?.let { leaguesRepository.getLeagueDetails(it).first() } ?: return@launch

			val uiState = if (series == null) {
				SeriesFormScreenUiState.Create(
					form = SeriesFormUiState(
						numberOfGames = league.numberOfGames ?: Series.DefaultNumberOfGames,
						date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
						preBowl = SeriesPreBowl.REGULAR,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						leagueExcludeFromStatistics = league.excludeFromStatistics,
						alley = null,
						isDatePickerVisible = false,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = false,
						isShowingDiscardChangesDialog = false,
					),
					topBar = SeriesFormTopBarUiState(
						existingDate = null,
					),
				)
			} else {
				SeriesFormScreenUiState.Edit(
					initialValue = SeriesUpdate(
						id = series.properties.id,
						alleyId = series.alley?.id,
						date = series.properties.date,
						preBowl = series.properties.preBowl,
						excludeFromStatistics = series.properties.excludeFromStatistics,
					),
					form = SeriesFormUiState(
						numberOfGames = null,
						date = series.properties.date,
						isDatePickerVisible = false,
						preBowl = series.properties.preBowl,
						excludeFromStatistics = series.properties.excludeFromStatistics,
						leagueExcludeFromStatistics = league.excludeFromStatistics,
						alley = series.alley,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = true,
						isShowingDiscardChangesDialog = false,
					),
					topBar = SeriesFormTopBarUiState(
						existingDate = series.properties.date,
					),
				)
			}

			_uiState.value = uiState
		}
	}

	private fun editAlley() {
		sendEvent(SeriesFormScreenEvent.EditAlley(alleyId = when (val state = _uiState.value) {
			SeriesFormScreenUiState.Loading -> return
			is SeriesFormScreenUiState.Create -> state.form.alley?.id
			is SeriesFormScreenUiState.Edit -> state.form.alley?.id
		}))
	}

	private fun updateAlley(alleyId: UUID?) {
		if (!hasLoadedInitialState) return
		viewModelScope.launch {
			val alleyDetails = alleyId?.let { alleysRepository.getAlleyDetails(it).first() }
			_uiState.updateForm { it.copy(alley = alleyDetails) }
		}
	}

	private fun handleBackClicked() {
		if (_uiState.value.hasAnyChanges()) {
			setDiscardChangesDialog(isVisible = true)
		} else {
			dismiss()
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDiscardChangesDialog = isVisible) }
	}

	private fun setDatePicker(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isDatePickerVisible = isVisible) }
	}

	private fun setArchiveSeriesPrompt(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingArchiveDialog = isVisible) }
	}

	private fun archiveSeries() {
		viewModelScope.launch {
			val series = when (val uiState = _uiState.value) {
				SeriesFormScreenUiState.Loading -> return@launch
				is SeriesFormScreenUiState.Create -> return@launch
				is SeriesFormScreenUiState.Edit -> uiState.initialValue
			}

			seriesRepository.archiveSeries(series.id)
			dismiss()
		}
	}

	private fun updateDate(date: LocalDate) {
		_uiState.updateForm { it.copy(date = date, isDatePickerVisible = false) }
	}

	private fun updatePreBowl(preBowl: SeriesPreBowl) {
		_uiState.updateForm { it.copy(preBowl = preBowl) }
	}

	private fun updateExcludeFromStatistics(excludeFromStatistics: ExcludeFromStatistics) {
		_uiState.updateForm {
			when {
				it.leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE -> return@updateForm it
				it.preBowl == SeriesPreBowl.PRE_BOWL -> return@updateForm it
			}
			it.copy(excludeFromStatistics = excludeFromStatistics)
		}
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		_uiState.updateForm { it.copy(numberOfGames = numberOfGames.coerceIn(League.NumberOfGamesRange)) }
	}

	private fun saveSeries() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				SeriesFormScreenUiState.Loading -> return@launch
				is SeriesFormScreenUiState.Create -> {
					val series = SeriesCreate(
						leagueId = leagueId ?: return@launch,
						id = seriesId ?: UUID.randomUUID(),
						alleyId = state.form.alley?.id,
						date = state.form.date,
						preBowl = state.form.preBowl,
						excludeFromStatistics = state.form.excludeFromStatistics,
						numberOfGames = state.form.numberOfGames ?: Series.DefaultNumberOfGames,
					)

					seriesRepository.insertSeries(series)
					dismiss(seriesId = series.id)
				}
				is SeriesFormScreenUiState.Edit -> {
					val series = state.form.updatedModel(state.initialValue)
					seriesRepository.updateSeries(series)
					dismiss()
				}
			}
		}
	}

	private fun dismiss(seriesId: UUID? = null) {
		sendEvent(SeriesFormScreenEvent.Dismissed(seriesId))
	}
}