package ca.josephroque.bowlingcompanion.feature.teamform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamForm
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormTopBar
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormUiAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun TeamFormRoute(
	onDismiss: () -> Unit,
	onManageTeamMembers: (Set<BowlerID>, ResourcePickerResultKey) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: TeamFormViewModel = hiltViewModel(),
	resultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
) {
	val teamFormScreenState = viewModel.uiState.collectAsState().value

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(TEAM_FORM_MEMBER_PICKER_RESULT_KEY) { BowlerID(it) }
			.onEach { viewModel.handleAction(TeamFormScreenUiAction.MembersUpdated(it)) }
			.launchIn(this)
	}

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						TeamFormScreenEvent.Dismissed -> onDismiss()
						is TeamFormScreenEvent.ManageTeamMembers ->
							onManageTeamMembers(it.existingMembers, TEAM_FORM_MEMBER_PICKER_RESULT_KEY)
					}
				}
		}
	}

	TeamFormScreen(
		state = teamFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamFormScreen(
	state: TeamFormScreenUiState,
	onAction: (TeamFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(TeamFormScreenUiAction.LoadTeam)
	}

	BackHandler {
		onAction(TeamFormScreenUiAction.TeamForm(TeamFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			TeamFormTopBar(
				state = when (state) {
					TeamFormScreenUiState.Loading -> TeamFormTopBarUiState()
					is TeamFormScreenUiState.Create -> state.topBar
					is TeamFormScreenUiState.Edit -> state.topBar
				},
				onAction = { onAction(TeamFormScreenUiAction.TeamForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			TeamFormScreenUiState.Loading -> Unit
			is TeamFormScreenUiState.Create -> TeamForm(
				state = state.form,
				onAction = { onAction(TeamFormScreenUiAction.TeamForm(it)) },
				modifier = Modifier.padding(padding),
			)
			is TeamFormScreenUiState.Edit -> TeamForm(
				state = state.form,
				onAction = { onAction(TeamFormScreenUiAction.TeamForm(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
