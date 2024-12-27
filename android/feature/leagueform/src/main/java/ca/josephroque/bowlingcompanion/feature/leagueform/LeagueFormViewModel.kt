package ca.josephroque.bowlingcompanion.feature.leagueform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.league.LeagueArchived
import ca.josephroque.bowlingcompanion.core.analytics.trackable.league.LeagueCreated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.league.LeagueUpdated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.IncludeAdditionalPinFall
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiAction
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Integer.max
import java.lang.Integer.min
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class LeagueFormViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val leaguesRepository: LeaguesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<LeagueFormScreenEvent>() {

	private val _uiState: MutableStateFlow<LeagueFormScreenUiState> =
		MutableStateFlow(LeagueFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is LeagueFormScreenUiState.Loading

	private val bowlerId = Route.AddLeague.getBowler(savedStateHandle)
	private val leagueId = Route.EditLeague.getLeague(savedStateHandle)

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
			is LeagueFormUiAction.ExcludeFromStatisticsChanged -> updateExcludeFromStatistics(
				action.excludeFromStatistics,
			)
			is LeagueFormUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is LeagueFormUiAction.IncludeAdditionalPinFallChanged -> updateIncludeAdditionalPinFall(
				action.includeAdditionalPinFall,
			)
			is LeagueFormUiAction.AdditionalPinFallChanged -> updateAdditionalPinFall(
				action.additionalPinFall,
			)
			is LeagueFormUiAction.AdditionalGamesChanged -> updateAdditionalGames(action.additionalGames)
			is LeagueFormUiAction.ArchiveClicked -> setArchiveLeaguePrompt(isVisible = true)
			is LeagueFormUiAction.ConfirmArchiveClicked -> archiveLeague()
			is LeagueFormUiAction.DismissArchiveClicked -> setArchiveLeaguePrompt(isVisible = false)
		}
	}

	private fun loadLeague() {
		if (hasLoadedInitialState) return

		viewModelScope.launch {
			val leagueId = Route.EditLeague.getLeague(savedStateHandle)
			if (leagueId == null) {
				_uiState.value = LeagueFormScreenUiState.Create(
					topBar = LeagueFormTopBarUiState(
						existingName = null,
					),
					form = LeagueFormUiState(
						isEditing = false,
					),
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
					numberOfGames = league.numberOfGames,
				)

				val additionalGames = update.additionalGames

				_uiState.value = LeagueFormScreenUiState.Edit(
					initialValue = update,
					topBar = LeagueFormTopBarUiState(
						existingName = league.name,
					),
					form = LeagueFormUiState(
						isEditing = true,
						name = league.name,
						nameErrorId = null,
						excludeFromStatistics = league.excludeFromStatistics,
						includeAdditionalPinFall = if ((additionalGames ?: 0) > 0) {
							IncludeAdditionalPinFall.INCLUDE
						} else {
							IncludeAdditionalPinFall.NONE
						},
						additionalPinFall = league.additionalPinFall ?: 0,
						additionalGames = additionalGames ?: 0,
						recurrence = league.recurrence,
						numberOfGames = league.numberOfGames ?: Series.DEFAULT_NUMBER_OF_GAMES,
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
							id = leagueId ?: LeagueID.randomID(),
							name = state.form.name,
							recurrence = state.form.recurrence ?: LeagueRecurrence.REPEATING,
							numberOfGames = state.form.numberOfGames,
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
						analyticsClient.trackEvent(LeagueCreated)
					} else {
						_uiState.updateForm { form ->
							form.copy(
								nameErrorId = if (state.form.name.isBlank()) {
									@Suppress("ktlint:standard:max-line-length")
									ca.josephroque.bowlingcompanion.feature.leagueform.ui.R.string.league_form_property_name_missing
								} else {
									null
								},
							)
						}
					}
				is LeagueFormScreenUiState.Edit ->
					if (state.isSavable()) {
						val league = state.form.updatedModel(id = state.initialValue.id)
						leaguesRepository.updateLeague(league)
						recentlyUsedRepository.didRecentlyUseLeague(league.id)
						sendEvent(LeagueFormScreenEvent.Dismissed)
						analyticsClient.trackEvent(LeagueUpdated)
					} else {
						_uiState.updateForm { form ->
							form.copy(
								nameErrorId = if (state.form.name.isBlank()) {
									@Suppress("ktlint:standard:max-line-length")
									ca.josephroque.bowlingcompanion.feature.leagueform.ui.R.string.league_form_property_name_missing
								} else {
									null
								},
							)
						}
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
		_uiState.updateForm {
			it.copy(isShowingDiscardChangesDialog = isVisible)
		}
	}

	private fun archiveLeague() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				LeagueFormScreenUiState.Loading, is LeagueFormScreenUiState.Create -> Unit
				is LeagueFormScreenUiState.Edit -> {
					leaguesRepository.archiveLeague(state.initialValue.id)
					analyticsClient.trackEvent(LeagueArchived)
				}
			}

			setArchiveLeaguePrompt(isVisible = false)
			sendEvent(LeagueFormScreenEvent.Dismissed)
		}
	}

	private fun setArchiveLeaguePrompt(isVisible: Boolean) {
		_uiState.updateForm {
			it.copy(isShowingArchiveDialog = isVisible)
		}
	}

	private fun updateName(name: String) {
		_uiState.updateForm {
			it.copy(name = name, nameErrorId = null)
		}
	}

	private fun updateRecurrence(recurrence: LeagueRecurrence) {
		_uiState.updateForm {
			it.copy(recurrence = recurrence)
		}
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		_uiState.updateForm {
			it.copy(
				numberOfGames = max(
					min(numberOfGames, League.NumberOfGamesRange.last),
					League.NumberOfGamesRange.first,
				),
			)
		}
	}

	private fun updateExcludeFromStatistics(excludeFromStatistics: ExcludeFromStatistics) {
		_uiState.updateForm {
			it.copy(excludeFromStatistics = excludeFromStatistics)
		}
	}

	private fun updateIncludeAdditionalPinFall(includeAdditionalPinFall: IncludeAdditionalPinFall) {
		_uiState.updateForm {
			it.copy(includeAdditionalPinFall = includeAdditionalPinFall)
		}
	}

	private fun updateAdditionalPinFall(additionalPinFall: Int) {
		_uiState.updateForm {
			it.copy(additionalPinFall = additionalPinFall)
		}
	}

	private fun updateAdditionalGames(additionalGames: Int) {
		_uiState.updateForm {
			it.copy(additionalGames = additionalGames)
		}
	}
}
