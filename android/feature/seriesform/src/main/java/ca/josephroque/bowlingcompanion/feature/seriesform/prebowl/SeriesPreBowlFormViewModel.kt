package ca.josephroque.bowlingcompanion.feature.seriesform.prebowl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.series.SeriesPreBowlUpdated
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@HiltViewModel
class SeriesPreBowlFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val seriesRepository: SeriesRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<SeriesPreBowlFormScreenEvent>() {
	private val _uiState: MutableStateFlow<SeriesPreBowlFormScreenUiState> =
		MutableStateFlow(SeriesPreBowlFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val leagueId = Route.SeriesPreBowl.getLeague(savedStateHandle)!!

	init {
		_uiState.value = SeriesPreBowlFormScreenUiState.Loaded(
			form = SeriesPreBowlFormUiState(
				appliedDate = Clock.System.now().toLocalDate(),
			),
			topBar = SeriesPreBowlFormTopBarUiState(),
		)
	}

	fun handleAction(action: SeriesPreBowlFormScreenUiAction) {
		when (action) {
			is SeriesPreBowlFormScreenUiAction.SeriesUpdated -> updateSeries(seriesId = action.seriesId)
			is SeriesPreBowlFormScreenUiAction.Form -> handleFormAction(action.action)
			is SeriesPreBowlFormScreenUiAction.TopBar -> handleTopBarAction(action.action)
		}
	}

	private fun handleFormAction(action: SeriesPreBowlFormUiAction) {
		when (action) {
			SeriesPreBowlFormUiAction.SeriesClicked -> showSeriesPicker()
			SeriesPreBowlFormUiAction.AppliedDateClicked -> setAppliedDatePicker(isVisible = true)
			SeriesPreBowlFormUiAction.AppliedDatePickerDismissed -> setAppliedDatePicker(isVisible = false)
			is SeriesPreBowlFormUiAction.AppliedDateChanged -> updateAppliedDate(action.date)
		}
	}

	private fun handleTopBarAction(action: SeriesPreBowlFormTopBarUiAction) {
		when (action) {
			SeriesPreBowlFormTopBarUiAction.BackClicked -> dismiss()
			SeriesPreBowlFormTopBarUiAction.DoneClicked -> saveSeries()
		}
	}

	private fun setAppliedDatePicker(isVisible: Boolean) {
		_uiState.updateForm {
			it.copy(form = it.form.copy(isAppliedDatePickerVisible = isVisible))
		}
	}

	private fun updateAppliedDate(date: LocalDate) {
		_uiState.updateForm {
			it.copy(form = it.form.copy(appliedDate = date, isAppliedDatePickerVisible = false))
		}
	}

	private fun updateSeries(seriesId: SeriesID?) {
		viewModelScope.launch {
			val seriesDetails = seriesId?.let { seriesRepository.getSeriesDetails(seriesId).first() }
			_uiState.updateForm {
				it.copy(
					topBar = SeriesPreBowlFormTopBarUiState(isDoneEnabled = seriesDetails != null),
					form = it.form.copy(
						series = if (seriesDetails == null) {
							null
						} else {
							SeriesSummary(seriesDetails.properties.id, seriesDetails.properties.date)
						},
					),
				)
			}
		}
	}

	private fun showSeriesPicker() {
		val state = _uiState.value as? SeriesPreBowlFormScreenUiState.Loaded ?: return
		sendEvent(SeriesPreBowlFormScreenEvent.ShowSeriesPicker(leagueId, state.form.series?.id))
	}

	private fun saveSeries() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				SeriesPreBowlFormScreenUiState.Loading -> return@launch
				is SeriesPreBowlFormScreenUiState.Loaded -> {
					val series = state.form.series ?: return@launch
					seriesRepository.usePreBowl(series.id, state.form.appliedDate)
					dismiss()
					analyticsClient.trackEvent(SeriesPreBowlUpdated)
				}
			}
		}
	}

	private fun dismiss() {
		sendEvent(SeriesPreBowlFormScreenEvent.Dismissed)
	}
}
