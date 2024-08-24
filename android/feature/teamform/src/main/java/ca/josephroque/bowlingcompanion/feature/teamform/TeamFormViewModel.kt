package ca.josephroque.bowlingcompanion.feature.teamform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.model.TeamCreate
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamform.ui.R
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormUiAction
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TeamFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val teamsRepository: TeamsRepository,
//	private var recentlyUsedRepository: RecentlyUsedRepository,
//	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<TeamFormScreenEvent>() {
	private val isEditing = Route.EditTeam.getTeam(savedStateHandle) != null
	private var teamId = Route.EditTeam.getTeam(savedStateHandle) ?: UUID.randomUUID().also {
		savedStateHandle[Route.EditTeam.ARG_TEAM] = it.toString()
	}

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is TeamFormScreenUiState.Loading

	private val _uiState: MutableStateFlow<TeamFormScreenUiState> =
		MutableStateFlow(TeamFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	fun handleAction(action: TeamFormScreenUiAction) {
		when (action) {
			TeamFormScreenUiAction.LoadTeam -> loadTeam()
			is TeamFormScreenUiAction.TeamForm -> handleTeamFormAction(action.action)
			is TeamFormScreenUiAction.MembersUpdated -> updateMembers(action.members)
		}
	}

	private fun handleTeamFormAction(action: TeamFormUiAction) {
		when (action) {
			TeamFormUiAction.BackClicked -> handleBackClicked()
			TeamFormUiAction.DoneClicked -> saveTeam()
			TeamFormUiAction.DeleteClicked -> setDeleteTeamPrompt(isVisible = true)
			TeamFormUiAction.ConfirmDeleteClicked -> deleteTeam()
			TeamFormUiAction.DismissDeleteClicked -> setDeleteTeamPrompt(isVisible = false)
			TeamFormUiAction.ManageTeamMembersClicked -> manageTeamMembers()
			TeamFormUiAction.DiscardChangesClicked -> sendEvent(TeamFormScreenEvent.Dismissed)
			TeamFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is TeamFormUiAction.NameChanged -> updateName(action.name)
		}
	}

	private fun loadTeam() {
		if (hasLoadedInitialState) return
		viewModelScope.launch {
			val team = if (isEditing) teamsRepository.getTeamUpdate(teamId).first() else null
			val uiState = if (team == null) {
				TeamFormScreenUiState.Create(
					form = TeamFormUiState(),
					topBar = TeamFormTopBarUiState(),
				)
			} else {
				TeamFormScreenUiState.Edit(
					initialValue = team,
					form = TeamFormUiState(
						name = team.name,
						members = team.members,
					),
					topBar = TeamFormTopBarUiState(
						existingName = team.name,
					),
				)
			}

			_uiState.value = uiState
		}
	}

	private fun handleBackClicked() {
		if (_uiState.value.hasAnyChanges()) {
		setDiscardChangesDialog(isVisible = true)
		} else {
			sendEvent(TeamFormScreenEvent.Dismissed)
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDiscardChangesDialog = isVisible) }
	}

	private fun deleteTeam() {
		viewModelScope.launch {
			val team = when (val uiState = _uiState.value) {
				TeamFormScreenUiState.Loading -> return@launch
				is TeamFormScreenUiState.Create -> return@launch
				is TeamFormScreenUiState.Edit -> uiState.initialValue
			}

			teamsRepository.deleteTeam(team.id)
			sendEvent(TeamFormScreenEvent.Dismissed)
			// TODO: analyticsClient.trackEvent(TeamDeleted)
		}
	}

	private fun saveTeam() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				TeamFormScreenUiState.Loading -> Unit
				is TeamFormScreenUiState.Create -> if (state.isSavable()) {
					val team = TeamCreate(
						id = teamId,
						name = state.form.name,
						members = state.form.members,
					)

					teamsRepository.insertTeam(team)
					// TODO: recentlyUsedRepository.didRecentlyUseTeam(team.id)
					sendEvent(TeamFormScreenEvent.Dismissed)

					// TODO: analyticsClient.trackEvent(TeamCreated)
				} else {
					_uiState.updateForm {
						it.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.team_form_property_name_missing else null,
							membersErrorId = if (state.form.members.size < 2) R.string.team_form_property_team_members_too_few else null,
						)
					}
				}
				is TeamFormScreenUiState.Edit -> if (state.isSavable()) {
					val team = state.form.updatedModel(existing = state.initialValue)

					teamsRepository.updateTeam(team)
					// TODO: recentlyUsedRepository.didRecentlyUseTeam(team.id)
					sendEvent(TeamFormScreenEvent.Dismissed)
					// TODO: analyticsClient.trackEvent(TeamUpdated)
				} else {
					_uiState.updateForm {
						it.copy(
							nameErrorId = if (it.name.isBlank()) R.string.team_form_property_name_missing else null,
							membersErrorId = if (it.members.size < 2) R.string.team_form_property_team_members_too_few else null,
						)
					}
				}
			}
		}
	}

	private fun updateMembers(members: List<UUID>) {
		viewModelScope.launch {
			val teamMembers = teamsRepository.getTeamMembers(members).first()
			_uiState.updateForm { it.copy(members = teamMembers) }
		}
	}

	private fun manageTeamMembers() {
		sendEvent(
			TeamFormScreenEvent.ManageTeamMembers(
				existingMembers = when (val state = _uiState.value) {
					TeamFormScreenUiState.Loading -> return
					is TeamFormScreenUiState.Create -> state.form.members.map(TeamMemberListItem::id)
					is TeamFormScreenUiState.Edit -> state.form.members.map(TeamMemberListItem::id)
				}
			),
		)
	}

	private fun updateName(name: String) {
		_uiState.updateForm { it.copy(name = name) }
	}

	private fun setDeleteTeamPrompt(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDeleteDialog = isVisible) }
	}
}