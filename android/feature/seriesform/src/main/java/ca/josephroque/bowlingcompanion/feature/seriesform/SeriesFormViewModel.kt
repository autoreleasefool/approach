package ca.josephroque.bowlingcompanion.feature.seriesform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.series.SeriesArchived
import ca.josephroque.bowlingcompanion.core.analytics.trackable.series.SeriesCreated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.series.SeriesUpdated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.teamseries.TeamSeriesCreated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesLeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesCreate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

val SERIES_FORM_ALLEY_PICKER_RESULT_KEY = ResourcePickerResultKey("SeriesFormAlleyPickerResultKey")
val SERIES_FORM_LEAGUE_PICKER_RESULT_KEY = ResourcePickerResultKey("SeriesFormLeaguePickerResultKey")

@HiltViewModel
class SeriesFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val alleysRepository: AlleysRepository,
	private val seriesRepository: SeriesRepository,
	private val leaguesRepository: LeaguesRepository,
	private val gamesRepository: GamesRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
	private val analyticsClient: AnalyticsClient,
	private val featureFlags: FeatureFlagsClient,
) : ApproachViewModel<SeriesFormScreenEvent>() {
	private val _uiState: MutableStateFlow<SeriesFormScreenUiState> =
		MutableStateFlow(SeriesFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is SeriesFormScreenUiState.Loading

	private val teamId by lazy { Route.AddTeamSeries.getTeam(savedStateHandle) }
	private val teamLeagues by lazy { Route.AddTeamSeries.getLeagues(savedStateHandle) }
	private val leagueId by lazy { Route.AddSeries.getLeague(savedStateHandle) }
	private val seriesId by lazy { Route.EditSeries.getSeries(savedStateHandle) }

	fun handleAction(action: SeriesFormScreenUiAction) {
		when (action) {
			SeriesFormScreenUiAction.LoadSeries -> loadSeries()
			is SeriesFormScreenUiAction.AlleyUpdated -> updateAlley(alleyId = action.alleyId)
			is SeriesFormScreenUiAction.LeagueUpdated -> updateLeague(leagueId = action.leagueId)
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
			SeriesFormUiAction.LeagueClicked -> editLeague()
			SeriesFormUiAction.DateClicked -> setDatePicker(isVisible = true)
			SeriesFormUiAction.DatePickerDismissed -> setDatePicker(isVisible = false)
			SeriesFormUiAction.DiscardChangesClicked -> dismiss()
			SeriesFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is SeriesFormUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is SeriesFormUiAction.DateChanged -> updateDate(action.date)
			is SeriesFormUiAction.PreBowlChanged -> updatePreBowl(action.preBowl)
			is SeriesFormUiAction.ExcludeFromStatisticsChanged ->
				updateExcludeFromStatistics(action.excludeFromStatistics)
			is SeriesFormUiAction.AppliedDateChanged -> updateAppliedDate(action.date)
			SeriesFormUiAction.AppliedDateClicked -> setAppliedDatePicker(isVisible = true)
			SeriesFormUiAction.AppliedDatePickerDismissed -> setAppliedDatePicker(isVisible = false)
			is SeriesFormUiAction.IsUsingPreBowlChanged -> updateIsUsingPreBowl(action.isUsingPreBowl)
			is SeriesFormUiAction.ManualScoreChanged -> updateManualScore(action.index, action.score)
			is SeriesFormUiAction.IsCreatingManualSeriesChanged ->
				updateIsCreatingManualSeries(action.isCreatingManualSeries)
		}
	}

	private fun loadSeries() {
		if (hasLoadedInitialState) return

		viewModelScope.launch {
			val series = seriesId?.let { seriesRepository.getSeriesDetails(it).first() }
			val league = (leagueId ?: series?.league?.id)?.let {
				leaguesRepository.getLeagueDetails(it).first()
			}

			val uiState = if (league == null) {
				val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
				SeriesFormScreenUiState.TeamCreate(
					form = SeriesFormUiState(
						league = SeriesFormUiState.SeriesLeague.NoLeague,
						numberOfGames = Series.DEFAULT_NUMBER_OF_GAMES,
						date = currentDate,
						appliedDate = currentDate,
						preBowl = SeriesPreBowl.REGULAR,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						alley = null,
						isDatePickerVisible = false,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = false,
						isShowingDiscardChangesDialog = false,
						isPreBowlSectionVisible = false,
						isPreBowlFormEnabled = false,
						isAppliedDatePickerVisible = false,
						isUsingPreBowl = false,
						isCreatingManualSeries = false,
						manualScores = emptyList(),
						isManualSeriesEnabled = featureFlags.isEnabled(FeatureFlag.MANUAL_TEAM_SERIES_FORM),
						isMovingSeriesEnabled = false,
					),
					topBar = SeriesFormTopBarUiState(
						existingDate = null,
						isSaveButtonEnabled = true,
					),
				)
			} else if (series == null) {
				val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
				SeriesFormScreenUiState.Create(
					form = SeriesFormUiState(
						league = SeriesFormUiState.SeriesLeague.InitialLeague(league.asSummary()),
						numberOfGames = league.numberOfGames ?: Series.DEFAULT_NUMBER_OF_GAMES,
						date = currentDate,
						appliedDate = currentDate,
						preBowl = SeriesPreBowl.REGULAR,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						leagueExcludeFromStatistics = league.excludeFromStatistics,
						alley = null,
						isDatePickerVisible = false,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = false,
						isShowingDiscardChangesDialog = false,
						isPreBowlSectionVisible = true,
						isPreBowlFormEnabled = featureFlags.isEnabled(FeatureFlag.PRE_BOWL_FORM),
						isAppliedDatePickerVisible = false,
						isUsingPreBowl = false,
						isCreatingManualSeries = false,
						manualScores = emptyList(),
						isManualSeriesEnabled = featureFlags.isEnabled(FeatureFlag.MANUAL_SERIES_FORM),
						isMovingSeriesEnabled = false,
					),
					topBar = SeriesFormTopBarUiState(
						existingDate = null,
						isSaveButtonEnabled = true,
					),
				)
			} else {
				SeriesFormScreenUiState.Edit(
					initialValue = SeriesUpdate(
						id = series.properties.id,
						alleyId = series.alley?.id,
						date = series.properties.date,
						appliedDate = series.properties.appliedDate,
						preBowl = series.properties.preBowl,
						excludeFromStatistics = series.properties.excludeFromStatistics,
					),
					form = SeriesFormUiState(
						league = SeriesFormUiState.SeriesLeague.InitialLeague(league.asSummary()),
						numberOfGames = null,
						date = series.properties.date,
						appliedDate = series.properties.appliedDate ?: series.properties.date,
						isDatePickerVisible = false,
						preBowl = series.properties.preBowl,
						excludeFromStatistics = series.properties.excludeFromStatistics,
						leagueExcludeFromStatistics = league.excludeFromStatistics,
						alley = series.alley,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = true,
						isShowingDiscardChangesDialog = false,
						isAppliedDatePickerVisible = false,
						isPreBowlSectionVisible = true,
						isUsingPreBowl = series.properties.appliedDate != null &&
							series.properties.preBowl == SeriesPreBowl.PRE_BOWL,
						isPreBowlFormEnabled = featureFlags.isEnabled(FeatureFlag.PRE_BOWL_FORM),
						isCreatingManualSeries = false,
						manualScores = emptyList(),
						isManualSeriesEnabled = false,
						isMovingSeriesEnabled = when (league.recurrence) {
							LeagueRecurrence.ONCE -> false
							LeagueRecurrence.REPEATING -> featureFlags.isEnabled(FeatureFlag.MOVING_SERIES_BETWEEN_LEAGUES)
						},
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
		sendEvent(
			SeriesFormScreenEvent.EditAlley(
				alleyId = when (val state = _uiState.value) {
					SeriesFormScreenUiState.Loading -> return
					is SeriesFormScreenUiState.TeamCreate -> state.form.alley?.id
					is SeriesFormScreenUiState.Create -> state.form.alley?.id
					is SeriesFormScreenUiState.Edit -> state.form.alley?.id
				},
			),
		)
	}

	private fun editLeague() {
		when (val state = _uiState.value) {
			SeriesFormScreenUiState.Loading -> return
			is SeriesFormScreenUiState.TeamCreate -> return
			is SeriesFormScreenUiState.Create -> return
			is SeriesFormScreenUiState.Edit -> viewModelScope.launch {
				val series = seriesId?.let { seriesRepository.getSeriesDetails(it).first() } ?: return@launch
				sendEvent(
					SeriesFormScreenEvent.EditLeague(
						bowlerId = series.bowler.id,
						leagueId = state.form.league.id,
					),
				)
			}
		}
	}

	private fun updateAlley(alleyId: AlleyID?) {
		if (!hasLoadedInitialState) return
		viewModelScope.launch {
			val alley = alleyId?.let { alleysRepository.getAlleyDetails(it).first() }
			_uiState.updateForm {
				it.copy(
					alley = if (alley == null) {
						null
					} else {
						SeriesDetails.Alley(id = alley.id, name = alley.name)
					},
				)
			}
		}
	}

	private fun updateLeague(leagueId: LeagueID?) {
		if (!hasLoadedInitialState) return
		viewModelScope.launch {
			val leagueDetails = leagueId?.let { leaguesRepository.getLeagueDetails(it).first() } ?: return@launch
			_uiState.updateForm { it.copy(league = it.league.changedTo(leagueDetails.asSummary())) }
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

	private fun setAppliedDatePicker(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isAppliedDatePickerVisible = isVisible) }
	}

	private fun setArchiveSeriesPrompt(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingArchiveDialog = isVisible) }
	}

	private fun archiveSeries() {
		viewModelScope.launch {
			val series = when (val uiState = _uiState.value) {
				SeriesFormScreenUiState.Loading,
				is SeriesFormScreenUiState.Create,
				is SeriesFormScreenUiState.TeamCreate,
				-> return@launch
				is SeriesFormScreenUiState.Edit -> uiState.initialValue
			}

			seriesRepository.archiveSeries(series.id)
			dismiss()

			analyticsClient.trackEvent(SeriesArchived)
		}
	}

	private fun updateDate(date: LocalDate) {
		_uiState.updateForm { it.copy(date = date, isDatePickerVisible = false) }
	}

	private fun updateAppliedDate(date: LocalDate) {
		_uiState.updateForm { it.copy(appliedDate = date, isAppliedDatePickerVisible = false) }
	}

	private fun updatePreBowl(preBowl: SeriesPreBowl) {
		_uiState.updateForm {
			when {
				it.leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE ->
					it.copy(
						preBowl = preBowl,
						excludeFromStatistics = ExcludeFromStatistics.EXCLUDE,
					)
				preBowl == SeriesPreBowl.PRE_BOWL ->
					it.copy(
						preBowl = preBowl,
						excludeFromStatistics = if (it.isUsingPreBowl) {
							it.excludeFromStatistics
						} else {
							ExcludeFromStatistics.EXCLUDE
						},
					)
				it.leagueExcludeFromStatistics == ExcludeFromStatistics.INCLUDE &&
					preBowl == SeriesPreBowl.REGULAR ->
					it.copy(
						preBowl = preBowl,
						isUsingPreBowl = false,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					)
				else -> it.copy(preBowl = preBowl)
			}
		}
	}

	private fun updateIsUsingPreBowl(isUsingPreBowl: Boolean) {
		_uiState.updateForm {
			if (it.preBowl == SeriesPreBowl.PRE_BOWL) {
				it.copy(
					isUsingPreBowl = isUsingPreBowl,
					excludeFromStatistics = if (isUsingPreBowl) {
						it.excludeFromStatistics
					} else {
						ExcludeFromStatistics.EXCLUDE
					},
				)
			} else {
				it
			}
		}
	}

	private fun updateIsCreatingManualSeries(isCreatingManualSeries: Boolean) {
		_uiState.updateForm {
			it.copy(
				isCreatingManualSeries = isCreatingManualSeries,
				manualScores = if (isCreatingManualSeries) {
					List(it.numberOfGames ?: 0) { 0 }
				} else {
					emptyList()
				},
			)
		}
	}

	private fun updateManualScore(index: Int, score: String) {
		val intScore = score.toIntOrNull()?.coerceIn(0..Game.MAX_SCORE) ?: 0
		_uiState.updateForm {
			it.copy(
				manualScores = it.manualScores.toMutableList().apply {
					set(
						index,
						if (it.manualScores[index] == 0 && intScore % 10 == 0) {
							intScore / 10
						} else {
							intScore
						},
					)
				},
			)
		}
	}

	private fun updateExcludeFromStatistics(excludeFromStatistics: ExcludeFromStatistics) {
		_uiState.updateForm {
			when {
				it.leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE ->
					it.copy(excludeFromStatistics = ExcludeFromStatistics.EXCLUDE)
				it.preBowl == SeriesPreBowl.PRE_BOWL ->
					it.copy(
						excludeFromStatistics = if (it.isUsingPreBowl) {
							excludeFromStatistics
						} else {
							ExcludeFromStatistics.EXCLUDE
						},
					)
				else -> it.copy(excludeFromStatistics = excludeFromStatistics)
			}
		}
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		_uiState.updateForm {
			it.copy(
				numberOfGames = numberOfGames.coerceIn(League.NumberOfGamesRange),
				manualScores = if (it.isCreatingManualSeries) {
					if (it.manualScores.size < numberOfGames) {
						it.manualScores + List(numberOfGames - it.manualScores.size) { 0 }
					} else {
						it.manualScores.take(numberOfGames)
					}
				} else {
					it.manualScores
				},
			)
		}
	}

	private fun saveSeries() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				SeriesFormScreenUiState.Loading -> return@launch
				is SeriesFormScreenUiState.TeamCreate -> {
					// FIXME: Should be able to select manual scores
					val teamSeries = TeamSeriesCreate(
						teamId = teamId ?: return@launch,
						id = TeamSeriesID.randomID(),
						leagues = teamLeagues.takeIf { it.isNotEmpty() } ?: return@launch,
						date = state.form.date,
						numberOfGames = state.form.numberOfGames ?: Series.DEFAULT_NUMBER_OF_GAMES,
						preBowl = SeriesPreBowl.REGULAR,
						alleyId = state.form.alley?.id,
						excludeFromStatistics = state.form.excludeFromStatistics,
						manualScores = null,
					)

					teamSeriesRepository.insertTeamSeries(teamSeries)
					val initialGameId = gamesRepository.getTeamSeriesGameIds(teamSeries.id).first().first()
					sendEvent(
						SeriesFormScreenEvent.StartTeamSeries(
							teamSeriesId = teamSeries.id,
							initialGameId = initialGameId,
						),
					)
					analyticsClient.trackEvent(TeamSeriesCreated)
				}
				is SeriesFormScreenUiState.Create -> {
					val isUnusedPreBowl = state.form.preBowl == SeriesPreBowl.PRE_BOWL &&
						!state.form.isUsingPreBowl

					val series = SeriesCreate(
						leagueId = leagueId ?: return@launch,
						id = seriesId ?: SeriesID.randomID(),
						alleyId = state.form.alley?.id,
						date = state.form.date,
						preBowl = state.form.preBowl,
						excludeFromStatistics = if (isUnusedPreBowl) {
							ExcludeFromStatistics.EXCLUDE
						} else {
							state.form.excludeFromStatistics
						},
						numberOfGames = state.form.numberOfGames ?: Series.DEFAULT_NUMBER_OF_GAMES,
						appliedDate = if (state.form.isUsingPreBowl) state.form.appliedDate else null,
						manualScores = if (state.form.isCreatingManualSeries) {
							state.form.manualScores
						} else {
							null
						},
					)

					seriesRepository.insertSeries(series)
					dismiss(seriesId = series.id)
					analyticsClient.trackEvent(SeriesCreated)
				}
				is SeriesFormScreenUiState.Edit -> {
					when (state.form.league) {
						SeriesFormUiState.SeriesLeague.NoLeague, is SeriesFormUiState.SeriesLeague.InitialLeague -> {
							val series = state.form.updatedModel(state.initialValue)
							seriesRepository.updateSeries(series)
						}
						is SeriesFormUiState.SeriesLeague.UpdatedLeague -> {
							val series = SeriesLeagueUpdate(id = state.initialValue.id, leagueId = state.form.league.id)
							seriesRepository.updateSeriesLeague(series)
						}
					}

					dismiss()
					analyticsClient.trackEvent(SeriesUpdated)
				}
			}
		}
	}

	private fun dismiss(seriesId: SeriesID? = null) {
		sendEvent(SeriesFormScreenEvent.Dismissed(seriesId))
	}
}
