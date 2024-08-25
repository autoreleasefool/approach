package ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SourcePickerViewModel @Inject constructor(
	private val statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	featureFlagsClient: FeatureFlagsClient,
	@ApplicationScope private val externalScope: CoroutineScope,
) : ApproachViewModel<SourcePickerScreenEvent>() {
	private var didLoadDefaultSource = false
	private val source: MutableStateFlow<TrackableFilter.Source?> = MutableStateFlow(null)
	private val sourceSummaries = source.map {
		if (it == null) {
			statisticsRepository.getDefaultSource()
		} else {
			statisticsRepository.getSourceDetails(it)
		}
	}
	private val isTeamsEnabled = featureFlagsClient.isEnabled(FeatureFlag.TEAMS)

	val uiState = sourceSummaries
		.map { source ->
			SourcePickerScreenUiState.Loaded(
				topBar = SourcePickerTopBarUiState(
					isApplyEnabled = source != null,
				),
				sourcePicker = SourcePickerUiState(
					isTeamsEnabled = isTeamsEnabled,
					source = source,
				),
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SourcePickerScreenUiState.Loading,
		)

	fun handleAction(action: SourcePickerScreenUiAction) {
		when (action) {
			is SourcePickerScreenUiAction.DidAppear -> loadDefaultSource()
			is SourcePickerScreenUiAction.UpdatedTeam -> setFilterTeam(action.team)
			is SourcePickerScreenUiAction.UpdatedBowler -> setFilterBowler(action.bowler)
			is SourcePickerScreenUiAction.UpdatedLeague -> setFilterLeague(action.league)
			is SourcePickerScreenUiAction.UpdatedSeries -> setFilterSeries(action.series)
			is SourcePickerScreenUiAction.UpdatedGame -> setFilterGame(action.game)
			is SourcePickerScreenUiAction.SourcePicker -> handleSourcePickerAction(action.action)
		}
	}

	private fun handleSourcePickerAction(action: SourcePickerUiAction) {
		when (action) {
			SourcePickerUiAction.Dismissed -> sendEvent(SourcePickerScreenEvent.Dismissed)
			SourcePickerUiAction.ApplyFilterClicked -> showDetailedStatistics()
			SourcePickerUiAction.TeamClicked -> showTeamPicker()
			SourcePickerUiAction.BowlerClicked -> showBowlerPicker()
			SourcePickerUiAction.LeagueClicked -> showLeaguePicker()
			SourcePickerUiAction.SeriesClicked -> showSeriesPicker()
			SourcePickerUiAction.GameClicked -> showGamePicker()
		}
	}

	private fun setFilterTeam(teamId: UUID?) {
		teamId?.let {
			source.value = TrackableFilter.Source.Team(it)
		}
	}

	private fun setFilterBowler(bowlerId: BowlerID?) {
		bowlerId?.let {
			source.value = TrackableFilter.Source.Bowler(it)
		}
	}

	private fun setFilterLeague(leagueId: LeagueID?) {
		leagueId?.let {
			source.value = TrackableFilter.Source.League(it)
		}
	}

	private fun setFilterSeries(seriesId: SeriesID?) {
		seriesId?.let {
			source.value = TrackableFilter.Source.Series(it)
		}
	}

	private fun setFilterGame(gameId: GameID?) {
		gameId?.let {
			source.value = TrackableFilter.Source.Game(it)
		}
	}

	private fun showDetailedStatistics() {
		val source = source.value ?: return
		sendEvent(SourcePickerScreenEvent.ShowStatistics(TrackableFilter(source = source)))

		externalScope.launch {
			userDataRepository.setLastTrackableFilterSource(source)
		}
	}

	private fun loadDefaultSource() {
		if (didLoadDefaultSource) return
		didLoadDefaultSource = true
		viewModelScope.launch {
			val defaultSource = userDataRepository.userData.first().lastTrackableFilter
			if (source.value == null) {
				source.value = defaultSource
			}
		}
	}

	private fun showTeamPicker() {
		viewModelScope.launch {
			when (val source = sourceSummaries.first()) {
				is TrackableFilter.SourceSummaries.Team -> sendEvent(
					SourcePickerScreenEvent.EditTeam(source.team.id),
				)
				is TrackableFilter.SourceSummaries.Bowler, null -> sendEvent(
					SourcePickerScreenEvent.EditTeam(null),
				)
			}
		}
	}

	private fun showBowlerPicker() {
		viewModelScope.launch {
			when (val source = sourceSummaries.first()) {
				is TrackableFilter.SourceSummaries.Bowler -> sendEvent(
					SourcePickerScreenEvent.EditBowler(source.bowler.id),
				)
				is TrackableFilter.SourceSummaries.Team, null -> sendEvent(
					SourcePickerScreenEvent.EditBowler(null),
				)
			}
		}
	}

	private fun showLeaguePicker() {
		viewModelScope.launch {
			// Only show league picker if bowler is selected
			val source = when (val sourceSummaries = sourceSummaries.first()) {
				is TrackableFilter.SourceSummaries.Team, null -> null
				is TrackableFilter.SourceSummaries.Bowler -> sourceSummaries
			} ?: return@launch

			sendEvent(SourcePickerScreenEvent.EditLeague(source.bowler.id, source.league?.id))
		}
	}

	private fun showSeriesPicker() {
		viewModelScope.launch {
			// Only show series picker if league is selected
			val source = when (val sourceSummaries = sourceSummaries.first()) {
				is TrackableFilter.SourceSummaries.Team, null -> null
				is TrackableFilter.SourceSummaries.Bowler -> sourceSummaries
			} ?: return@launch

			sendEvent(
				SourcePickerScreenEvent.EditSeries(source.league?.id ?: return@launch, source.series?.id),
			)
		}
	}

	private fun showGamePicker() {
		viewModelScope.launch {
			// Only show game picker if series is selected
			val source = when (val sourceSummaries = sourceSummaries.first()) {
				is TrackableFilter.SourceSummaries.Team, null -> null
				is TrackableFilter.SourceSummaries.Bowler -> sourceSummaries
			} ?: return@launch

			sendEvent(SourcePickerScreenEvent.EditGame(source.series?.id ?: return@launch, source.game?.id))
		}
	}
}
