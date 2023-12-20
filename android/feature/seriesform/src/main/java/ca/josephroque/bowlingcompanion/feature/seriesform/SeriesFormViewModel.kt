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
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.LEAGUE_ID
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.SERIES_ID
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

	private val leagueId = savedStateHandle.get<String>(LEAGUE_ID)?.let {
		UUID.fromString(it)
	}

	private val seriesId = savedStateHandle.get<String>(SERIES_ID)?.let {
		UUID.fromString(it)
	}

	fun handleAction(action: SeriesFormScreenUiAction) {
		when (action) {
			SeriesFormScreenUiAction.LoadSeries -> loadSeries()
			is SeriesFormScreenUiAction.AlleyUpdated -> updateAlley(alleyId = action.alleyId)
			is SeriesFormScreenUiAction.SeriesForm -> handleSeriesFormAction(action.action)
		}
	}

	private fun handleSeriesFormAction(action: SeriesFormUiAction) {
		when (action) {
			SeriesFormUiAction.BackClicked -> sendEvent(SeriesFormScreenEvent.Dismissed)
			SeriesFormUiAction.DoneClicked -> saveSeries()
			SeriesFormUiAction.ArchiveClicked -> setArchiveSeriesPrompt(isVisible = true)
			SeriesFormUiAction.ConfirmArchiveClicked -> archiveSeries()
			SeriesFormUiAction.DismissArchiveClicked -> setArchiveSeriesPrompt(isVisible = false)
			SeriesFormUiAction.AlleyClicked -> sendEvent(SeriesFormScreenEvent.EditAlley(alleyId = getFormUiState()?.alley?.id))
			SeriesFormUiAction.DateClicked -> setDatePicker(isVisible = true)
			SeriesFormUiAction.DatePickerDismissed -> setDatePicker(isVisible = false)
			is SeriesFormUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is SeriesFormUiAction.DateChanged -> updateDate(action.date)
			is SeriesFormUiAction.PreBowlChanged -> updatePreBowl(action.preBowl)
			is SeriesFormUiAction.ExcludeFromStatisticsChanged -> updateExcludeFromStatistics(action.excludeFromStatistics)
		}
	}

	private fun getFormUiState(): SeriesFormUiState? =
		when (val uiState = uiState.value) {
			is SeriesFormScreenUiState.Create -> uiState.form
			is SeriesFormScreenUiState.Edit -> uiState.form
			else -> null
		}

	private fun setFormUiState(state: SeriesFormUiState) {
		when (val uiState = uiState.value) {
			is SeriesFormScreenUiState.Create -> _uiState.value = uiState.copy(form = state)
			is SeriesFormScreenUiState.Edit -> _uiState.value = uiState.copy(form = state)
			else -> Unit
		}
	}

	private fun loadSeries() {
		if (getFormUiState() != null) return
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
					),
					topBar = SeriesFormTopBarUiState(
						existingDate = series.properties.date,
					),
				)
			}

			_uiState.value = uiState
		}
	}

	private fun updateAlley(alleyId: UUID?) {
		// FIXME: prevent other form updates while alley is loading
		val uiState = getFormUiState() ?: return
		viewModelScope.launch {
			val alleyDetails = alleyId?.let { alleysRepository.getAlleyDetails(it).first() }
			setFormUiState(uiState.copy(alley = alleyDetails))
		}
	}

	private fun setDatePicker(isVisible: Boolean) {
		val uiState = getFormUiState() ?: return
		setFormUiState(uiState.copy(isDatePickerVisible = isVisible))
	}

	private fun setArchiveSeriesPrompt(isVisible: Boolean) {
		val uiState = getFormUiState() ?: return
		setFormUiState(uiState.copy(isShowingArchiveDialog = isVisible))
	}

	private fun archiveSeries() {
		val formState = getFormUiState() ?: return
		viewModelScope.launch {
			val series = when (val uiState = _uiState.value) {
				SeriesFormScreenUiState.Loading -> return@launch
				is SeriesFormScreenUiState.Create -> return@launch
				is SeriesFormScreenUiState.Edit -> uiState.initialValue
			}

			seriesRepository.archiveSeries(series.id)
			setFormUiState(state = formState.copy(isShowingArchiveDialog = false))
			sendEvent(SeriesFormScreenEvent.Dismissed)
		}
	}

	private fun updateDate(date: LocalDate) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(date = date))
		setDatePicker(isVisible = false)
	}

	private fun updatePreBowl(preBowl: SeriesPreBowl) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(preBowl = preBowl))
	}

	private fun updateExcludeFromStatistics(excludeFromStatistics: ExcludeFromStatistics) {
		val state = getFormUiState() ?: return
		when {
			state.leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE -> return
			state.preBowl == SeriesPreBowl.PRE_BOWL -> return
		}
		setFormUiState(state.copy(excludeFromStatistics = excludeFromStatistics))
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(numberOfGames = numberOfGames.coerceIn(League.NumberOfGamesRange)))
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
					sendEvent(SeriesFormScreenEvent.Dismissed)
				}
				is SeriesFormScreenUiState.Edit -> {
					val series = state.form.updatedModel(state.initialValue)
					seriesRepository.updateSeries(series)
					sendEvent(SeriesFormScreenEvent.Dismissed)
				}
			}
		}
	}
}