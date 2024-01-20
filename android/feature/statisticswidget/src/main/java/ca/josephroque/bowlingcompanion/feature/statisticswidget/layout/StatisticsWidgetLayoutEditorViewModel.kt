package ca.josephroque.bowlingcompanion.feature.statisticswidget.layout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.widget.WidgetDeleted
import ca.josephroque.bowlingcompanion.core.analytics.trackable.widget.WidgetLayoutUpdated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorTopBarUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StatisticsWidgetLayoutEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val statisticsWidgetRepository: StatisticsWidgetsRepository,
	private val analyticsClient: AnalyticsClient,
): ApproachViewModel<StatisticsWidgetLayoutEditorScreenEvent>() {
	private val context = Route.StatisticsWidgetLayoutEditor.getContext(savedStateHandle)!!
	private val initialSource: StatisticsWidgetInitialSource? = Route.StatisticsWidgetLayoutEditor.getInitialSource(savedStateHandle)?.let {
		val split = it.split("_")
		when (split[0]) {
			"bowler" -> StatisticsWidgetInitialSource.Bowler(UUID.fromString(split[1]))
			else -> null
		}
	}

	private val _uiState: MutableStateFlow<StatisticsWidgetLayoutEditorScreenUiState> =
		MutableStateFlow(StatisticsWidgetLayoutEditorScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()
	private val hasLoadedWidgets = _uiState.value is StatisticsWidgetLayoutEditorScreenUiState.Loaded

	fun handleAction(action: StatisticsWidgetLayoutEditorScreenUiAction) {
		when (action) {
			StatisticsWidgetLayoutEditorScreenUiAction.LoadWidgets -> loadWidgets()
			is StatisticsWidgetLayoutEditorScreenUiAction.LayoutEditor -> handleLayoutEditorAction(action.action)
		}
	}

	private fun handleLayoutEditorAction(action: StatisticsWidgetLayoutEditorUiAction) {
		when (action) {
			StatisticsWidgetLayoutEditorUiAction.BackClicked -> dismiss()
			StatisticsWidgetLayoutEditorUiAction.AddWidgetClicked -> addWidget()
			is StatisticsWidgetLayoutEditorUiAction.WidgetMoved -> moveWidget(action.from, action.to)
			is StatisticsWidgetLayoutEditorUiAction.WidgetClicked -> handleWidgetClicked(action.widget)
			is StatisticsWidgetLayoutEditorUiAction.ToggleDeleteMode -> toggleDeleteMode(action.deleteMode)
		}
	}

	private fun loadWidgets() {
		if (hasLoadedWidgets) return
		viewModelScope.launch {
			val widgets = statisticsWidgetRepository.getStatisticsWidgets(context).first()
			_uiState.update {
				StatisticsWidgetLayoutEditorScreenUiState.Loaded(
					topBar = StatisticsWidgetLayoutEditorTopBarUiState(),
					layoutEditor = StatisticsWidgetLayoutEditorUiState(
						widgets = widgets,
					),
				)
			}
		}
	}

	private fun dismiss() {
		sendEvent(StatisticsWidgetLayoutEditorScreenEvent.Dismissed)
	}

	private fun addWidget() {
		when (val state = _uiState.value) {
			StatisticsWidgetLayoutEditorScreenUiState.Loading -> Unit
			is StatisticsWidgetLayoutEditorScreenUiState.Loaded ->
				sendEvent(StatisticsWidgetLayoutEditorScreenEvent.AddWidget(
					context = context,
					initialSource = initialSource,
					priority = state.layoutEditor.widgets.size,
				))
		}
	}

	private fun moveWidget(from: Int, to: Int) {
		when (val state = _uiState.value) {
			StatisticsWidgetLayoutEditorScreenUiState.Loading -> return
			is StatisticsWidgetLayoutEditorScreenUiState.Loaded -> {
				val validIndexRange = 0..<state.layoutEditor.widgets.size
				if (!validIndexRange.contains(from) || !validIndexRange.contains(to)) {
					return
				}
			}
		}

		val state = _uiState.updateWidgets {
			it.copy(
				layoutEditor = it.layoutEditor.copy(
					widgets = it.layoutEditor.widgets.toMutableList()
						.apply { add(to.coerceAtMost(it.layoutEditor.widgets.size - 1), removeAt(from)) }
				)
			)
		}

		if (state != null) {
			viewModelScope.launch {
				statisticsWidgetRepository.updateStatisticsWidgetsOrder(
					widgets = state.layoutEditor.widgets.map(StatisticsWidget::id),
				)
			}
		}

		analyticsClient.trackEvent(WidgetLayoutUpdated(
			context = context,
			numberOfWidgets = state?.layoutEditor?.widgets?.size ?: 0,
		))
	}

	private fun handleWidgetClicked(widget: StatisticsWidget) {
		val state = _uiState.updateWidgets {
			if (it.layoutEditor.isDeleteModeEnabled) {
				it.copy(
					layoutEditor = it.layoutEditor.copy(
						widgets = it.layoutEditor.widgets.toMutableList()
							.apply { remove(widget) },
					),
				)
			} else {
				it
			}
		}

		if (state?.layoutEditor?.isDeleteModeEnabled == true) {
			viewModelScope.launch {
				statisticsWidgetRepository.deleteStatisticWidget(widget.id)
			}

			analyticsClient.trackEvent(WidgetDeleted(context = context))
		}
	}

	private fun toggleDeleteMode(isDeleteModeEnabled: Boolean) {
		_uiState.updateWidgets {
			it.copy(
				layoutEditor = it.layoutEditor.copy(isDeleteModeEnabled = isDeleteModeEnabled),
				topBar = it.topBar.copy(isDeleteModeEnabled = isDeleteModeEnabled),
			)
		}
	}
}