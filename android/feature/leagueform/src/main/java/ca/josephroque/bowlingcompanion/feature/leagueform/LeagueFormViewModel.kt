package ca.josephroque.bowlingcompanion.feature.leagueform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.LEAGUE_ID
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.GamesPerSeries
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.IncludeAdditionalPinFall
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiAction
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LeagueFormViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val leaguesRepository: LeaguesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
): ApproachViewModel<LeagueFormScreenEvent>() {

	private val _uiState: MutableStateFlow<LeagueFormScreenUiState> = MutableStateFlow(LeagueFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val bowlerId = savedStateHandle.get<String>(BOWLER_ID)?.let {
		UUID.fromString(it)
	}

	private val leagueId = savedStateHandle.get<String>(LEAGUE_ID)?.let {
		UUID.fromString(it)
	}

	fun handleAction(action: LeagueFormScreenUiAction) {
		when (action) {
			LeagueFormScreenUiAction.LoadLeague -> loadLeague()
			is LeagueFormScreenUiAction.LeagueForm -> handleLeagueFormAction(action.action)
		}
	}

	private fun handleLeagueFormAction(action: LeagueFormUiAction) {
		when (action) {
			LeagueFormUiAction.BackClicked -> handleBackClicked()
			LeagueFormUiAction.DoneClicked -> saveLeague()
			LeagueFormUiAction.DiscardChangesClicked -> sendEvent(LeagueFormScreenEvent.Dismissed)
			LeagueFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is LeagueFormUiAction.NameChanged -> updateName(action.name)
			is LeagueFormUiAction.RecurrenceChanged -> updateRecurrence(action.recurrence)
			is LeagueFormUiAction.ExcludeFromStatisticsChanged -> updateExcludeFromStatistics(action.excludeFromStatistics)
			is LeagueFormUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is LeagueFormUiAction.GamesPerSeriesChanged -> updateGamesPerSeries(action.gamesPerSeries)
			is LeagueFormUiAction.IncludeAdditionalPinFallChanged -> updateIncludeAdditionalPinFall(action.includeAdditionalPinFall)
			is LeagueFormUiAction.AdditionalPinFallChanged -> updateAdditionalPinFall(action.additionalPinFall)
			is LeagueFormUiAction.AdditionalGamesChanged -> updateAdditionalGames(action.additionalGames)
			is LeagueFormUiAction.ArchiveClicked -> setArchiveLeaguePrompt(isVisible = true)
			is LeagueFormUiAction.ConfirmArchiveClicked -> archiveLeague()
			is LeagueFormUiAction.DismissArchiveClicked -> setArchiveLeaguePrompt(isVisible = false)
		}
	}

	private fun getFormUiState(): LeagueFormUiState? =
		when (val state = _uiState.value) {
			LeagueFormScreenUiState.Loading -> null
			is LeagueFormScreenUiState.Create -> state.form
			is LeagueFormScreenUiState.Edit -> state.form
		}

	private fun setFormUiState(state: LeagueFormUiState) {
		when (val uiState = _uiState.value) {
			LeagueFormScreenUiState.Loading -> Unit
			is LeagueFormScreenUiState.Create -> _uiState.value = uiState.copy(form = state)
			is LeagueFormScreenUiState.Edit -> _uiState.value = uiState.copy(form = state)
		}
	}

	private fun loadLeague() {
		viewModelScope.launch {
			val leagueId = savedStateHandle.get<String>(LEAGUE_ID)?.let {
				UUID.fromString(it)
			}
			if (leagueId == null) {
				_uiState.value = LeagueFormScreenUiState.Create(
					topBar = LeagueFormTopBarUiState(
						existingName = null,
					),
					form = LeagueFormUiState(),
				)
			} else {
				val league = leaguesRepository.getLeagueDetails(leagueId)
					.first()
				val update = LeagueUpdate(
					id = league.id,
					name = league.name,
					additionalGames = league.additionalGames,
					additionalPinFall = league.additionalPinFall,
					excludeFromStatistics = league.excludeFromStatistics,
				)

				val additionalGames = update.additionalGames

				_uiState.value = LeagueFormScreenUiState.Edit(
					initialValue = update,
					topBar = LeagueFormTopBarUiState(
						existingName = league.name,
					),
					form = LeagueFormUiState(
						name = league.name,
						nameErrorId = null,
						excludeFromStatistics = league.excludeFromStatistics,
						includeAdditionalPinFall = if ((additionalGames ?: 0) > 0) IncludeAdditionalPinFall.INCLUDE else IncludeAdditionalPinFall.NONE,
						additionalPinFall = league.additionalPinFall ?: 0,
						additionalGames = additionalGames ?: 0,
						recurrence = null,
						numberOfGames = null,
						gamesPerSeries = null,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = true,
						isShowingDiscardChangesDialog = false,
					),
				)
			}
		}
	}

	private fun saveLeague() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				LeagueFormScreenUiState.Loading -> Unit
				is LeagueFormScreenUiState.Create ->
					if (state.isSavable()) {
						val bowlerId = this@LeagueFormViewModel.bowlerId ?: return@launch

						val stateAdditionalGames = state.form.additionalGames
						val stateAdditionalPinFall = state.form.additionalPinFall

						val additionalGames = if (stateAdditionalGames > 0) stateAdditionalGames else null
						val additionalPinFall = if ((additionalGames ?: 0) > 0) stateAdditionalPinFall else null

						val league = LeagueCreate(
							bowlerId = bowlerId,
							id = leagueId ?: UUID.randomUUID(),
							name = state.form.name,
							recurrence = state.form.recurrence ?: LeagueRecurrence.REPEATING,
							numberOfGames = when (state.form.gamesPerSeries) {
								GamesPerSeries.DYNAMIC, null -> null
								GamesPerSeries.STATIC -> state.form.numberOfGames
							},
							additionalPinFall = when (state.form.includeAdditionalPinFall) {
								IncludeAdditionalPinFall.INCLUDE -> additionalPinFall
								IncludeAdditionalPinFall.NONE -> null
							},
							additionalGames = when (state.form.includeAdditionalPinFall) {
								IncludeAdditionalPinFall.INCLUDE -> additionalGames
								IncludeAdditionalPinFall.NONE -> null
							},
							excludeFromStatistics = state.form.excludeFromStatistics,
						)

						leaguesRepository.insertLeague(league)
						recentlyUsedRepository.didRecentlyUseLeague(league.id)
						sendEvent(LeagueFormScreenEvent.Dismissed)
					} else {
						_uiState.value = state.copy(
							form = state.form.copy(
								nameErrorId = if (state.form.name.isBlank()) ca.josephroque.bowlingcompanion.feature.leagueform.ui.R.string.league_form_property_name_missing else null
							)
						)
					}
				is LeagueFormScreenUiState.Edit ->
					if (state.isSavable()) {
						val league = state.form.updatedModel(id = state.initialValue.id)
						leaguesRepository.updateLeague(league)
						recentlyUsedRepository.didRecentlyUseLeague(league.id)
						sendEvent(LeagueFormScreenEvent.Dismissed)
					} else {
						_uiState.value = state.copy(
							form = state.form.copy(
								nameErrorId = if (state.form.name.isBlank()) ca.josephroque.bowlingcompanion.feature.leagueform.ui.R.string.league_form_property_name_missing else null
							)
						)
					}
			}
		}
	}

	private fun handleBackClicked() {
		if (_uiState.value.hasAnyChanges()) {
			setDiscardChangesDialog(isVisible = true)
		} else {
			sendEvent(LeagueFormScreenEvent.Dismissed)
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			isShowingDiscardChangesDialog = isVisible,
		))
	}

	private fun archiveLeague() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				LeagueFormScreenUiState.Loading, is LeagueFormScreenUiState.Create -> Unit
				is LeagueFormScreenUiState.Edit ->
					leaguesRepository.archiveLeague(state.initialValue.id)
			}

			setArchiveLeaguePrompt(isVisible = false)
			sendEvent(LeagueFormScreenEvent.Dismissed)
		}

	}

	private fun setArchiveLeaguePrompt(isVisible: Boolean) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			isShowingArchiveDialog = isVisible,
		))
	}

	private fun updateName(name: String) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			name = name,
			nameErrorId = null,
		))
	}

	private fun updateRecurrence(recurrence: LeagueRecurrence) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			recurrence = recurrence,
			gamesPerSeries = when (recurrence) {
				LeagueRecurrence.REPEATING -> state.gamesPerSeries
				LeagueRecurrence.ONCE -> GamesPerSeries.STATIC
			}
		))
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			numberOfGames = max(min(numberOfGames, League.NumberOfGamesRange.last), League.NumberOfGamesRange.first),
		))
	}

	private fun updateGamesPerSeries(gamesPerSeries: GamesPerSeries) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			gamesPerSeries = gamesPerSeries,
		))
	}

	private fun updateExcludeFromStatistics(excludeFromStatistics: ExcludeFromStatistics) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			excludeFromStatistics = excludeFromStatistics,
		))
	}

	private fun updateIncludeAdditionalPinFall(includeAdditionalPinFall: IncludeAdditionalPinFall) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			includeAdditionalPinFall = includeAdditionalPinFall,
		))
	}

	private fun updateAdditionalPinFall(additionalPinFall: Int) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			additionalPinFall = additionalPinFall,
		))
	}

	private fun updateAdditionalGames(additionalGames: Int) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			additionalGames = additionalGames,
		))
	}
}